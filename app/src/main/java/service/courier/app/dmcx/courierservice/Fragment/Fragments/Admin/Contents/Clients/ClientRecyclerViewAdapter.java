package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.Clients;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Models.Client;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class ClientRecyclerViewAdapter extends RecyclerView.Adapter<ClientRecyclerViewAdapter.ClientRecyclerViewHolder> {

    private List<Client> clients;

    public ClientRecyclerViewAdapter(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public ClientRecyclerViewAdapter.ClientRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_admin_single_client, parent, false);
        return new ClientRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClientRecyclerViewAdapter.ClientRecyclerViewHolder holder, int position) {
        holder.clientNameTV.setText(clients.get(position).getName());
        holder.clientStatusTV.setText(clients.get(position).getStatus());

        final Client currentClient = clients.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference =
                        Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.client)
                                .child(currentClient.getId()).child(AFModel.work);

                final AlertDialog spotsDialog = new SpotsDialog(MainActivity.instance, "Please wait...");
                spotsDialog.show();

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Object data = dataSnapshot.getValue();
                        boolean isNothing = data.toString().isEmpty();
                        spotsDialog.dismiss();

                        if (isNothing) {
                            View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_admin_assign_work_to_client, null);
                            Vars.appDialog.create(MainActivity.instance, dialogView).show();

                            Button yesBTN = dialogView.findViewById(R.id.yesBTN);
                            Button noBTN = dialogView.findViewById(R.id.noBTN);

                            noBTN.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Vars.appDialog.dismiss();
                                }
                            });

                            yesBTN.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Vars.appDialog.dismiss();
                                    spotsDialog.show();

                                    reference.setValue(Vars.TASK_CODE).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            spotsDialog.dismiss();

                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.instance, "Task assigned successfully.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }  else {
                            Toast.makeText(MainActivity.instance, "Already task assigned!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public class ClientRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView clientNameTV;
        public TextView clientStatusTV;
        public CircleImageView clientImageCIV;

        public ClientRecyclerViewHolder(View itemView) {
            super(itemView);

            clientNameTV = itemView.findViewById(R.id.clientNameTV);
            clientStatusTV = itemView.findViewById(R.id.clientStatusTV);
            clientImageCIV = itemView.findViewById(R.id.clientImageCIV);
        }
    }

}
