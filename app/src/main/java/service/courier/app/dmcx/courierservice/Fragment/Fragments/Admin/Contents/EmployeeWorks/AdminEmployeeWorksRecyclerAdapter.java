package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.EmployeeWorks;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Models.Work;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Utility.AppAnimation;
import service.courier.app.dmcx.courierservice.Utility.AppUtils;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminEmployeeWorksRecyclerAdapter extends RecyclerView.Adapter<AdminEmployeeWorksRecyclerAdapter.AdminClientWorksRecyclerViewHolder> {

    private List<Work> works;

    public AdminEmployeeWorksRecyclerAdapter(List<Work> works) {
        this.works = works;
    }

    @Override
    public AdminClientWorksRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_admin_work_employee_work, parent, false);
        return new AdminClientWorksRecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AdminClientWorksRecyclerViewHolder holder, int position) {

        final String title = works.get(position).getWork_title();
        final String status = AppUtils.FirstLetterCapital(works.get(position).getWork_status());
        final String desc = works.get(position).getWork_description();
        final String time = works.get(position).getWork_time() == null ? "Not given" : works.get(position).getWork_time();
        final String fare = works.get(position).getWork_fare() == null ? "Not given" : works.get(position).getWork_fare();
        final String pickup = works.get(position).getWork_pickup() == null ? "Not given" : works.get(position).getWork_pickup();
        final String drop = works.get(position).getWork_drop() == null ? "Not given" : works.get(position).getWork_drop();


        holder.workTitleTV.setText(title);
        holder.workStatusTV.setText("Status: " + status);

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
