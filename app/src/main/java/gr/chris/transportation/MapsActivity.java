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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import gr.chris.transportation.legacy.CAlertDialog;
import gr.chris.transportation.legacy.Engine;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private Location lastKnownLocation;

    boolean locationPermissionGranted=true;

    GoogleMap map;
    SQLEngine mydb;
    HashMap<String,RouteDrawing> visibleRoutes=new HashMap<String,RouteDrawing>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
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
        final int[] mapTypeIDs={GoogleMap.MAP_TYPE_HYBRID,GoogleMap.MAP_TYPE_SATELLITE,GoogleMap.MAP_TYPE_NORMAL,GoogleMap.MAP_TYPE_TERRAIN,GoogleMap.MAP_TYPE_NONE};
        String[] mapTypes={"Both","Satellite Image","Normal","Terrain","Nothing"};

        Spinner opts=(Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mapTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opts.setAdapter(adapter);
        opts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mMap!=null){
                    mMap.setMapType(mapTypeIDs[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void select(View v){

        ArrayList<Engine.Route> avaRoutes=mydb.getAllRoutes();
         new CAlertDialog(MapsActivity.this, null, avaRoutes, "OK", true,visibleRoutes, new CAlertDialog.Result() {
            @Override
            public void selected(String routeID,boolean addOrRemove) {
                ////////Toast.makeText(getApplicationContext(),routeID,Toast.LENGTH_SHORT).show();
                //showRoute(routeID);
                if(addOrRemove){
                    visibleRoutes.put(routeID,showRoute(routeID,true));
                }else{
                    RouteDrawing toRemove =visibleRoutes.get(routeID);
                    toRemove.polyline.remove();
                    for(Marker m:toRemove.marksArrayList){
                        m.remove();
                    }
                    visibleRoutes.remove(routeID);
                }
            }
        });

    }

    Polyline last=null;

    public int randomColor(){
        Random r=new Random();
        int[] sels={Color.WHITE,Color.YELLOW,Color.GREEN,Color.MAGENTA};
        //int color=Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255));
        return sels[r.nextInt(sels.length)];
    }

    public float pythDistance(double x1,double y1,double x2,double y2){
        Location one=new Location("One");
        one.setLatitude(x1);
        one.setLongitude(y1);
        Location two=new Location("two");
        two.setLatitude(x2);
        two.setLongitude(y2);
        return one.distanceTo(two);
    }

    ArrayList<Marker> nearbyMS=new ArrayList<Marker>();

    public void viewS(View v){
        float camZoom=map.getCameraPosition().zoom;
        float calc=(21/camZoom)*280;
        Log.v("ZOOM:",String.valueOf(map.getCameraPosition().zoom));
        float radius=calc;

        LatLng viewCoords=map.getCameraPosition().target;
        ArrayList<SQLEngine.Station> nearby=new ArrayList<SQLEngine.Station>();
        for(SQLEngine.Station s:mydb.getAllStations()){
            if(pythDistance(viewCoords.latitude,viewCoords.longitude,s.lat,s.lon)<radius){
                nearby.add(s);
                Log.v("Z2OM:",String.valueOf(pythDistance(viewCoords.latitude,viewCoords.longitude,s.lat,s.lon)));
            }
        }
        for(SQLEngine.Station s:nearby) {
            MarkerOptions mop = new MarkerOptions();
            mop.position(new LatLng(s.lat, s.lon));
            mop.title(s.name);
            mop.snippet(s.sym);
            mop.zIndex(0.5f);
            nearbyMS.add(map.addMarker(mop));
        }

    }


    public RouteDrawing showRoute(String routeId,boolean goToRoute){
        ArrayList<SQLEngine.Station> route=mydb.getRoute(routeId);
        RouteDrawing rd=new RouteDrawing();
        PolylineOptions routeLine=new PolylineOptions();
        routeLine.clickable(true);
        routeLine.geodesic(true);

        routeLine.color(randomColor());
        routeLine.width(5);
        for(SQLEngine.Station s:route) {
            routeLine.add(new LatLng(s.lat, s.lon));
            MarkerOptions mop = new MarkerOptions();
            mop.position(new LatLng(s.lat, s.lon));
            mop.title(s.name);
            mop.snippet(s.sym);
            mop.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop));
            mop.zIndex(0.5f);
            rd.marksArrayList.add(mMap.addMarker(mop));

        }






        if(last!=null) {
            last.remove();
        }

        if(goToRoute) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(route.get(0).gmloc, 18.0f));
        }
        rd.polyline=mMap.addPolyline(routeLine);
        rd.polylineID=rd.polyline.getId();
        Toast.makeText(this,rd.polyline.getId(),Toast.LENGTH_SHORT);
        Log.v("MESSAGE","ADDED POLYLINE ID:"+rd.polyline.getId());
        return rd;
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




    public void showTrains(){

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
        mMap.setMapType(mMap.MAP_TYPE_HYBRID);
        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.dark_theme));


        File routes=databaseFile(this);
        mydb = new SQLEngine(this,routes.toString());


        int init=mydb.getM1id();
        int[] colors={Color.GREEN,Color.RED,Color.BLUE};
        for(int g=init;g<init+5;g+=2) {
            RouteDrawing pre=new RouteDrawing();
            ArrayList<SQLEngine.Station> route = mydb.getRoute(String.valueOf(g));


            PolylineOptions routeLine = new PolylineOptions();
            routeLine.clickable(true);
            routeLine.geodesic(true);
            routeLine.color(colors[(g-init)/2]);
            routeLine.width(20);

            for (SQLEngine.Station s : route) {
                routeLine.add(new LatLng(s.lat, s.lon));
                MarkerOptions mop = new MarkerOptions();
                mop.position(new LatLng(s.lat, s.lon));
                mop.title(s.name);
                mop.snippet(s.sym);
                mop.zIndex(0.2f);
                mop.icon(BitmapDescriptorFactory.fromResource(R.drawable.trainsm));
                pre.marksArrayList.add(mMap.addMarker(mop));
            }
            pre.polyline=mMap.addPolyline(routeLine);
            pre.polylineID=pre.polyline.getId();
            visibleRoutes.put(String.valueOf(g),pre);

        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.1255073,23.7637454),18.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Toast.makeText(MapsActivity.this,marker.getTitle(), Toast.LENGTH_SHORT).show();
              return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                ArrayList<Engine.Route> avaRoutes=mydb.getRoutes(marker.getSnippet());
                new CAlertDialog(MapsActivity.this,marker.getTitle(),"",null,avaRoutes,"OK",false,mydb,new CAlertDialog.Result(){

                    @Override
                    public void selected(String routeID, boolean addOrRemove) {
                        visibleRoutes.put(routeID,showRoute(routeID,false));
                    }
                });
                if(!nearbyMS.isEmpty()){
                    for(Marker m:nearbyMS){
                        m.remove();
                    }
                    nearbyMS.clear();
                }

            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Toast.makeText(getApplicationContext(),polyline.getId(),Toast.LENGTH_SHORT);
                Log.v("MESSAGE","CLICKED POLYLINE ID:"+polyline.getId());
                ArrayList<Marker> fin=null;
                String routeID=null;
                for(Map.Entry<String,RouteDrawing> g: visibleRoutes.entrySet()) {
                    if (g.getValue().polylineID.equals(polyline.getId())) {
                        routeID = g.getKey();
                        fin = g.getValue().marksArrayList;
                        Log.v("MESSAGE", "Found Polyline ID" + polyline.getId());
                        break;
                    }
                }

                ArrayList<String> markNames=new ArrayList<String>();
                for(Marker m:fin){
                    markNames.add(m.getTitle());
                }
                Engine.Route res=mydb.getRouteFromID(routeID);
                new CAlertDialog(MapsActivity.this,getString(R.string.route)+" "+res.busId+"-"+res.routeName,fin,mMap);
            }
        });



        updateLocationUI(mMap);

        mMap.setTrafficEnabled(false);

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