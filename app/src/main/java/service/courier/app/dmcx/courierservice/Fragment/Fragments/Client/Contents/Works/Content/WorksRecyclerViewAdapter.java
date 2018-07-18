package service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Contents.Works.Content;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Objects;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Models.Work;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class WorksRecyclerViewAdapter extends RecyclerView.Adapter<WorksRecyclerViewAdapter.WorkRecyclerViewHolder> {

    private List<Work> works;

    public WorksRecyclerViewAdapter(List<Work> works) {
        this.works = works;
    }

    @Override
    public WorkRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MainActivity.instance).inflate(R.layout.layout_client_single_work, parent, false);
        return new WorkRecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final WorkRecyclerViewHolder holder, int position) {

        holder.taskTitleTV.setText(works.get(position).getWork_title());
        holder.taskDescTV.setText(works.get(position).getWork_description());

        holder.assignIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isTrue = false;
                try {
                    isTrue = Objects.equals(holder.assignIB.getDrawable().getConstantState(), MainActivity.instance.getResources().getDrawable(R.drawable.not_assign_gray).getConstantState());
                } catch (NullPointerException ex) {
                    return;
                }

                final DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.works)
                                                        .child(Vars.appFirebase.getCurrentUser().getUid());

                if (isTrue) {
                    holder.assignIB.setImageResource(R.drawable.assign_black);
                } else {
                    holder.assignIB.setImageResource(R.drawable.not_assign_gray);
                }
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
        public ImageButton assignIB;

        public WorkRecyclerViewHolder(View itemView) {
            super(itemView);

            taskTitleTV = itemView.findViewById(R.id.taskTitleTV);
            taskDescTV = itemView.findViewById(R.id.taskDescTV);
            assignIB = itemView.findViewById(R.id.assignIB);
        }
    }
}
