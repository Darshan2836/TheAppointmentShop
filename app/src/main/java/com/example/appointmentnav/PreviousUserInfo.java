package com.example.appointmentnav;

public class PreviousUserInfo {
    String date,shopuid,time,shoptype,bookingid;

    public PreviousUserInfo(String date, String shopuid, String time,String shoptype,String bookingid) {
        this.date = date;
        this.shopuid = shopuid;
        this.time = time;
        this.shoptype = shoptype;
        this.bookingid = bookingid;
    }

    public String getBookingid() {
        return bookingid;
    }

    public void setBookingid(String bookingid) {
        this.bookingid = bookingid;
    }

    public PreviousUserInfo() {
    }

    public String getDate() {
        return date;
    }

    public String getShoptype() {
        return shoptype;
    }

    public void setShoptype(String shoptype) {
        this.shoptype = shoptype;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShopuid() {
        return shopuid;
    }

    public void setShopuid(String shopuid) {
        this.shopuid = shopuid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
