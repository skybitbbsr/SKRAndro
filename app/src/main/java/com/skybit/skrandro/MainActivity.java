package com.skybit.skrandro;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button button;
    private TextView textbusno;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String URL_SEND = "http://cetbusservice.000webhostapp.com/send_location.php/";
    private String busno, result;
    private int counter = 0;
    private Double lat = 10.0, lng = 10.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        button = (Button) findViewById(R.id.share);
        textbusno = (TextView) findViewById(R.id.text_bus_no);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        displayData();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                BufferedReader bufferedReader;
                lat = location.getLatitude();
                lng = location.getLongitude();

                String output = "Lattitude: " + lat + "\n" + "Longitude: " + lng;
                Toast.makeText(MainActivity.this, output, Toast.LENGTH_SHORT).show();
                String s = "?id=" + busno + "&longitude=" + lng + "&latitude=" + lat;
                try {
                    URL url = new URL(URL_SEND + s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    result = bufferedReader.readLine();
                    if (result.equals("Sent")) {
                        ++counter;
                        Toast.makeText(MainActivity.this, "Sent Location to Database " + counter + " times", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Not Sent Location to Database", Toast.LENGTH_SHORT).show();
                    }
                    bufferedReader.close();
                    con.disconnect();
                } catch (Exception e) {
                    String st = e.toString();
                    Toast.makeText(MainActivity.this, st, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                buildAlertMessageNoGps();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10);
                return;
            } else {
                configureButton();
            }
        } else {
            configureButton();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap;
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(position).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                // User chose the "Settings" item, show the app settings UI...
                Intent in = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(in);
            }

            case R.id.action_check_update: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.skybit.cetbbus.driver"));
                startActivity(intent);
            }

            case R.id.action_about: {

            }

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void displayData() {
        Intent i = getIntent(); // gets the previously created intent
        String busno = i.getStringExtra("busNumber");
        textbusno.setText("Bus Number: " + busno);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    private void configureButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();

                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (button.getText() == "START DRIVING") {
                        button.setText("STOP DRIVING");
                    }
                    locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

                }
            }
        });
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
