package com.example.product_sale_app.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import java.util.Locale;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.product_sale_app.R;
import com.example.product_sale_app.model.BaseResponseModel;
import com.example.product_sale_app.model.StoreLocationDto;
import com.example.product_sale_app.network.RetrofitClient;
import com.example.product_sale_app.network.service.StoreApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private LatLng nearestStore;
    private Button btnDirections;
    private final List<LatLng> storeLocations = new ArrayList<>();

    private final ActivityResultLauncher<String> requestLocationPerm =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) initLocation();
                else Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnDirections = findViewById(R.id.btnDirections);
        btnDirections.setOnClickListener(v -> launchDirections());

        // Use RetrofitClient for fetching stores
        StoreApiService storeApi = RetrofitClient.getRetrofitInstance()
                .create(StoreApiService.class);

        storeApi.getAllLocations().enqueue(new Callback<BaseResponseModel<List<StoreLocationDto>>>() {
            @Override
            public void onResponse(Call<BaseResponseModel<List<StoreLocationDto>>> call,
                                   Response<BaseResponseModel<List<StoreLocationDto>>> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(StoreMapActivity.this,
                            "Failed to load stores: HTTP " + resp.code(),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                for (StoreLocationDto dto : resp.body().getData()) {
                    storeLocations.add(new LatLng(dto.getLatitude(), dto.getLongitude()));
                }

                SupportMapFragment mapFrag = (SupportMapFragment)
                        getSupportFragmentManager().findFragmentById(R.id.map);
                mapFrag.getMapAsync(StoreMapActivity.this);
            }
            @Override
            public void onFailure(Call<BaseResponseModel<List<StoreLocationDto>>> call, Throwable t) {
                Toast.makeText(StoreMapActivity.this,
                        "Error loading stores: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        for (LatLng store : storeLocations) {
            map.addMarker(new MarkerOptions()
                    .position(store)
                    .title("Store: " + store.latitude + ", " + store.longitude));
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocations.get(0), 13f));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPerm.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            initLocation();
        }
    }

    private void initLocation() {
        if (map == null) return;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(loc -> {
                    if (loc != null) {
                        currentLocation = loc;
                        updateNearestStore();
                    } else Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show());
    }

    private void updateNearestStore() {
        if (currentLocation == null || storeLocations.isEmpty()) return;

        LatLng userLatLng = new LatLng(
                currentLocation.getLatitude(),
                currentLocation.getLongitude()
        );

        double minDist = Double.MAX_VALUE;
        LatLng bestStore = null;

        for (LatLng store : storeLocations) {
            // compute distance in meters
            float[] result = new float[1];
            Location.distanceBetween(
                    userLatLng.latitude, userLatLng.longitude,
                    store.latitude,       store.longitude,
                    result
            );
            double dist = result[0];

            Log.d("StoreMap", String.format(
                    Locale.US,
                    "Store @ %.6f,%.6f → %.1fm",
                    store.latitude, store.longitude, dist
            ));

            if (dist < minDist) {
                minDist = dist;
                bestStore = store;
            }
        }

        if (bestStore == null) return;
        nearestStore = bestStore;
        Log.d("StoreMap", String.format(
                Locale.US,
                "Nearest store is @ %.6f,%.6f (%.1fm away)",
                nearestStore.latitude, nearestStore.longitude, minDist
        ));

        // redraw markers, highlighting the nearest
        map.clear();
        for (LatLng store : storeLocations) {
            boolean isNearest = store.equals(nearestStore);
            map.addMarker(new MarkerOptions()
                    .position(store)
                    .title(isNearest ? "Nearest Store" : "Store")
                    .icon(
                            isNearest
                                    ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                                    : BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
            );
        }

        // zoom to include user + nearest store
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(userLatLng)
                .include(nearestStore)
                .build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }


    private void launchDirections() {
        if (currentLocation == null || nearestStore == null) {
            Toast.makeText(this, "Waiting for location…", Toast.LENGTH_SHORT).show();
            return;
        }

        String uri = String.format(Locale.US,
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
    }}
