package service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import service.courier.app.dmcx.courierservice.R;

public class EmployeeHome extends Fragment {

    public static EmployeeHome instance;

    public static final String TAG = "EMPLOYEE-HOME";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee_home, container, false);

        instance = this;

        return view;
    }

}
