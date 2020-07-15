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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Calendar;
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
    private List<String> availabilitylist = new ArrayList<>();
    private FloatingActionButton fab;
    private String finaldate = null;
    private String customername,customernumber;
    private DatabaseReference userData,mref,mref2,mref3,mref4;
    private FirebaseAuth mAuth;
    private String uid;
    private String appointmentnumber;
    private int i,j;
    private String count;
    private Dialog epicDialog;
    private Button success;
    private ProgressDialog progressDialog;
    private int bookingId = 0 ;


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



        //Database conectivity
        userData = FirebaseDatabase.getInstance().getReference("APPOINTMENTS");
        mref = FirebaseDatabase.getInstance().getReference("USER").child(uid);
        mref4 = FirebaseDatabase.getInstance().getReference("BOOKINGID");


        //get data from previous activity
        bundle = getIntent().getExtras();
        final String timestring = bundle.getString("time");
        final String shopname = bundle.getString("nameshop");
        final String totalseats = bundle.getString("seat");
        final String shopuid = bundle.getString("shopuid");

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

        //Get Shop address and number


        //time converstion
        try {
            timelist = gettime(timestring);
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
                finaldate = (dateFormat.format(date.getTime()));
                updateView(totalseats, shopname, gridLayoutManager);
            }
        });


        //epic Dialog
        epicDialog = new Dialog(this);





        //fab onclicklistner
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.CurrentTime != null) {
                    mref2 = FirebaseDatabase.getInstance().getReference("APPOINTMENTS").child(shopname).child(finaldate);
                    mref2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int i;
                            for (i = 1; i <= Integer.parseInt(totalseats); i++) {
                                final String myUserinfo = "Appointment" + i + " " + Common.CurrentTime;
                                if (!(dataSnapshot.hasChild(myUserinfo))) {
                                    //booking Id
                                    mref4.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // This method is called once with the initial value and again
                                           bookingId = dataSnapshot.getValue(Integer.class);
                                            bookingId++;
                                            updateBookingId(bookingId);
                                            final AppointmentInfo appointmentInfo = new AppointmentInfo(customername, Common.CurrentTime, customernumber,uid,bookingId,myUserinfo);
                                            final int finalBookingId = bookingId;
                                            userData.child(shopname).child(finaldate).child(myUserinfo).setValue(appointmentInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mref.child("UpcomingAppointment").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                               // int j=1;
                                                               // while(dataSnapshot.hasChild("Appointment"+j))  "Appointment"+j
                                                              // {
                                                              //      j++;
                                                              //  }
                                                                BookedAppointmentInfo bookedAppointmentInfo = new BookedAppointmentInfo(finaldate,Common.CurrentTime,shopuid,myUserinfo,finalBookingId);
                                                                mref.child("UpcomingAppointment").child(String.valueOf(bookingId)).setValue(bookedAppointmentInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful())
                                                                        {
                                                                            Toast.makeText(AppointmentBooking.this, "Appointment Booked", Toast.LENGTH_LONG).show();
                                                                            successDialog();
                                                                        }
                                                                        else
                                                                        {
                                                                            userData.child(shopname).child(finaldate).child(myUserinfo).removeValue();
                                                                            Toast.makeText(AppointmentBooking.this, "Something Went Wrong", Toast.LENGTH_LONG).show();

                                                                        }

                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(AppointmentBooking.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            // Failed to read value
                                        }
                                    });

                                    break;
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(AppointmentBooking.this,"Select a Time",Toast.LENGTH_LONG).show();
                }

            }
        });


    }



    private void updateBookingId(int bookingId)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BOOKINGID");

        myRef.setValue(bookingId);
    }


    //On Success Dialog Box
    private void successDialog()
    {
    epicDialog.setContentView(R.layout.dialogcustom);
    success = (Button) epicDialog.findViewById(R.id.okdialogbutton);
    success.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            epicDialog.dismiss();
            Intent intent = new Intent(AppointmentBooking.this, MenuPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    });
    epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    epicDialog.show();
    }





    private void updateView(final String totalseats, String shopname, final RecyclerView.LayoutManager gridLayoutManager) {
        //Check Availability
        //Progress Dialog
        progressDialog = new ProgressDialog(AppointmentBooking.this);
        progressDialog.setMessage("Loading..!!!");
        progressDialog.show();
        DatabaseReference mref1 = FirebaseDatabase.getInstance().getReference("APPOINTMENTS").child(shopname);
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




    //function to add time in list
    private List<String> gettime(String timestring1) throws ParseException {
        String t1,t2,m1,m2,ap1,ap2;
        char t1temp1,t1temp2;
        int i;
        List<String> templists = new ArrayList<>();
         String timestring = timestring1.replace(" ", "");
        t1 =timestring.substring(0,2);
        m1 = timestring.substring(3,5);
        t2 = timestring.substring(9,11);
        m2 = timestring.substring(13,14);
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
        templists.add(df.format(cal.getTime()));
        return templists;
        }

    }


