package service.courier.app.dmcx.courierservice.Fragment.Fragments.Client;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import service.courier.app.dmcx.courierservice.R;

public class CHome extends Fragment {

    public static final String TAG = "CLIENT-HOME";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_home, container, false);
        return view;
    }
}
