package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.ClientWorks;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Dialog.AppDialog;
import service.courier.app.dmcx.courierservice.Models.Work;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Utility.AppUtils;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminClientWorksRecyclerAdapter extends RecyclerView.Adapter<AdminClientWorksRecyclerAdapter.AdminClientWorksRecyclerViewHolder> {

    private List<Work> works;

    public AdminClientWorksRecyclerAdapter(List<Work> works) {
        this.works = works;
    }

    @Override
    public AdminClientWorksRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_admin_work_client_work, parent, false);
        return new AdminClientWorksRecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AdminClientWorksRecyclerViewHolder holder, int position) {

        final String title = works.get(position).getWork_title();
        final String status = AppUtils.FirstLetterCapital(works.get(position).getWork_status());
        final String desc = works.get(position).getWork_description();

        holder.workTitleTV.setText(title);
        holder.workStatusTV.setText("Status: " + status);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_admin_work_client_work_detail, null);
                Vars.appDialog.create(MainActivity.instance, dialogView).transparent();
                Vars.appDialog.show();

                ImageButton closeDialogIB = dialogView.findViewById(R.id.closeDialogIB);
                TextView dWorkTitleTV = dialogView.findViewById(R.id.dWorkTitleTV);
                TextView dWorkStatus = dialogView.findViewById(R.id.dWorkStatus);
                TextView dWorkDescTV = dialogView.findViewById(R.id.dWorkDescTV);

                dWorkTitleTV.setText(title);
                dWorkStatus.setText("Status: " + status);
                dWorkDescTV.setText(desc);

                closeDialogIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Vars.appDialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return works.size();
    }

    public class AdminClientWorksRecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView workTitleTV;
        public TextView workStatusTV;
        public ImageView workIconIV;

        public AdminClientWorksRecyclerViewHolder(View itemView) {
            super(itemView);

            workTitleTV = itemView.findViewById(R.id.workTitleTV);
            workStatusTV = itemView.findViewById(R.id.workStatusTV);
            workIconIV = itemView.findViewById(R.id.workIconIV);
        }
    }
}
