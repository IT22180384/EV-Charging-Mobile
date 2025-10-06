package com.example.evcharging.view.stations;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.evcharging.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class StationsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText searchEditText;
    private ImageView btnClearSearch;
    private TextView stationsCountText;
    private View stationInfoCard;
    private List<Marker> allMarkers = new ArrayList<>();
    private Marker selectedMarker;

    // Expanded list of EV charging stations in Sri Lanka
    private final LatLng[] chargingStations = {
        // Colombo area
        new LatLng(6.9271, 79.8612), // Colombo City Center
        new LatLng(6.9319, 79.8478), // Kollupitiya Station
        new LatLng(6.8649, 79.8997), // Rajagiriya
        new LatLng(6.9023, 79.8607), // Bambalapitiya
        new LatLng(6.9147, 79.9729), // Kotte
        new LatLng(6.9534, 79.8606), // Pettah
        new LatLng(6.8782, 79.8792), // Nugegoda
        new LatLng(6.9497, 79.8500), // Fort
        new LatLng(6.8905, 79.8569), // Wellawatte
        new LatLng(6.8623, 79.9200), // Maharagama
        new LatLng(6.9148, 79.8731), // Dehiwala
        new LatLng(6.9388, 79.8542), // Cinnamon Gardens
        new LatLng(6.8448, 79.9553), // Boralesgamuwa
        new LatLng(6.9695, 79.9220), // Kotahena
        new LatLng(6.8293, 79.9720), // Homagama
        
        // Gampaha area
        new LatLng(7.0906, 79.9999), // Gampaha
        new LatLng(7.1554, 79.8987), // Negombo
        new LatLng(7.0732, 80.0074), // Kiribathgoda
        new LatLng(7.1667, 79.8833), // Katunayake
        new LatLng(7.2167, 79.8833), // Minuwangoda
        
        // Kalutara area
        new LatLng(6.5854, 79.9607), // Kalutara
        new LatLng(6.6431, 79.9969), // Panadura
        new LatLng(6.7279, 79.8912), // Moratuwa
        
        // Kandy area
        new LatLng(7.2906, 80.6337), // Kandy
        new LatLng(7.3319, 80.6350), // Peradeniya
        
        // Galle area
        new LatLng(6.0329, 80.2168), // Galle
        new LatLng(5.9549, 80.5550), // Matara
        
        // Other major cities
        new LatLng(8.3114, 80.4037), // Anuradhapura
        new LatLng(7.9520, 81.0306), // Polonnaruwa
        new LatLng(6.0535, 81.2152)  // Hambantota
    };

    private final String[] stationNames = {
        "Colombo City Center", "Kollupitiya Station", "Rajagiriya Hub", "Bambalapitiya Point", 
        "Kotte Super Charger", "Pettah Express", "Nugegoda Fast Charge", "Fort Main Station",
        "Wellawatte Quick Charge", "Maharagama Central", "Dehiwala Station", "Cinnamon Gardens Hub",
        "Boralesgamuwa Point", "Kotahena Express", "Homagama Station", "Gampaha Hub",
        "Negombo Airport Station", "Kiribathgoda Point", "Katunayake Express", "Minuwangoda Station",
        "Kalutara South", "Panadura Central", "Moratuwa Tech Hub", "Kandy Central",
        "Peradeniya University", "Galle Heritage", "Matara South", "Anuradhapura Ancient",
        "Polonnaruwa Historic", "Hambantota Port"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations);
        
        // Hide action bar for full screen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // Initialize views
        initializeViews();
        
        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        
        setupClickListeners();
        setupSearch();
    }

    private void initializeViews() {
        searchEditText = findViewById(R.id.search_edit_text);
        btnClearSearch = findViewById(R.id.btn_clear_search);
        stationsCountText = findViewById(R.id.text_stations_count);
        stationInfoCard = findViewById(R.id.station_info_card);
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            onBackPressed();
        });
        
        // My location button
        findViewById(R.id.btn_my_location).setOnClickListener(v -> {
            if (mMap != null) {
                getCurrentLocationAndMove();
            }
        });
        
        // Search container click
        findViewById(R.id.search_container).setOnClickListener(v -> {
            searchEditText.requestFocus();
        });
        
        // Clear search button
        btnClearSearch.setOnClickListener(v -> {
            searchEditText.setText("");
            btnClearSearch.setVisibility(View.GONE);
            showAllMarkers();
        });
        
        // Station info card actions
        findViewById(R.id.btn_directions).setOnClickListener(v -> {
            Toast.makeText(this, "Opening directions", Toast.LENGTH_SHORT).show();
        });
        
        findViewById(R.id.btn_reserve).setOnClickListener(v -> {
            Toast.makeText(this, "Reserving station", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnClearSearch.setVisibility(View.VISIBLE);
                    filterStations(s.toString());
                } else {
                    btnClearSearch.setVisibility(View.GONE);
                    showAllMarkers();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        // Apply modern map style
        try {
            boolean success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) {
                // Use default style if custom style fails
            }
        } catch (Exception e) {
            // Use default style
        }
        
        // Configure map UI
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        
        // Add charging station markers
        addChargingStationMarkers();
        
        // Set default location to Colombo, Sri Lanka
        LatLng colombo = new LatLng(6.9271, 79.8612);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombo, 10));
        
        // Enable my location
        enableMyLocation();
        
        // Set marker click listener
        mMap.setOnMarkerClickListener(marker -> {
            if (marker.getTitle() != null && !marker.getTitle().equals("Your Location")) {
                selectedMarker = marker;
                showStationInfo(marker);
            }
            return false;
        });
        
        // Hide station info when map is clicked
        mMap.setOnMapClickListener(latLng -> {
            hideStationInfo();
        });
    }

    private void addChargingStationMarkers() {
        allMarkers.clear();
        for (int i = 0; i < chargingStations.length; i++) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(chargingStations[i])
                    .title(stationNames[i])
                    .snippet("Available • Fast Charging")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            
            Marker marker = mMap.addMarker(markerOptions);
            if (marker != null) {
                allMarkers.add(marker);
            }
        }
        updateStationCount();
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            
            mMap.setMyLocationEnabled(true);
            getCurrentLocationAndMove();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getCurrentLocationAndMove() {
        if (ActivityCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                                
                                // Add a marker for current location
                                mMap.clear();
                                addChargingStationMarkers();
                                mMap.addMarker(new MarkerOptions()
                                        .position(currentLocation)
                                        .title("Your Location")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            } else {
                                Toast.makeText(StationsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void filterStations(String query) {
        query = query.toLowerCase().trim();
        int visibleCount = 0;
        
        for (Marker marker : allMarkers) {
            if (marker.getTitle() != null && 
                marker.getTitle().toLowerCase().contains(query)) {
                marker.setVisible(true);
                visibleCount++;
            } else {
                marker.setVisible(false);
            }
        }
        
        updateStationCount(visibleCount);
    }

    private void showAllMarkers() {
        for (Marker marker : allMarkers) {
            marker.setVisible(true);
        }
        updateStationCount();
    }

    private void updateStationCount() {
        updateStationCount(allMarkers.size());
    }

    private void updateStationCount(int count) {
        if (stationsCountText != null) {
            stationsCountText.setText(count + " stations");
        }
    }

    private void showStationInfo(Marker marker) {
        if (stationInfoCard != null) {
            // Update station info
            TextView stationName = findViewById(R.id.text_station_name);
            TextView stationDistance = findViewById(R.id.text_station_distance);
            
            if (stationName != null) {
                stationName.setText(marker.getTitle());
            }
            if (stationDistance != null) {
                stationDistance.setText("Available • Fast Charging");
            }
            
            // Show the card
            stationInfoCard.setVisibility(View.VISIBLE);
        }
    }

    private void hideStationInfo() {
        if (stationInfoCard != null) {
            stationInfoCard.setVisibility(View.GONE);
        }
        selectedMarker = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}