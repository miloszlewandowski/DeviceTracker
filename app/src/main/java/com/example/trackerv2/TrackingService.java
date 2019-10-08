package com.example.trackerv2;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;


import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();

    public TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
      super.onCreate();
      buildNotification();
      loginToFirebase();
    }

    //Create the persistent notification
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        //Create the persistent notification
        Notification.Builder builder = new Notification.Builder(this).setContentTitle(getString(R.string.app_name)).setContentText(getString(R.string.tracking_enabled_notification)).setOngoing(true).setContentIntent(broadcastIntent).setSmallIcon(R.drawable.tracking_enabled);
        startForeground(1,builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Unregister the BroadcastReceiver when the notification is tapped

            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private void loginToFiebase() {
        //Authenticate with Firebase, using the email and password create earlier

        String email = getString(R.string.test_email);
        String password = getString(R.string.test_password);

        //Call OnCompleteListener if the user is signed in successfully
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(Task<AuthResult> task) {
                //If the user has been authenticated:
                if (task.isSuccessful()) {
                    //call requestLocationUpdates
                    requestLocationUpdates();
                } else {
                    //If it fails , log the error
                    Log.d(TAG, "Fireabase auth. failed");

                }
            }
        });

    }

    //Initiate the request to track the deive's location
    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

        //Specify how often your app should request the device's location
        request.setInterval(10000);

        ///Get the most accurate location data available
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = getString(R.string.firebase_path);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        //If the app currently has access the location permission...
        if(permission == PackageManager.PERMISSION_GRANTED) {

            //.. then request location updates
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    //Get a reference to the database, so your app can perform read and write operations
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {

                        //Save the location data to database
                        ref.setValue(location);
                    }
                }
            }, null);
        }

    }




    private void loginToFirebase() {

    }


}
