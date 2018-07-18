package service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Contents.Works.BottomNavigationView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.R;

public class AcceptedWorksFragment extends Fragment {

    public static final String TAG = "CLIENT=WORKS-ACCEPTEDWORKSFRAGMENT";

    private RecyclerView acceptedWorksRV;

//    private void loadRecyclerView() {
//        clients = new ArrayList<>();
//
//        final AlertDialog sportsDialog = new SpotsDialog(MainActivity.instance, "Please wait...");
//        sportsDialog.show();
//
//        DatabaseReference reference = Vars.appFirebase.getDbReference();
//        reference.child(AFModel.users).child(AFModel.clients).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    if (!clients.isEmpty()) {
//                        clients.clear();
//                    }
//
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Client client = snapshot.getValue(Client.class);
//                        assert client != null;
//                        if (client.getId().equals(Vars.appFirebase.getCurrentUser().getUid())) {
//                            clients.add(client);
//                        }
//                    }
//
//                    worksRecyclerViewAdapter = new WorksRecyclerViewAdapter(clients);
//                    worksRecyclerViewAdapter.notifyDataSetChanged();
//                    worksRV.setAdapter(worksRecyclerViewAdapter);
//                }
//
//                sportsDialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_works_bnv_assigned_works, container, false);

        acceptedWorksRV = view.findViewById(R.id.acceptedWorksRV);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.instance);
        acceptedWorksRV.hasFixedSize();
        acceptedWorksRV.setLayoutManager(linearLayoutManager);


        return view;
    }
}
