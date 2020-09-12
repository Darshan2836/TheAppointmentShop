package com.example.appointmentnav;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.login.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppointmentCompletedBill extends AppCompatActivity {

    private Bundle bundle;
    private TextView shopname,address,time,date,bookingid;
    private ImageView shopimage;
    private Button cancel;
    private String shopnametext,shopaddresstext,shopimagetext;
    private Date datedate;
    private String datetext;
    private String bookingidtext,shopuid,timetext,customeruid,appointmentinfo;
    private ProgressDialog progressDialog;
    private String shoptype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_completed_bill);

        //tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //initialize progress dialog
        progressDialog = new ProgressDialog(AppointmentCompletedBill.this);
        progressDialog.setMessage("Loading");
        progressDialog.show();


        //findview by Id
        shopname = (TextView) findViewById(R.id.billname);
        address = (TextView) findViewById(R.id.billaddress);
        time = (TextView) findViewById(R.id.billtime);
        date = (TextView) findViewById(R.id.billdate);
        bookingid = (TextView) findViewById(R.id.billbookingid);
        shopimage = (ImageView) findViewById(R.id.billimage);
        cancel = (Button) findViewById(R.id.cancelappointment);


        //customeruid
        //Auth instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        customeruid = currentuser.getUid();

        // get info from previous activity and setText
        bundle = getIntent().getExtras();
        shopuid = bundle.getString("uidtext");
        datetext = bundle.getString("datetext");
        timetext = bundle.getString("timetext");
        bookingidtext = bundle.getString("bookingid");
        shoptype = bundle.getString("shoptype");



        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("SHOP").child(shoptype).child(shopuid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                shopnametext = dataSnapshot.child("shopname").getValue().toString();
                shopaddresstext = dataSnapshot.child("address").getValue().toString();
                shopimagetext = dataSnapshot.child("image").getValue().toString();
                shopname.setText(shopnametext);
                address.setText(shopaddresstext);
                Picasso.get().load(shopimagetext).into(shopimage);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        time.setText(timetext);
        bookingid.setText(bookingidtext);
        try {
            datedate = new SimpleDateFormat("dd-MM-yyyy").parse(datetext);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
        final String parsedDate = formatter.format(datedate);
        date.setText(parsedDate);


    }
    }
