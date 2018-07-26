package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import service.courier.app.dmcx.courierservice.R;

public class AdminProfileEdit extends Fragment {

    public static final String TAG = "ADMIN-PROFILE-EDIT";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile_edit, container, false);
        return view;
    }
}
