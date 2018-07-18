package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.Clients;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Home;
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
                View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_admin_clients_assign_work, null);
                Vars.appDialog.create(MainActivity.instance, dialogView).show();

                final EditText workTitleET = dialogView.findViewById(R.id.workTitleET);
                final EditText workDescET = dialogView.findViewById(R.id.workDescET);
                final Button assignBTN = dialogView.findViewById(R.id.assignBTN);
                final Button cancelBTN = dialogView.findViewById(R.id.cancelBTN);

                cancelBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Vars.appDialog.dismiss();
                    }
                });

                assignBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String title = workTitleET.getText().toString();
                        final String desc = workDescET.getText().toString();

                        if (title.equals("") || desc.equals("")) {
                            Toast.makeText(MainActivity.instance, "Title & Description is needed!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Vars.appDialog.dismiss();

                        final AlertDialog spotsDialog = new SpotsDialog(MainActivity.instance, "Please wait...");
                        spotsDialog.show();

                        Map<String, Object> map = new HashMap<>();
                        map.put(AFModel.work_title, title);
                        map.put(AFModel.work_description, desc);
                        map.put(AFModel.work_status, AFModel.val_work_status_request);

                        final DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users)
                                                                .child(AFModel.works).child(currentClient.getId());

                        reference.push().setValue(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    spotsDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.instance, "Work assigned!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.instance, "Work can't assign! Contact with the developer.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
