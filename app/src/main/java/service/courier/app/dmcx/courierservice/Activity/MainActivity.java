package service.courier.app.dmcx.courierservice.Activity;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Clients;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Home;
import service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Profile;
import service.courier.app.dmcx.courierservice.Fragment.Manager.AppFragmentManager;
import service.courier.app.dmcx.courierservice.R;
import service.courier.app.dmcx.courierservice.Variables.Vars;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private boolean isUserAdmin;

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
        Vars.appFirebase.signOutUser();
        Vars.reset();
        startAuthActivity();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        Vars.appFirebase = new AppFirebase();
        isUserAdmin = Vars.isUserAdmin;

        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(instance, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (isUserAdmin)
            navigationView.inflateMenu(R.menu.drawer_menu_admin);
        else
            navigationView.inflateMenu(R.menu.drawer_menu_client);

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
                            case R.id.profileANI: {
                                loadNavFragment("Profile",0, AppFragmentManager.fragmentContainer, new Profile(), Profile.TAG);
                                break;
                            }
                            case R.id.signOutANI: {
                                signOutUser();
                                break;
                            }

                            case R.id.homeCNI: {
                                Toast.makeText(MainActivity.instance, "CLIENT HOME", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case R.id.workListCNI: {
                                Toast.makeText(MainActivity.instance, "CLIENT WORK", Toast.LENGTH_SHORT).show();
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
                loadToolbarPosition(15);
                AppFragmentManager.replace(MainActivity.instance, AppFragmentManager.fragmentMapContainer, new Home(), Home.TAG);
                navigationView.setCheckedItem(R.id.homeANI);
            } else {
                loadToolbarPosition(0);
                Toast.makeText(MainActivity.instance, "CLIENT HOME", Toast.LENGTH_SHORT).show();
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
