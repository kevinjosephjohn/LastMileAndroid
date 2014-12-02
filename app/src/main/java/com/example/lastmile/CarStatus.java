package com.example.lastmile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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

import at.markushi.ui.CircleButton;

public class CarStatus extends NavigationDrawer {
    TextView driver_name, driver_carname, driver_carnumber;
    CircleButton call;

    GoogleMap mMap;
    Context context;
    float numberXOffset;
    Typeface regular, bold, light;
    String id;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // overridePendingTransition(R.anim.activity_open_translate,
        // R.anim.activity_close_scale);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String drivername = intent.getStringExtra("drivername");
        Double driver_lat = Double.parseDouble(intent.getStringExtra("lat"));
        Double driver_lng = Double.parseDouble(intent.getStringExtra("lng"));
        Double client_lat = Double.parseDouble(intent
                .getStringExtra("client_lat"));
        Double client_lng = Double.parseDouble(intent
                .getStringExtra("client_lng"));
        String duration = intent.getStringExtra("duration");
        String[] separated = duration.split(" ");
        String carname = intent.getStringExtra("carname");
        String carno = intent.getStringExtra("carno");
        final String phone = intent.getStringExtra("phone");
        setContentView(R.layout.car_status);
        bold = Typeface.createFromAsset(this.getAssets(), "fonts/bold.otf");
        regular = Typeface.createFromAsset(this.getAssets(),
                "fonts/regular.otf");
        light = Typeface.createFromAsset(this.getAssets(), "fonts/light.otf");
        context = this;
        driver_name = (TextView) findViewById(R.id.drivername);
        driver_carname = (TextView) findViewById(R.id.carname);
        driver_carnumber = (TextView) findViewById(R.id.carnumber);
//        call = (CircleButton) findViewById(R.id.call);
//        call.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:" + phone));
//                startActivity(intent);
//
//            }
//        });
        findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(context);
            }
        });
        findViewById(R.id.call).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);

            }
        });
        // duration_number = (TextView) findViewById(R.id.eta_number);
        // duration_text = (TextView) findViewById(R.id.eta_text);
        // call = (Button) findViewById(R.id.call_button);
        // more = (Button) findViewById(R.id.more_button);

        driver_name.setText(drivername);
        driver_carname.setText(carname);
        driver_carnumber.setText(carno);
        // duration_number.setText(separated[0]);
        // duration_text.setText(separated[1]);

        driver_carname.setTypeface(regular);
        driver_carnumber.setTypeface(regular);
        // duration_number.setTypeface(bold);
        // duration_text.setTypeface(regular);
        driver_name.setTypeface(regular);
        // call.setTypeface(regular);
        // more.setTypeface(regular);

        Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/bold.otf");
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.pin_pickup_green);
        Bitmap mutableBitmap = icon.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint mPictoPaint = new Paint();
        mPictoPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPictoPaint.setColor(Color.WHITE);
        mPictoPaint.setTypeface(bold);
        Resources r = getResources();
        mPictoPaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 9, r.getDisplayMetrics()));

        float textYOffset = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
        float textXOffset = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 14, r.getDisplayMetrics());
        float numberYOffset = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 22, r.getDisplayMetrics());
        if (separated[0].length() == 2) {
            numberXOffset = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 17, r.getDisplayMetrics());
        } else {
            numberXOffset = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
        }

        canvas.drawText(separated[1].toUpperCase(), textXOffset, textYOffset,
                mPictoPaint);
        canvas.drawText(separated[0].toUpperCase(), numberXOffset,
                numberYOffset, mPictoPaint);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        // CameraPosition cameraPosition = new CameraPosition.Builder()
        // .target(new LatLng(client_lat, client_lng)).zoom(14).build();
        // mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        List<Marker> markers = new ArrayList<Marker>();
        Marker car = mMap.addMarker(new MarkerOptions().position(
                new LatLng(driver_lat, driver_lng)).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.car)));
        Marker user = mMap.addMarker(new MarkerOptions().position(
                new LatLng(client_lat, client_lng)).icon(
                BitmapDescriptorFactory.fromBitmap(mutableBitmap)));
        markers.add(car);
        markers.add(user);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        final LatLngBounds bounds = builder.build();

        OnCameraChangeListener listener = new OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition position) {

                // TODO Auto-generated method stub
                int padding = 100; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                        padding);
                mMap.moveCamera(cu);
                mMap.setOnCameraChangeListener(null);

            }
        };
        mMap.setOnCameraChangeListener(listener);

        set();

        // Log.i("id", id);
        // Log.i("drivername", drivername);
        // Log.i("lat", lat);
        // Log.i("lng", lng);
        // Log.i("duration", duration);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.carstatus, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.cancel:

                showDialog(context);

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // closing transition animations
        // overridePendingTransition(R.anim.activity_open_scale,
        // R.anim.activity_close_translate);
    }

    public void showDialog(final Context context) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cancel_dialog);
        dialog.setCancelable(false);
        Button yesButton = (Button) dialog.findViewById(R.id.yes);
        Button noButton = (Button) dialog.findViewById(R.id.no);
        yesButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {


                new CancelRide().execute();


            }

        });
        noButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }

        });
        dialog.show();

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
                    "http://128.199.134.210/api/request/");
            String responseBody = null;


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("type", "cancel"));
                nameValuePairs.add(new BasicNameValuePair("id", id));



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
            dialog.dismiss();
            Intent intent = new Intent(CarStatus.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);


        }


    }



}
