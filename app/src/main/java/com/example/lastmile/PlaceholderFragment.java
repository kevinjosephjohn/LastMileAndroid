package com.example.lastmile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class PlaceholderFragment extends Fragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
            * FASTEST_INTERVAL_IN_SECONDS;
    Context context;
    InternetUtils check;
    CircularProgressButton pickup_button;
    Button changefragment;
    TextView pickup_text, StreetName, ETA, ETA_TEXT;
    ImageView pin_green, pin_red, current_location;
    Typeface regular, bold, light;
    RelativeLayout test;
    LocationClient mLocationClient;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;
    SharedPreferences pref;
    String lat, lng, lat_start, lng_start;
    Editor editor;
    OnCameraChangeListener listener;
    private GoogleMap mMap;
    int pickup_button_click = 0;
    private pickuprequest mTask;
    String street;

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        pref = context.getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        mLocationClient = new LocationClient(context, this, this);
        final View rootView = inflater.inflate(R.layout.fragment_main,
                container, false);
        mTask = new pickuprequest();

        //Create a Card
        Card card = new Card(context);


        //Set card in the cardView
        CardViewNative cardView = (CardViewNative) rootView.findViewById(R.id.carddemo);
        cardView.setCard(card);


        pin_red = (ImageView) rootView.findViewById(R.id.pin_red);
        pin_green = (ImageView) rootView.findViewById(R.id.pin);
        pin_red.setVisibility(View.GONE);
        pin_green.setVisibility(View.GONE);
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        current_location = (ImageView) rootView
                .findViewById(R.id.current_location);

        listener = new OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition position) {

                // TODO Auto-generated method stub

                current_location.setVisibility(View.VISIBLE);

                LatLng center = position.target;

                String pinLocation = Double.toString(center.latitude) + ","
                        + Double.toString(center.longitude);
                lat = Double.toString(center.latitude);
                lng = Double.toString(center.longitude);

                if (!check.isConnected(context))
                    showDialog(context, lat, lng);
                else
                    new pickup().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, lat, lng);

                // Toast.makeText(getApplicationContext(), pinLocation,
                // Toast.LENGTH_SHORT).show();
                Log.i("Passing To Api", pinLocation);

            }
        };

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        bold = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/bold.otf");
        regular = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/regular.otf");
        light = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/light.otf");
        pickup_text = (TextView) rootView.findViewById(R.id.pickuplocation);
        pickup_text.setText("PICKUP LOCATION");
        pickup_text.setTypeface(light);
        check = new InternetUtils();

        current_location.setVisibility(View.GONE);
        current_location.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                mMap.setOnCameraChangeListener(null);
                current_location.setVisibility(View.GONE);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude())).zoom(15)
                        .build();

                mMap.animateCamera(

                        CameraUpdateFactory.newCameraPosition(cameraPosition), 500,
                        new GoogleMap.CancelableCallback() {

                            @Override
                            public void onFinish() {
                                // DO some stuff here!

                            }

                            @Override
                            public void onCancel() {
                                Log.d("animation", "onCancel");

                            }
                        });

                new CountDownTimer(600, 100) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        mMap.setOnCameraChangeListener(listener);
                        if (!check.isConnected(context))
                            showDialog(context, lat_start, lng_start);
                        else
                            lat = lat_start;
                        lng = lng_start;
                        new pickup().execute(lat_start, lng_start);


                    }
                }.start();

            }
        });

        return rootView;

    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        mCurrentLocation = location;


    }

    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        mCurrentLocation = mLocationClient.getLastLocation();
        // Toast.makeText(context, "this is my Toast message!!! =)",
        // Toast.LENGTH_LONG).show();
        if (mCurrentLocation != null) {
            mMap.addMarker(new MarkerOptions().position(
                    new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation
                            .getLongitude())).icon(
                    BitmapDescriptorFactory
                            .fromResource(R.drawable.location_dot)));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude())).zoom(15).build();

            mMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            lat_start = Double.toString(mCurrentLocation.getLatitude());
            lng_start = Double.toString(mCurrentLocation.getLongitude());
            Log.i(lat_start, lng_start);

            if (!check.isConnected(context))
                showDialog(context, lat_start, lng_start);
            else {
                lat = lat_start;
                lng = lng_start;
                new pickup().execute(lat_start, lng_start);
            }
        } else {

            LocationDialog(context);
        }

    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub

    }

    public void showDialog(final Context context, final String lat_start,
                           final String lng_start) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.internetdialog);
        dialog.setCancelable(false);
        final CircularProgressButton dialogButton = (CircularProgressButton) dialog
                .findViewById(R.id.tryagain);
        dialogButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialogButton.setIndeterminateProgressMode(true);
                dialogButton.setProgress(50);

                new CountDownTimer(2000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {

                        if (!check.isConnected(context)) {
                            dialogButton.setProgress(0);
                            dialog.show();
                        } else {
                            dialog.dismiss();
                            new pickup().execute(lat_start, lng_start);
                        }

                    }
                }.start();

            }

        });
        dialog.show();

    }

    public void LocationDialog(final Context context) {

        TextView line1, line2;
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.internetdialog);

        dialog.setCancelable(false);
        final CircularProgressButton dialogButton = (CircularProgressButton) dialog
                .findViewById(R.id.tryagain);
        line1 = (TextView) dialog.findViewById(R.id.textView1);
        line2 = (TextView) dialog.findViewById(R.id.textView2);

        line1.setText("LOCATION SERVICES");
        line2.setText("LOCATION SERVICES ALLOWS APP TO GATHER AND USE DATA INDICATING YOUR APPROXIMATE LOCATION.");
        line2.setGravity(Gravity.CENTER);

        dialogButton.setText("ENABLE");

        dialogButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // dialogButton.setIndeterminateProgressMode(true);
                // dialogButton.setProgress(50);

                // new CountDownTimer(1000, 1000) {
                //
                // public void onTick(long millisUntilFinished) {
                //
                // }
                //
                // public void onFinish() {

                Intent gpsOptionsIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
                dialog.dismiss();

                // }
                // }.start();

            }

        });
        dialog.show();
    }

    private class pickup extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            pickup_button = (CircularProgressButton) getView().findViewById(
                    R.id.pickup_button);
            TextView StreetName = (TextView) getView().findViewById(
                    R.id.StreetName);
            TextView ETA = (TextView) getView().findViewById(R.id.ETA);
            TextView ETA_TEXT = (TextView) getView()
                    .findViewById(R.id.ETA_TEXT);
            StreetName.setTypeface(regular);
            ETA_TEXT.setTypeface(bold);
            ETA.setTypeface(bold);
            // ETA_TEXT.setText("");
            // ETA.setText("");
            pickup_button.setIndeterminateProgressMode(true);
            pickup_button.setProgress(50);
            pickup_button.setIconProgress(null);


        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            // Create a new HttpClient and Post Header

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    "http://128.199.134.210/api/cars/");
            String responseBody = null;
            String lat = params[0];
            String lng = params[1];

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("lat", params[0]));
                nameValuePairs.add(new BasicNameValuePair("lng", params[1]));
                nameValuePairs.add(new BasicNameValuePair("uid", pref
                        .getString("uid", "null")));
                Log.i("uid", pref.getString("uid", "null"));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity);
                Log.i("Response", responseBody);
                // Log.i("Parameters", params[0]);

            } catch (ClientProtocolException e) {

                showDialog(context, lat, lng);

                // TODO Auto-generated catch block
            } catch (IOException e) {
                showDialog(context, lat, lng);
                // TODO Auto-generated catch block
            }
            return responseBody;

        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(String result) {
            try {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude())).icon(
                        BitmapDescriptorFactory
                                .fromResource(R.drawable.location_dot)));
                pin_red = (ImageView) getView().findViewById(R.id.pin_red);
                pin_green = (ImageView) getView().findViewById(R.id.pin);
                TextView StreetName = (TextView) getView().findViewById(
                        R.id.StreetName);
                TextView ETA = (TextView) getView().findViewById(R.id.ETA);
                TextView ETA_TEXT = (TextView) getView().findViewById(
                        R.id.ETA_TEXT);
                StreetName.setTypeface(regular);
                ETA_TEXT.setTypeface(bold);
                ETA.setTypeface(bold);

                JSONObject data = new JSONObject(result);
                JSONArray car_locations = (JSONArray) data.get("cars");
                int length = car_locations.length();
                String time = data.getString("eta");
                street = data.getString("address");
                String[] separated = time.split(" ");
                Log.i("Stree Name", street);
                Log.i("ETA", time);

                if (length != 0) {
                    for (int i = 0; i < car_locations.length(); i++) {

                        JSONObject jsonProductObject = car_locations
                                .getJSONObject(i);

                        double latitude = Double.parseDouble(jsonProductObject
                                .getString("lat"));
                        double longitude = Double.parseDouble(jsonProductObject
                                .getString("lng"));
                        float rotation = Float.valueOf(jsonProductObject
                                .getString("rot"));

                        mMap.addMarker(new MarkerOptions().rotation(rotation).position(
                                new LatLng(latitude, longitude)).icon(
                                BitmapDescriptorFactory
                                        .fromResource(R.drawable.car)));
                    }
                }

                if (separated[0].equalsIgnoreCase("NO")) {

                    pickup_button.setProgress(-1);
                    pickup_button.setClickable(false);
                    pin_green.setVisibility(View.GONE);
                    pin_red.setVisibility(View.VISIBLE);

                } else {
                    pickup_button.setProgress(0);
                    pin_red.setVisibility(View.GONE);
                    pin_green.setVisibility(View.VISIBLE);

                    pickup_button.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            pickup_button_click++;


                            if (pickup_button_click == 1) {
                                //Single click

                                mMap.setOnCameraChangeListener(null);
                                mMap.getUiSettings().setAllGesturesEnabled(false);
                                Log.i(lat, lng);

                                current_location.setVisibility(View.GONE);
                                mTask = new pickuprequest();
                                mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,lat, lng);
                                new CountDownTimer(30000, 1000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        pickup_button_click = 0;
                                        mMap.getUiSettings().setAllGesturesEnabled(true);
                                        mMap.setOnCameraChangeListener(listener);
                                        mTask.cancel(true);


                                        pickup_button.setProgress(0);

                                    }
                                }.start();

                            } else if (pickup_button_click == 2) {
                                //Double click
                                new CancelRide().execute(lat,lng);
                                pickup_button_click = 0;
                                mMap.getUiSettings().setAllGesturesEnabled(true);
                                mMap.setOnCameraChangeListener(listener);
                                mTask.cancel(true);


                                pickup_button.setProgress(0);


                            }


                        }
                    });
                }
                StreetName.setText(street);
                ETA_TEXT.setText(separated[1].toUpperCase());
                ETA.setText(separated[0]);

            } catch (JSONException e) {

                // TODO Auto-generated catch block
                showDialog(context, lat, lng);
                e.printStackTrace();
            }
            mMap.setOnCameraChangeListener(listener);

        }


    }

    private class pickuprequest extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            pickup_button = (CircularProgressButton) getView().findViewById(
                    R.id.pickup_button);
            pickup_button.setIndeterminateProgressMode(true);
            pickup_button.setProgress(50);
            pickup_button.setIconProgress(getResources().getDrawable(R.drawable.ic_action_cancel));

        }

        @Override
        protected String doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    "http://128.199.134.210/api/request/");
            String responseBody = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("lat", params[0]));
                nameValuePairs.add(new BasicNameValuePair("lng", params[1]));
                nameValuePairs.add(new BasicNameValuePair("uid", pref
                        .getString("uid", "null")));
                nameValuePairs.add(new BasicNameValuePair("type", "request"));
                nameValuePairs.add(new BasicNameValuePair("rideraddress", street));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity);
                Log.i("Response", responseBody);
                // Log.i("Parameters", params[0]);


            } catch (ClientProtocolException e) {

                // TODO Auto-generated catch block
            } catch (IOException e) {

                // TODO Auto-generated catch block
            }

            return responseBody;

            // TODO Auto-generated method stub

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject data = new JSONObject(result);

                JSONObject driver_details = data.getJSONObject("driver");
                String id = driver_details.getString("id");
                String drivername = driver_details.getString("drivename")
                        .toUpperCase();
                String driver_lat = driver_details.getString("lat");
                String driver_lng = driver_details.getString("lng");
                String duration = driver_details.getString("duration");
                String carname = driver_details.getString("carname")
                        .toUpperCase();
                String carno = driver_details.getString("carno");
                String phone = driver_details.getString("phone");
                Log.i("id", id);
                Log.i("drivername", drivername);
                Log.i("lat", lat);
                Log.i("lng", lng);
                Log.i("duration", duration);
                Log.i("carname", carname);
                Log.i("carno", carno);
                Log.i("phone", phone);
                Intent intent = new Intent(getActivity(), CarStatus.class);
                intent.putExtra("id", id);
                intent.putExtra("drivername", drivername);
                intent.putExtra("lat", driver_lat);
                intent.putExtra("phone", phone);
                intent.putExtra("lng", driver_lng);
                intent.putExtra("client_lat", lat);
                intent.putExtra("client_lng", lng);
                intent.putExtra("duration", duration);
                intent.putExtra("carname", carname);
                intent.putExtra("carno", carno);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            } catch (JSONException e) {

                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }
    private class CancelRide extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            // Create a new HttpClient and Post Header

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    "http://128.199.134.210/api/request/cancel.php");
            String responseBody = null;


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("lat", params[0]));
                nameValuePairs.add(new BasicNameValuePair("lng", params[1]));




                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity);
                Log.i("Response", responseBody);
                // Log.i("Parameters", params[0]);

            } catch (ClientProtocolException e) {



                // TODO Auto-generated catch block
            } catch (IOException e) {

                // TODO Auto-generated catch block
            }
            return responseBody;

        }


        @Override
        protected void onPostExecute(String result) {



        }


    }



}