package service.courier.app.dmcx.courierservice.Fragment.Manager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AppFragmentManager {

    public static final int fragmentContainer = R.id.fragmentContainer;
    public static final int fragmentMapContainer = R.id.fragmentMapContainer;

    private static void swichFragmentContiner(AppCompatActivity appCompatActivity, int container) {
        FrameLayout fC = appCompatActivity.findViewById(fragmentContainer);
        FrameLayout fM = appCompatActivity.findViewById(fragmentMapContainer);

        if (container == fragmentMapContainer) {
            fM.setVisibility(View.VISIBLE);
            fC.setVisibility(View.GONE);
        } else {
            fC.setVisibility(View.VISIBLE);
            fM.setVisibility(View.GONE);
        }
    }

    public static void replace(AppCompatActivity appCompatActivity, int container, Fragment fragment, String tag) {
        swichFragmentContiner(appCompatActivity, container);

        appCompatActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(container, fragment, tag)
                .commit();

        Vars.currentFragment = fragment;
    }

    public static void replace(AppCompatActivity appCompatActivity, int container, Fragment fragment, String tag, Bundle bundle) {
        swichFragmentContiner(appCompatActivity, container);

        fragment.setArguments(bundle);
        appCompatActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(container, fragment, tag)
                .commit();

        Vars.currentFragment = fragment;
    }

    public static void replace(AppCompatActivity appCompatActivity, int container, Fragment fragment) {
        appCompatActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(container, fragment)
                .commit();
    }

}
