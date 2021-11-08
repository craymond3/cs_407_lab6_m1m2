package com.example.lab6milestone1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 24; // this number can be anything!!
    // 9. Let us define the mMap variable as well as another variable that can represent the latitude & longitude of the marker we want to create.
    private final LatLng mDestinationLatLngBascom = new LatLng(43.07579544680326, -89.40435578297969); // Bascom Hall
    private GoogleMap mMap;

    //M2
    private FusedLocationProviderClient mFusedLocationProviderClient; // save the instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Milestone 2 stuff (obtain a FisedLocationProvideClient)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //5. Next, make sure your Main Activity Java file is showing  the layout xml and retrieving the SupportMapFragment (the code to be added is highlighted in cyan for your convenience):
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById((R.id.fragment_map));
        /*
        8. Now we're going to go back to our Main Activity java file & work on displaying a marker/pin on the map.  To do this we need to ensure that:
        The MapFragment is downloaded and initialized.
        After this, we display a marker.
        Calling .getMapAsync(..) on the mapFragment will conveniently notify you when the mapFragment is initialized and is ready, so we can create our markers after the said notification as shown:
         */
        //In Step-8, we're saving the GoogleMap object we obtain in another variable (mMap) for later use.
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            //code to display marker
            /* 10. Creating the marker:
            To create a fresh new marker and add it to our map, we use the following call:
             and we can then just chain commands to customize the marker (chain starts at .position)*/
            //googleMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destination"));
            /*
            In the above call,  “.position”  determines where the marker is placed. The LatLng object we created in Step-9 will be used here to set the marker at that location.
            "Destination" is now the text that pops up when you tap on the marker. If nothing shows up on the map, make sure that the “Google Maps Android API v2” is enabled (see trouble shooting for more details).
             */
            // now for lab milestone change dst to bascom hall!
            googleMap.addMarker(new MarkerOptions().position(mDestinationLatLngBascom).title("Bascom Hall"));

            // dont call until after map is ready above from getMapAsync!
            displayMyLocation();
        });
    }

    private void displayMyLocation() {
        // check if permission granted
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        //if not ask for it
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        //if perm granted, display marker at current location
        else {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, task-> {
                Location mLastKnownLocation = task.getResult();
                if (task.isSuccessful() && mLastKnownLocation != null){
                    //make red marker for last known location
                    LatLng lastLocationLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(lastLocationLatLng).title("Device Last Location"));
                    //make polyline from last location to Bascom Hall
                    mMap.addPolyline(new PolylineOptions().add(lastLocationLatLng,mDestinationLatLngBascom));
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            //if req is cancelled, res array is empty
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayMyLocation();
            }
        }
    }

}