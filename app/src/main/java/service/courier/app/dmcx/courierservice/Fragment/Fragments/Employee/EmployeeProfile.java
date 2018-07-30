package service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.Fragment.Manager.AppFragmentManager;
import service.courier.app.dmcx.courierservice.Models.Admin;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.Models.Work;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class EmployeeProfile extends Fragment {

    public static EmployeeProfile instance;

    public static final String TAG = "EMPLOYEE-PROFILE";

    private ImageButton profileEditIB;
    private CircleImageView profileImageCIV;
    private TextView profileNameTV;
    private TextView profileEmailTV;
    private TextView profilePhoneTV;
    private CircleImageView adminCIV;
    private TextView adminNameTV;
    private TextView adminEmailTV;
    private TextView adminPhoneNoTV;
    private TextView pendingWorkTV;
    private TextView accecptedWorkTV;

    private int totalPendingWorks = 0;
    private int totalAcceptedWorks = 0;

    private void loadProfileInfo() {
        final AlertDialog spotDialog = new SpotsDialog(MainActivity.instance, "Loading info...");
        spotDialog.show();

        Vars.appFirebase.getDbEmployeesReference().child(Vars.appFirebase.getCurrentUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final DataSnapshot snapshot = dataSnapshot;
                if (dataSnapshot.exists()) {
                    Vars.appFirebase.checkEmployeeData(snapshot, new AppFirebase.FirebaseCallback() {
                        @Override
                        public void ProcessCallback(boolean isTaskCompleted) {
                            Employee employee = snapshot.getValue(Employee.class);
                            if (employee != null) {
                                final String name = employee.getName();
                                final String image_path = employee.getImage_path();
                                final String email = employee.getEmail();
                                final String phone = employee.getPhone_no();
                                final String employeeId = employee.getId();
                                final String adminId = employee.getAdmin_id();

                                profileNameTV.setText(name);
                                profileEmailTV.setText(email);
                                profilePhoneTV.setText(phone);

                                if (!image_path.equals("")) {
                                    Picasso.with(MainActivity.instance)
                                            .load(image_path)
                                            .placeholder(R.drawable.default_avater)
                                            .into(profileImageCIV);
                                }

                                if(phone.equals("")) {
                                    profilePhoneTV.setText("Not Given...");
                                }

                                Vars.appFirebase.getDbAdminsReference().child(adminId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            final Admin admin = dataSnapshot.getValue(Admin.class);
                                            if (admin != null) {
                                                final String adminName = admin.getName();
                                                final String adminImagePath = admin.getImage_path();
                                                final String adminEmail = admin.getEmail() != null ? admin.getEmail() : "Developer Option";
                                                final String adminPhone = admin.getPhone_no();

                                                adminNameTV.setText(adminName);
                                                adminEmailTV.setText(adminEmail);
                                                adminPhoneNoTV.setText(adminPhone);
                                                if (!image_path.equals("")) {
                                                    Picasso.with(MainActivity.instance)
                                                            .load(adminImagePath)
                                                            .networkPolicy(NetworkPolicy.OFFLINE)
                                                            .placeholder(R.drawable.default_avater)
                                                            .into(adminCIV);
                                                }

                                                if(adminPhone.equals("")) {
                                                    adminPhoneNoTV.setText("Not Given...");
                                                }

                                                Vars.appFirebase.getDbWorksReference().child(employeeId).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        if (dataSnapshot.exists()) {
                                                            for (DataSnapshot workSnapshot : dataSnapshot.getChildren()) {
                                                                Work work = workSnapshot.getValue(Work.class);
                                                                if (work != null) {
                                                                    if (work.getWork_status().equals(AFModel.val_work_status_request)) {
                                                                        totalPendingWorks++;
                                                                    } else if (work.getWork_status().equals(AFModel.val_work_status_accept)) {
                                                                        totalAcceptedWorks++;
                                                                    }
                                                                }
                                                            }

                                                            accecptedWorkTV.setText(String.valueOf(totalAcceptedWorks));
                                                            pendingWorkTV.setText(String.valueOf(totalPendingWorks));

                                                            spotDialog.dismiss();
                                                        } else {
                                                            spotDialog.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            } else {
                                                spotDialog.dismiss();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
                } else {
                    spotDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee_profile, container, false);

        instance = this;

        profileEditIB = view.findViewById(R.id.profileEditIB);
        profileImageCIV = view.findViewById(R.id.profileImageCIV);
        profileNameTV = view.findViewById(R.id.profileNameTV);
        profileEmailTV = view.findViewById(R.id.profileEmailTV);
        profilePhoneTV = view.findViewById(R.id.profilePhoneTV);
        adminCIV = view.findViewById(R.id.adminCIV);
        adminNameTV = view.findViewById(R.id.adminNameTV);
        adminEmailTV = view.findViewById(R.id.adminEmailTV);
        adminPhoneNoTV = view.findViewById(R.id.adminPhoneNoTV);
        pendingWorkTV = view.findViewById(R.id.pendingWorkTV);
        accecptedWorkTV = view.findViewById(R.id.accecptedWorkTV);

        loadProfileInfo();

        profileEditIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppFragmentManager.replace(MainActivity.instance, AppFragmentManager.fragmentContainer, new EmployeeProfileEdit(), EmployeeProfileEdit.TAG);
            }
        });

        return view;
    }
}
