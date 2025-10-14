package com.example.evcharging.view.stations;

import android.Manifest;
import android.content.Intent;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.evcharging.R;
import com.example.evcharging.model.ChargingStation;
import com.example.evcharging.view.main.MainActivity;
import com.example.evcharging.viewmodel.StationViewModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<ChargingStation> chargingStations = new ArrayList<>();
    private Map<Marker, ChargingStation> markerStationMap = new HashMap<>();
    private StationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations);
        
        // Hide action bar for full screen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(StationViewModel.class);

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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToMainActivity();
            }
        });

        // Load charging stations from API via ViewModel
        loadChargingStations();
    }

    private void initializeViews() {
        searchEditText = findViewById(R.id.search_edit_text);
        btnClearSearch = findViewById(R.id.btn_clear_search);
        stationsCountText = findViewById(R.id.text_stations_count);
        stationInfoCard = findViewById(R.id.station_info_card);
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> navigateToMainActivity());

        // My location button
        findViewById(R.id.btn_my_location).setOnClickListener(v -> {
            if (mMap != null) {
                getCurrentLocationAndMove();
            }
        });
        
        // Search container click
        findViewById(R.id.search_container).setOnClickListener(v -> searchEditText.requestFocus());
        
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

        // Add markers if stations are already loaded
        if (!chargingStations.isEmpty()) {
            addChargingStationMarkers();
        }
    }

    private void loadChargingStations() {
        viewModel.loadChargingStations(true, new StationViewModel.StationsCallback() {
            @Override
            public void onStationsLoaded(List<ChargingStation> stations) {
                chargingStations.clear();
                chargingStations.addAll(stations);

                // Add markers to map if it's ready
                if (mMap != null) {
                    addChargingStationMarkers();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(StationsActivity.this,
                    "Failed to load stations: " + errorMessage,
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addChargingStationMarkers() {
        allMarkers.clear();
        markerStationMap.clear();

        for (ChargingStation station : chargingStations) {
            LatLng position = new LatLng(station.getLatitude(), station.getLongitude());

            // Create snippet with availability info
            String snippet = (station.getAvailableSlots() > 0 ? "Available" : "Full") +
                           " • " + station.getStationType() + " Charging";

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(station.getName())
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                        station.getAvailableSlots() > 0 ?
                        BitmapDescriptorFactory.HUE_GREEN :
                        BitmapDescriptorFactory.HUE_RED));

            Marker marker = mMap.addMarker(markerOptions);
            if (marker != null) {
                allMarkers.add(marker);
                markerStationMap.put(marker, station);
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
            ChargingStation station = markerStationMap.get(marker);

            // Update station info
            TextView stationName = findViewById(R.id.text_station_name);
            TextView stationDistance = findViewById(R.id.text_station_distance);
            
            if (stationName != null && station != null) {
                stationName.setText(station.getName());
            }
            if (stationDistance != null && station != null) {
                String info = (station.getAvailableSlots() > 0 ? "Available" : "Full") +
                            " • " + station.getStationType() + " Charging";
                stationDistance.setText(info);
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

    private void navigateToMainActivity() {
        Intent intent = new Intent(StationsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
