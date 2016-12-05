package course.examples.creaturun;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static course.examples.creaturun.R.id.map;

/**
 * Useful sources for this code:
 * http://stackoverflow.com/questions/33739971/how-to-show-my-current-location-in-google-map-android-using-google-api-client
 * http://stackoverflow.com/questions/33540755/android-check-permission
 * http://stackoverflow.com/questions/14396587/how-to-get-bitmap-of-mapview-from-android-google-maps-api-v2
 * http://stackoverflow.com/questions/17012741/how-to-make-supportmapfragment-not-fill-the-screen-height
 * http://stackoverflow.com/questions/11010386/send-bitmap-using-intent-android
 */
public class RunningActivity2 extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.SnapshotReadyCallback {

    // Necessary so that we can request permissions from the user if they aren'timerThread available
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    // Used for the intent that will be sent to the 'Run Summary' activity
    public static final String DISTANCE_KEY = "Distance";
    public static final String ELAPSED_TIME_KEY = "Elapsed_time";
    public static final String SPEED_KEY = "Speed";
    public static final String BITMAP_KEY = "Bitmap";

    // Used for requesting location updates
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment; //map that displays the GoogleMap
    Marker currLocationMarker;

    long startTime; //time when run was started, may not be necessary
    long elapsedTime = 0; // keeps track of how much time has elapsed since user pressed start run
    long currTime; //time at which the current location was found (necessary for speed calculation)
    long prevTime; // time at which the prev location was found (necessary for speed calculation)

    TextView distanceView; // displays total distance
    TextView timeView; // displays total time
    TextView speedView; // displays *current* speed (not average)

    double movementSpeed; // the current movement speed
    Location prevLocation;
    double distance = 0.0; // the total distance

    ArrayList<Double> speedArray = new ArrayList<Double>(); // array of all speeds (used to generate average speed)
    Thread timerThread; // thread to keep track of time in the background

    ArrayList<Location> listLocsToDraw = new ArrayList<Location>(); // arraylist of points to draw
    // only holds two locations at a time since we draw a line on the map whenever
    // we move from one point to another (no need to redraw everything)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running2);

        distanceView = (TextView) findViewById(R.id.distanceView);
        timeView = (TextView) findViewById(R.id.timeView);
        speedView = (TextView) findViewById(R.id.speedView);

        startTime = System.currentTimeMillis();

        timerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                elapsedTime += 1000;
                                timeView.setText("Elapsed time: " + getTimeText(elapsedTime));

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        timerThread.start();

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mFragment.getMapAsync(this);
    }


    @Override
    @TargetApi(23)
    public void onMapReady(GoogleMap gMap) {
        mGoogleMap = gMap;
        if (!(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        mGoogleMap.setMyLocationEnabled(true);

        buildGoogleApiClient();

        mGoogleApiClient.connect();

    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    @TargetApi(23)
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        if (!(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            // maybe put a little creature icon here instead?
            listLocsToDraw.add(mLastLocation);
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
            prevLocation = mLastLocation;

            timeView.setText("Elapsed time: " + getTimeText(0));
            distanceView.setText("Distance traveled: 0.0 miles");
            speedView.setText("Speed: 0.0 miles/hour");

            startTime = mLastLocation.getTime();
            prevTime = mLastLocation.getTime();
            currTime = mLastLocation.getTime();
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000); //2 seconds
        mLocationRequest.setFastestInterval(1000); //1 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    // Formats time properly
    private String getTimeText(long milliseconds) {
        long seconds = milliseconds / 1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h, m, s);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        listLocsToDraw.add(location);
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        if (listLocsToDraw.size() > 2) {
            listLocsToDraw.remove(0); // remove what's already been drawn so we don'timerThread draw it again
        }
        drawPrimaryLinePath(listLocsToDraw);
        //Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show(); //for testing

        double distanceToLast = location.distanceTo(prevLocation);
        currTime = location.getTime();
        distance += distanceToLast;
        prevLocation = location;
        movementSpeed = distanceToLast / ((currTime - prevTime) * (Math.pow(2.77778, -7))); //conversion from milliseconds to hours
        speedArray.add(movementSpeed);

        prevTime = currTime;
        distanceView.setText("Distance traveled: " + new DecimalFormat("#.##").format(distance * 0.000621371) + " miles");
        speedView.setText("Speed: " + String.valueOf(new DecimalFormat("#.##").format(movementSpeed)) + " mph");

        // zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(17).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    private void drawPrimaryLinePath(ArrayList<Location> listLocsToDraw) {
        if (mGoogleMap == null) {
            return;
        }
        if (listLocsToDraw.size() < 2) {
            return;
        }
        PolylineOptions options = new PolylineOptions();
        options.color(Color.parseColor("#CC0000FF"));
        options.width(5);
        options.visible(true);

        for (Location locRecorded : listLocsToDraw) {
            options.add(new LatLng(locRecorded.getLatitude(),
                    locRecorded.getLongitude()));
        }
        mGoogleMap.addPolyline(options);
    }

    // Method called when end run is clicked
    public void onClickEndRun(View v) {
        // stop updating the view
        timerThread.interrupt();
        stopLocationUpdates();
        mGoogleMap.snapshot(this);
    }

    public double calculateAverageSpeed() {
        double totalSum = 0;
        for (Double d : speedArray) {
            totalSum += d;
        }
        return totalSum / speedArray.size();
    }

    @Override
    public void onSnapshotReady(Bitmap snapshot) {
        Bitmap bitmap = snapshot;
        Intent intent = new Intent(RunningActivity2.this, RunSummary.class);
        intent.putExtra(DISTANCE_KEY, distance);
        intent.putExtra(SPEED_KEY, calculateAverageSpeed());
        intent.putExtra(ELAPSED_TIME_KEY, getTimeText(elapsedTime));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra(BITMAP_KEY, byteArray);
        startActivity(intent);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
}
