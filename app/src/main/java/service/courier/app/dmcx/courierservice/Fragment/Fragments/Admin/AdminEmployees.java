package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.Employees.AdminEmployeeRecyclerViewAdapter;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Utility.AppUtils;
import service.courier.app.dmcx.courierservice.Utility.AppValidator;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminEmployees extends Fragment {

    public static final String TAG = "ADMIN-CLIENTS";

    private CoordinatorLayout adminEmployeeCL;
    private RecyclerView employeeRV;
    private FloatingActionButton addNewFab;
    private AdminEmployeeRecyclerViewAdapter adminEmployeeRecyclerViewAdapter;

    private List<Employee> employees;
    private List<String> usernames;

    private void loadRecyclerView() {
        employees = new ArrayList<>();
        usernames = new ArrayList<>();

        final AlertDialog sportsDialog = new SpotsDialog(MainActivity.instance, "Please wait...");
        sportsDialog.show();

        DatabaseReference reference = Vars.appFirebase.getDbEmployeesReference();
        reference.orderByChild(AFModel.username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!employees.isEmpty()) {
                        employees.clear();
                    }
                    if (!usernames.isEmpty()) {
                        usernames.clear();
                    }

                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot != null) {
                            Vars.appFirebase.checkEmployeeData(snapshot, new AppFirebase.FirebaseCallback() {
                                @Override
                                public void ProcessCallback(boolean isTaskCompleted) {
                                    if (isTaskCompleted) {
                                        Employee employee = snapshot.getValue(Employee.class);
                                        assert employee != null;
                                        if (employee.getAdmin_id().equals(Vars.appFirebase.getCurrentUser().getUid())) {
                                            employees.add(employee);
                                            usernames.add(employee.getName());
                                        }
                                    }
                                }

                                @Override
                                public void ExceptionCallback(String exception) {

                                }
                            });
                        }
                    }

                    adminEmployeeRecyclerViewAdapter = new AdminEmployeeRecyclerViewAdapter(employees);
                    adminEmployeeRecyclerViewAdapter.notifyDataSetChanged();
                    employeeRV.setAdapter(adminEmployeeRecyclerViewAdapter);
                }

                sportsDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ItemTouchHelper.Callback swipeRecyclerItemCallback() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                final boolean[] isDeleteSuccess = {true};
                final int position = viewHolder.getAdapterPosition();
                final Employee employee = employees.get(position);

                employees.remove(position);
                adminEmployeeRecyclerViewAdapter.notifyItemRemoved(position);

                Snackbar.make(adminEmployeeCL, "Item deleted!", Snackbar.LENGTH_SHORT)
                        .setActionTextColor(Color.WHITE)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                isDeleteSuccess[0] = false;
                                employees.add(position, employee);
                                adminEmployeeRecyclerViewAdapter.notifyDataSetChanged();
                                employeeRV.smoothScrollToPosition(position);
                            }
                        })
                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);

                                if (isDeleteSuccess[0]) {
                                    final DatabaseReference reference = Vars.appFirebase.getDbEmployeesReference().child(employee.getId());
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                if (!dataSnapshot.hasChild(AFModel.email)) {
                                                    Toast.makeText(MainActivity.instance, "This values are for development purpose and can't delete!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                final Employee removalEmployee = dataSnapshot.getValue(Employee.class);

                                                View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_admin_email_password, null);
                                                Vars.appDialog.create(MainActivity.instance, dialogView).transparent();
                                                Vars.appDialog.show();

                                                final EditText adminEmailET = dialogView.findViewById(R.id.adminEmailET);
                                                final EditText adminPasswordET = dialogView.findViewById(R.id.adminPasswordET);
                                                final Button cancelBTN = dialogView.findViewById(R.id.cancelBTN);
                                                final Button confirmBTN = dialogView.findViewById(R.id.confirmBTN);

                                                cancelBTN.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Vars.appDialog.dismiss();
                                                    }
                                                });

                                                confirmBTN.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Vars.appDialog.dismiss();

                                                        final AlertDialog spotDialog = new SpotsDialog(MainActivity.instance, "Deleting! Please wait...");
                                                        spotDialog.show();

                                                        final String adminEmail = adminEmailET.getText().toString();
                                                        final String adminPassword = adminPasswordET.getText().toString();

                                                        assert removalEmployee != null;
                                                        String decrypt = "";
                                                        try {
                                                            decrypt = AppUtils.Hash.decrypt(removalEmployee.getPassword());
                                                        } catch (Exception ex) {
                                                            Toast.makeText(MainActivity.instance, "Decrytion failed!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        final String employeeEmail = removalEmployee.getEmail();
                                                        final String employeePassword = decrypt;

                                                        final boolean isAdminEmailEmpty = AppValidator.empty(adminEmail);
                                                        boolean isAdminEmailNotValid = !AppValidator.validEmail(adminEmail);
                                                        boolean isAdminPasswordEmpty = AppValidator.empty(adminPassword);
                                                        boolean isAdminPasswordNotValid = !AppValidator.validPassword(adminPassword);

                                                        if (isAdminEmailEmpty) {
                                                            Toast.makeText(MainActivity.instance, "Email field is empty!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        } else if (isAdminEmailNotValid) {
                                                            Toast.makeText(MainActivity.instance, "Email is not valid!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        } else if (isAdminPasswordEmpty) {
                                                            Toast.makeText(MainActivity.instance, "Password field is empty!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        } else if (isAdminPasswordNotValid) {
                                                            Toast.makeText(MainActivity.instance, "Password must be at least 6 charecters!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        Vars.appFirebase.signInUser(employeeEmail, employeePassword, new AppFirebase.FirebaseCallback() {
                                                            @Override
                                                            public void ProcessCallback(boolean isTaskCompleted) {
                                                                if (isTaskCompleted) {
                                                                    final FirebaseUser userClient = Vars.appFirebase.getCurrentUser();
                                                                    AuthCredential authCredential = EmailAuthProvider.getCredential(employeeEmail, employeePassword);

                                                                    userClient.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                userClient.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Vars.appFirebase.signInUser(adminEmail, adminPassword, new AppFirebase.FirebaseCallback() {
                                                                                                @Override
                                                                                                public void ProcessCallback(boolean isTaskCompleted) {
                                                                                                    if (isTaskCompleted) {
                                                                                                        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()) {
                                                                                                                    DatabaseReference reference = Vars.appFirebase.getDbWorksReference().child(employee.getId());
                                                                                                                    reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                DatabaseReference statusReference = Vars.appFirebase.getDbStatusReference().child(employee.getId());
                                                                                                                                statusReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                        if (task.isSuccessful()) {
                                                                                                                                            usernames.remove(employee.getName());
                                                                                                                                            Toast.makeText(MainActivity.instance, "All the records are deleted successfully!", Toast.LENGTH_SHORT).show();     }
                                                                                                                                    }
                                                                                                                                });
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });

                                                                                                                    Toast.makeText(MainActivity.instance, "Deleted permenently!", Toast.LENGTH_SHORT).show();
                                                                                                                } else {
                                                                                                                    Toast.makeText(MainActivity.instance, "Error! While delete single work!", Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                                spotDialog.dismiss();
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void ExceptionCallback(String exception) {

                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    spotDialog.dismiss();
                                                                }
                                                            }

                                                            @Override
                                                            public void ExceptionCallback(String exception) {

                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }).show();

            }
        };

        return simpleCallback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_employee, container, false);

        adminEmployeeCL = view.findViewById(R.id.adminEmployeeCL);
        employeeRV = view.findViewById(R.id.employeeRV);
        addNewFab = view.findViewById(R.id.addNewFab);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.instance);
        employeeRV.setLayoutManager(linearLayoutManager);
        employeeRV.hasFixedSize();

        loadRecyclerView();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeRecyclerItemCallback());
        itemTouchHelper.attachToRecyclerView(employeeRV);

        addNewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_admin_create_new_employee, null);
                Vars.appDialog.create(MainActivity.instance, dialogView);
                Vars.appDialog.show();

                final EditText employeeNameET = dialogView.findViewById(R.id.employeeNameET);
                final EditText employeeEmailET = dialogView.findViewById(R.id.employeeEmailET);
                final EditText employeePasswordET = dialogView.findViewById(R.id.employeePasswordET);
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
                        final String name = employeeNameET.getText().toString();
                        final String email = employeeEmailET.getText().toString();
                        final String passwd = employeePasswordET.getText().toString();
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

                        for (String username : usernames) {
                            if (username.equals(name)) {
                                Toast.makeText(MainActivity.instance, "Name is already exists! Name must be uncommon.", Toast.LENGTH_SHORT).show();
                                return;
                            }
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
                                                String hashed = "";
                                                try {
                                                    hashed = AppUtils.Hash.encrypt(passwd);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                Map<String, Object> employeeMap = new HashMap<>();
                                                employeeMap.put(AFModel.image_path, "");
                                                employeeMap.put(AFModel.id, Vars.appFirebase.getCurrentUser().getUid());
                                                employeeMap.put(AFModel.username, name);
                                                employeeMap.put(AFModel.email, email);
                                                employeeMap.put(AFModel.password, hashed);
                                                employeeMap.put(AFModel.admin_id, adminId);
                                                employeeMap.put(AFModel.phone_no, "");
                                                employeeMap.put(AFModel.created_at, System.currentTimeMillis());
                                                employeeMap.put(AFModel.modified_at, System.currentTimeMillis());

                                                final DatabaseReference reference = Vars.appFirebase.getDbEmployeesReference().child(Vars.appFirebase.getCurrentUser().getUid());
                                                Vars.appFirebase.insert(reference, employeeMap, new AppFirebase.FirebaseCallback() {
                                                    @Override
                                                    public void ProcessCallback(boolean isTaskCompleted) {
                                                        if (isTaskCompleted) {
                                                            Vars.appFirebase.signInUser(adminEmail, adminPasswd, new AppFirebase.FirebaseCallback() {
                                                                @Override
                                                                public void ProcessCallback(boolean isTaskCompleted) {
                                                                    Vars.appDialog.dismiss();
                                                                    spotsDialog.dismiss();
                                                                    loadRecyclerView();

                                                                    Toast.makeText(MainActivity.instance, "Employee created!", Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void ExceptionCallback(String exception) {}
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void ExceptionCallback(String exception) {
                                                        if (!exception.equals("")) {
                                                            spotsDialog.dismiss();
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
