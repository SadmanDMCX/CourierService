package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.Works;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminEmployeeWorks;
import service.courier.app.dmcx.courierservice.Fragment.Manager.AppFragmentManager;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminWorksRecyclerAdapter extends RecyclerView.Adapter<AdminWorksRecyclerAdapter.AdminWorksRecyclerViewHolder> {

    private List<Employee> employees;

    public AdminWorksRecyclerAdapter(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public AdminWorksRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_admin_work, parent, false);
        return new AdminWorksRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdminWorksRecyclerViewHolder holder, int position) {

        final int itemPosition = position;

        final String name = employees.get(position).getName();
        final String id = employees.get(position).getId();

        holder.employeeNameTV.setText(name);

        final AlertDialog spotDialog = new SpotsDialog(MainActivity.instance, "Loading data...");
        spotDialog.show();
        DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.works).child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.noOfWorkTV.setText("Tasks: " + dataSnapshot.getChildrenCount());
                spotDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(Vars.Transporter.ARGS_ADMIN_EMPLOYEE_ID, id);

                AppFragmentManager.replace(MainActivity.instance, AppFragmentManager.fragmentContainer, new AdminEmployeeWorks(), AdminEmployeeWorks.TAG, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class AdminWorksRecyclerViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView employeeCIV;
        public TextView employeeNameTV;
        public TextView noOfWorkTV;
        public ImageView workIV;

        public AdminWorksRecyclerViewHolder(View itemView) {
            super(itemView);

            employeeCIV = itemView.findViewById(R.id.employeeCIV);
            employeeNameTV = itemView.findViewById(R.id.employeeNameTV);
            noOfWorkTV = itemView.findViewById(R.id.noOfWorkTV);
            workIV = itemView.findViewById(R.id.workIV);
        }
    }
}
