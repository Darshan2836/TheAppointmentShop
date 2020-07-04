package com.example.appointment;

import java.util.Calendar;

public class AppointmentInfo {

    private  String name;
    private String time;
    private String number;
    private String customeruid;

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

    public void setCustomeruid(String customeruid) {
        this.customeruid = customeruid;
    }

    public AppointmentInfo() {
    }

    public AppointmentInfo(String name, String time, String number,String customeruid) {
        this.name = name;
        this.time = time;
        this.number = number;
        this.customeruid = customeruid;
    }


}
