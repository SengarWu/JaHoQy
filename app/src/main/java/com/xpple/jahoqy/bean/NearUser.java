package com.xpple.jahoqy.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by Koreleone on 2015/9/12.
 */
public class NearUser extends BmobObject {
    private User user;
    private BmobGeoPoint place;

    public BmobGeoPoint getPlace() {
        return place;
    }

    public void setPlace(BmobGeoPoint place) {
        this.place = place;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
