package gr.chris.transportation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private Location lastKnownLocation;

    boolean locationPermissionGranted=true;

    GoogleMap map;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if(!databaseFile(this).exists()||databaseFile(this).length()==0){
            AssetManager am=getAssets();
            try {
                InputStream in=am.open("ROUTES.db");
                int size=in.available();
                Toast.makeText(this,String.valueOf(size),Toast.LENGTH_SHORT).show();
                byte[] buffer=new byte[size];
                in.read(buffer);
                in.close();

                File file=databaseFile(this);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(buffer);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> items=new ArrayList<String>();
        items.add("None");
        items.add("Normal");
        items.add("Satellite");
        items.add("Terrain");
        items.add("Hybrid");

        Spinner opts=(Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opts.setAdapter(adapter);
        opts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mMap!=null){
                    mMap.setMapType(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public static File databaseFile(Context paramContext)
    {
        File localFile2 = paramContext.getExternalFilesDir(null);
        File localFile1 = localFile2;
        if (localFile2 == null) {
            localFile1 = paramContext.getFilesDir();
        }
        return new File(localFile1, "ROUTES.db");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        map=googleMap;
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);


        File routes=databaseFile(this);
        mydb = new DBHelper(this,routes.toString());
        ArrayList<DBHelper.Station> route=mydb.getRoute("222");




        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng home=new LatLng(38.0144327,23.7559625);
        mMap.addMarker(new MarkerOptions().position(home).title("Αθανασίου Διάκου 22"));
        mMap.addCircle(new CircleOptions().center(home).radius(10000));
        PolygonOptions routeLine=new PolygonOptions();
        routeLine.clickable(true);
        routeLine.geodesic(true);

        routeLine.strokeColor(Color.GREEN);
        routeLine.strokeWidth(2);
        for(DBHelper.Station s:route){
            routeLine.add(new LatLng(s.lat,s.lon));
            MarkerOptions mop=new MarkerOptions();
            mop.position(new LatLng(s.lat,s.lon));
            mop.title(s.name);
            mop.snippet(s.sym);
            mop.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop));
            mMap.addMarker(mop);
        }

        mMap.addPolygon(routeLine);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(home,18.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity.this,marker.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        updateLocationUI(mMap);

        mMap.setTrafficEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng loc) {
                TextView tv=(TextView)findViewById(R.id.textView);
                StringBuilder sb=new StringBuilder();
                sb.append("Latitude:");
                sb.append(loc.latitude);
                sb.append("\n");
                sb.append("Longitude:");
                sb.append(loc.longitude);
                tv.setText(sb.toString());
            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                TextView tv=(TextView)findViewById(R.id.textView);
                tv.setText("Route:504");
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI(map);
    }

    private void updateLocationUI(GoogleMap map) {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /*private void getDeviceLocation(final GoogleMap map) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.

        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }*/

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}