package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin;

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

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.Fragment.Manager.AppFragmentManager;
import service.courier.app.dmcx.courierservice.Models.Admin;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminProfile extends Fragment {

    public static AdminProfile instance;

    public static final String TAG = "ADMIN-PROFILE";

    private ImageButton profileEditIB;
    private CircleImageView profileImageCIV;
    private TextView profileNameTV;
    private TextView profileEmailTV;
    private TextView profilePhoneTV;
    private TextView totalEmployeesTV;

    private int totalEmployees;

    private void loadProfileData() {
        final AlertDialog spotDialog = new SpotsDialog(MainActivity.instance, "Loading profile...");
        spotDialog.show();

        Vars.appFirebase.getDbAdminsReference().child(Vars.appFirebase.getCurrentUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Admin admin = dataSnapshot.getValue(Admin.class);
                if (admin != null) {
                    final String name = admin.getName();
                    final String phone = admin.getPhone_no();
                    final String email = Vars.appFirebase.getCurrentUser().getEmail();

                    profileNameTV.setText(name);
                    profileEmailTV.setText(email);
                    profilePhoneTV.setText(phone);

                    if (phone.equals("")) {
                        profilePhoneTV.setText("Not Given");
                    }

                    Vars.appFirebase.getDbEmployeesReference().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Vars.appFirebase.checkEmployeeData(snapshot, new AppFirebase.FirebaseCallback() {
                                    @Override
                                    public void ProcessCallback(boolean isTaskCompleted) {
                                        if (isTaskCompleted) {
                                            Employee employee = snapshot.getValue(Employee.class);
                                            if (employee != null) {
                                                if (employee.getAdmin_id().equals(Vars.appFirebase.getCurrentUserId())) {
                                                    totalEmployees++;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void ExceptionCallback(String exception) {

                                    }
                                });
                            }

                            spotDialog.dismiss();
                            if (totalEmployees == 1)
                                totalEmployeesTV.setText("Employee: " + totalEmployees);
                            else
                                totalEmployeesTV.setText("Employees: " + totalEmployees);
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        instance = this;

        profileEditIB = view.findViewById(R.id.profileEditIB);
        profileImageCIV = view.findViewById(R.id.profileImageCIV);
        profileNameTV = view.findViewById(R.id.profileNameTV);
        profileEmailTV = view.findViewById(R.id.profileEmailTV);
        profilePhoneTV = view.findViewById(R.id.profilePhoneTV);
        totalEmployeesTV = view.findViewById(R.id.totalEmployeesTV);

        loadProfileData();

        profileEditIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppFragmentManager.replace(MainActivity.instance, AppFragmentManager.fragmentContainer, new AdminProfileEdit(), AdminProfileEdit.TAG);
            }
        });

        return view;
    }
}
