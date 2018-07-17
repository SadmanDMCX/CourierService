package service.courier.app.dmcx.courierservice.Fragment.Fragments.Client;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import service.courier.app.dmcx.courierservice.R;

public class Works extends Fragment {

    public static final String TAG = "CLIENT-WORK";

    private RecyclerView worksRV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_work, container, false);

        worksRV = view.findViewById(R.id.worksRV);

        return view;
    }
}
