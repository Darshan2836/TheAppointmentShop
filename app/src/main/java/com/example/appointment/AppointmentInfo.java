package com.example.appointment;

import java.util.Calendar;

public class AppointmentInfo {

    private  String name;
    private String time;
    private String number;
    private String customeruid;
    private int bookingid;
    private String appointmentinfo;


    public String getAppointmentinfo() {
        return appointmentinfo;
    }

    public void setAppointmentinfo(String appointmentinfo) {
        this.appointmentinfo = appointmentinfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCustomeruid() {
        return customeruid;
    }

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }

    public void setCustomeruid(String customeruid) {
        this.customeruid = customeruid;
    }

    public AppointmentInfo() {
    }

    public AppointmentInfo(String name, String time, String number,String customeruid,int bookingid,String appointmentinfo) {
        this.name = name;
        this.time = time;
        this.number = number;
        this.customeruid = customeruid;
        this.bookingid = bookingid;
        this.appointmentinfo = appointmentinfo;
    }


}
