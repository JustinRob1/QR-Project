package com.example.qr_project.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.qr_project.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    FirebaseFirestore db;

    /**
     * The MapActivity allows user to interact with the map
     * The geolocation of the map can be stored on the FireStore database as the user wishes
     * The user has a choice to reveal their location or not.
     * Extend to the part that the user can even choose to reveal their location once
     * The user has a choice and ability to chance the setting of their location reveal preferences
     * The QRCodes scanned within a location; and that location can be added to the database
     * The location also works with the QRCode
     * The connection to the database allows user to check for the geolocation of the QRCodes later
     * on the app if they want to see
     * The geolocation also allows the user to check for their friends' QRCodes and where the QRCodes
     * have scanned at the exact location on the geolocation map
     * @param savedInstanceState
     * @see FirebaseFirestore
     * @see QRCodeActivity
     * @see PictureActivity
     * @see UserHomeActivity
     * @see UserProfileActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(location.getLatitude(), location.getLongitude()));
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
                                // Use Bitmap and Picasso for the location app
                                // Use the image of the location and the image of the photo
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



        // Initialize search bar
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * This Query is to get the location information from the search query
             * This also gets the exact address of the geolocation of the user when opening the QRCodes
             * This too gets the exact address of the geolocation of the scanned QRCodes
             * Getting the exact address through its longitude and latitude
             * @param query
             * @return true
             * @return
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Hide the keyboard after search query submitted
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                // Use Geocoder to get location information from search query
                Geocoder geocoder = new Geocoder(MapActivity.this);
                try {
                    List<Address> addresses = geocoder.getFromLocationName(query, 1);
                    if (!addresses.isEmpty()) {
                        // Getting the address of the geolocation
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    } else {
                        Toast.makeText(MapActivity.this, "No results found for \"" + query + "\"", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    @Override
    /**
     * This functionality checks for the permission to access user's location implemented with googleMap
     * Set the map type to hybrid
     * Check if the user has granted location permission
     * Request location permission
     * Show the user's current location on the map
     * @param googleMap
     */
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
    /**
     * Show the current location of the user
     * Get the permission from the user on their location
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Show the user's current location on the map
            showCurrentLocation();
        }
    }
}
