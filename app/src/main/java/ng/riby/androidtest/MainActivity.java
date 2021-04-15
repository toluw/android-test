package ng.riby.androidtest;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import ng.riby.androidtest.room.AppDatabase;
import ng.riby.androidtest.room.Item;
import ng.riby.androidtest.room.ItemDao;

public class MainActivity extends AppCompatActivity {


    Button start, stop;
    TextView result;
    private static final int PERMISSION_CODE = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private FusedLocationProviderClient fusedLocationClient;
    double startLat, startLong, stopLat, stopLong;
    AppDatabase database;
    ItemDao itemDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        result = (TextView) findViewById(R.id.result);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Initialize Database
        database = Room.databaseBuilder(this, AppDatabase.class, "mydb")
                .allowMainThreadQueries()
                .build();
        itemDAO = database.getItemDAO();

    }

    public void startClicked(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startTracking();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    PERMISSION_CODE
            );
        }
    }


    private void startTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                           startLat = location.getLatitude();
                           startLong = location.getLongitude();
                           result.setText("Tracking in Progress....");
                           start.setVisibility(View.GONE);
                           stop.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   Toast.makeText(getApplicationContext(),"Could not start tracking, check that GPS is enabled",Toast.LENGTH_LONG).show();
                }
            });

        }

    }

    public void stopClicked(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            stopLat = location.getLatitude();
                            stopLong = location.getLongitude();
                            //Post Data to Room DB
                            Item item = new Item(startLat, stopLat, startLong, stopLong);
                            itemDAO.insert(item);

                            //Get Data from Room DB
                            Item item1 = itemDAO.getItems();
                            double startLat1 = item1.getStartLat();
                            double startLong1 = item1.getStartLong();
                            double stopLat1 = item1.getStopLat();
                            double stopLong1 = item1.getStopLong();

                            //Calculate distance
                            float[]results = new float[1];
                            Location.distanceBetween(startLat1,startLong1,stopLat1,stopLong1,results);
                            float distance = results[0];
                            String dist = String.valueOf(distance);
                            result.setText("Distance = "+dist+"m");
                            start.setVisibility(View.VISIBLE);
                            stop.setVisibility(View.GONE);

                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Could not stop tracking, check that GPS is enabled",Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTracking();
                }  else {
                    Toast.makeText(getApplicationContext(),"Tracking could not be done because requested permission is not granted",Toast.LENGTH_LONG).show();
                }

        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


}
