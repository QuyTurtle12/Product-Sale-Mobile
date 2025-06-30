package com.example.product_sale_app.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;
import java.util.Arrays;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.product_sale_app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class StoreMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private LatLng nearestStore;
    private Button btnDirections;

    // 1) List all your store locations here
    private final List<LatLng> STORE_LOCATIONS = Arrays.asList(
            new LatLng(10.84140157104944, 106.80991518650657),
            new LatLng(10.762622, 106.660172),
            new LatLng(10.780000, 106.700000)
    );

    private final ActivityResultLauncher<String> requestLocationPerm =
            registerForActivityResult(new RequestPermission(), granted -> {
                if (granted) initLocation();
                else Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnDirections      = findViewById(R.id.btnDirections);
        btnDirections.setOnClickListener(v -> launchDirections());

        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        // Pin *all* stores
        for (LatLng store : STORE_LOCATIONS) {
            map.addMarker(new MarkerOptions()
                    .position(store)
                    .title("Store: " + store.latitude + ", " + store.longitude));
        }

        // Zoom to first store by default
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(STORE_LOCATIONS.get(0), 13f));

        // Ask for permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPerm.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            initLocation();
        }
    }

    private void initLocation() {
        if (map == null) return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(loc -> {
                    if (loc != null) {
                        currentLocation = loc;
                        updateNearestStore();   // <-- compute closest store
                    } else {
                        Toast.makeText(this,
                                "Could not get current location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Could not get current location", Toast.LENGTH_SHORT).show()
                );
    }

    // 2) Find the nearest store
    private void updateNearestStore() {
        float minDist = Float.MAX_VALUE;
        for (LatLng store : STORE_LOCATIONS) {
            float[] result = new float[1];
            Location.distanceBetween(
                    currentLocation.getLatitude(), currentLocation.getLongitude(),
                    store.latitude, store.longitude,
                    result
            );
            if (result[0] < minDist) {
                minDist = result[0];
                nearestStore = store;
            }
        }

        // Optional: adjust camera to show user + store
        LatLng userLatLng = new LatLng(
                currentLocation.getLatitude(),
                currentLocation.getLongitude()
        );
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(userLatLng)
                .include(nearestStore)
                .build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    // 3) Launch directions to that nearest store
    private void launchDirections() {
        if (currentLocation == null || nearestStore == null) {
            Toast.makeText(this, "Waiting for location…", Toast.LENGTH_SHORT).show();
            return;
        }

        String uri = String.format(
                "https://www.google.com/maps/dir/?api=1" +
                        "&origin=%f,%f" +
                        "&destination=%f,%f" +
                        "&travelmode=driving",
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                nearestStore.latitude,
                nearestStore.longitude
        );

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        }
    }
}
