package service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee.Contents.Works.BottomNavigationView;

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
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee.Contents.Works.Content.AcceptedWorksRecyclerViewAdapter;
import service.courier.app.dmcx.courierservice.Models.Work;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AcceptedWorksFragment extends Fragment {

    public static final String TAG = "CLIENT=WORKS-ACCEPTEDWORKSFRAGMENT";
    public static AcceptedWorksFragment instance;

    private RecyclerView acceptedWorksRV;

    private AcceptedWorksRecyclerViewAdapter acceptedWorksRecyclerViewAdapter;

    private List<Work> works;

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
                    reference.orderByChild(AFModel.created_at).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!works.isEmpty()) {
                                works.clear();
                            }

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Work work = snapshot.getValue(Work.class);
                                if (work.getWork_status().equals(AFModel.val_work_status_accept)) {
                                    works.add(work);
                                }
                            }

                            acceptedWorksRecyclerViewAdapter = new AcceptedWorksRecyclerViewAdapter(works);
                            acceptedWorksRecyclerViewAdapter.notifyDataSetChanged();
                            acceptedWorksRV.setAdapter(acceptedWorksRecyclerViewAdapter);

                            sportsDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

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
        View view = inflater.inflate(R.layout.fragment_employee_works_bnv_assigned_works, container, false);

        instance = this;

        acceptedWorksRV = view.findViewById(R.id.acceptedWorksRV);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.instance);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        acceptedWorksRV.hasFixedSize();
        acceptedWorksRV.setLayoutManager(linearLayoutManager);

        loadRecyclerView();

        return view;
    }
}
