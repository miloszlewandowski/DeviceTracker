package com.example.trackerv2;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class ReceiverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private DatabaseReference refDatabase;
    private static final String TAG = TrackingService.class.getSimpleName();
    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkStateReceiver = new NetworkStateReceiver();
        this.registerReceiver(this.networkStateReceiver, intentFilter);
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loginToFirebase();
        setContentView(R.layout.activity_receiver);
        // Obtain the SupportMapFragment and get notified when the map is ready tobe used.
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        refDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;


        refDatabase.addChildEventListener(new ChildEventListener() {

            Marker marker;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                LatLng newLocation = new LatLng(dataSnapshot.child("latitude").getValue(Double.class), dataSnapshot.child("longitude").getValue(Double.class));
                marker = ReceiverActivity.this.googleMap.addMarker(new MarkerOptions().position(newLocation).title(dataSnapshot.getKey()).draggable(false));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(marker.getPosition());
                LatLngBounds bounds = builder.build();
                int padding = 0;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                ReceiverActivity.this.googleMap.moveCamera(cu);
                ReceiverActivity.this.googleMap.animateCamera(cu);
                ReceiverActivity.this.googleMap.animateCamera(CameraUpdateFactory.zoomTo( 17.0f ));
                ReceiverActivity.this.googleMap.getUiSettings().setMapToolbarEnabled(false);
                marker.setTitle("Transmitted terminal's last location");
                marker.setSnippet(Double.toString(newLocation.latitude) + " , " + Double.toString(newLocation.longitude));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                LatLng newLocation = new LatLng(dataSnapshot.child("latitude").getValue(Double.class), dataSnapshot.child("longitude").getValue(Double.class));
                marker.setPosition(newLocation);
                marker.setTitle("Transmitted terminal's last location");
                marker.setSnippet(Double.toString(newLocation.latitude) + " , " + Double.toString(newLocation.longitude));
                ReceiverActivity.this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 17.0f));
                ReceiverActivity.this.googleMap.getUiSettings().setMapToolbarEnabled(false);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }



    private void loginToFirebase() {
        //Authenticate with Firebase, using the email and password create earlier

        String login = getString(R.string.firebase_login);
        String password = getString(R.string.firebase_password);

        //Call OnCompleteListener if the user is signed in successfully
        FirebaseAuth.getInstance().signInWithEmailAndPassword(login,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(Task<AuthResult> task) {
                //If the user has been authenticated:
                if (task.isSuccessful()) {
                    //If it works , log it
                    Log.d(TAG, "Firebase auth. was successful");
                } else {
                    //If it fails , log the error
                    Log.d(TAG, "Firebase auth. failed");

                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(this.networkStateReceiver);
        super.onDestroy();
    }
}