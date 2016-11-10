/*
*
* Project forked from github user <user>.
* Original program queried for GPS coordinates and appended TextView.
*
* Modified to keep track of locations within a set resolution, calculate distance
* between each point, and show total distance in TextView
*
* The purpose of this app is to perform experiments defined in the project assigned
* by Dr Aaron Gordon for Mobile Devices Programming class at MSU Denver
*
* Date: 11/04/2016
* Author: Nathan Larson
*
* */

package testing.gps_location;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button b;
    private TextView t;
    private TextView prev;
    private LocationManager locationManager;
    private LocationListener listener;
    float dist = 0;
    boolean enable = false;
    final ArrayList <Location> locationList = new ArrayList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        t = (TextView) findViewById(R.id.textView);
        b = (Button) findViewById(R.id.button);
        prev = (TextView) findViewById(R.id.prevDist);




        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                if (enable == true) {
                    if (locationList.size() == 0) {
                        locationList.add(location);

                    }
                    //t.append("\n " + location.getLongitude() + " " + location.getLatitude());
                    dist += location.distanceTo(locationList.get(locationList.size()-1));
                    locationList.add(location);
                    t.setText(Float.toString(dist));
                }
                else {
                    locationList.clear();


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

        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                    configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
                enable = !enable;
                if (enable == true) {
                    b.setText("Disable");
                    t.setText(Float.toString(dist));

                }
                else {
                    b.setText("Enable");
                    prev.append("Route calculated: " + Float.toString(dist));
                    dist = 0;
                    t.setText("Press Enable to start");

                }
            }
        });
    }
}


