package com.example.civiladvocacy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;

    private TextView currentLoc;

    private final List<Official> officialList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OfficialAdapter oAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        oAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(oAdapter);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        currentLoc = findViewById(R.id.location);

        if (hasNetworkConnection()) {
            mFusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this);
            determineLocation();
        }
        else {
            updateCurrentLocation(getString(R.string.no_data));
            showNoInternetDialog();
        }
    }

    @Override
    public void onClick(View v) {
        int posClicked = recyclerView.getChildLayoutPosition(v);
        Official o = officialList.get(posClicked);
        Intent intent = new Intent(this, OfficialActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable("OFFICIAL", o);
        extras.putString("LOCATION", currentLoc.getText().toString());
        intent.putExtras(extras);
        startActivity(intent);
    }

    private void determineLocation() {
        // Check perm - if not then start the  request and return
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some situations this can be null.
                    if (location != null) {
                        String zipcode = getPostalCode(location);
                        CivicInfoDownloader.downloadCivicInfo(this, zipcode);
                    }
                    else Log.d(TAG, "determineLocation: LOCATION IS NULL");
                })
                .addOnFailureListener(this, e ->
                        Toast.makeText(MainActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    showLocationDialog();
                }
            }
        }
    }
    private String getPostalCode(Location loc) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String postalCode = null;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            postalCode = addresses.get(0).getPostalCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return postalCode;
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.mainact_opt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.aboutMenuItem) {
            // launch about activity
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.locationMenuItem) {
            if (hasNetworkConnection()) {
                // show address dialog
                showLocationDialog();
            } else showNoInternetDialog();
        } else {
            Log.d(TAG, "onOptionsItemSelected: Unknown Menu Item: " + item.getTitle());
        }
        return super.onOptionsItemSelected(item);
    }

    public void showLocationDialog() {
        // Single input value dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);


        builder.setPositiveButton("OK", (dialog, id) -> {
            String location = et.getText().toString();
            CivicInfoDownloader.downloadCivicInfo(this, location);
        });

        builder.setNegativeButton("CANCEL", (dialog, id) -> {});

        builder.setTitle("Enter Address");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showNoInternetDialog() {
        // Simple dialog - ok button.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be accessed/loaded without an internet connection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateCurrentLocation(String loc) {
        currentLoc.setText(loc);
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public void updateOfficialList(ArrayList<Official> oList) {
        officialList.clear();
        officialList.addAll(oList);
        oAdapter.notifyDataSetChanged();
    }

    public void downloadFailed() {
        officialList.clear();
        oAdapter.notifyDataSetChanged();
        updateCurrentLocation(getString(R.string.no_data));
    }

}