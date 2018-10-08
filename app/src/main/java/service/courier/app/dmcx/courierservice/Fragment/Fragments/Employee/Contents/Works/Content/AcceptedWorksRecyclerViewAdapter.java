package service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee.Contents.Works.Content;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee.Contents.Works.BottomNavigationView.AcceptedWorksFragment;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee.Contents.Works.BottomNavigationView.PendingWorksFragment;
import service.courier.app.dmcx.courierservice.Models.Work;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Utility.AppAnimation;
import service.courier.app.dmcx.courierservice.Utility.AppUtils;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AcceptedWorksRecyclerViewAdapter extends RecyclerView.Adapter<AcceptedWorksRecyclerViewAdapter.WorkRecyclerViewHolder> {

    private List<Work> works;

    public AcceptedWorksRecyclerViewAdapter(List<Work> works) {
        this.works = works;
    }

    @Override
    public WorkRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MainActivity.instance).inflate(R.layout.layout_single_employee_works_accepted_work, parent, false);
        return new WorkRecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final WorkRecyclerViewHolder holder, int position) {

        final String title = works.get(position).getWork_title();
        final String status = AppUtils.FirstLetterCapital(works.get(position).getWork_status());
        final String desc = works.get(position).getWork_description();
        final String time = works.get(position).getWork_time() == null ? "Not given" : works.get(position).getWork_time();
        final String fare = works.get(position).getWork_fare() == null ? "Not given" : works.get(position).getWork_fare();
        final String pickup = works.get(position).getWork_pickup() == null ? "Not given" : works.get(position).getWork_pickup();
        final String drop = works.get(position).getWork_drop() == null ? "Not given" : works.get(position).getWork_drop();

        holder.workTitleTV.setText(title);
        holder.workDescTV.setText(desc);

        final DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.works)
                .child(Vars.appFirebase.getCurrentUser().getUid()).child(works.get(position).getWork_id());

        holder.doneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put(AFModel.work_status, AFModel.val_work_status_done);
                reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.instance, "Work done!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(Vars.APPTAG, "Error Done Work: " + task.getException().getMessage());
                        }
                    }
                });
            }
        });

        holder.notDoneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put(AFModel.work_status, AFModel.val_work_status_not_done);
                reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.instance, "Work not done!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(Vars.APPTAG, "Error Not Done Work: " + task.getException().getMessage());
                        }
                    }
                });
            }
        });

        holder.assignedIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.instance, "Work Accepted!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_admin_work_employee_work_detail, null);
                Vars.appDialog.create(MainActivity.instance, dialogView).transparent();
                Vars.appDialog.show();

                final ImageButton closeDialogIB = dialogView.findViewById(R.id.closeDialogIB);
                TextView dWorkTitleTV = dialogView.findViewById(R.id.dWorkTitleTV);
                TextView dWorkStatus = dialogView.findViewById(R.id.dWorkStatus);
                TextView dWorkDescTV = dialogView.findViewById(R.id.dWorkDescTV);
                TextView dWorkTimeTV = dialogView.findViewById(R.id.dWorkTimeTV);
                TextView dWorkFareTV = dialogView.findViewById(R.id.dWorkFareTV);
                TextView dWorkPickUpTV = dialogView.findViewById(R.id.dWorkPickUpTV);
                TextView dWorkDropTV = dialogView.findViewById(R.id.dWorkDropTV);

                dWorkTitleTV.setText(new StringBuilder("Job: ").append(title));
                dWorkStatus.setText(new StringBuilder("Status: " ).append(status));
                dWorkDescTV.setText(desc);
                dWorkTimeTV.setText(new StringBuilder("Time: ").append(time));
                dWorkFareTV.setText(new StringBuilder("Fare: ").append(fare));
                dWorkPickUpTV.setText(new StringBuilder("Pick Up: ").append(pickup));
                dWorkDropTV.setText(new StringBuilder("Drop: ").append(drop));

                AppAnimation.rotateAnimationRight(closeDialogIB);
                closeDialogIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppAnimation.rotateAnimationLeft(closeDialogIB);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Vars.appDialog.dismiss();
                            }
                        }, 600);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return works.size();
    }

    public class WorkRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView workTitleTV;
        public TextView workDescTV;
        public ImageView assignedIV;
        public Button doneBTN;
        public Button notDoneBTN;

        public WorkRecyclerViewHolder(View itemView) {
            super(itemView);

            workTitleTV = itemView.findViewById(R.id.workTitleTV);
            workDescTV = itemView.findViewById(R.id.workDescTV);
            assignedIV = itemView.findViewById(R.id.assignedIV);
            doneBTN = itemView.findViewById(R.id.doneBTN);
            notDoneBTN = itemView.findViewById(R.id.notDoneBTN);
        }
    }
}
