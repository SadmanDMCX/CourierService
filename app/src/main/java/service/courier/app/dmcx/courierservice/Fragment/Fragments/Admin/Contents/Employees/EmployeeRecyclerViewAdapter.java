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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class EmployeeRecyclerViewAdapter extends RecyclerView.Adapter<EmployeeRecyclerViewAdapter.ClientRecyclerViewHolder> {

    private List<Employee> employees;

    public EmployeeRecyclerViewAdapter(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public EmployeeRecyclerViewAdapter.ClientRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_admin_employee, parent, false);
        return new ClientRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmployeeRecyclerViewAdapter.ClientRecyclerViewHolder holder, final int position) {
        holder.employeeNameTV.setText(employees.get(position).getName());
        holder.employeeStatusTV.setText(employees.get(position).getStatus());
        if (employees.get(position).getStatus().equals(AFModel.val_status_online)) {
            holder.employeeIV.setImageResource(R.drawable.employee_black);
        } else {
            holder.employeeIV.setImageResource(R.drawable.employee_gray);
        }

        final Employee currentEmployee = employees.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_admin_employees_assign_work, null);
                Vars.appDialog.create(MainActivity.instance, dialogView).show();

                final EditText workTitleET = dialogView.findViewById(R.id.workTitleET);
                final EditText workDescET = dialogView.findViewById(R.id.workDescET);
                final Button assignBTN = dialogView.findViewById(R.id.assignBTN);
                final Button cancelBTN = dialogView.findViewById(R.id.cancelBTN);

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

                        Map<String, Object> map = new HashMap<>();
                        map.put(AFModel.work_id, pushId);
                        map.put(AFModel.work_title, title);
                        map.put(AFModel.work_description, desc);
                        map.put(AFModel.work_status, AFModel.val_work_status_request);
                        map.put(AFModel.created_at, System.currentTimeMillis());
                        map.put(AFModel.modified_at, System.currentTimeMillis());

                        assert pushId != null;
                        reference.child(pushId).setValue(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    spotsDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.instance, "Work assigned!", Toast.LENGTH_SHORT).show();
                                    } else {
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
