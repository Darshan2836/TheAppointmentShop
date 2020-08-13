package com.example.appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.appointmentnav.AppointmentBill;
import com.example.appointmentnav.CustomDialogClass;
import com.example.login.R;
import com.example.menu.MenuPage;
import com.example.menu.home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class AppointmentBooking extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    RecyclerView datalist;
    private  Bundle bundle;
    private List<String> timelist = new ArrayList<>();
    private List<String> timelisttemp = new ArrayList<>();
    private List<String> availabilitylist = new ArrayList<>();
    private FloatingActionButton fab;
    private String finaldate = null,defaultdate = null;
    private String customername,customernumber;
    private DatabaseReference mref;
    private FirebaseAuth mAuth;
    private String uid;
    private String shoptype;
    private int i,j;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking);

         //find view by ID
        datalist = (RecyclerView) findViewById(R.id.recyclerviewbook);
        fab = (FloatingActionButton) findViewById(R.id.fab);



        //Auth instance
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        uid = currentuser.getUid();



        //get data from previous activity
        bundle = getIntent().getExtras();
        final String timestring = bundle.getString("time");
        final String shopname = bundle.getString("nameshop");
        final String totalseats = bundle.getString("seat");
        final String shopuid = bundle.getString("shopuid");
          shoptype = bundle.getString("shoptype");


        //Database conectivity
        mref = FirebaseDatabase.getInstance().getReference("USER").child(uid);


        //Get name n number of customer
        // Read from the database
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                customername = dataSnapshot.child("name").getValue().toString();
                customernumber = dataSnapshot.child("mobile").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });



        //get availability list
        try {
            timelisttemp = gettime(timestring);
            timelist = getTodayTime(timelisttemp);
            availabilitylist = getAvailability();
        } catch (ParseException e) {
            e.printStackTrace();
        }



        //set grid layout
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3, GridLayoutManager.VERTICAL,false);



        //set default date
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.US);
        Calendar cal = Calendar.getInstance();
        finaldate = (dateFormat.format(cal.getTime()));
        updateView(totalseats, shopname, gridLayoutManager);




        /* starts before 1 month from now */
        final Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 0);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 4);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView, int dx, int dy) {
                super.onCalendarScroll(calendarView, dx, dy);
            }

            @Override
            public void onDateSelected(Calendar date, int position) {
                Calendar cal = Calendar.getInstance();
                defaultdate = (dateFormat.format(cal.getTime()));
                finaldate = (dateFormat.format(date.getTime()));
                if(defaultdate.equals(finaldate))
                {
                    //get availability list when date is default(i.e today's date)
                    try {
                        timelisttemp = gettime(timestring);
                        timelist = getTodayTime(timelisttemp);
                        availabilitylist = getAvailability();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    //when other date is selected
                    try {
                        timelist = gettime(timestring);
                        availabilitylist = getAvailability();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                updateView(totalseats, shopname, gridLayoutManager);
            }
        });






        //fab onclicklistner
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Common.CurrentTime != null) {
                    if (Common.CurrentAvaliability == 0) {

                        BookingConfirmedCustomDialog cdd = new BookingConfirmedCustomDialog(AppointmentBooking.this, shopname, finaldate, totalseats, customername, customernumber, uid, shopuid ,shoptype);
                        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        cdd.show();
                    }
                    else
                    {
                        Toast.makeText(AppointmentBooking.this,"Slot is Full",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(AppointmentBooking.this,"Select a Time",Toast.LENGTH_LONG).show();
                }


            }
        });


    }


    //function to get today's time
    private List<String> getTodayTime(List<String> timelist) throws ParseException {
        int i=0;
        DateFormat df = new SimpleDateFormat("hh:mm aa");
         List<String> templists2= new ArrayList<>();
        Calendar cal2 = Calendar.getInstance();
        for (i=0;i<timelist.size();i++)
        {
            Date date1 = df.parse(timelist.get(i));
            Date date2 = df.parse(df.format(cal2.getTime()));
            if(date1.after(date2))
            {
                templists2.add(timelist.get(i));
            }
        }
        return  templists2;
    }


    //update timings on date change or when slots are booked
    private void updateView(final String totalseats, String shopname, final RecyclerView.LayoutManager gridLayoutManager) {
        //Check Availability
        //Progress Dialog
        progressDialog = new ProgressDialog(AppointmentBooking.this);
        progressDialog.setMessage("Loading..!!!");
        progressDialog.show();
        DatabaseReference mref1 = FirebaseDatabase.getInstance().getReference("APPOINTMENTS").child(shoptype).child(shopname);
        mref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (i = 0; i < timelist.size(); i++) {
                    final String temptime = timelist.get(i);
                    int x = 0;
                    for (j = 1; j <= Integer.parseInt(totalseats); j++) {
                        final String myUserinfo = "Appointment" + j + " " + temptime;
                        if (snapshot.child(finaldate).hasChild(myUserinfo)) {
                            x++;
                        }
                    }
                    if (x == Integer.parseInt(totalseats)) {
                        availabilitylist.set(i,"Full");
                    }
                    else
                    {
                        availabilitylist.set(i, "Available");
                    }
                }
                Log.d("list", availabilitylist.toString());


                //Time slot adapter settings
                adapter = (RecyclerView.Adapter) new Adaptar(timelist,availabilitylist,AppointmentBooking.this);
                datalist.setLayoutManager(gridLayoutManager);
                datalist.setAdapter((RecyclerView.Adapter) adapter);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }




    //function for availability
    private List<String> getAvailability() throws ParseException {
        List<String> temp = new ArrayList<>();
        int i;
        for(i=0;i<timelist.size();i++)
        {
            temp.add("Available");
        }
        return  temp;
    }



    //get today's time according to time



    //function to add time in list
    private List<String> gettime(String timestring1) throws ParseException {
        String t1,t2,m1,m2,ap1,ap2;
        List<String> templists = new ArrayList<>();
         String timestring = timestring1.replace(" ", "");
        t1 =timestring.substring(0,2);
        m1 = timestring.substring(3,5);
        t2 = timestring.substring(9,11);
        m2 = timestring.substring(12,14);
        ap1 = timestring.substring(5,7);
        ap2 = timestring.substring(14);

        DateFormat df = new SimpleDateFormat("hh:mm aa");
        Calendar cal = Calendar.getInstance();
        Calendar cal1 = Calendar.getInstance();
        cal.set(Calendar.HOUR, Integer.parseInt(t1));
        cal.set(Calendar.MINUTE, Integer.parseInt(m1));
        if(ap1.equalsIgnoreCase("AM")){cal.set(Calendar.AM_PM, 0);}else {cal.set(Calendar.AM_PM, 1);}

        cal1.set(Calendar.HOUR, Integer.parseInt(t2));
        cal1.set(Calendar.MINUTE, Integer.parseInt(m2));
        if(ap2.equalsIgnoreCase("AM")){cal1.set(Calendar.AM_PM, 0);}else {cal1.set(Calendar.AM_PM, 1);}
        while (!(df.format(cal.getTime()).equals(df.format(cal1.getTime()))) ){
            templists.add(df.format(cal.getTime()));
            cal.add(Calendar.MINUTE, 30);
        }

        return templists;
        }

    }


