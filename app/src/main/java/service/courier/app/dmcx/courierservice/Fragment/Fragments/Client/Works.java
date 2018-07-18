package service.courier.app.dmcx.courierservice.Fragment.Fragments.Client;

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
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.Clients.ClientRecyclerViewAdapter;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Contents.WorksRecyclerViewAdapter;
import service.courier.app.dmcx.courierservice.Models.Client;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class Works extends Fragment {

    public static final String TAG = "CLIENT-WORKS";

    private RecyclerView worksRV;

    private WorksRecyclerViewAdapter worksRecyclerViewAdapter;

    private List<Client> clients;

    private void loadRecyclerView() {
        clients = new ArrayList<>();

        final AlertDialog sportsDialog = new SpotsDialog(MainActivity.instance, "Please wait...");
        sportsDialog.show();

        DatabaseReference reference = Vars.appFirebase.getDbReference();
        reference.child(AFModel.users).child(AFModel.client).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!clients.isEmpty()) {
                        clients.clear();
                    }

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Client client = snapshot.getValue(Client.class);
                        assert client != null;
                        if (client.getId().equals(Vars.appFirebase.getCurrentUser().getUid())) {
                            if (!client.getWork().equals("")) {
                                clients.add(client);
                            }
                        }
                    }

                    worksRecyclerViewAdapter = new WorksRecyclerViewAdapter(clients);
                    worksRecyclerViewAdapter.notifyDataSetChanged();
                    worksRV.setAdapter(worksRecyclerViewAdapter);
                }

                sportsDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_works, container, false);

        worksRV = view.findViewById(R.id.worksRV);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.instance);
        worksRV.hasFixedSize();
        worksRV.setLayoutManager(linearLayoutManager);

        loadRecyclerView();

        return view;
    }
}
