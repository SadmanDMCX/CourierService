package service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Models.Status;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class EmployeeHome extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "EMPLOYEE-HOME";

    public static EmployeeHome instance;

    private static final int LOCATION_PERMISSION_CODE = 1234;
    private static final float DEFAULT_MAP_ZOOM = 18f;

    private GoogleMap mMap;
    private Marker mMapMarker;

    private ImageButton gps;

    // Checker
    private boolean checkPermission() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, LOCATION_PERMISSION_CODE);
        } else {
            return true;
        }

        return false;
    }
    // Checker

    private void loadEmployeePosition() {
        @SuppressLint("HardwareIds")
        String deviceId = Vars.localDB.retriveStringValue(Vars.PREFS_DEVICE_UNIQUE_ID, Settings.Secure.getString(MainActivity.instance.getContentResolver(), Settings.Secure.ANDROID_ID));
        final DatabaseReference statusReference =
                Vars.appFirebase.getDbStatusReference().child(Vars.appFirebase.getCurrentUserId()).child(deviceId);
        statusReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Status status = dataSnapshot.getValue(Status.class);
                    if (status != null) {
                        LatLng latLng = new LatLng(status.getLatitude(), status.getLongitude());
                        moveMapCamera(latLng, DEFAULT_MAP_ZOOM);
                        addMapMarker(latLng, "You", status.getDevice_name());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void moveMapCamera(LatLng latLng, float zoom) {
        Log.d(Vars.APPTAG, "moveMapCamera: moving camera to position.");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void addMapMarker(LatLng latLng, String title, String deviceName) {
        if (mMapMarker != null) {
            mMapMarker.remove();
        }

        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title(title);
        options.snippet(deviceName);
        mMapMarker = mMap.addMarker(options);
        mMapMarker.setPosition(latLng);
    }

    private void eventListeners() {
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadEmployeePosition();
            }
        });
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void init() {
        initMap();
        eventListeners();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee_home, container, false);

        instance = this;

        gps = view.findViewById(R.id.gps);

        init();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(MainActivity.instance, "Map is ready.", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (checkPermission()) {
            loadEmployeePosition();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }
    }

}
