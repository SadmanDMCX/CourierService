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

public class PendingWorksRecyclerViewAdapter extends RecyclerView.Adapter<PendingWorksRecyclerViewAdapter.WorkRecyclerViewHolder> {

    private List<Work> works;

    private void loadParams() {
        PendingWorksFragment.reload();
        AcceptedWorksFragment.reload();
    }

    public PendingWorksRecyclerViewAdapter(List<Work> works) {
        this.works = works;
    }

    @Override
    public WorkRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MainActivity.instance).inflate(R.layout.layout_client_works_single_pending_work, parent, false);
        return new WorkRecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final WorkRecyclerViewHolder holder, int position) {
        final int currentPosition = position;

        holder.taskTitleTV.setText(works.get(position).getWork_title());
        holder.taskDescTV.setText(works.get(position).getWork_description());

        final DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.works)
                .child(Vars.appFirebase.getCurrentUser().getUid()).child(works.get(position).getWork_id());

        holder.acceptBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put(AFModel.work_status, AFModel.val_work_status_accept);

                reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.instance, "Work accepted!", Toast.LENGTH_SHORT).show();
                            loadParams();
                        } else {
                            Log.d(Vars.APPTAG, "Error Accept Work: " + task.getException().getMessage());
                        }
                    }
                });
            }
        });

        holder.denyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put(AFModel.work_status, AFModel.val_work_status_deny);
                reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.instance, "Work denied!", Toast.LENGTH_SHORT).show();
                            loadParams();
                        } else {
                            Log.d(Vars.APPTAG, "Error Deny Work: " + task.getException().getMessage());
                        }
                    }
                });
            }
        });

        holder.assignIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.instance, "New work assigned!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return works.size();
    }

    public class WorkRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView taskTitleTV;
        public TextView taskDescTV;
        public ImageView assignIB;
        public Button acceptBTN;
        public Button denyBTN;

        public WorkRecyclerViewHolder(View itemView) {
            super(itemView);

            taskTitleTV = itemView.findViewById(R.id.taskTitleTV);
            taskDescTV = itemView.findViewById(R.id.taskDescTV);
            assignIB = itemView.findViewById(R.id.assignIV);
            acceptBTN = itemView.findViewById(R.id.acceptBTN);
            denyBTN = itemView.findViewById(R.id.denyBTN);
        }
    }
}
