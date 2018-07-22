package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.Works.AdminWorksRecyclerAdapter;
import service.courier.app.dmcx.courierservice.Models.Client;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminWorks extends Fragment {

    public static final String TAG = "ADMIN-WORKS";

    private RecyclerView adminWorkListRV;

    private AdminWorksRecyclerAdapter adminWorksRecyclerAdapter;

    private List<Client> clients;

    private void loadRecylerAdapter() {
        clients = new ArrayList<>();

        final AlertDialog spotDialog = new SpotsDialog(MainActivity.instance, "Please wait...");
        spotDialog.show();

        DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.clients);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!clients.isEmpty()) {
                        clients.clear();
                    }

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Client client = snapshot.getValue(Client.class);
                        assert client != null;
                        if (client.getAdmin_id().equals(Vars.appFirebase.getCurrentUser().getUid())) {
                            clients.add(client);
                        }
                    }

                    adminWorksRecyclerAdapter = new AdminWorksRecyclerAdapter(clients);
                    adminWorksRecyclerAdapter.notifyDataSetChanged();
                    adminWorkListRV.setAdapter(adminWorksRecyclerAdapter);
                }

                spotDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_work, container, false);

        adminWorkListRV = view.findViewById(R.id.adminWorkListRV);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.instance);
        adminWorkListRV.hasFixedSize();
        adminWorkListRV.setLayoutManager(linearLayoutManager);

        loadRecylerAdapter();

        return view;
    }
}
