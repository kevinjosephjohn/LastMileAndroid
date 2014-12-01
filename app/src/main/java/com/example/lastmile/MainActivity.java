package com.example.lastmile;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends NavigationDrawer {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		overridePendingTransition(R.anim.activity_open_translate,
//				R.anim.activity_close_scale);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        set();


    }

    @Override
    protected void onPause() {
        super.onPause();
        // closing transition animations
//		overridePendingTransition(R.anim.activity_open_scale,
//				R.anim.activity_close_translate);
//		overridePendingTransition(R.anim.activity_open_translate,
//				R.anim.activity_close_scale);
    }

    public void hamburger(View v) {

        opendrawer();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
