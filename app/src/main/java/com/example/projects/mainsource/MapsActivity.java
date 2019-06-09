package com.example.projects.mainsource;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,View.OnClickListener,GoogleMap.OnMarkerClickListener {
    //private GeofencingClient mGeofencingClient;
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 500.0f; // in meters
    private GoogleMap mMap;
    double latitude;
    double longitude;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    FloatingActionButton fab_main_button, fab_search, fab_nearby, fab_list, fab_profile;
    boolean flag = false;
    boolean flag1 = false;
    Button search, lable_prof,lable_srch,lable_nrby,lable_lst;
    AutoCompleteTextView autoCompleteTextView;
    private FirebaseAuth mAuth;
    //List<HashMap<String, String>> list_latlong_json = new ArrayList<>();
    List<String> place_address = new ArrayList<>();
    List<String> place_lat = new ArrayList<>();
    List<String> place_lng = new ArrayList<>();
    GeoFire geoFire;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fab_main_button = (FloatingActionButton) findViewById(R.id.fab_main);
        fab_search = (FloatingActionButton) findViewById(R.id.search_stations);
        fab_nearby = (FloatingActionButton) findViewById(R.id.nearby_search);
        fab_list = (FloatingActionButton) findViewById(R.id.station_list);
        fab_profile = (FloatingActionButton) findViewById(R.id.profile);
        search = (Button) findViewById(R.id.search_button);
        lable_prof = (Button) findViewById(R.id.label_Profile);
        lable_lst = (Button) findViewById(R.id.label_List);
        lable_srch = (Button) findViewById(R.id.label_Search);
        lable_nrby = (Button) findViewById(R.id.label_Nearby);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        //mGeofencingClient = LocationServices.getGeofencingClient(this);

        mAuth = FirebaseAuth.getInstance();


        fab_main_button.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        //fab_nearby.setOnClickListener(this);
        fab_list.setOnClickListener(this);
        fab_profile.setOnClickListener(this);

        autoCompleteTextView.setThreshold(1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, place_address);
        autoCompleteTextView.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        get_json();


       //list_latlong_json = parse();

        /*for(int i=0;i<place_lat.size();i++)
       {
           String lol=list_latlong_json.get(i).get("latitude");
           String lol2 =list_latlong_json.get(i).get("longitude");

           Log.d("test","Lol1:"+lol+"lol2:"+lol2);
               place_address.add(list_latlong_json.get(i).get("Address"));

       }*/



    }

    /*public List<HashMap<String, String>> parse() {
        String json;
        JSONArray jsonArray = null;
        try {
            InputStream is = getAssets().open("cngstations.json");

            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
             jsonArray = new JSONArray(json);
        } catch (IOException e)

        {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }


    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;
        Log.d("Places", "getPlaces");

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
                Log.d("Places", "Adding places");

            } catch (JSONException e) {
                Log.d("Places", "Error in Adding places");
                e.printStackTrace();
            }
        }
        return placesList;

    }

    private HashMap<String,String> getPlace(JSONObject jsonObject) {
        HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
        try {
            Log.d("getPlace", "Entered");
            String lat1 = jsonObject.getString("Latitude");
            String lng1 = jsonObject.getString("Longitude");
            String add = jsonObject.getString("Address");

            googlePlaceMap.put("latitude",lat1);
            googlePlaceMap.put("longitude",lng1);
            googlePlaceMap.put("Address",add);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }*/

    private void get_json()
    {

        String json;
        try {
            InputStream is = getAssets().open("cngstations.json");

            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer,"UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            double lat=0.0;
            double lng=0.0;
            for(int i = 0; i <jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                 place_lat.add(jsonObject.getString("Latitude"));
                 place_lng.add(jsonObject.getString("Longitude"));
                 place_address.add(jsonObject.getString("Address"));
              //  Log.d("MapsActivity", "Latitude" + place_lat.get(i) + "Longitude" + place_lng.get(i)+"Addres"+place_address.get(i));
                    /*latlong_json.put("latitude", lat1);
                    latlong_json.put("longitude", lng1);*/
            }
           //list_latlong_json.add(latlong_json);

                    for(int i=0;i<place_lat.size();i++)
                    {
                        if (!(place_lat.get(i).trim().equals("") && place_lng.get(i).trim().equals(""))) {
                            lat = Double.parseDouble(place_lat.get(i));
                            lng = Double.parseDouble(place_lng.get(i));
                    }



                    //    Log.d("MapsActivity", "Latitude:" + lat + "Longitude:" + lng);
                    }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(this);


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        /*for(int i=0;i<place_lat.size();i++) {

              if(!(place_lat.get(i).trim().equals("") && place_lng.get(i).trim().equals(""))) {
                  double end_latitude = Double.parseDouble(place_lat.get(i));
                  double end_longitude = Double.parseDouble(place_lng.get(i));
                  Location.distanceBetween(latitude, longitude, end_latitude, end_longitude, result);
              }
        }
        for (int i=0;i<result.length ;i++)
        {
            if(!result.equals("")) {
                Log.d("DIs", "Distance:" + result[i]);
            }
        }*/



    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
  /*      mLocationRequest.setInterval(180000);
        mLocationRequest.setFastestInterval(180000);
  */
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users_LATLNG");
        //DatabaseReference current_user_db1 = FirebaseDatabase.getInstance().getReference().child("Users_LATLNG").child(user_id);
            geoFire = new GeoFire(current_user_db);
          // GeoFire geoFire1 = new GeoFire(current_user_db1);
           geoFire.setLocation(user_id,new GeoLocation(location.getLatitude(),location.getLongitude()));
         //  geoFire1.setLocation(user_id,new GeoLocation(location.getLatitude(),location.getLongitude()));
        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);*/

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
      //  Toast.makeText(MapsActivity.this, "Your Current Location", Toast.LENGTH_LONG).show();

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latitude, longitude));

        fab_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<place_lat.size();i++)
                {

                    double distance = caldistance(latitude,longitude,Double.parseDouble(place_lat.get(i)),Double.parseDouble(place_lng.get(i)),place_address.get(i));
                    boolean flag5=true;
                    if (flag5 == false) {
                        fab_show();
                        flag5 = true;
                    } else {
                        fab_hide();
                        flag5 = false;
                    }
           /* Log.d("TestDist","Distance:Check"+latitude+","+longitude+","+place_lat.get(i)+","+place_lng.get(i));
            Log.d("TestDist","Distance:Check"+"dist:"+distance1);*/

                }
            }
        });


        /*for(int i=0;i<result.length;i++)
        {
            Log.d("Testt","Distance:1"+result[i]);
        }*/

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    public void fab_show() {
        fab_nearby.show();
        fab_search.show();
        fab_profile.show();
        fab_list.show();

        lable_nrby.setVisibility(View.VISIBLE);
        lable_srch.setVisibility(View.VISIBLE);
        lable_prof.setVisibility(View.VISIBLE);
        lable_lst.setVisibility(View.VISIBLE);
    }

    public void fab_hide() {
        fab_list.hide();
        fab_profile.hide();
        fab_search.hide();
        fab_nearby.hide();
        lable_nrby.setVisibility(View.GONE);
        lable_srch.setVisibility(View.GONE);
        lable_prof.setVisibility(View.GONE);
        lable_lst.setVisibility(View.GONE);
    }


        @Override
        public void onClick (View view){
        try {
            switch (view.getId()) {

                case R.id.search_button:

                    String addresstxt = autoCompleteTextView.getText().toString();
                    List<android.location.Address> addressList = new ArrayList<>();
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(addresstxt, 5);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    Address address = addressList.get(0);
                    double a =address.getLatitude(); double b = address.getLongitude();
                     MarkerOptions markerOptions =new MarkerOptions();
                     LatLng latLng = new LatLng(a,b);
                     markerOptions.position(latLng);
                     markerOptions.title(""+addresstxt);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mMap.addMarker(markerOptions);
                    autoCompleteTextView.setText("");
                    break;

                case R.id.fab_main:

                    if (flag == false) {
                        fab_show();
                        flag = true;
                    } else {
                        fab_hide();
                        flag = false;
                    }
                    break;

               /* case R.id.nearby_search:
                    break;*/


                    case R.id.station_list:
                        Intent intent = new Intent(MapsActivity.this,NewListActivity.class);
                        String [] place_array = new String[place_address.size()];
                        place_address.toArray(place_array);
                        intent.putExtra("strings", place_array);
                        startActivity(intent);
                        Toast.makeText(this, "List of Stations", Toast.LENGTH_SHORT).show();
                        boolean flag2=true;
                        if (flag2 == false) {
                            fab_show();
                            flag2 = true;
                        } else {
                            fab_hide();
                            flag2 = false;
                        }

                break;

                case R.id.search_stations:
                    Toast.makeText(this, "search stations", Toast.LENGTH_SHORT).show();

                    if (flag1 == false) {
                        autoCompleteTextView.setVisibility(View.VISIBLE);
                        search.setVisibility(View.VISIBLE);
                        flag1 = true;
                    } else {
                        autoCompleteTextView.setVisibility(View.GONE);
                        search.setVisibility(View.GONE);
                        flag1 = false;
                    }

                    boolean flag4=true;
                    if (flag4 == false) {
                        fab_show();
                        flag4 = true;
                    } else {
                        fab_hide();
                        flag4 = false;
                    }
                    break;


                case R.id.profile:
                    Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(MapsActivity.this,User_profile.class);
                    startActivity(intent1);
                    boolean flag3 = true;
                    if (flag3 == false) {
                        fab_show();
                        flag3 = true;
                    } else {
                        fab_hide();
                        flag3 = false;
                    }
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public double caldistance(double lat1, double lon1, double lat2, double lon2, String s) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;

        if(dist<8)
        {
            if(lat2!=0.0 && lon2!=0.0)
            {
                Log.d("testting","inside");
                MarkerOptions markerOptions = new MarkerOptions();
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.gasstation);
                LatLng latLng = new LatLng(lat2,lon2);
                markerOptions.position(latLng);
                markerOptions.title(""+s);
                markerOptions.icon(icon);
                mMap.addMarker(markerOptions);
                Log.d("distzyada",""+dist);
                Log.d("ltln",""+ lat2+"," +lon2);
                double a = lat1;
                double b = lon2;
                createGeofence(a,b);
                addcircle(a,b);
            }
        }
        return dist;
    }
  // private Circle geoFenceLimits;
    private void addcircle(double a, double b) {

        Log.d("TAG", "drawGeofence");

      LatLng latLng = new LatLng(a,b);

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .strokeColor(Color.BLUE)
                .fillColor(Color.TRANSPARENT)
                .radius( GEOFENCE_RADIUS );
          mMap.addCircle( circleOptions );
    }

    private Geofence createGeofence(double lat2,double lon2)
    {
        Log.d("TAG", "createGeofence");
            return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion( lat2, lon2, 500.0f)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

    private static double deg2rad(double deg)
    {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void gettingLocations(double latitude, double longitude)
    {
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), 10);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                 System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                 MarkerOptions markerOptions = new MarkerOptions();
                 LatLng latLng =new LatLng(location.latitude,location.longitude);
                 BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car);

                markerOptions.position(latLng);
                markerOptions.title("userpostion");
                markerOptions.icon(icon);
                marker =  mMap.addMarker(markerOptions);
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));

                marker.remove();
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng =new LatLng(location.latitude,location.longitude);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car);

                markerOptions.position(latLng);
                markerOptions.title("userpostion");
                markerOptions.icon(icon);
                marker =  mMap.addMarker(markerOptions);
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng position = marker.getPosition();
        gettingLocations(position.latitude,position.longitude);
        return false;
    }

/*   @Override
    protected void onStart() {

        super.onStart();
    }*/
}
