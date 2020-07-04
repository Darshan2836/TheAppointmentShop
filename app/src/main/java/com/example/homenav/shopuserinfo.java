package com.example.homenav;

import android.net.Uri;

public class shopuserinfo {
    private String shopname,locality,time,image,address,number,seat;

    public String getAddress() {
        return address;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public shopuserinfo() {
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public shopuserinfo(String shopname, String locality, String time,String image,String address,String number,String seat) {
        this.shopname = shopname;
        this.locality = locality;
        this.time = time;
        this.image = image;
        this.address = address;
        this.number = number;
        this.seat = seat;
    }
}
