package com.example.lastmile;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context,
                                ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.drawable.drawer_list_item, null);
        }

        TextView itemname = (TextView) convertView.findViewById(R.id.item_name);

//		Typeface bold = Typeface.createFromAsset(context.getAssets(),
//				"fonts/bold.otf");
        Typeface regular = Typeface.createFromAsset(context.getAssets(),
                "fonts/regular.otf");
        itemname.setTypeface(regular);

        itemname.setText(navDrawerItems.get(position).getItem_name());

        return convertView;

    }

}