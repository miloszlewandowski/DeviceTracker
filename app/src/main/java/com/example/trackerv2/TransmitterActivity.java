package com.example.trackerv2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.example.trackerv2.TrackingService.LOCATION_ACCESS;

public class TransmitterActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 100;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //Check if this app has access to the location permission
            int permission = ContextCompat.checkSelfPermission(TransmitterActivity.this, LOCATION_ACCESS);

            //If the location permission has been granted, then start the TrackerService
            if (permission == PackageManager.PERMISSION_GRANTED) {
                startTrackingService();
            }  else {
                //If the app doesn't currently have access to the user's location, then request access
                ActivityCompat.requestPermissions(TransmitterActivity.this, new String[]{LOCATION_ACCESS}, PERMISSIONS_REQUEST);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Check if GPS tracking and internet connection are enabled
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);

        VerificationAsyncTask verificationAsynctask = new VerificationAsyncTask(this, locationManager, connectivityManager, runnable);
        verificationAsynctask.execute();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        //If the permission has been granted -
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);

            VerificationAsyncTask verificationAsynctask = new VerificationAsyncTask(this, locationManager, connectivityManager, runnable);
            verificationAsynctask.execute();
        } else { startAuthorizationActivity(); }

    }

    //Return to AuthorizationActivity
    private void startAuthorizationActivity() {

        startActivity(new Intent(this, AuthorizationActivity.class));

        //If the user denies the permission request, then display a toast with some more information
        Toast.makeText(this, "Please enable location services to allow device tracking", Toast.LENGTH_SHORT).show();

        //Close main activity
        finish();
    }

    //Start the TrackingService
    private void startTrackingService() {

        startService(new Intent(this, TrackingService.class));

        //Close main activity
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
