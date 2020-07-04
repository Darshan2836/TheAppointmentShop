package com.example.appointment;

public class BookedAppointmentInfo {
    String name,locality,address,time,image,number,date;

    public BookedAppointmentInfo() {
    }

    public BookedAppointmentInfo(String name, String locality, String address, String time, String image, String number,String date) {
        this.name = name;
        this.locality = locality;
        this.address = address;
        this.time = time;
        this.image = image;
        this.number = number;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
