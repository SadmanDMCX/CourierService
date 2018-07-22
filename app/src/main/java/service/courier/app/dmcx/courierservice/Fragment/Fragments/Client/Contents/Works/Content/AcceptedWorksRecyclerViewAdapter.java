package service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Contents.Works.Content;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Contents.Works.BottomNavigationView.AcceptedWorksFragment;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Contents.Works.BottomNavigationView.PendingWorksFragment;
import service.courier.app.dmcx.courierservice.Models.Work;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AcceptedWorksRecyclerViewAdapter extends RecyclerView.Adapter<AcceptedWorksRecyclerViewAdapter.WorkRecyclerViewHolder> {

    private List<Work> works;

    private void loadParams() {
        PendingWorksFragment.reload();
        AcceptedWorksFragment.reload();
    }

    public AcceptedWorksRecyclerViewAdapter(List<Work> works) {
        this.works = works;
    }

    @Override
    public WorkRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MainActivity.instance).inflate(R.layout.layout_single_client_works_accepted_work, parent, false);
        return new WorkRecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final WorkRecyclerViewHolder holder, int position) {

        holder.workTitleTV.setText(works.get(position).getWork_title());
        holder.workDescTV.setText(works.get(position).getWork_description());

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
                            loadParams();
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
                            loadParams();
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
