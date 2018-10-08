package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.Employees;

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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Fragment.Manager.AppFragmentManager;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.Models.Status;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Utility.AppUtils;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminEmployeeRecyclerViewAdapter extends RecyclerView.Adapter<AdminEmployeeRecyclerViewAdapter.ClientRecyclerViewHolder> {

    private List<Employee> employees;
    private PlaceAutocompleteFragment placeAutoCompleteFragment = null;

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

        String image_path = employees.get(position).getImage_path();
        if (!image_path.equals("")) {
            Picasso.with(MainActivity.instance)
                    .load(image_path)
                    .placeholder(R.drawable.default_avater)
                    .into(holder.employeeImageCIV);
        }

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
                View workAssignDialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_admin_employees_assign_work, null);
                Vars.appDialog.create(MainActivity.instance, workAssignDialogView);
                Vars.appDialog.show();

                final EditText workTitleET = workAssignDialogView.findViewById(R.id.workTitleET);
                final EditText workDescET = workAssignDialogView.findViewById(R.id.workDescET);
                final EditText workTimeET = workAssignDialogView.findViewById(R.id.workTimeET);
                final EditText workPickupET = workAssignDialogView.findViewById(R.id.workPickupET);
                final EditText workDropET = workAssignDialogView.findViewById(R.id.workDropET);
                final EditText workFareET = workAssignDialogView.findViewById(R.id.workFareET);
                final Button assignBTN = workAssignDialogView.findViewById(R.id.assignBTN);
                final Button cancelBTN = workAssignDialogView.findViewById(R.id.cancelBTN);

                final Employee currentEmployee = employees.get(itemPosition);

                placeAutoCompleteFragment = (PlaceAutocompleteFragment) MainActivity.instance.getFragmentManager().findFragmentById(R.id.setDestinationPACF);
                final EditText placeACSI = (placeAutoCompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input));
                final Place[] workPlace = new Place[1];
                placeACSI.setTextSize(14.0f);
                placeAutoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
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
                placeAutoCompleteFragment.setFilter(filter);

                workTitleET.setText("");
                workDescET.setText("");
                placeAutoCompleteFragment.setText("");

                cancelBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.instance.getFragmentManager().beginTransaction().remove(placeAutoCompleteFragment).commit();
                        Vars.appDialog.dismiss();
                    }
                });

                assignBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String title = workTitleET.getText().toString();
                        final String desc = workDescET.getText().toString();
                        final String time = workTimeET.getText().toString();
                        final String pick = workPickupET.getText().toString();
                        final String drop = workDropET.getText().toString();
                        final String fare = workFareET.getText().toString();

                        if (title.equals("") || desc.equals("") || drop.equals("") || fare.equals("") || pick.equals("") || time.equals("")) {
                            Toast.makeText(MainActivity.instance, "All the contents are necessary!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        MainActivity.instance.getFragmentManager().beginTransaction().remove(placeAutoCompleteFragment).commit();
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
                        workMap.put(AFModel.work_time, time);
                        workMap.put(AFModel.work_pickup, pick);
                        workMap.put(AFModel.work_drop, drop);
                        workMap.put(AFModel.work_fare, fare);

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

                                        DatabaseReference notificationContentReference = Vars.appFirebase.getDbNotificationsReference().child(AFModel.contents).child(Vars.appFirebase.getCurrentUserId());

                                        String pushId = notificationContentReference.push().getKey();
                                        notificationContentReference.child(pushId).setValue(notificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        public CircleImageView employeeImageCIV;
        public TextView employeeNameTV;
        public TextView employeeStatusTV;
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
