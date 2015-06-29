package llamadigital.nearbyphotos;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.widget.GridView;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;

public class MainActivity extends ActionBarActivity implements LocationListener {
    private GridView gridView;
    private ImageAdapter imageAdapter;

    private ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
    private List<String> titleList = new ArrayList<String>();

    private LocationManager lms;
    private Double longitude;
    private Double latitude;
    private final int radius = 5;
    private final String radiusUnits = "km";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridView);

        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationServiceInitial();
        } else {
            Toast.makeText(this, "Please turn on your network location", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        try {
            new NetworkConnection().execute(Double.toString(latitude), Double.toString(longitude), Integer.toString(radius), radiusUnits).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageAdapter = new ImageAdapter(MainActivity.this, bitmapList, titleList);
        gridView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        locationServiceInitial();
    }

    private void locationServiceInitial() {
        lms = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Toast.makeText(this, "Your location: (" + longitude + ", " + latitude + ")", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Cannot get your location!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class NetworkConnection extends AsyncTask<String, Integer, Integer> {
        private REST rest;
        private Flickr flickr;
        private SearchParameters sp;
        private PhotosInterface pi;
        private PhotoList pl;
        private final String baseUrl = "api.flickr.com";
        private final String apiKey = "1156efcc389876f7dac5243ad1da1816";
        // private final String secret = "&secret=934a0a254bdd6b56";
        ProgressDialog pDialog;

        protected Integer doInBackground(String... str) {
            try {
                rest = new REST();
                rest.setHost(baseUrl);
                flickr = new Flickr(apiKey, rest);

                sp = new SearchParameters();
                sp.setSort(SearchParameters.DATE_POSTED_DESC);
                sp.setLatitude(str[0]);
                sp.setLongitude(str[1]);
                sp.setRadius(Integer.parseInt(str[2]));
                sp.setRadiusUnits(str[3]);

                pi = flickr.getPhotosInterface();
                pl =  pi.search(sp, 50, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (pl != null) {
                for (int i = 0; i < pl.size(); i++) {
                    Photo photo = (Photo) pl.get(i);
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(photo.getLargeSquareUrl()).getContent());
                        bitmapList.add(bitmap);
                        String title = photo.getTitle();
                        titleList.add(title);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                Toast.makeText(MainActivity.this, "No photo found", Toast.LENGTH_LONG).show();
            }
            return Integer.valueOf(0);
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading Image...");
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }

        protected void onPostExecute(Integer i) {
            super.onPostExecute(null);
            pDialog.dismiss();
        }
    }
}
