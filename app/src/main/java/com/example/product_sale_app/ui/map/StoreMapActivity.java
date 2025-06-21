package com.example.product_sale_app.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.MarkerOptions;

public class StoreMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final LatLng STORE_LATLNG = new LatLng(10.84140157104944, 106.80991518650657); // FPT University HCM
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private Button btnDirections;

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

        // 1) Pin the store
        map.addMarker(new MarkerOptions()
                .position(STORE_LATLNG)
                .title("Our Store, HCM City"));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(STORE_LATLNG, 15f));

        // 2) Ask for location permission
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
        // show blue dot
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(loc -> {
                    if (loc != null) {
                        currentLocation = loc;
                        // optionally move camera to user:
                        // map.animateCamera(CameraUpdateFactory.newLatLng(
                        //     new LatLng(loc.getLatitude(), loc.getLongitude())
                        // ));
                    } else {
                        Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show()
                );
    }

    private void launchDirections() {
        if (currentLocation == null) {
            Toast.makeText(this, "Waiting for location…", Toast.LENGTH_SHORT).show();
            return;
        }

        String uri = String.format(
                "https://www.google.com/maps/dir/?api=1" +
                        "&origin=%f,%f" +
                        "&destination=%f,%f" +
                        "&travelmode=driving",
                currentLocation.getLatitude(), currentLocation.getLongitude(),
                STORE_LATLNG.latitude,      STORE_LATLNG.longitude);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // fallback
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        }
    }
}
