package service.courier.app.dmcx.courierservice.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import service.courier.app.dmcx.courierservice.Firebase.AFModel;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminClientWorks;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminClients;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminHome;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminProfile;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.AdminWorks;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.ClientHome;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.ClientWorks;
import service.courier.app.dmcx.courierservice.Fragment.Manager.AppFragmentManager;
import service.courier.app.dmcx.courierservice.Models.Admin;
import service.courier.app.dmcx.courierservice.Models.Client;
import service.courier.app.dmcx.courierservice.Variables.Vars;
import service.courier.app.dmcx.courierservice.R;

public class MainActivity extends AppCompatActivity {

    private static final int ERROR_DIALOGREQUEST = 9001;
    private static final int LOCATION_PERMISSION_CODE = 1234;
    private static final int TOOLBAR_MARGIN_SIZE = 15;

    @SuppressLint("StaticFieldLeak")
    public static MainActivity instance;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FusedLocationProviderClient clientFusedLocationProviderClient;
    private LocationRequest clientLocationRequest;
    private LocationCallback clientLocationCallback;

    private Animation aSlideUpToPositionFast;

    // Checker
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
            ActivityCompat.requestPermissions(instance, permissions, LOCATION_PERMISSION_CODE);
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
        getSupportActionBar().setTitle(title);
        loadToolbarPosition(marginSize);
        AppFragmentManager.replace(MainActivity.instance, container, fragment, tag);
    }

    private void signOutUser() {
        if (Vars.isUserAdmin) {
            Map<String, Object> map = new HashMap<>();
            map.put(AFModel.status, AFModel.val_status_offline);
            signOutMethod(AFModel.admins, map);
        } else {
            clientFusedLocationProviderClient.removeLocationUpdates(clientLocationCallback);

            Map<String, Object> map = new HashMap<>();
            map.put(AFModel.latitude, "");
            map.put(AFModel.longitude, "");
            map.put(AFModel.status, AFModel.val_status_offline);
            signOutMethod(AFModel.clients, map);
        }

        Vars.appFirebase.signOutUser();
        Vars.localDB.clearDB();
        Vars.reset();
        startAuthActivity();
    }

    private void signOutMethod(String user, Map<String, Object> map) {
        DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(user).child(Vars.appFirebase.getCurrentUser().getUid());
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(instance, "You are now offline!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(Vars.APPTAG, "ExceptionCallback: " + task.getException().getMessage());
                }
            }
        });
    }

    private void loadNavHeader(String clild, final Class object) {
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
                        developerName.setText(admin.getName());
                        developerMail.setText(Vars.appFirebase.getCurrentUser().getEmail());
                    } else if (classObject instanceof Client) {
                        Client client = (Client) classObject;
                        developerName.setText(client.getName());
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

    private void clientFusedLocationInit() {
        clientLocationRequest = new LocationRequest();
        clientLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        clientLocationRequest.setInterval(5000);
        clientLocationRequest.setFastestInterval(3000);
        clientLocationRequest.setSmallestDisplacement(10);

        clientLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();

                if (location != null) {
                    Log.d(Vars.APPTAG, "getDeviceLocation: location found.");

                    DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.clients).child(Vars.appFirebase.getCurrentUser().getUid());
                    Map<String, Object>  map = new HashMap<>();
                    map.put(AFModel.latitude, String.valueOf(location.getLatitude()));
                    map.put(AFModel.longitude, String.valueOf(location.getLongitude()));
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
        };

        clientFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.instance);
        if (checkPermission()) {
            clientFusedLocationProviderClient.requestLocationUpdates(clientLocationRequest, clientLocationCallback, Looper.myLooper());
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        Vars.appFirebase = new AppFirebase();
        boolean isUserAdmin = Vars.isUserAdmin;

        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(instance, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (isUserAdmin) {
            loadNavHeader(AFModel.admins, Admin.class);
            navigationView.inflateMenu(R.menu.drawer_menu_admin);
        } else {
            if (isServiceOk()) {
                if (checkPermission()) {
                    clientFusedLocationInit();
                }
            }

            loadNavHeader(AFModel.clients, Client.class);
            navigationView.inflateMenu(R.menu.drawer_menu_client);
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
                        switch (menuItem.getItemId()) {
                            case R.id.homeANI: {
                                loadNavFragment("Courier Service", TOOLBAR_MARGIN_SIZE, AppFragmentManager.fragmentMapContainer, new AdminHome(), AdminHome.TAG);
                                break;
                            }
                            case R.id.clientsANI: {
                                loadNavFragment("Clients",0, AppFragmentManager.fragmentContainer, new AdminClients(), AdminClients.TAG);
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

                            case R.id.homeCNI: {
                                loadNavFragment("Home", 0, AppFragmentManager.fragmentContainer, new ClientHome(), ClientHome.TAG);
                                break;
                            }
                            case R.id.worksCNI: {
                                loadNavFragment("Works", 0, AppFragmentManager.fragmentContainer, new ClientWorks(), ClientWorks.TAG);
                                break;
                            }
                            case R.id.profileCNI: {
                                Toast.makeText(MainActivity.instance, "CLIENT PROFILE", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case R.id.signOutCNI: {
                                signOutUser();
                                break;
                            }
                        }
                    }
                }, 400);

                return false;
            }
        });

        if (savedInstanceState == null) {
            if (isUserAdmin) {
                loadNavFragment("Courier Service", TOOLBAR_MARGIN_SIZE, AppFragmentManager.fragmentMapContainer, new AdminHome(), AdminHome.TAG);
                navigationView.setCheckedItem(R.id.homeANI);
            } else {
                loadNavFragment("Home", 0, AppFragmentManager.fragmentContainer, new ClientHome(), ClientHome.TAG);
                navigationView.setCheckedItem(R.id.homeCNI);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (Vars.currentFragment.getTag().equals(AdminClientWorks.TAG)) {
            AppFragmentManager.replace(MainActivity.instance, AppFragmentManager.fragmentContainer, new AdminWorks(), AdminWorks.TAG);
        } else if (!Vars.currentFragment.getTag().equals(AdminHome.TAG) && Vars.isUserAdmin) {
            loadNavFragment("Courier Service", TOOLBAR_MARGIN_SIZE, AppFragmentManager.fragmentMapContainer, new AdminHome(), AdminHome.TAG);
            navigationView.setCheckedItem(R.id.homeANI);
        } else if (!Vars.currentFragment.getTag().equals(ClientHome.TAG) && !Vars.isUserAdmin) {
            loadNavFragment("Home", 0, AppFragmentManager.fragmentContainer, new ClientHome(), ClientHome.TAG);
            navigationView.setCheckedItem(R.id.homeCNI);
        } else {
            super.onBackPressed();
        }
    }
}
