package com.tanmay.orderfoodhere.view.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.tanmay.orderfoodhere.R;
import com.tanmay.orderfoodhere.location.GeocoderAPI;
import com.tanmay.orderfoodhere.location.PlacesAutoCompleteAdapter;
import com.tanmay.orderfoodhere.permissions.PermissionUtil;
import com.tanmay.orderfoodhere.utils.ConstantClass;
import com.tanmay.orderfoodhere.view.adapters.DrawerAdapter;
import com.tanmay.orderfoodhere.view.fragments.Desserts;
import com.tanmay.orderfoodhere.view.fragments.Mains;
import com.tanmay.orderfoodhere.view.fragments.SidesSalads;
import com.tanmay.orderfoodhere.view.interfaces.GeocoderListener;
import com.tanmay.orderfoodhere.view.interfaces.OnDrawerItemClickListener;

/**
 * Created by TaNMay on 5/31/2016.
 */

public class Dashboard extends AppCompatActivity implements OnDrawerItemClickListener,
        ViewPager.OnPageChangeListener, Mains.OnFragmentInteractionListener,
        Desserts.OnFragmentInteractionListener, SidesSalads.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GeocoderListener {

    public static String TAG = "Dashboard ==>";
    public static final String currentAddress = "CurrentAddress";
    public static final String locationCoordinates = "LocationCoordinates";
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_LOCATION = 1;

    Context context;

    Toolbar toolbar;
    DrawerLayout mDrawer;
    ViewPager mViewPager;
    FrameLayout drawerFrame;
    RecyclerView mRecyclerView;
    SmartTabLayout viewPagerTab;
    RelativeLayout entireLayout;
    AutoCompleteTextView autocompleteView;
    Location mLastLocation;
    GeocoderAPI geocoderAPI;
    GoogleApiClient mGoogleApiClient;

    RecyclerView.Adapter mAdapter;
    FragmentPagerItemAdapter adapter;

    LinearLayoutManager mLayoutManager;
    ActionBarDrawerToggle mDrawerToggle;

    int drawerWidth;
    String description;

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .enableAutoManage(this, this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        setContentView(R.layout.activity_dashboard);

        context = this;
        initView();
        setSupportActionBar(toolbar);

        geocoderAPI = new GeocoderAPI();
        GeocoderAPI.geocoderListener = Dashboard.this;

        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(context, R.layout.autocomplete_list_item));
        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Get data associated with the specified position
                // in the list (AdapterView)
                description = (String) parent.getItemAtPosition(position);
                hideSoftKeyboard(Dashboard.this);
                geocoderAPI.getCoords(context, description, locationCoordinates);
            }
        });

        drawerWidth = getResources().getDisplayMetrics().widthPixels;
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) drawerFrame.getLayoutParams();
        params.width = drawerWidth;
        drawerFrame.setLayoutParams(params);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerAdapter(this, ConstantClass.DRAWER, ConstantClass.DRAWER_ICONS);
        mRecyclerView.setAdapter(mAdapter);
        DrawerAdapter.click = Dashboard.this;
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Mains", Mains.class)
                .add("Sides/Salads", SidesSalads.class)
                .add("Desserts", Desserts.class)
                .create());

        mViewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(mViewPager);
        viewPagerTab.setOnPageChangeListener(this);
    }

    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.ad_toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.ad_recycler_view);
        mDrawer = (DrawerLayout) findViewById(R.id.ad_drawer_layout);
        drawerFrame = (FrameLayout) findViewById(R.id.drawer);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        autocompleteView = (AutoCompleteTextView) toolbar.findViewById(R.id.at_location);
        entireLayout = (RelativeLayout) findViewById(R.id.cd_entire_layout);
    }

    @Override
    public void onDrawerItemClick(int position) {
        switch (position - 1) {
            case 0:
                Toast.makeText(context, "Last Order.", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(context, "Contact Us.", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(context, "About Us.", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(context, "Logout.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, "...", Toast.LENGTH_SHORT).show();
                mDrawer.closeDrawers();
        }
    }

    @Override
    public void onDrawerCloseClick() {
        mDrawer.closeDrawers();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        if ((int) Build.VERSION.SDK_INT < 23) {
            Log.d(TAG, Build.VERSION.SDK_INT + " API Level");
            getLocation();
        } else {
            Log.d(TAG, Build.VERSION.SDK_INT + " API Level 23");
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        boolean requestPermission = PermissionUtil.requestLocationPermissions(this);
        if (requestPermission == true) {
            if (checkSelfPermission(PERMISSIONS_LOCATION[0]) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSIONS_LOCATION[1]) == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                // Display a SnackBar with an explanation and a button to trigger the request.
                ActivityCompat.requestPermissions(Dashboard.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
            }
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }

    public void getLocation() {
        Log.d(TAG, "On Connected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(TAG, "Latitude: " + mLastLocation.getLatitude() + "Longitude: " + mLastLocation.getLongitude());
            geocoderAPI.getAddress(context, mLastLocation.getLatitude(), mLastLocation.getLongitude(), currentAddress);
        } else {
            Log.d(TAG, "Current Lcoation: " + "null");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            Log.i(TAG, "Received response for permissions request.");
            // We have requested multiple permissions for location, so all of them need to be checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted.
                getLocation();
            } else {
                Log.i(TAG, "All permissions were NOT granted.");
                Snackbar.make(entireLayout, "All permissions were not granted. You cannot proceed.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "To proceed, enable permissions from Settings -> Apps -> Android Support " +
                                "-> Permissions", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "On Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "On Connection Failed");
    }

    @Override
    public void onAddressResponse(String label, String address) {
        if (label.equals(currentAddress)) {
            autocompleteView.setText(address);
        } else {
            Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCoordinateResponse(String label, Double lat, Double lng) {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
