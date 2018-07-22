package service.courier.app.dmcx.courierservice.Fragment.Fragments.Client;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Contents.Works.BottomNavigationView.AcceptedWorksFragment;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Contents.Works.BottomNavigationView.PendingWorksFragment;
import service.courier.app.dmcx.courierservice.Fragment.Manager.AppFragmentManager;
import service.courier.app.dmcx.courierservice.R;

public class ClientWorks extends Fragment {

    public static final String TAG = "CLIENT-WORKS";

    private BottomNavigationView worksBNV;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_works, container, false);

        worksBNV = view.findViewById(R.id.worksBNV);

        worksBNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.pendingWorksBNI: {
                        AppFragmentManager.replace(MainActivity.instance, R.id.fragmentContainerBNV, new PendingWorksFragment());
                        break;
                    }
                    case R.id.acceptedWorksBNI: {
                        AppFragmentManager.replace(MainActivity.instance, R.id.fragmentContainerBNV, new AcceptedWorksFragment());
                        break;
                    }
                }

                return false;
            }
        });

        AppFragmentManager.replace(MainActivity.instance, R.id.fragmentContainerBNV, new PendingWorksFragment());
        return view;
    }
}
