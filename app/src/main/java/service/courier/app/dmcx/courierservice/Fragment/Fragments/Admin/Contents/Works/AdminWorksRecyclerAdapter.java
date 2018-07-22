package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.Works;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminClientWorks;
import service.courier.app.dmcx.courierservice.Fragment.Manager.AppFragmentManager;
import service.courier.app.dmcx.courierservice.Models.Client;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminWorksRecyclerAdapter extends RecyclerView.Adapter<AdminWorksRecyclerAdapter.AdminWorksRecyclerViewHolder> {

    private List<Client> clients;

    public AdminWorksRecyclerAdapter(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public AdminWorksRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_admin_work, parent, false);
        return new AdminWorksRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdminWorksRecyclerViewHolder holder, int position) {

        final int itemPosition = position;

        final String name = clients.get(position).getName();
        final String id = clients.get(position).getId();

        holder.clientNameTV.setText(name);

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
                bundle.putString(Vars.Transporter.CLIENT_ID, id);

                AppFragmentManager.replace(MainActivity.instance, AppFragmentManager.fragmentContainer, new AdminClientWorks(), AdminClientWorks.TAG, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public class AdminWorksRecyclerViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView clientCIV;
        public TextView clientNameTV;
        public TextView noOfWorkTV;
        public ImageView workIV;

        public AdminWorksRecyclerViewHolder(View itemView) {
            super(itemView);

            clientCIV = itemView.findViewById(R.id.clientCIV);
            clientNameTV = itemView.findViewById(R.id.clientNameTV);
            noOfWorkTV = itemView.findViewById(R.id.noOfWorkTV);
            workIV = itemView.findViewById(R.id.workIV);
        }
    }
}
