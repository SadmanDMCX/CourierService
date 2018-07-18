package service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Contents;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Models.Client;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class WorksRecyclerViewAdapter extends RecyclerView.Adapter<WorksRecyclerViewAdapter.WorkRecyclerViewHolder> {

    private List<Client> clients;

    public WorksRecyclerViewAdapter(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public WorkRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MainActivity.instance).inflate(R.layout.layout_client_single_work, parent, false);
        return new WorkRecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final WorkRecyclerViewHolder holder, int position) {

        holder.taskNameTV.setText("New Task");
        holder.taskDescTV.setText("Task description is here.");

        holder.assignIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isTrue = false;
                try {
                    isTrue = Objects.equals(holder.assignIB.getDrawable().getConstantState(), MainActivity.instance.getResources().getDrawable(R.drawable.assign_black).getConstantState());
                } catch (NullPointerException ex) {
                    return;
                }

                final DatabaseReference reference =
                        Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.client)
                                .child(Vars.appFirebase.getCurrentUser().getUid()).child(AFModel.work);

                final AlertDialog spotsDialog = new SpotsDialog(MainActivity.instance, "Please wait...");
                spotsDialog.show();

                if (isTrue) {
                    reference.setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            spotsDialog.dismiss();
                            if (task.isSuccessful()) {
                                holder.assignIB.setImageResource(R.drawable.not_assign_gray);
                            }
                        }
                    });
                } else {
                    reference.setValue(Vars.TASK_CODE).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            spotsDialog.dismiss();
                            if (task.isSuccessful()) {
                                holder.assignIB.setImageResource(R.drawable.assign_black);
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public class WorkRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView taskNameTV;
        public TextView taskDescTV;
        public ImageButton assignIB;

        public WorkRecyclerViewHolder(View itemView) {
            super(itemView);

            taskNameTV = itemView.findViewById(R.id.taskNameTV);
            taskDescTV = itemView.findViewById(R.id.taskDescTV);
            assignIB = itemView.findViewById(R.id.assignIB);
        }
    }
}
