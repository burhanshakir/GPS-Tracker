package com.example.burhan.gpstracker;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText target;
    TextView current;
    Button button;
    String t;
    LocationManager locationManager;
    LocationListener listener;
    Geocoder geocoder;
    List<Address> addresses;
    Location currentLoc,targetLoc;
    int flag = 0;
    double currentLong,currentLat,targetLong,targetLat,distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        target = (EditText) findViewById(R.id.etTarget);
        current = (TextView) findViewById(R.id.tvCurrent);
        button = (Button) findViewById(R.id.bStart);
        final ProgressDialog progress = new ProgressDialog(this);

        geocoder = new Geocoder(this, Locale.getDefault());
        currentLoc = new Location("");
        targetLoc = new Location("");

        progress.setMessage("Getting Current Location..");
        progress.show();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                currentLong = location.getLongitude();
                currentLat = location.getLatitude();

                currentLoc.setLatitude(currentLat);
                currentLoc.setLongitude(currentLong);
                try {
                    addresses = geocoder.getFromLocation(currentLat,currentLong, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String address = addresses.get(0).getAddressLine(0)+ " " + addresses.get(0).getLocality();
                progress.dismiss();
                //Toast.makeText(MainActivity.this, "Location:"+address, Toast.LENGTH_SHORT).show();
                current.setText(address);

                distance = currentLoc.distanceTo(targetLoc);

                //Toast.makeText(MainActivity.this,"Distance:"+distance, Toast.LENGTH_LONG).show();
                if(distance<=1000)
                {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(MainActivity.this)
                                    .setSmallIcon(R.drawable.map_location)
                                    .setContentTitle("GPS Tracker")
                                    .setContentText("Within 1km of "+ t );

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(123,mBuilder.build());
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        current_loc();
    }


    void current_loc(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, listener);
    }
    public void start(View v)
    {
        if(flag == 0)
        {
            t = target.getText().toString();
            if(!t.isEmpty() && t!=null)
            {
                flag++;
                button.setText("Stop");
                try {
                    addresses = geocoder.getFromLocationName(t, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses.size() > 0) {
                    targetLat = addresses.get(0).getLatitude();
                    targetLong = addresses.get(0).getLongitude();

                    targetLoc.setLongitude(targetLong);
                    targetLoc.setLatitude(targetLat);

                    distance = currentLoc.distanceTo(targetLoc);

                    //Toast.makeText(MainActivity.this, "Lat:"+targetLat+",Long:"+targetLong, Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, "Distance:" + distance, Toast.LENGTH_LONG).show();

                    if (distance <= 1000) {
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(this)
                                        .setSmallIcon(R.drawable.map_location)
                                        .setContentTitle("GPS Tracker")
                                        .setContentText("Within 1km of " + t);

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(123, mBuilder.build());
                    }
                }
            }
            else
                Toast.makeText(MainActivity.this, "Enter a location..", Toast.LENGTH_SHORT).show();
        }
        else
        {
            targetLoc.reset();
            finish();
        }


    }
}
