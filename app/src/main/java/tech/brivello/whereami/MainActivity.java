package tech.brivello.whereami;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static tech.brivello.whereami.Constants.RESULT_DATA_KEY;

public class MainActivity extends AppCompatActivity {

    private boolean permissionsGranted;
    private Button getLoc;
    private TextView Loc;
    private TextView Add;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double lat;
    private double lon;
    private final String JSONtest="{\"place_id\":\"99915440\",\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. http:\\/\\/www.openstreetmap.org\\/copyright\",\"osm_type\":\"way\",\"osm_id\":\"132429929\",\"lat\":\"37.4012236\",\"lon\":\"-112.0989079\",\"display_name\":\"Deer Range Road, Kane County, Utah, United States of America\",\"address\":{\"road\":\"Deer Range Road\",\"county\":\"Kane County\",\"state\":\"Utah\",\"country\":\"United States of America\",\"country_code\":\"us\"},\"boundingbox\":[\"37.3817675\",\"37.4143856\",\"-112.1067363\",\"-112.0939526\"]}";
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Add=findViewById(R.id.AddDisplay);
        Loc=findViewById(R.id.LocDisplay);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Loc=findViewById(R.id.LocDisplay);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Loc.setText("\n " + location.getLatitude() + " " + location.getLongitude());

                lat= location.getLatitude();
                lon= location.getLongitude();
                //new getAddress().execute();
                mLastLocation=location;
                mResultReceiver=new AddressResultReceiver(new Handler());
                Intent lService = new Intent(MainActivity.this, FetchAddressIntentService.class);
                lService.putExtra(Constants.RECEIVER, mResultReceiver);
                lService.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
                startService(lService);
                /*NominatimReverseGeocodingJAPI nominatim = new NominatimReverseGeocodingJAPI();
                Adress adress = nominatim.getAdress(location.getLatitude(), location.getLongitude());
                */ //Add.setText("success");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 19);
                return;
            } else {
                permissionsGranted=true;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 19:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted=true;
                }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("MissingPermission")
    public void buttonEventHandler(View view) {
        if(permissionsGranted) {
            locationManager.requestLocationUpdates("gps", 5000, 5, locationListener);
        }
    }
    public class getAddress extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            NominatimReverseGeocodingJAPI nominatim = new NominatimReverseGeocodingJAPI();
            Adress adress = nominatim.getAdress(lat, lon);
            Add.setText(adress.toString());
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    class AddressResultReceiver extends ResultReceiver {
        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            Add.setText(resultData.get(RESULT_DATA_KEY).toString());
            Log.d("address", String.valueOf(resultCode));
            // Display the address string
            // or an error message sent from the intent service.
            /*mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (mAddressOutput == null) {
                mAddressOutput = "";
            }
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }
            */
        }

    }
}
