package service.courier.app.dmcx.courierservice.Fragment.Fragments.Client;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class ClientHome extends Fragment {

    public static final String TAG = "CLIENT-HOME";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int ERROR_DIALOGREQUEST = 9001;
    private static final int LOCATION_PERMISSION_CODE = 1234;

    private boolean isLocationPermissionGranted;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private boolean isServiceOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.instance);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.instance, available, ERROR_DIALOGREQUEST);
            dialog.show();
        }

        Toast.makeText(MainActivity.instance, "You can't make map request!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean checkPermission() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, LOCATION_PERMISSION_CODE);
        } else {
            isLocationPermissionGranted = true;
            return true;
        }

        isLocationPermissionGranted = false;
        return false;
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.instance);
        try {
            if (checkPermission()) {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(Vars.APPTAG, "getDeviceLocation: location found.");

                            Location currentLocation = (Location) location.getResult();
                            DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.clients).child(Vars.appFirebase.getCurrentUser().getUid());
                            Map<String, Object>  map = new HashMap<>();
                            map.put(AFModel.latitude, String.valueOf(currentLocation.getLatitude()));
                            map.put(AFModel.longitude, String.valueOf(currentLocation.getLongitude()));

                            reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful())  {
                                        Toast.makeText(MainActivity.instance, "Location can't update!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Log.d(Vars.APPTAG, "getDeviceLocation: no location found.");
                            Toast.makeText(MainActivity.instance, "Unable to get location!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            Log.d(Vars.APPTAG, "getDeviceLocation: " + ex.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_home, container, false);

        if (isServiceOk()) {
            if (checkPermission()) {
                getDeviceLocation();
            }
        }

        return view;
    }
}
