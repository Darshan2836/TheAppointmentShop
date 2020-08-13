package com.example.appointment;

public class BookedAppointmentInfo {
    private String shoptype,date, time, shopuid, appointmentinfo;
    private int bookingid;

    public BookedAppointmentInfo(String date, String time, String shopuid, String appointmentinfo, int bookingid,String shoptype) {
        this.date = date;
        this.time = time;
        this.shopuid = shopuid;
        this.appointmentinfo = appointmentinfo;
        this.bookingid = bookingid;
        this.shoptype = shoptype;
    }

    public BookedAppointmentInfo() {
    }

    public String getShoptype() {
        return shoptype;
    }

    public void setShoptype(String shoptype) {
        this.shoptype = shoptype;
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

    public String getShopuid() {
        return shopuid;
    }

    public void setShopuid(String shopuid) {
        this.shopuid = shopuid;
    }

    public String getAppointmentinfo() {
        return appointmentinfo;
    }

    public void setAppointmentinfo(String appointmentinfo) {
        this.appointmentinfo = appointmentinfo;
    }

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }
}
