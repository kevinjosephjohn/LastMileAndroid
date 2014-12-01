package com.example.lastmile;

public class NavDrawerItem {

    private String item_name;
    private String item_description = "0";

    public NavDrawerItem() {
    }

    public NavDrawerItem(String name, String description) {
        this.item_name = name;
        this.item_description = description;
    }


    public String getItem_name() {
        return this.item_name;
    }

    public void setItem_name(String title) {
        this.item_name = title;
    }

    public String getItem_description() {
        return this.item_description;
    }

    public void setItem_description(String count) {
        this.item_description = count;
    }


}
