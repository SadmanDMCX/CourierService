package service.courier.app.dmcx.courierservice.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Dialog.AppDialog;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.LocalDatabase.LocalDB;
import service.courier.app.dmcx.courierservice.Models.Admin;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Utility.AppValidator;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AuthActivity extends AppCompatActivity {

    public static AuthActivity instance;

    private ConstraintLayout signInCL;
    private ConstraintLayout signUpCL;
    private EditText nameSUET;
    private EditText emailSUET;
    private EditText passwordSUET;
    private Button signInSIBTN;
    private EditText emailSIET;
    private EditText passwordSIET;
    private Button signUpSUBTN;
    private ImageButton switchBTN;

    private Animation aslideDownToPosition;
    private Animation aslidePositionToDown;

    private HashMap<String, String> admins;

    private void loadAnimations() {
        aslideDownToPosition = AnimationUtils.loadAnimation(AuthActivity.instance, R.anim.slide_down_to_position);
        aslidePositionToDown = AnimationUtils.loadAnimation(AuthActivity.instance, R.anim.slide_position_to_down);
    }

    private void startMainActivity() {
        Intent intent = new Intent(AuthActivity.instance, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadAdminDetails() {
        admins = new HashMap<>();

        final AlertDialog spotsDialog = new SpotsDialog(instance, "Checking availablity...");
        spotsDialog.show();

        DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.admins);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!admins.isEmpty()) {
                    admins.clear();
                }

                @SuppressWarnings("unchecked")
                Map<String, Admin> adminMap = (Map<String, Admin>) dataSnapshot.getValue();
                if (adminMap != null) {
                    for (Map.Entry<String, Admin> entry : adminMap.entrySet()) {
                        Map singleAdminMap = (Map) entry.getValue();
                        String singleAdminMapKey = entry.getKey();
                        String singleAdminMapValue = (String) singleAdminMap.get(AFModel.username);

                        admins.put(singleAdminMapKey, singleAdminMapValue);
                    }
                }

                spotsDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        instance = this;
        Vars.appFirebase = new AppFirebase();
        Vars.appDialog = new AppDialog();
        Vars.localDB = new LocalDB(instance);

        signInCL = findViewById(R.id.signInCL);
        signUpCL = findViewById(R.id.signUpCL);

        emailSIET = findViewById(R.id.emailSIET);
        passwordSIET = findViewById(R.id.passwordSIET);
        signInSIBTN = findViewById(R.id.signInSIBTN);

        nameSUET = findViewById(R.id.nameSUET);
        emailSUET = findViewById(R.id.emailSUET);
        passwordSUET = findViewById(R.id.passwordSUET);
        signUpSUBTN = findViewById(R.id.signUpSUBTN);

        switchBTN = findViewById(R.id.switchBTN);

        loadAnimations();
        switchBTN.setAnimation(aslideDownToPosition);

        switchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAnimations();

                if (signInCL.getVisibility() == View.VISIBLE) {
                    nameSUET.setAnimation(aslideDownToPosition);
                    emailSUET.setAnimation(aslideDownToPosition);
                    passwordSUET.setAnimation(aslideDownToPosition);
                    signUpSUBTN.setAnimation(aslideDownToPosition);

                    signInCL.setVisibility(View.GONE);
                    signUpCL.setVisibility(View.VISIBLE);

                    loadAdminDetails();
                } else {
                    emailSIET.setAnimation(aslideDownToPosition);
                    passwordSIET.setAnimation(aslideDownToPosition);
                    signInSIBTN.setAnimation(aslideDownToPosition);

                    signUpCL.setVisibility(View.GONE);
                    signInCL.setVisibility(View.VISIBLE);
                }
            }
        });

        signInSIBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailSIET.getText().toString();
                final String passwd = passwordSIET.getText().toString();

                final boolean isEmailEmpty = AppValidator.empty(email);
                boolean isEmailNotValid = !AppValidator.validEmail(email);
                boolean isPasswordEmpty = AppValidator.empty(passwd);
                boolean isPasswordNotValid = !AppValidator.validPassword(passwd);

                if (isEmailEmpty) {
                    Toast.makeText(instance, "Email field is empty!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isEmailNotValid) {
                    Toast.makeText(instance, "Email is not valid!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isPasswordEmpty) {
                    Toast.makeText(instance, "Password field is empty!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isPasswordNotValid) {
                    Toast.makeText(instance, "Password must be at least 6 charecters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final AlertDialog spotsDialog = new SpotsDialog(instance, "Please wait...");
                spotsDialog.show();

                Vars.appFirebase.signInUser(email, passwd, new AppFirebase.FirebaseCallback() {
                    @Override
                    public void ProcessCallback(boolean isTaskCompleted) {
                        if (isTaskCompleted) {
                            Vars.appFirebase.isUserAdmin(new AppFirebase.FirebaseCallback() {
                                @Override
                                public void ProcessCallback(boolean isTaskCompleted) {
                                    spotsDialog.dismiss();

                                    Vars.isUserAdmin = isTaskCompleted;
                                    Vars.localDB.saveBooleanValue(Vars.PREFS_IS_USER_ADMIN, isTaskCompleted);

                                    @SuppressLint("HardwareIds")
                                    String deviceId = Secure.getString(instance.getContentResolver(), Secure.ANDROID_ID);
                                    Vars.localDB.saveStringValue(Vars.PREFS_DEVICE_UNIQUE_ID, deviceId);
                                    startMainActivity();
                                }

                                @Override
                                public void ExceptionCallback(String exception) {

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
                            Toast.makeText(instance, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        signUpSUBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = nameSUET.getText().toString();
                final String email = emailSUET.getText().toString();
                final String passwd = passwordSUET.getText().toString();

                boolean isNameEmpty = AppValidator.empty(name);
                boolean isEmailEmpty = AppValidator.empty(email);
                boolean isEmailNotValid = !AppValidator.validEmail(email);
                boolean isPasswordEmpty = AppValidator.empty(passwd);
                boolean isPasswordNotValid = !AppValidator.validPassword(passwd);

                if (isNameEmpty) {
                    Toast.makeText(instance, "Name is empty!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isEmailEmpty) {
                    Toast.makeText(instance, "Email field is empty!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isEmailNotValid) {
                    Toast.makeText(instance, "Email is not valid!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isPasswordEmpty) {
                    Toast.makeText(instance, "Password field is empty!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isPasswordNotValid) {
                    Toast.makeText(instance, "Password must be at least 6 charecters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!admins.isEmpty()) {
                    for (Map.Entry<String, String> admin : admins.entrySet()) {
                        if (admin.getValue().equals(name)) {
                            Toast.makeText(instance, "Name is already exists!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                // Sign up main
                View dialogView = LayoutInflater.from(AuthActivity.instance).inflate(R.layout.dialog_signup_security_code, null);
                Vars.appDialog.create(AuthActivity.instance, dialogView);
                Vars.appDialog.show();

                final EditText securityCodeET = dialogView.findViewById(R.id.securityCodeET);
                Button proceedBTN = dialogView.findViewById(R.id.proceedBTN);
                Button cancelBTN = dialogView.findViewById(R.id.cancelBTN);

                cancelBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Vars.appDialog.dismiss();
                    }
                });

                proceedBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Vars.appDialog.dismiss();
                        String securityCode = securityCodeET.getText().toString();

                        if (securityCode.equals("")) {
                            Toast.makeText(AuthActivity.instance, "Security code needed!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!securityCode.equals(Vars.SECURITY_CODE)) {
                            Toast.makeText(AuthActivity.instance, "Security code not maching!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final AlertDialog spotsDialog = new SpotsDialog(instance, "Please wait...");
                        spotsDialog.show();

                        Vars.appFirebase.signUpUser(email, passwd, new AppFirebase.FirebaseCallback() {
                            @Override
                            public void ProcessCallback(boolean isTaskCompleted) {
                                spotsDialog.dismiss();

                                if (isTaskCompleted) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put(AFModel.image_path, "");
                                    map.put(AFModel.id, Vars.appFirebase.getCurrentUser().getUid());
                                    map.put(AFModel.username, name);
                                    map.put(AFModel.phone_no, "");
                                    map.put(AFModel.email, email);
                                    map.put(AFModel.created_at, System.currentTimeMillis());
                                    map.put(AFModel.modified_at, System.currentTimeMillis());

                                    final DatabaseReference reference =
                                            Vars.appFirebase.getDbAdminsReference().child(Vars.appFirebase.getCurrentUser().getUid());

                                    Vars.appFirebase.insert(reference, map, new AppFirebase.FirebaseCallback() {
                                        @Override
                                        public void ProcessCallback(boolean isTaskCompleted) {
                                            if (isTaskCompleted) {
                                                Vars.appFirebase.isUserAdmin(new AppFirebase.FirebaseCallback() {
                                                    @Override
                                                    public void ProcessCallback(boolean isTaskCompleted) {
                                                        @SuppressLint("HardwareIds")
                                                        String deviceId = Secure.getString(instance.getContentResolver(), Secure.ANDROID_ID);
                                                        Vars.localDB.saveStringValue(Vars.PREFS_DEVICE_UNIQUE_ID, deviceId);

                                                        Vars.isUserAdmin = isTaskCompleted;
                                                        Vars.localDB.saveBooleanValue(Vars.PREFS_IS_USER_ADMIN, isTaskCompleted);
                                                        startMainActivity();
                                                    }

                                                    @Override
                                                    public void ExceptionCallback(String exception) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void ExceptionCallback(String exception) {
                                            if (!exception.equals("")) {
                                                Log.d("APPTAG", "ExceptionCallback: " + exception);
                                                Toast.makeText(instance, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            }

                            @Override
                            public void ExceptionCallback(String exception) {
                                if (!exception.equals("")) {
                                    Log.d("APPTAG", "ExceptionCallback: " + exception);
                                    Toast.makeText(instance, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Vars.appFirebase.getCurrentUser() != null) {
            Vars.isUserAdmin = Vars.localDB.retriveBooleanValue(Vars.PREFS_IS_USER_ADMIN);
            startMainActivity();
        }
    }
}
