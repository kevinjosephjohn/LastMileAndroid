package com.example.lastmile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.dd.CircularProgressButton;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateAccountActivity extends Activity {
    String fname, lname, pass, uname, mobile;
    EditText email, password, firstname, lastname, phone;
    Context context = this;
    boolean status_internet;
    InternetUtils check;
    SharedPreferences pref;
    Editor editor;
    CircularProgressButton create_button;

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.createaccount);


        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        firstname = (EditText) findViewById(R.id.firstname_login);
        lastname = (EditText) findViewById(R.id.lastname_login);
        phone = (EditText) findViewById(R.id.phone_login);
        email = (EditText) findViewById(R.id.email_login);
        password = (EditText) findViewById(R.id.password_login);
        create_button = (CircularProgressButton) findViewById(R.id.create_button);

        check = new InternetUtils();
        status_internet = check.isConnected(context);
        if (!status_internet)
            showDialog(context);

    }

    public void login(View v) {
        int flag_email = 0, flag_pass = 0, flag_first = 0, flag_last = 0, flag_phone = 0;
        if (firstname.getText().length() == 0) {
            firstname.setError("First Name Cannot Be Empty");
            flag_first = 1;
        }
        if (lastname.getText().length() == 0) {
            lastname.setError("Last Name Cannot Be Empty");
            flag_last = 1;
        }
        if (phone.getText().length() < 10) {
            phone.setError("Enter a valid phone number");
            flag_phone = 1;
        }

        if (email.getText().length() == 0) {

            flag_email = 1;
        }
        if (password.getText().length() < 8) {
            password.setError("Password should contain minimum 8 characters");
            flag_pass = 1;
        }

        if (flag_email == 0 && flag_pass == 0 && flag_first == 0
                && flag_last == 0 && flag_phone == 0)

        {

            fname = firstname.getText().toString();
            lname = lastname.getText().toString();
            mobile = phone.getText().toString();
            uname = email.getText().toString();
            pass = password.getText().toString();
            status_internet = check.isConnected(context);
            if (!status_internet)
                showDialog(context);
            else {
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute(fname, lname, mobile, uname, pass);
            }

        }

    }

    public void showDialog(final Context context) {

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
                        dialogButton.setClickable(false);
                    }

                    public void onFinish() {

                        if (!check.isConnected(context)) {

                            dialogButton.setProgress(0);
                            dialog.show();
                        } else {
                            dialog.dismiss();
                            recreate();
                        }

                    }
                }.start();

            }

        });
        dialog.show();

    }

    @Override
    public void recreate() {

        super.recreate();

    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    "http://128.199.134.210/api/authenticate/");
            String responseBody = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("type", "register"));
                nameValuePairs.add(new BasicNameValuePair("fname", params[0]));
                nameValuePairs.add(new BasicNameValuePair("lname", params[1]));
                nameValuePairs.add(new BasicNameValuePair("phone", params[2]));
                nameValuePairs.add(new BasicNameValuePair("email", params[3]));
                nameValuePairs
                        .add(new BasicNameValuePair("password", params[4]));
                nameValuePairs.add(new BasicNameValuePair("gcm_regid", pref
                        .getString("registration_id", "")));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity);
                Log.i("Response", responseBody);
                // Log.i("Parameters", params[0]);

            } catch (ClientProtocolException e) {
                showDialog(context);
                // TODO Auto-generated catch block
            } catch (IOException e) {
                showDialog(context);
                // TODO Auto-generated catch block
            }
            return responseBody;

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject data = new JSONObject(result);

                String error = data.getString("error");

                if (error.equals("0")) {
                    JSONObject user_details = data.getJSONObject("user");
                    String fname = user_details.getString("fname");
                    String lname = user_details.getString("lname");
                    String email = user_details.getString("email");
                    String phone = user_details.getString("phone");
                    String uid = user_details.getString("uid");

                    editor.putString("is_login", "true");
                    editor.putString("first_name", fname);
                    editor.putString("last_name", lname);
                    editor.putString("email", email);
                    editor.putString("phone", phone);
                    editor.putString("uid", uid);

                    editor.commit();

                    create_button.setProgress(100);
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            create_button.setClickable(false);
                        }

                        public void onFinish() {

                            Intent intent = new Intent(
                                    CreateAccountActivity.this,
                                    MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                    }.start();
                } else {
                    String error_msg = data.getString("error_msg");
                    if (error.equals("5") || error.equals("6")) {
                        email.setError(error_msg);
                        create_button.setProgress(-1);
                        new CountDownTimer(2000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                create_button.setClickable(false);
                            }

                            public void onFinish() {
                                create_button.setClickable(true);
                                create_button.setProgress(0);

                            }
                        }.start();
                    }
                    if (error.equals("7")) {
                        phone.setError(error_msg);
                        create_button.setProgress(-1);
                        new CountDownTimer(2000, 1000) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {

                                create_button.setProgress(0);

                            }
                        }.start();

                    }
                }
            } catch (JSONException e) {
                showDialog(context);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

            phone = (EditText) findViewById(R.id.phone_login);
            email = (EditText) findViewById(R.id.email_login);
            create_button = (CircularProgressButton) findViewById(R.id.create_button);
            create_button.setIndeterminateProgressMode(true);
            create_button.setProgress(50);
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {

            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

}
