package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.AuthActivity;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Constent.Clients.ClientRecyclerViewAdapter;
import service.courier.app.dmcx.courierservice.Models.Client;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Utility.AppValidator;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class Clients extends Fragment {

    public static final String TAG = "ADMIN-CLIENTS";

    private RecyclerView serviceManListRV;
    private FloatingActionButton addNewFab;
    private ClientRecyclerViewAdapter clientRecyclerViewAdapter;

    private List<Client> clients;

    private List<Client> loadData() {
        clients = new ArrayList<>();
        return clients;
    }

    private void loadRecyclerView() {
        clientRecyclerViewAdapter = new ClientRecyclerViewAdapter(loadData());
        clientRecyclerViewAdapter.notifyDataSetChanged();

        serviceManListRV.setAdapter(clientRecyclerViewAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_clients, container, false);

        serviceManListRV = view.findViewById(R.id.serviceManListRV);
        addNewFab = view.findViewById(R.id.addNewFab);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.instance);
        serviceManListRV.setLayoutManager(linearLayoutManager);
        serviceManListRV.hasFixedSize();

        loadRecyclerView();

        addNewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_admin_create_new_client, null);
                Vars.appDialog.create(MainActivity.instance, dialogView);
                Vars.appDialog.show();

                final EditText clientNameET = dialogView.findViewById(R.id.clientNameET);
                final EditText clientEmailET = dialogView.findViewById(R.id.clientEmailET);
                final EditText clientPasswordET = dialogView.findViewById(R.id.clientPasswordET);
                final EditText adminPasswordET = dialogView.findViewById(R.id.adminPasswordET);
                final Button createBTN = dialogView.findViewById(R.id.createBTN);
                final Button cancelBTN = dialogView.findViewById(R.id.cancelBTN);

                cancelBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Vars.appDialog.dismiss();
                    }
                });

                createBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String name = clientNameET.getText().toString();
                        final String email = clientEmailET.getText().toString();
                        final String passwd = clientPasswordET.getText().toString();
                        final String adminPasswd = adminPasswordET.getText().toString();

                        boolean isNameEmpty = AppValidator.empty(name);
                        boolean isEmailEmpty = AppValidator.empty(email);
                        boolean isEmailNotValid = !AppValidator.validEmail(email);
                        boolean isPasswordEmpty = AppValidator.empty(passwd);
                        boolean isPasswordNotValid = !AppValidator.validPassword(passwd);
                        final boolean isAdminPasswordEmpty = AppValidator.empty(adminPasswd);
                        boolean isAdminPasswordNotValid = !AppValidator.validPassword(adminPasswd);

                        if (isNameEmpty) {
                            Toast.makeText(MainActivity.instance, "Name is empty!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (isEmailEmpty) {
                            Toast.makeText(MainActivity.instance, "Email field is empty!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (isEmailNotValid) {
                            Toast.makeText(MainActivity.instance, "Email is not valid!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (isPasswordEmpty) {
                            Toast.makeText(MainActivity.instance, "Password field is empty!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (isPasswordNotValid) {
                            Toast.makeText(MainActivity.instance, "Password must be at least 6 charecters!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (isAdminPasswordEmpty) {
                            Toast.makeText(MainActivity.instance, "Admin password must not be empty!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (isAdminPasswordNotValid) {
                            Toast.makeText(MainActivity.instance, "Admin password must be at least 6 charecters!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final AlertDialog spotsDialog = new SpotsDialog(MainActivity.instance, "Please wait...");
                        spotsDialog.show();

                        final String adminId = Vars.appFirebase.getCurrentUser().getUid();
                        final String adminEmail = Vars.appFirebase.getCurrentUser().getEmail();

                        Vars.appFirebase.signInUser(adminEmail, adminPasswd, new AppFirebase.FirebaseCallback() {
                            @Override
                            public void ProcessCallback(boolean isTaskCompleted) {
                                if (isTaskCompleted) {
                                    Vars.appFirebase.signUpUser(email, passwd, new AppFirebase.FirebaseCallback() {
                                        @Override
                                        public void ProcessCallback(boolean isTaskCompleted) {
                                            if (isTaskCompleted) {
                                                Map<String, Object> map = new HashMap<>();
                                                map.put(AFModel.image_path, "");
                                                map.put(AFModel.username, name);
                                                map.put(AFModel.admin_id, adminId);
                                                map.put(AFModel.phone_no, "");
                                                map.put(AFModel.status, "Online");
                                                map.put(AFModel.current_location, "");
                                                map.put(AFModel.work, "");
                                                map.put(AFModel.created_at, System.currentTimeMillis());
                                                map.put(AFModel.modified_at, System.currentTimeMillis());

                                                DatabaseReference reference =
                                                        Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.client)
                                                        .child(Vars.appFirebase.getCurrentUser().getUid());

                                                Vars.appFirebase.insert(reference, map, new AppFirebase.FirebaseCallback() {
                                                    @Override
                                                    public void ProcessCallback(boolean isTaskCompleted) {
                                                        if (isTaskCompleted) {
                                                            Vars.appFirebase.signInUser(adminEmail, adminPasswd, new AppFirebase.FirebaseCallback() {
                                                                @Override
                                                                public void ProcessCallback(boolean isTaskCompleted) {
                                                                    Vars.appDialog.dismiss();
                                                                    spotsDialog.dismiss();

                                                                    Toast.makeText(MainActivity.instance, "Client created!", Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void ExceptionCallback(String exception) {}
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void ExceptionCallback(String exception) {
                                                        if (!exception.equals("")) {
                                                            Log.d("APPTAG", "ExceptionCallback: " + exception);
                                                            Toast.makeText(MainActivity.instance, exception, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                spotsDialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void ExceptionCallback(String exception) {
                                            if (!exception.equals("")) {
                                                Log.d("APPTAG", "ExceptionCallback: " + exception);
                                                Toast.makeText(MainActivity.instance, exception, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void ExceptionCallback(String exception) {
                                if (!exception.equals("")) {
                                    Log.d("APPTAG", "ExceptionCallback: " + exception);
                                    Toast.makeText(MainActivity.instance, "Admin credentials not maching!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        return view;
    }
}
