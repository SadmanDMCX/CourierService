package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.Employees;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.Models.Status;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Utility.AppUtils;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminEmployeeRecyclerViewAdapter extends RecyclerView.Adapter<AdminEmployeeRecyclerViewAdapter.ClientRecyclerViewHolder> {

    private List<Employee> employees;

    public AdminEmployeeRecyclerViewAdapter(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public AdminEmployeeRecyclerViewAdapter.ClientRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_admin_employee, parent, false);
        return new ClientRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdminEmployeeRecyclerViewAdapter.ClientRecyclerViewHolder holder, int position) {

        final int itemPosition = position;

        holder.employeeNameTV.setText(employees.get(position).getName());

        Vars.appFirebase.getDbStatusReference().child(employees.get(position).getId())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isOffline = true;
                holder.employeeStatusTV.setText("");

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Status status = snapshot.getValue(Status.class);
                            if (status != null) {
                                if (status.getState().equals(AFModel.val_state_online)) {
                                    isOffline = false;
                                    break;
                                }
                            }
                        }

                        if (!isOffline) {
                            holder.employeeStatusTV.append(AppUtils.FirstLetterCapital(AFModel.val_state_online));
                            holder.employeeIV.setImageResource(R.drawable.employee_black);
                        } else {
                            holder.employeeStatusTV.append(AppUtils.FirstLetterCapital(AFModel.val_state_offline));
                            holder.employeeIV.setImageResource(R.drawable.employee_gray);
                        }
                    }
                } else {
                    holder.employeeStatusTV.setText("Not in service.");
                    holder.employeeIV.setImageResource(R.drawable.employee_gray);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_admin_employees_assign_work, null);
                Vars.appDialog.create(MainActivity.instance, dialogView).show();

                final EditText workTitleET = dialogView.findViewById(R.id.workTitleET);
                final EditText workDescET = dialogView.findViewById(R.id.workDescET);
                final Button assignBTN = dialogView.findViewById(R.id.assignBTN);
                final Button cancelBTN = dialogView.findViewById(R.id.cancelBTN);

                final Employee currentEmployee = employees.get(itemPosition);

                PlaceAutocompleteFragment place = (PlaceAutocompleteFragment) MainActivity.instance.getFragmentManager().findFragmentById(R.id.setDestinationPACF);
                final EditText placeACSI = (place.getView().findViewById(R.id.place_autocomplete_search_input));
                final Place[] workPlace = new Place[1];
                placeACSI.setTextSize(14.0f);
                place.setOnPlaceSelectedListener(new PlaceSelectionListener() {

                    @Override
                    public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                        workPlace[0] = place;
                    }

                    @Override
                    public void onError(com.google.android.gms.common.api.Status status) {
                        Toast.makeText(MainActivity.instance,status.toString(),Toast.LENGTH_SHORT).show();
                    }
                });

                AutocompleteFilter filter = new AutocompleteFilter.Builder()
                        .setCountry("BD")
                        .build();
                place.setFilter(filter);



                cancelBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Vars.appDialog.dismiss();
                    }
                });

                assignBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String title = workTitleET.getText().toString();
                        final String desc = workDescET.getText().toString();

                        if (title.equals("") || desc.equals("")) {
                            Toast.makeText(MainActivity.instance, "Title & Description is needed!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Vars.appDialog.dismiss();

                        final AlertDialog spotsDialog = new SpotsDialog(MainActivity.instance, "Please wait...");
                        spotsDialog.show();

                        final DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users)
                                .child(AFModel.works).child(currentEmployee.getId());
                        String pushId = reference.push().getKey();

                        Map<String, Object> workMap = new HashMap<>();
                        workMap.put(AFModel.work_id, pushId);
                        workMap.put(AFModel.work_title, title);
                        workMap.put(AFModel.work_description, desc);
                        workMap.put(AFModel.latitude, workPlace[0] != null ? workPlace[0].getLatLng().latitude : 360);
                        workMap.put(AFModel.longitude, workPlace[0] != null ? workPlace[0].getLatLng().longitude : 360);
                        workMap.put(AFModel.work_status, AFModel.val_work_status_request);
                        workMap.put(AFModel.created_at, System.currentTimeMillis());
                        workMap.put(AFModel.modified_at, System.currentTimeMillis());

                        assert pushId != null;
                        reference.child(pushId).setValue(workMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Map<String, Object> notificationMap = new HashMap<>();
                                        notificationMap.put(AFModel.from, Vars.appFirebase.getCurrentUserId());
                                        notificationMap.put(AFModel.to, currentEmployee.getId());
                                        notificationMap.put(AFModel.message, "A new work is assigned.");

                                        String deviceId = Vars.localDB.retriveStringValue(Vars.PREFS_DEVICE_UNIQUE_ID, "");
                                        Vars.appFirebase.getDbNotificationsReference().child(Vars.appFirebase.getCurrentUserId()).child(deviceId).setValue(notificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.instance, "Work assigned and notification sent!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(MainActivity.instance, "Some issue occured!", Toast.LENGTH_SHORT).show();
                                                }
                                                spotsDialog.dismiss();
                                            }
                                        });
                                    } else {
                                        spotsDialog.dismiss();
                                        Toast.makeText(MainActivity.instance, "Work can't assign! Contact with the developer.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class ClientRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView employeeNameTV;
        public TextView employeeStatusTV;
        public CircleImageView employeeImageCIV;
        public ImageView employeeIV;

        public ClientRecyclerViewHolder(View itemView) {
            super(itemView);

            employeeNameTV = itemView.findViewById(R.id.employeeNameTV);
            employeeStatusTV = itemView.findViewById(R.id.employeeStatusTV);
            employeeImageCIV = itemView.findViewById(R.id.employeeImageCIV);
            employeeIV = itemView.findViewById(R.id.employeeIV);
        }
    }

}
