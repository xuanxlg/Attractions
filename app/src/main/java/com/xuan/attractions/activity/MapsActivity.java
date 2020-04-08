package com.xuan.attractions.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xuan.attractions.R;
import com.xuan.attractions.http.GoogleServer;
import com.xuan.attractions.http.WikiServer;
import com.xuan.attractions.object.GoogleMapsNearSearch;
import com.xuan.attractions.object.WikiContent;

import net.gotev.speech.Speech;
import net.gotev.speech.TextToSpeechCallback;

import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private Context mContext;

    // Taipei 101
    private double latitude = 25.034608;
    private double longitude = 121.564614;

    Handler handler = new Handler();

    private RelativeLayout contentView;
    private TextView tvTitle, tvContent;
    private Button btStartTts, btStopTts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mContext = this;

        tvTitle = (TextView)findViewById(R.id.title);
        tvContent = (TextView)findViewById(R.id.content);
        contentView = (RelativeLayout)findViewById(R.id.contentView);
        btStartTts = (Button)findViewById(R.id.btStartTts);
        btStopTts = (Button)findViewById(R.id.btStopTts);
        
        btStartTts.setOnClickListener(ttsStartListener);
        btStopTts.setOnClickListener(ttsStopListener);

        Speech.init(this);
        Speech.getInstance().setLocale(Locale.TAIWAN);
        Speech.getInstance().setTextToSpeechQueueMode(TextToSpeech.QUEUE_FLUSH);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(false);  // 右下角的放大縮小功能
        mMap.getUiSettings().setZoomGesturesEnabled(false);
//        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
        mMap.getUiSettings().setMapToolbarEnabled(false);    // 右下角的導覽及開啟 Google Map功能

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        if (checkPermission()) {
            getLocation();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Speech.getInstance().shutdown();
    }

    @Override
    public void onBackPressed() {
        System.out.println("onBackPressed()");

        if (contentView.getVisibility() == View.VISIBLE) {
            Speech.getInstance().stopTextToSpeech();
            contentView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }

    }

    private boolean checkPermission() {
        System.out.println("checkPermission()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        } else {
            return true;
        }
    }

    private void getLocation() {
        System.out.println("getLocation()");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        System.out.println("locationManager: "+locationManager);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new TulingLocationListener());

//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
//        Location location = locationManager.getLastKnownLocation(bestProvider);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        System.out.println("location: "+location);

//        System.out.println("location.getLatitude(): "+location.getLatitude());
//        System.out.println("location.getLongitude(): "+location.getLongitude());

        if (null != location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        final LatLng currentLocation = new LatLng(latitude, longitude);
        System.out.println("currentLocation: "+currentLocation);


        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);  原本的座標值是雪梨某處
//        mMap.addMarker(new MarkerOptions().position(currentLocation).title("台北101"));

//        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync();

        mMap.setMyLocationEnabled(true); // 右上角的定位功能；這行會出現紅色底線，不過仍可正常編譯執行
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 1));
        handler.postDelayed(new Runnable() {
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
            }
        }, 2000);
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        /**
         *  1: World
         *  5: Landmass/continent
         *  10: City
         *  15: Streets
         *  20: Buildings
         **/
        handler.postDelayed(new Runnable() {
            public void run() {
                setNearSearch(currentLocation);
            }
        }, 4000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("onRequestPermissionsResult()");
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void setNearSearch(final LatLng location) {
        System.out.println("setNearSearch()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ArrayList<GoogleMapsNearSearch> locationsArray = new GoogleServer().get(location);

                    System.out.println("locationsArray.size(): "+locationsArray.size());
                    for (final GoogleMapsNearSearch googleMapsNearSearch : locationsArray) {
                        handler.post(new Runnable() {
                            public void run() {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(googleMapsNearSearch.getLatitude(), googleMapsNearSearch.getLongitude()))
                                        .title(googleMapsNearSearch.getName()));
                            }
                        });

                    }

                } catch (final Exception e) {
                    System.out.println("e: " + e.toString());
                }
            }
        }).start();

    }

    public boolean onMarkerClick(final Marker marker) {
        return false;
    }

    public void onInfoWindowClick(Marker marker) {
        System.out.println("onInfoWindowClick()");

        getWikiMostSimilar(marker.getTitle());

//        try {
//            final WikiContent wikiContent = new WikiServer().get(marker.getTitle());
//            handler.post(new Runnable() {
//                public void run() {
//                    System.out.println("wikiContent.getContent(): "+wikiContent.getContent());
//                }
//            });
//        } catch (final Exception e) {
//            System.out.println("onInfoWindowClick() e: " + e.toString());
//        }

    }

    private void getWikiMostSimilar(final String attraction) {
        System.out.println("getWikiMostSimilar()");
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    final ArrayList<String> mostSimilars = new WikiServer().getMostSimilar(attraction);
                    System.out.println("mostSimilars: "+mostSimilars);

                    if (mostSimilars.size() > 0) {
                        Looper.prepare();

                        final Dialog dialog = new Dialog(mContext);
                        dialog.setTitle("要查看的Wiki介紹是...");
                        dialog.setContentView(R.layout.wiki_similars_list);
                        dialog.setCancelable(true);
                        ListView list = (ListView) dialog.findViewById(R.id.similar_list);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
//                                handler.post(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(mContext, mostSimilars.get(i), Toast.LENGTH_LONG).show();
//                                    }
//                                });
                                getWikiContent(mostSimilars.get(i));
                                dialog.dismiss();
                            }
                        });
                        ListAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, mostSimilars);
                        list.setAdapter(adapter);

                        dialog.show();

                        Looper.loop();
                    }

//                    if (!TextUtils.isEmpty(mostSimilar)) {
//                        getWikiContent(mostSimilar);
//                    }
                } catch (final Exception e) {
                    System.out.println("getWikiMostSimilar() e: " + e.toString());
                }
            }
        }).start();
    }

    private void getWikiContent(final String attractions) {
        System.out.println("getWikiContent()");
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    final WikiContent wikiContent = new WikiServer().getExtract(attractions);
                    handler.post(new Runnable() {
                        public void run() {
//                            Toast.makeText(mContext, wikiContent.getTitle()+ "\n\n" +wikiContent.getContent(), Toast.LENGTH_LONG).show();
                            tvTitle.setText(wikiContent.getTitle());
                            tvContent.setText(wikiContent.getContent());

                            contentView.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (final Exception e) {
                    System.out.println("getWikiContent() e: " + e.toString());
                }
            }
        }).start();
    }

    private Button.OnClickListener ttsStartListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!TextUtils.isEmpty(tvContent.getText())) {
                Speech.getInstance().say(tvContent.getText().toString(), new TextToSpeechCallback() {
                    @Override
                    public void onStart() {
                        System.out.println("TextToSpeechCallback() onStart()");
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("TextToSpeechCallback() onCompleted()");
                    }

                    @Override
                    public void onError() {
                        System.out.println("TextToSpeechCallback() onError()");
                    }
                });
            }

        }
    };

    private Button.OnClickListener ttsStopListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            Speech.getInstance().stopTextToSpeech();

        }
    };

}
