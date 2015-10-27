package com.lessask;

/**
 * Created by huangji on 2015/10/27.
 */
public class DrawerItem {
    private int icon;
    private String name;

    public DrawerItem(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
