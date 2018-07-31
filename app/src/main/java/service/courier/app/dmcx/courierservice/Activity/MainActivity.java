package service.courier.app.dmcx.courierservice.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminEmployeeWorks;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminEmployees;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminHome;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminProfile;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminProfileEdit;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminWorks;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee.EmployeeHome;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee.EmployeeProfile;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee.EmployeeProfileEdit;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Employee.EmployeeWorks;
import service.courier.app.dmcx.courierservice.Fragment.Manager.AppFragmentManager;
import service.courier.app.dmcx.courierservice.Models.Admin;
import service.courier.app.dmcx.courierservice.Models.Employee;
import service.courier.app.dmcx.courierservice.Variables.Vars;
import service.courier.app.dmcx.courierservice.R;

public class MainActivity extends AppCompatActivity {

    private static final int TOOLBAR_MARGIN_SIZE = 15;

    @SuppressLint("StaticFieldLeak")
    public static MainActivity instance;

    private static final int LOCATION_PERMISSION_CODE = 1234;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Animation aSlideUpToPositionFast;

    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Static Method
    public static class MainActivityClass {
        public static void signOut() {
            MainActivity.instance.signOutUser();
        }
    }
    // Static Method

    // Checker
    private boolean isServicesOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.instance);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.instance, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }

        Toast.makeText(MainActivity.instance, "You can't make map request!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean checkPermission() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.instance, permissions, LOCATION_PERMISSION_CODE);
        } else {
            return true;
        }

        return false;
    }
    // Checker

    private void loadAnimation() {
        aSlideUpToPositionFast = AnimationUtils.loadAnimation(instance, R.anim.slide_up_to_position_fast);
    }

    private void loadToolbarPosition(int value) {
        int dip = value;
        if (value != 0) {
            final float scale = instance.getResources().getDisplayMetrics().density;
            dip = (int) (value * scale + 0.5f);
        }

        final int margin = dip;
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        layoutParams.setMargins(margin, margin, margin, margin);
        appBarLayout.setLayoutParams(layoutParams);

        loadAnimation();
        appBarLayout.setAnimation(aSlideUpToPositionFast);
    }

    private void startAuthActivity() {
        Intent intent = new Intent(MainActivity.instance, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadNavFragment(String title, int marginSize, int container, Fragment fragment, String tag) {
        toolbar.setTitle(title);
        loadToolbarPosition(marginSize);
        AppFragmentManager.replace(MainActivity.instance, container, fragment, tag);
    }

    private void signOutUser() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        Map<String, Object> map = new HashMap<>();
        map.put(AFModel.latitude, 360);
        map.put(AFModel.longitude, 360);
        map.put(AFModel.state, AFModel.val_state_offline);

        @SuppressLint("HardwareIds")
        final String deviceId = Vars.localDB.retriveStringValue(Vars.PREFS_DEVICE_UNIQUE_ID, Secure.getString(instance.getContentResolver(), Secure.ANDROID_ID));
        Vars.appFirebase.getDbStatusReference().child(Vars.appFirebase.getCurrentUserId()).child(deviceId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DatabaseReference notificationTokenReference =
                            Vars.appFirebase.getDbNotificationsReference().child(AFModel.tokens).child(Vars.appFirebase.getCurrentUserId()).child(deviceId);
                    notificationTokenReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(instance, "You are now offline!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.instance, "Can't recive notification.", Toast.LENGTH_SHORT).show();
                            }

                            Vars.appFirebase.signOutUser();
                            Vars.localDB.clearDB();
                            Vars.reset();
                            startAuthActivity();
                        }
                    });
                } else {
                    Log.d(Vars.APPTAG, "ExceptionCallback: " + Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    private void fusedLocationInit() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();

                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    @SuppressLint("HardwareIds")
                    String deviceId = Vars.localDB.retriveStringValue(Vars.PREFS_DEVICE_UNIQUE_ID, Secure.getString(instance.getContentResolver(), Secure.ANDROID_ID));
                    Map<String, Object> statusMap = new HashMap<>();
                    statusMap.put(AFModel.latitude, latLng.latitude);
                    statusMap.put(AFModel.longitude, latLng.longitude);
                    statusMap.put(AFModel.device_name, Build.MODEL);
                    statusMap.put(AFModel.state, AFModel.val_state_online);

                    Vars.appFirebase.getDbStatusReference().child(Vars.appFirebase.getCurrentUserId()).child(deviceId)
                            .setValue(statusMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        @SuppressLint("HardwareIds")
                                        String deviceId = Vars.localDB.retriveStringValue(Vars.PREFS_DEVICE_UNIQUE_ID, Secure.getString(MainActivity.instance.getContentResolver(), Secure.ANDROID_ID));
                                        String tokenId = FirebaseInstanceId.getInstance().getToken();

                                        Map<String, Object> tokenMap = new HashMap<>();
                                        tokenMap.put(AFModel.token_id, tokenId);
                                        DatabaseReference notificationTokenReference =
                                                Vars.appFirebase.getDbNotificationsReference().child(AFModel.tokens).child(Vars.appFirebase.getCurrentUserId()).child(deviceId);
                                        notificationTokenReference.setValue(tokenMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.instance, "Can't recive notification.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(MainActivity.instance, "Location couldn't save.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                } else {
                    Log.d(Vars.APPTAG, "getDeviceLocation: no location found.");
                    Toast.makeText(MainActivity.instance, "Unable to get location!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.instance);
        if (checkPermission()) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    private void loadAdminNavHeader(String clild, final Class object) {
        View navHederView = navigationView.getHeaderView(0);
        final ImageView navHeaderUserCIV = navHederView.findViewById(R.id.navHeaderUserCIV);
        final TextView developerName = navHederView.findViewById(R.id.developerName);
        final TextView developerMail = navHederView.findViewById(R.id.developerMail);

        final AlertDialog spotDialog = new SpotsDialog(instance, "Loading User Info...");
        spotDialog.show();

        DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(clild).child(Vars.appFirebase.getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Object classObject = dataSnapshot.getValue(object);

                    assert classObject != null;
                    if (classObject instanceof Admin) {
                        Admin admin = (Admin) classObject;
                        String image_path = admin.getImage_path();
                        if (!image_path.equals("")) {
                            Picasso.with(MainActivity.instance)
                                    .load(image_path)
                                    .placeholder(R.drawable.default_avater)
                                    .into(navHeaderUserCIV);
                        }
                        developerName.setText(admin.getName());
                        developerMail.setText(Vars.appFirebase.getCurrentUser().getEmail());
                    } else if (classObject instanceof Employee) {
                        Employee employee = (Employee) classObject;
                        String image_path = employee.getImage_path();
                        if (!image_path.equals("")) {
                            Picasso.with(MainActivity.instance)
                                    .load(image_path)
                                    .placeholder(R.drawable.default_avater)
                                    .into(navHeaderUserCIV);
                        }
                        developerName.setText(employee.getName());
                        developerMail.setText(Vars.appFirebase.getCurrentUser().getEmail());
                    }
                }

                spotDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        Vars.appFirebase = new AppFirebase();

        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(instance, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (isServicesOk())
            if (checkPermission())
                fusedLocationInit();
            else
                return;

        if (Vars.isUserAdmin) {
            navigationView.inflateMenu(R.menu.drawer_menu_admin);
            loadAdminNavHeader(AFModel.admins, Admin.class);
        } else {
            navigationView.inflateMenu(R.menu.drawer_menu_employee);
            loadAdminNavHeader(AFModel.employees, Employee.class);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                final MenuItem menuItem = item;
                navigationView.setCheckedItem(item.getItemId());
                drawerLayout.closeDrawer(GravityCompat.START);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Vars.isUserAdmin) {
                            switch (menuItem.getItemId()) {
                                case R.id.homeANI: {
                                    loadNavFragment("Courier Service", TOOLBAR_MARGIN_SIZE, AppFragmentManager.fragmentMapContainer, new AdminHome(), AdminHome.TAG);
                                    break;
                                }
                                case R.id.employeesANI: {
                                    loadNavFragment("Employees",0, AppFragmentManager.fragmentContainer, new AdminEmployees(), AdminEmployees.TAG);
                                    break;
                                }
                                case R.id.worksANI: {
                                    loadNavFragment("Works",0, AppFragmentManager.fragmentContainer, new AdminWorks(), AdminWorks.TAG);
                                    break;
                                }
                                case R.id.profileANI: {
                                    loadNavFragment("Profile",0, AppFragmentManager.fragmentContainer, new AdminProfile(), AdminProfile.TAG);
                                    break;
                                }
                                case R.id.signOutANI: {
                                    signOutUser();
                                    break;
                                }
                            }
                        } else {
                            switch (menuItem.getItemId()) {
                                case R.id.homeCNI: {
                                    loadNavFragment("Courier Service", 15, AppFragmentManager.fragmentMapContainer, new EmployeeHome(), EmployeeHome.TAG);
                                    break;
                                }
                                case R.id.worksCNI: {
                                    loadNavFragment("Works", 0, AppFragmentManager.fragmentContainer, new EmployeeWorks(), EmployeeWorks.TAG);
                                    break;
                                }
                                case R.id.profileCNI: {
                                    loadNavFragment("Profile", 0, AppFragmentManager.fragmentContainer, new EmployeeProfile(), EmployeeProfile.TAG);
                                    break;
                                }
                                case R.id.signOutCNI: {
                                    signOutUser();
                                    break;
                                }
                            }
                        }
                    }
                }, 400);

                return false;
            }
        });

        if (savedInstanceState == null) {
            if (Vars.isUserAdmin) {
                loadNavFragment("Courier Service", TOOLBAR_MARGIN_SIZE, AppFragmentManager.fragmentMapContainer, new AdminHome(), AdminHome.TAG);
                navigationView.setCheckedItem(R.id.homeANI);
            } else {
                loadNavFragment("Courier Service", 15, AppFragmentManager.fragmentMapContainer, new EmployeeHome(), EmployeeHome.TAG);
                navigationView.setCheckedItem(R.id.homeCNI);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Vars.appDialog.create(MainActivity.instance, "Permission", "I need password to get your current location. I don't harm any of your private data and other stuff.",
                                    "Yes", "No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Vars.appDialog.dismiss();
                                            checkPermission();
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Vars.appDialog.dismiss();
                                            finish();
                                        }
                                    }).show();
                            return;
                        }
                    }
                }
                break;
            }
        }

        if (isServicesOk()) {
            if (checkPermission()) {
                finish();
                startActivity(getIntent());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (Vars.currentFragment.getTag().equals(AdminEmployeeWorks.TAG)) {
            AppFragmentManager.replace(MainActivity.instance, AppFragmentManager.fragmentContainer, new AdminWorks(), AdminWorks.TAG);
        } else if (Vars.currentFragment.getTag().equals(AdminProfileEdit.TAG)) {
            loadNavFragment("Profile",0, AppFragmentManager.fragmentContainer, new AdminProfile(), AdminProfile.TAG);
        } else if (Vars.currentFragment.getTag().equals(EmployeeProfileEdit.TAG)) {
            loadNavFragment("Profile",0, AppFragmentManager.fragmentContainer, new EmployeeProfile(), EmployeeProfile.TAG);
        } else if (!Vars.currentFragment.getTag().equals(AdminHome.TAG) && Vars.isUserAdmin) {
            loadNavFragment("Courier Service", TOOLBAR_MARGIN_SIZE, AppFragmentManager.fragmentMapContainer, new AdminHome(), AdminHome.TAG);
            navigationView.setCheckedItem(R.id.homeANI);
        } else if (!Vars.currentFragment.getTag().equals(EmployeeHome.TAG) && !Vars.isUserAdmin) {
            loadNavFragment("Courier Service", 0, AppFragmentManager.fragmentContainer, new EmployeeHome(), EmployeeHome.TAG);
            navigationView.setCheckedItem(R.id.homeCNI);
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);

            Map<String, Object> map = new HashMap<>();
            map.put(AFModel.latitude, 360);
            map.put(AFModel.longitude, 360);
            map.put(AFModel.state, AFModel.val_state_offline);

            @SuppressLint("HardwareIds")
            String deviceId = Vars.localDB.retriveStringValue(Vars.PREFS_DEVICE_UNIQUE_ID, Secure.getString(instance.getContentResolver(), Secure.ANDROID_ID));
            Vars.appFirebase.getDbStatusReference().child(Vars.appFirebase.getCurrentUserId()).child(deviceId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(instance, "You are now offline!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(Vars.APPTAG, "ExceptionCallback: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                }
            });

            super.onBackPressed();
        }
    }
}
