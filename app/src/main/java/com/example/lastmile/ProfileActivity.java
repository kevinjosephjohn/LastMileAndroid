package com.example.lastmile;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ProfileActivity extends NavigationDrawer {
    TextView emailid, name, number;
    SharedPreferences pref;
    Editor editor;

    String fname, lname, fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        emailid = (TextView) findViewById(R.id.emailid);
        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        fname = pref.getString("first_name", "");
        lname = pref.getString("last_name", "");
        fullname = fname + " " + lname;

        emailid.setText(pref.getString("email", ""));
        name.setText(fullname);
        number.setText(pref.getString("phone", ""));
        set();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // closing transition animations

    }
}
