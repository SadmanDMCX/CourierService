package service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tooltip.Tooltip;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Models.Admin;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Utility.AppValidator;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class EmployeeProfileEdit extends Fragment {

    public static final String TAG = "EMPLOYEE-PROFILE-EDIT";

    private TextView authenticationTV;
    private EditText profileNameET;
    private EditText profilePhoneET;
    private EditText profileOldEmailET;
    private EditText profileOldPasswordET;
    private EditText profileNewEmailET;
    private EditText profileNewPasswordET;
    private Button saveBTN;

    private String profileName;
    private String profilePhone;

    private void loadProfileData() {
        final AlertDialog spotDialog = new SpotsDialog(MainActivity.instance, "Loading profile...");
        spotDialog.show();

        Vars.appFirebase.getDbEmployeesReference().child(Vars.appFirebase.getCurrentUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Employee employee = dataSnapshot.getValue(Employee.class);
                if (employee != null) {
                    final String name = employee.getName();
                    final String phone = employee.getPhone_no();
                    final String email = Vars.appFirebase.getCurrentUser().getEmail();


                    profileName = name;
                    profilePhone = phone;

                    profileNameET.setText(name);
                    profilePhoneET.setText(phone);
                    profileOldEmailET.setText(email);
                }

                spotDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee_profile_edit, container, false);

        authenticationTV = view.findViewById(R.id.authenticationTV);
        profileNameET = view.findViewById(R.id.profileNameET);
        profilePhoneET = view.findViewById(R.id.profilePhoneET);
        profileOldEmailET = view.findViewById(R.id.profileOldEmailET);
        profileOldPasswordET = view.findViewById(R.id.profileOldPasswordET);
        profileNewEmailET = view.findViewById(R.id.profileNewEmailET);
        profileNewPasswordET = view.findViewById(R.id.profileNewPasswordET);
        profileNewPasswordET = view.findViewById(R.id.profileNewPasswordET);
        saveBTN = view.findViewById(R.id.saveBTN);

        loadProfileData();

        final Tooltip.Builder tooltipBuilder = new Tooltip.Builder(authenticationTV).setText("For changing email and password you need to fill the new email and password fields and click save in the authenticate section.");;
        authenticationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tooltip tooltip = tooltipBuilder.build();
                if (!tooltip.isShowing()) {
                    tooltip.show();
                } else {
                    tooltip.dismiss();
                }
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = profileNameET.getText().toString();
                String phone = profilePhoneET.getText().toString();

                boolean isNameEmpty = AppValidator.empty(name);
                boolean isPhoneEmpty = AppValidator.empty(phone);

                Map<String, Object> profileMap = new HashMap<>();
                if (!isNameEmpty && !isPhoneEmpty) {
                    profileMap.put(AFModel.username, name);
                    profileMap.put(AFModel.phone_no, phone);
                } else if (!isNameEmpty) {
                    profileMap.put(AFModel.username, name);
                } else if (!isPhoneEmpty) {
                    profileMap.put(AFModel.phone_no, phone);
                } else {
                    Toast.makeText(MainActivity.instance, "Name or Phone one should be filled!", Toast.LENGTH_SHORT).show();
                }

                if (!name.equals(profileName) || !phone.equals(profilePhone)) {
                    Vars.appFirebase.getDbEmployeesReference().child(Vars.appFirebase.getCurrentUserId()).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.instance, "Updated!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.instance, "Failed! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // Auth
                String oldEmail = profileOldEmailET.getText().toString();
                String oldPassword = profileOldPasswordET.getText().toString();
                final String newEmail = profileNewEmailET.getText().toString();
                final String newPassword = profileNewPasswordET.getText().toString();

                boolean isOldEmailEmpty = AppValidator.empty(oldEmail);
                boolean isOldPasswordEmpty = AppValidator.empty(oldPassword);
                boolean isNewEmailEmpty = AppValidator.empty(newEmail);
                boolean isNewPasswordEmpty = AppValidator.empty(newPassword);

                if (!isOldEmailEmpty && !isOldPasswordEmpty && !isNewEmailEmpty && !isNewPasswordEmpty) {
                    boolean isOldEmailValid = AppValidator.validEmail(oldEmail);
                    boolean isOldPasswordValid = AppValidator.validPassword(oldPassword);
                    boolean isNewEmailValid = AppValidator.validEmail(newEmail);
                    boolean isNewPasswordValid = AppValidator.validPassword(newPassword);

                    if (isOldEmailValid && isOldPasswordValid && isNewEmailValid && isNewPasswordValid) {
                        AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, oldPassword);
                        Vars.appFirebase.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Vars.appFirebase.getCurrentUser().updateEmail(newEmail);
                                    Vars.appFirebase.getCurrentUser().updatePassword(newPassword);

                                    Map<String, Object> authMap = new HashMap<>();
                                    authMap.put(AFModel.email, newEmail);
                                    Vars.appFirebase.getDbEmployeesReference().child(Vars.appFirebase.getCurrentUserId()).updateChildren(authMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.instance, "Email and password are changed.", Toast.LENGTH_SHORT).show();
                                                MainActivity.MainActivityClass.signOut();
                                            } else {
                                                Toast.makeText(MainActivity.instance, "Failed! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(MainActivity.instance, "Failed! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.instance, "Check your email and password. Both Email must be valid and Passoword must have 6 charecters.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return view;
    }
}
