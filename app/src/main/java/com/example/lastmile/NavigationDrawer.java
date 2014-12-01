package com.example.lastmile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class NavigationDrawer extends ActionBarActivity {

    public DrawerLayout mDrawerLayout;
    SharedPreferences pref;
    Editor editor;
    Typeface regular, bold, light;
    private String[] mDrawerItems;
    private String[] mDrawerItemsDescription;
    private ListView mDrawerList;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigationdrawer);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

    }

    public void set() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerItems = getResources().getStringArray(R.array.drawer_items);
        mDrawerItemsDescription = getResources().getStringArray(
                R.array.drawer_items_description);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        navDrawerItems = new ArrayList<NavDrawerItem>();
        for (int i = 0; i < 4; i++)
            navDrawerItems.add(new NavDrawerItem(mDrawerItems[i],
                    mDrawerItemsDescription[i]));
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());


    }

    public void opendrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    private void displayView(int position) {
        // update the main content by replacing fragments

        switch (position) {
            case 0:
                mDrawerLayout.closeDrawers();

                new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {

                        startActivity(new Intent(NavigationDrawer.this,
                                ProfileActivity.class));

                    }
                }.start();

                break;
            case 1:
                Toast.makeText(getApplicationContext(), "Menu item 02",
                        Toast.LENGTH_LONG).show();
                mDrawerLayout.closeDrawers();
                break;
            case 2:
                Toast.makeText(getApplicationContext(), "Menu item 03",
                        Toast.LENGTH_LONG).show();
                mDrawerLayout.closeDrawers();
                break;
            case 3:
                pref.edit().clear().commit();
                startActivity(new Intent(NavigationDrawer.this,
                        SplashPageActivity.class));
                finish();
                break;

            default:
                Toast.makeText(getApplicationContext(), "Nothing Selected",
                        Toast.LENGTH_LONG).show();
                mDrawerLayout.closeDrawers();
                break;
        }

    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

}
