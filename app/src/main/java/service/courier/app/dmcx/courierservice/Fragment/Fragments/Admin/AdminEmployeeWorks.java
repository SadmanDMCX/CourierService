package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.EmployeeWorks.AdminEmployeeWorksRecyclerAdapter;
import service.courier.app.dmcx.courierservice.Fragment.Manager.AppFragmentManager;
import service.courier.app.dmcx.courierservice.Models.Work;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminEmployeeWorks extends Fragment {

    public static final String TAG = "ADMIN-CLIENT-WORKS";

    private CoordinatorLayout adminEmployeeWorkCL;
    private RecyclerView adminEmployeeWorkListRV;
    private FloatingActionButton deleteAllFAB;
    private TextView noDataFoundTV;

    private AdminEmployeeWorksRecyclerAdapter adminEmployeeWorksRecyclerAdapter;

    private List<Work> works;
    private String argClientId;

    private void loadRecyclerAdapter() {
        works = new ArrayList<>();

        DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.works).child(argClientId);
        reference.orderByChild(AFModel.created_at).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    deleteAllFAB.setVisibility(View.VISIBLE);
                    noDataFoundTV.setVisibility(View.GONE);

                    if (!works.isEmpty()) {
                        works.clear();
                    }

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Work work = snapshot.getValue(Work.class);
                        works.add(work);
                    }

                    adminEmployeeWorksRecyclerAdapter = new AdminEmployeeWorksRecyclerAdapter(works);
                    adminEmployeeWorksRecyclerAdapter.notifyDataSetChanged();
                    adminEmployeeWorkListRV.setAdapter(adminEmployeeWorksRecyclerAdapter);
                } else {
                    noDataFoundTV.setVisibility(View.VISIBLE);
                    deleteAllFAB.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ItemTouchHelper.Callback swipeRecyclerItemCallback() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                final boolean[] isDeleteSuccess = {true};
                final int position = viewHolder.getAdapterPosition();
                final Work work = works.get(position);

                works.remove(position);
                adminEmployeeWorksRecyclerAdapter.notifyItemRemoved(position);

                Snackbar.make(adminEmployeeWorkCL, "Item deleted!", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.WHITE)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                isDeleteSuccess[0] = false;
                                works.add(position, work);
                                adminEmployeeWorksRecyclerAdapter.notifyDataSetChanged();
                                adminEmployeeWorkListRV.smoothScrollToPosition(position);
                            }
                        })
                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);

                                if (isDeleteSuccess[0]) {
                                    DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.works).child(argClientId).child(work.getWork_id());
                                    reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.instance, "Deleted permenently!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(MainActivity.instance, "Error! While delete single work!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }).show();

            }
        };

        return simpleCallback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_work_employee_work, container, false);

        argClientId = getArguments().getString(Vars.Transporter.CLIENT_ID);

        adminEmployeeWorkCL = view.findViewById(R.id.adminEmployeeWorkCL);
        adminEmployeeWorkListRV = view.findViewById(R.id.adminEmployeeWorkListRV);
        deleteAllFAB = view.findViewById(R.id.deleteAllFAB);
        noDataFoundTV = view.findViewById(R.id.noDataFoundTV);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.instance);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        adminEmployeeWorkListRV.hasFixedSize();
        adminEmployeeWorkListRV.setLayoutManager(linearLayoutManager);

        loadRecyclerAdapter();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeRecyclerItemCallback());
        itemTouchHelper.attachToRecyclerView(adminEmployeeWorkListRV);

        deleteAllFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.dialog_confirm_cancel, null);
                Vars.appDialog.create(MainActivity.instance, dialogView);
                Vars.appDialog.show();

                TextView messageTitleTV = dialogView.findViewById(R.id.messageTitleTV);
                TextView messageBodyTV = dialogView.findViewById(R.id.messageBodyTV);
                Button confirmDBTN = dialogView.findViewById(R.id.confirmDBTN);
                Button cancelDBTN = dialogView.findViewById(R.id.cancelDBTN);

                messageTitleTV.setText("Delete");
                messageBodyTV.setText("Are you sure? It will delete all the data.");

                cancelDBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Vars.appDialog.dismiss();
                    }
                });

                confirmDBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Vars.appDialog.dismiss();

                        DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.works).child(argClientId);
                        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    AppFragmentManager.replace(MainActivity.instance, AppFragmentManager.fragmentContainer, new AdminWorks(), AdminWorks.TAG);
                                    Toast.makeText(MainActivity.instance, "All Data Deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        return view;
    }
}
