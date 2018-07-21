package service.courier.app.dmcx.courierservice.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Clients;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Home;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Profile;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.ClientHome;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Client.Works;
import service.courier.app.dmcx.courierservice.Fragment.Manager.AppFragmentManager;
import service.courier.app.dmcx.courierservice.Models.Admin;
import service.courier.app.dmcx.courierservice.Models.Client;
import service.courier.app.dmcx.courierservice.Variables.Vars;
import service.courier.app.dmcx.courierservice.R;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static MainActivity instance;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Animation aSlideUpToPositionFast;

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
        invalidateOptionsMenu();
        loadToolbarPosition(marginSize);
        AppFragmentManager.replace(MainActivity.instance, container, fragment, tag);
    }

    private void signOutUser() {
        if (Vars.isUserAdmin) {
            signOutAdmin();
        } else {
            signOutClient();
        }

        Vars.appFirebase.signOutUser();
        Vars.localDB.clearDB();
        Vars.reset();
        startAuthActivity();
    }

    private void signOutAdmin() {
        DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.admins).child(Vars.appFirebase.getCurrentUser().getUid());
        Map<String, Object> map = new HashMap<>();
        map.put(AFModel.status, AFModel.val_status_offline);
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

    private void signOutClient() {
        DatabaseReference reference = Vars.appFirebase.getDbReference().child(AFModel.users).child(AFModel.clients).child(Vars.appFirebase.getCurrentUser().getUid());
        Map<String, Object> map = new HashMap<>();
        map.put(AFModel.latitude, "");
        map.put(AFModel.longitude, "");
        map.put(AFModel.status, AFModel.val_status_offline);
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

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(instance, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (isUserAdmin) {
            loadNavHeader(AFModel.admins, Admin.class);
            navigationView.inflateMenu(R.menu.drawer_menu_admin);
        } else {
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
                                loadNavFragment("", 15, AppFragmentManager.fragmentMapContainer, new Home(), Home.TAG);
                                break;
                            }
                            case R.id.clientsANI: {
                                loadNavFragment("Clients",0, AppFragmentManager.fragmentContainer, new Clients(), Clients.TAG);
                                break;
                            }
                            case R.id.worksANI: {
                                Toast.makeText(MainActivity.instance, "ADMIN WORKS", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case R.id.profileANI: {
                                loadNavFragment("Profile",0, AppFragmentManager.fragmentContainer, new Profile(), Profile.TAG);
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
                            case R.id.workListCNI: {
                                loadNavFragment("Works", 0, AppFragmentManager.fragmentContainer, new Works(), Works.TAG);
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
                loadNavFragment("", 15, AppFragmentManager.fragmentMapContainer, new Home(), Home.TAG);
            } else {
                loadNavFragment("Home", 0, AppFragmentManager.fragmentContainer, new ClientHome(), ClientHome.TAG);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Vars.currentFragment != null) {
            if (Vars.currentFragment.getTag().equals(Home.TAG)) {
                getMenuInflater().inflate(R.menu.map_menu, menu);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
