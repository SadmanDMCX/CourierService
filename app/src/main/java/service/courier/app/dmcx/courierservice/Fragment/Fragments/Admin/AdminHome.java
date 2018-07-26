package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings.Secure;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.Models.Status;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminHome extends Fragment implements OnMapReadyCallback {

    public static AdminHome instance;

    public static final String TAG = "ADMIN-HOME";
    private static final int LOCATION_PERMISSION_CODE = 1234;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private static final float DEFAULT_MAP_ZOOM = 18f;

    private boolean isMarkerMapReady = false;
    private GoogleMap mMap;
    private Marker mMapMarker;
    private Map<String, Marker> mMarkersMap;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private ImageButton gps;

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void loadAdminPosition() {
        @SuppressLint("HardwareIds")
        String deviceId = Vars.localDB.retriveStringValue(Vars.PREFS_DEVICE_UNIQUE_ID, Secure.getString(MainActivity.instance.getContentResolver(), Secure.ANDROID_ID));
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

    private Marker addEmployeeMapMarker(LatLng latLng, String title, float iconColor) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title(title);
        options.icon(BitmapDescriptorFactory.defaultMarker(iconColor));

        try {
            Geocoder geocoder = new Geocoder(MainActivity.instance);
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            options.snippet(addressList.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return mMap.addMarker(options);
    }

    private void eventListeners() {
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAdminPosition();
            }
        });
    }

    private void loadEmployeePositions() {
        mMarkersMap = new HashMap<>();

        DatabaseReference employeeReference = Vars.appFirebase.getDbEmployeesReference();
        employeeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                        if (employeeSnapshot.exists()) {
                            Employee employee = employeeSnapshot.getValue(Employee.class);
                            if (employee != null) {
                                final boolean isAdminsClient = employee.getAdmin_id().equals(Vars.appFirebase.getCurrentUserId());
                                final String employeeId = employee.getId();
                                final String employeeName = employee.getName();

                                if (isAdminsClient) {
                                    final DatabaseReference statusReference = Vars.appFirebase.getDbStatusReference().child(employeeId);
                                    statusReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot statusSnapshot : dataSnapshot.getChildren()) {
                                                    if (statusSnapshot.hasChild(AFModel.latitude) && statusSnapshot.hasChild(AFModel.longitude)) {
                                                        Status status = statusSnapshot.getValue(Status.class);
                                                        if (status != null) {
                                                            double latitude = status.getLatitude();
                                                            double longitude = status.getLongitude();

                                                            if (latitude != 360 && longitude != 360) {
                                                                if (!isMarkerMapReady) {
                                                                    Marker marker = addEmployeeMapMarker(new LatLng(latitude, longitude), employeeName, BitmapDescriptorFactory.HUE_AZURE);
                                                                    mMarkersMap.put(employeeId, marker);
                                                                } else {
                                                                    boolean isUserIdExists = false;
                                                                    for (Map.Entry<String, Marker> markerEntry : mMarkersMap.entrySet()) {
                                                                        if (markerEntry.getKey().equals(employeeId)) {
                                                                            Marker marker = mMarkersMap.get(employeeId);
                                                                            if (marker != null) {
                                                                                isUserIdExists = true;
                                                                                marker.setPosition(new LatLng(latitude, longitude));
                                                                                break;
                                                                            }
                                                                        }
                                                                    }

                                                                    if (!isUserIdExists) {
                                                                        if (!mMarkersMap.keySet().contains(employeeId)) {
                                                                            Marker newMarker = addEmployeeMapMarker(new LatLng(latitude, longitude), employeeName, BitmapDescriptorFactory.HUE_AZURE);
                                                                            mMarkersMap.put(employeeId, newMarker);
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                if (isMarkerMapReady) {
                                                                    for (Map.Entry<String, Marker> markerEntry : mMarkersMap.entrySet()) {
                                                                        if (markerEntry.getKey().equals(employeeId)) {
                                                                            Marker marker = mMarkersMap.get(employeeId);
                                                                            if (marker != null) {
                                                                                marker.remove();
                                                                                mMarkersMap.remove(employeeId);
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                isMarkerMapReady = true;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        initMap();
        eventListeners();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

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
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            loadAdminPosition();
            loadEmployeePositions();
        }
    }
}
