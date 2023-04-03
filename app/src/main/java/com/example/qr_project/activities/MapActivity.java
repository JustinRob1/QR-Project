package com.example.qr_project.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.qr_project.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    FirebaseFirestore db;

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Places.initialize(getApplicationContext(), "AIzaSyBUux3nV7NYGBVtaRY4ZFmyppzqAm40zLU");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Set the fields to return for the autocomplete results
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the selected place
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // Get the location of the selected place
                LatLng latLng = place.getLatLng();

                // Move the camera to the selected location
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }

            @Override
            public void onError(Status status) {
                // Handle the error
            }
        });

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        db = FirebaseFirestore.getInstance();

        CollectionReference userRef = db.collection("users");
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();
                    List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) data.get("qrcodes");
                    if (qrCodes != null) {
                        for (Map<String, Object> qrCode : qrCodes) {
                            GeoPoint location = null;
                            Object locationObject = qrCode.get("location");
                            if (locationObject instanceof GeoPoint) {
                                location = (GeoPoint) locationObject;
                            }
                            if (location != null) {
                                // Add a small random offset to the latitude and longitude
                                double lat = location.getLatitude() + Math.random() * 0.00001;
                                double lng = location.getLongitude() + Math.random() * 0.00001;
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(lat, lng));
                                Marker marker = mMap.addMarker(markerOptions);
                                // Store the qrCode object as a tag of the marker
                                marker.setTag(qrCode);
                            }

                        }
                    }
                }

                // Set click listener for the marker
                mMap.setOnMarkerClickListener(clickedMarker -> {
                    Object tag = clickedMarker.getTag();
                    if (tag != null && tag instanceof Map) {
                        Map<String, Object> qrCode = (Map<String, Object>) tag;
                        String photoUrl = qrCode.get("photo") != null ? qrCode.get("photo").toString() : null;
                        // Show the custom view in an AlertDialog or other dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                        View markerView = getLayoutInflater().inflate(R.layout.marker_layout, null);
                        ImageView imageView = markerView.findViewById(R.id.marker_image);
                        TextView textView = markerView.findViewById(R.id.marker_title);
                        if (photoUrl != null) {
                            // Download the photo using Picasso and convert it to a Bitmap
                            Picasso.get().load(photoUrl).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    imageView.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                    Log.e("My Tag", "Error downloading photo", e);
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    // Do nothing
                                }
                            });
                        } else {
                            // Hide the image view if the photo is null
                            imageView.setVisibility(View.GONE);
                        }
                        textView.setText(Objects.requireNonNull(qrCode.get("name")).toString());
                        builder.setView(markerView);
                        builder.create().show();
                    }
                    return false;
                });

            } else {
                Log.d("My Tag", "Error getting documents: ", task.getException());
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set the map type to hybrid
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Check if the user has granted location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            // Show the user's current location on the map
            showCurrentLocation();
        }

        // Check if there are extras for latitude and longitude
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("latitude") && extras.containsKey("longitude")) {
            // Move the camera to the QR code's location
            double latitude = extras.getDouble("latitude");
            double longitude = extras.getDouble("longitude");
            LatLng qrCodeLocation = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(qrCodeLocation, 15));
        }
    }


    private void showCurrentLocation() {
        if (mMap == null) {
            return;
        }

        // Check if the user has granted location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Get the user's last known location
        Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Add a marker at the user's current location
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("You are here"));

            // Move the camera to the user's current location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Show the user's current location on the map
            showCurrentLocation();
        }
    }
}