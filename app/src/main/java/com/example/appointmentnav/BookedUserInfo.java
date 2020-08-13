package com.example.appointmentnav;

public class BookedUserInfo {
    public String time,date,shopuid,appointmentinfo,shoptype;
    public int bookingid;

    public BookedUserInfo() {
    }

    public BookedUserInfo( String time,String date,int bookingid,String shopuid,String appointmentinfo,String shoptype) {
        this.time = time;
        this.date = date;
        this.bookingid = bookingid;
        this.shopuid = shopuid;
        this.appointmentinfo =appointmentinfo;
        this.shoptype = shoptype;
    }

    public String getAppointmentinfo() {
        return appointmentinfo;
    }

    public void setAppointmentinfo(String appointmentinfo) {
        this.appointmentinfo = appointmentinfo;
    }

    public String getShoptype() {
        return shoptype;
    }

    public void setShoptype(String shoptype) {
        this.shoptype = shoptype;
    }

    public String getShopuid() {
        return shopuid;
    }

    public void setShopuid(String shopuid) {
        this.shopuid = shopuid;
    }

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
