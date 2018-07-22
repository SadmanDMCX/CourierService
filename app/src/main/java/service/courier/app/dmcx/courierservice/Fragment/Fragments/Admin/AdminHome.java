package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AdminHome extends Fragment implements OnMapReadyCallback, LocationListener {

    public static final String TAG = "ADMIN-HOME";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int ERROR_DIALOGREQUEST = 9001;
    private static final int LOCATION_PERMISSION_CODE = 1234;
    private static final float DEFAULT_MAP_ZOOM = 15f;

    private boolean isLocationPermissionGranted = false;
    private boolean isMarkerMapReady = false;
    private GoogleMap mMap;
    private Marker mMapMarker;
    private Map<String, Marker> mMarkersMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private ImageButton gps;

    private boolean isServicesOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.instance);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.instance, available, ERROR_DIALOGREQUEST);
            dialog.show();
            return false;
        }

        Toast.makeText(MainActivity.instance, "You can't make map request!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ActivityCompat.checkSelfPermission(MainActivity.instance, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.instance, COURSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, LOCATION_PERMISSION_CODE);
        } else {
            isLocationPermissionGranted = true;
            init();
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void getDeviceLocation() {
        Log.d(Vars.APPTAG, "getDeviceLocation: getting the device location.");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.instance);
        try {
            if (isLocationPermissionGranted) {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(Vars.APPTAG, "getDeviceLocation: location found.");

                            Location currentLocation = (Location) task.getResult();
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            moveMapCamera(latLng, DEFAULT_MAP_ZOOM);
                            addMapMarker(latLng, "You");
                        } else {
                            Log.d(Vars.APPTAG, "getDeviceLocation: no location found.");
                            Toast.makeText(MainActivity.instance, "Unable to get location!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException ex) {
            Log.d(Vars.APPTAG, "getDeviceLocation: " + ex.getMessage());
        }
    }

    private void moveMapCamera(LatLng latLng, float zoom) {
        Log.d(Vars.APPTAG, "moveMapCamera: moving camera to position.");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void addMapMarker(LatLng latLng, String title) {
        if (mMapMarker != null) {
            mMapMarker.remove();
        }

        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title(title);
        mMapMarker = mMap.addMarker(options);
        mMapMarker.setPosition(latLng);
    }

    private Marker addClientMapMarker(LatLng latLng, String title, float iconColor) {
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
                getDeviceLocation();
            }
        });
    }

    private void loadClientsPositions() {
        mMarkersMap = new HashMap<>();

        final AlertDialog spotDialog = new SpotsDialog(MainActivity.instance, "Please wait...");
        spotDialog.show();

        final DatabaseReference reference = Vars.appFirebase.getDbEmployeesReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Employee> map = (Map<String, Employee>) dataSnapshot.getValue();
                    if (map != null) {
                        for (Map.Entry<String, Employee> entry : map.entrySet()) {
                            Map clientMap = (Map) entry.getValue();

                            boolean isAdminsClient = clientMap.get(AFModel.admin_id).equals(Vars.appFirebase.getCurrentUserId());
                            String id = (String) clientMap.get(AFModel.id);
                            String name = (String) clientMap.get(AFModel.username);
                            String lat = (String) clientMap.get(AFModel.latitude);
                            String lon = (String) clientMap.get(AFModel.longitude);
                            double latitude = lat.equals("") ? 0.0 : Double.valueOf(lat);
                            double longitude = lon.equals("") ? 0.0 : Double.valueOf(lon);

                            if (isAdminsClient) {
                                if (!lat.equals("") && !lon.equals("")) {
                                    if (!isMarkerMapReady) {
                                        Marker marker = addClientMapMarker(new LatLng(latitude, longitude), name, BitmapDescriptorFactory.HUE_CYAN);
                                        mMarkersMap.put(id, marker);
                                    } else {
                                        boolean isUserIdExists = false;
                                        for (Map.Entry<String, Marker> markerEntry : mMarkersMap.entrySet()) {
                                            if (markerEntry.getKey().equals(id)) {
                                                Marker marker = mMarkersMap.get(id);
                                                if (marker != null) {
                                                    isUserIdExists = true;
                                                    marker.setPosition(new LatLng(latitude, longitude));
                                                    break;
                                                }
                                            }
                                        }

                                        if (!isUserIdExists) {
                                            if (!mMarkersMap.keySet().contains(id)) {
                                                Marker newMarker = addClientMapMarker(new LatLng(latitude, longitude), name, BitmapDescriptorFactory.HUE_CYAN);
                                                mMarkersMap.put(id, newMarker);
                                            }
                                        }
                                    }
                                } else {
                                    if (isMarkerMapReady) {
                                        for (Map.Entry<String, Marker> markerEntry : mMarkersMap.entrySet()) {
                                            if (markerEntry.getKey().equals(id)) {
                                                Marker marker = mMarkersMap.get(id);
                                                if (marker != null) {
                                                    marker.remove();
                                                    mMarkersMap.remove(id);
                                                    break;
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

                spotDialog.dismiss();
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

        gps = view.findViewById(R.id.gps);

        if (isServicesOk()) {
            getLocationPermission();
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(MainActivity.instance, "Map is ready.", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (isLocationPermissionGranted) {
            getDeviceLocation();
            loadClientsPositions();

            if (checkPermission()) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(false);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            isLocationPermissionGranted = false;
                            return;
                        }
                    }

                    isLocationPermissionGranted = true;
                    init();
                }
                break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(MainActivity.instance, location.getLatitude() + "", Toast.LENGTH_LONG).show();
    }
}
