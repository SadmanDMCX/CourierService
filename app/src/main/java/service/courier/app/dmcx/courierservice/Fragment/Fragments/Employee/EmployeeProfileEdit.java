package service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tooltip.Tooltip;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Utility.AppValidator;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class EmployeeProfileEdit extends Fragment {

    public static final String TAG = "EMPLOYEE-PROFILE-EDIT";
    private final int READ_WRITE_CAMERA_REQUEST_CODE = 991;

    private TextView authenticationTV;
    private ProgressBar imageLoadPB;
    private CircleImageView profileImageCIV;
    private EditText profileNameET;
    private EditText profilePhoneET;
    private EditText profileOldEmailET;
    private EditText profileOldPasswordET;
    private EditText profileNewEmailET;
    private EditText profileNewPasswordET;
    private FloatingActionButton changeImageFAB;
    private Button saveBTN;

    private String profileName;
    private String profilePhone;
    private Uri profileImageUri = null;

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
                    final String image_path = employee.getImage_path();
                    final String email = Vars.appFirebase.getCurrentUser().getEmail();

                    profileName = name;
                    profilePhone = phone;

                    profileNameET.setText(name);
                    profilePhoneET.setText(phone);
                    profileOldEmailET.setText(email);

                    if (!image_path.equals("")) {
                        Picasso.with(MainActivity.instance)
                                .load(image_path)
                                .placeholder(R.drawable.default_avater)
                                .into(profileImageCIV);
                    }
                }

                spotDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA }, READ_WRITE_CAMERA_REQUEST_CODE);
            return false;
        }

        return true;
    }

    private void cropImager() {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .setMaxCropResultSize(1000, 1000)
                .start(MainActivity.instance, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee_profile_edit, container, false);

        authenticationTV = view.findViewById(R.id.authenticationTV);
        imageLoadPB = view.findViewById(R.id.imageLoadPB);
        profileImageCIV = view.findViewById(R.id.profileImageCIV);
        profileNameET = view.findViewById(R.id.profileNameET);
        profilePhoneET = view.findViewById(R.id.profilePhoneET);
        profileOldEmailET = view.findViewById(R.id.profileOldEmailET);
        profileOldPasswordET = view.findViewById(R.id.profileOldPasswordET);
        profileNewEmailET = view.findViewById(R.id.profileNewEmailET);
        profileNewPasswordET = view.findViewById(R.id.profileNewPasswordET);
        profileNewPasswordET = view.findViewById(R.id.profileNewPasswordET);
        changeImageFAB = view.findViewById(R.id.changeImageFAB);
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

        changeImageFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    cropImager();
                }
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.instance, "Please wait...", Toast.LENGTH_LONG).show();

                if (profileImageUri != null) {
                    imageLoadPB.setVisibility(View.VISIBLE);
                    DatabaseReference reference = Vars.appFirebase.getDbEmployeesReference().child(Vars.appFirebase.getCurrentUserId());
                    Vars.appFirebase.addUserImageToStorage(reference, profileImageUri, new AppFirebase.FirebaseCallback() {
                        @Override
                        public void ProcessCallback(boolean isTaskCompleted) {
                            if (isTaskCompleted) {
                                imageLoadPB.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.instance, "Image Upload Success!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.instance, "Some error found!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void ExceptionCallback(String exception) {
                            if (!exception.equals("")) {
                                Toast.makeText(MainActivity.instance, exception, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    profileImageUri = null;
                }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (result != null) {
                profileImageUri = result.getUri();
                profileImageCIV.setImageURI(profileImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(MainActivity.instance, "Error! " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
            builder.setTitle("Permission")
                    .setCancelable(false)
                    .setMessage("Need permission to get the image from the storage. We don't access any of your private data. Be safe stay safe.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkPermission();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            cropImager();
        }
    }
}
