package com.example.geocodingthesis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Declare Variables
    String startpoint, endpoint, type;

    double startLat, startLong, endLat, endLong; //for location marking
    double places_lat1 = 0, places_long1 = 0, places_lat2 = 0, places_long2 = 0; // Google Places Variables
    int flag = 0;
    EditText getInput_startlocation;
    EditText getInput_endpoint;
    Button markstartlocation, markdestination, compute;
    TextView showstartlocation;
    TextView showdestination;
    TextView showdistance;
    //get Location Variables
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize Places
        Places.initialize(getApplicationContext(), "AIzaSyBreLDGKrJxxOjE2qOAquDqrLvZNS9os88");
        setContentView(R.layout.activity_main);
        //progressBar
        progressBar = findViewById(R.id.progressBar);
        //getter
        getInput_startlocation = (EditText) findViewById(R.id.input_startlocation);
        getInput_endpoint = (EditText) findViewById(R.id.input_endpoint);
        showstartlocation = (TextView) findViewById(R.id.from);
        showdestination = (TextView) findViewById(R.id.destination);
        showdistance = (TextView) findViewById(R.id.distance);
        //set edit text  focusable

        getInput_startlocation.setFocusable(false);
        getInput_startlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define type
                type = "starting";
                //Initialize place field list
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY,fields
                ).build(MainActivity.this);
                //Start Activity Result
                startActivityForResult(intent, 100);
            }
        });
        // sets edit text non-focusable
        getInput_endpoint.setFocusable(false);
        getInput_endpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define type
                type = "destination";
                //Initialize place field list
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY,fields
                ).build(MainActivity.this);
                //Start Activity Result
                startActivityForResult(intent, 100);
            }
        });
        //Set default text on text view showdistance



        //Button
        markstartlocation = (Button) findViewById(R.id.btn_markstart);
        markstartlocation.setOnClickListener(v -> {
            startpoint = getInput_startlocation.getText().toString();
            //getLocation
            String strAddress = startpoint;
            GeoCodeLocation locationAddress = new GeoCodeLocation();
            locationAddress.getAddressFromLocation(strAddress, getApplicationContext(), new
                    StartGeoCoderHandler());
            GeoCodeLocation get = new GeoCodeLocation();
            startLat = get.lat;
            startLong = get.lon;
        });
        markdestination = (Button) findViewById(R.id.btn_markdestination);
        markdestination.setOnClickListener(v -> {

            endpoint = getInput_endpoint.getText().toString();

            //getLocation
            String strAddress = endpoint;
            GeoCodeLocation locationAddress = new GeoCodeLocation();
            locationAddress.getAddressFromLocation(strAddress, getApplicationContext(), new
                    DestinationGeoCoderHandler());
            GeoCodeLocation get = new GeoCodeLocation();
            endLat = get.lat;
            endLong = get.lon;
        });
        compute = (Button) findViewById(R.id.btn_compute);
        compute.setOnClickListener(v -> {
            //Haversine Algorithm
            //double distanceresult = distance(startLat, startLong, endLat, endLong);
            //showdistance.setText(Double.toString(distanceresult));
            //Using Google API to compute distance between 2 points

        });
    }

    // methods
    private class DestinationGeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            showstartlocation.setText(locationAddress);
        }
    }

    private class StartGeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            showdestination.setText(locationAddress);
        }

    }
//Google Directions API
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        //Check condition
        if (requestCode == 100 && resultCode == RESULT_OK) {
            //if success
            //initializes place
            Place place = Autocomplete.getPlaceFromIntent(data);

            //check condition
            if (type.equals("starting")) {
                //when type is source
                //increase flag value
                flag++;
                //set address on edit text
                getInput_startlocation.setText(place.getAddress());
                String sSource = String.valueOf(place.getLatLng());
                sSource = sSource.replaceAll("lat/lng: ", "");
                sSource = sSource.replace("(", "");
                sSource = sSource.replace(")", "");
                String[] split = sSource.split(",");
                places_lat1 = Double.parseDouble(split[0]);
                places_long1 = Double.parseDouble(split[1]);
            } else {
                //when type is destination
                //increase flag
                flag++;
                //set address on edit text
                getInput_endpoint.setText(place.getAddress());
                String sDestination = String.valueOf(place.getLatLng());
                sDestination = sDestination.replaceAll("lat/lng: ", "");
                sDestination = sDestination.replace("(", "");
                sDestination = sDestination.replace(")", "");
                String[] split = sDestination.split(",");
                places_lat2 = Double.parseDouble(split[0]);
                places_long2 = Double.parseDouble(split[1]);
            }
            //check condition
            if (flag >= 2) {
                //when flag is greater than and equal to 2
                //Calculate distance
                showdistance.setText("Distance in KM: " + Double.toString(haversine(places_lat1, places_long1, places_lat2, places_long2)));
            } else if (requestCode == AutocompleteActivity.RESULT_ERROR) {
                //when error
                //initialize status
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*
    private double places_distance(double places_lat1, double places_long1, double places_lat2, double places_long2){
        //calculate longitude difference
        double longDiff = places_long1 - places_long2;
        //calculate distance
        double pdistance = Math.sin(deg2rad(places_lat1))*Math.sin(deg2rad(places_lat2))+Math.cos(deg2rad(places_lat1))*Math.cos(deg2rad(places_lat2))*Math.cos(deg2rad(longDiff));
        pdistance = Math.acos(pdistance);
        //convert distance to radian degree
        pdistance = rad2deg(pdistance);
        //distance in miles
        //pdistance = pdistance * 60 * 1.1515;
        // distance in km
        pdistance = pdistance * 1.609344;
        //Set Distance on text view
        showdistance.setText(String.format(Locale.US, "%2f Kilometers", pdistance));

        return pdistance;
    }
    // convert radian to degree
    private double rad2deg(double pdistance){
        return (pdistance * 180.0 / Math.PI);
    }
    //convert degree to radian
    private double deg2rad(double places_lat1){
        return (places_lat1*Math.PI/180);
    }*/

    //Haversine Algorithm

    public static double haversine(double startLat, double startLong, double endLat, double endLong){
        //distance between latitudes and longitudes
        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));
        //convert to radians
        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);
        //apply formula
        double a = Math.pow(Math.sin(dLat / 2), 2)+ Math.pow(Math.sin(dLong / 2), 2)* Math.cos(startLat)* Math.cos(endLat);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;

    }












//end of code
}

