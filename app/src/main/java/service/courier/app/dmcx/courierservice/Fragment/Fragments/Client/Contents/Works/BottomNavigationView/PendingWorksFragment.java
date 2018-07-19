package service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Contents.Works.BottomNavigationView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Contents.Works.Content.PendingWorksRecyclerViewAdapter;
import service.courier.app.dmcx.courierservice.Models.Work;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class PendingWorksFragment extends Fragment {

    public static final String TAG = "CLIENT-WORKS-PENDINGWORKFRAGMENT";
    public static PendingWorksFragment instance;

    private RecyclerView pendingWorksRV;

    private PendingWorksRecyclerViewAdapter pendingWorksRecyclerViewAdapter;

    private List<Work> works;

    public static void reload() {
        instance.loadRecyclerView();
    }

    private void loadRecyclerView() {
        works = new ArrayList<>();

        final AlertDialog sportsDialog = new SpotsDialog(MainActivity.instance, "Please wait...");
        sportsDialog.show();

        final DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users)
                .child(AFModel.works).child(Vars.appFirebase.getCurrentUser().getUid());

        Vars.appFirebase.check(reference, new AppFirebase.FirebaseCallback() {
            @Override
            public void ProcessCallback(boolean isTaskCompleted) {
                if (isTaskCompleted) {
                    reference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Work work = dataSnapshot.getValue(Work.class);
                            if (work.getWork_status().equals(AFModel.val_work_status_request)) {
                                works.add(work);
                            }

                            pendingWorksRecyclerViewAdapter = new PendingWorksRecyclerViewAdapter(works);
                            pendingWorksRecyclerViewAdapter.notifyDataSetChanged();
                            pendingWorksRV.setAdapter(pendingWorksRecyclerViewAdapter);

                            sportsDialog.dismiss();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Log.d(Vars.APPTAG, "onChildChanged: Happend");
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(Vars.APPTAG, "onChildRemoved: Happend");
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Log.d(Vars.APPTAG, "onChildMoved: Happend");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(Vars.APPTAG, "onCancelled: Happend");
                        }
                    });
                } else {
                    sportsDialog.dismiss();
                }
            }

            @Override
            public void ExceptionCallback(String exception) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_works_bnv_pending_works, container, false);

        instance = this;

        pendingWorksRV = view.findViewById(R.id.pendingWorksRV);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.instance);
        pendingWorksRV.hasFixedSize();
        pendingWorksRV.setLayoutManager(linearLayoutManager);

        loadRecyclerView();

        return view;
    }

}
