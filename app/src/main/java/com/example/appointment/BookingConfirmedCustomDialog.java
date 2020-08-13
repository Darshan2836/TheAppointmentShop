package com.example.appointment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.login.R;
import com.example.menu.MenuPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookingConfirmedCustomDialog extends Dialog implements
        android.view.View.OnClickListener  {


    public Activity c;
    public Dialog d;
    public Button yes, no;
    private DatabaseReference mref2,mref4,userData,mref;
    private Dialog epicDialog;
    private Button success;
    private String shopname,finaldate,totalseats,customername,customernumber,uid,shopuid,shoptype;
    private ProgressDialog progressDialog;

    public BookingConfirmedCustomDialog(Activity a, String shopname, String finaldate, String totalseats, String customername, String customernumber, String uid, String shopuid,String shoptype) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.shopname = shopname;
        this.finaldate = finaldate;
        this.totalseats = totalseats;
        this.customername = customername;
        this.customernumber = customernumber;
        this.uid = uid;
        this.shopuid = shopuid;
        this.shoptype = shoptype;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.book_appointment_dialogbox);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        //database reference
        mref4 = FirebaseDatabase.getInstance().getReference("BOOKINGID");
        userData = FirebaseDatabase.getInstance().getReference("APPOINTMENTS").child(shoptype);
        mref = FirebaseDatabase.getInstance().getReference("USER").child(uid);

        //epic Dialog
        epicDialog = new Dialog(c);
        //progress dialog
        progressDialog = new ProgressDialog(c);
        progressDialog.setMessage("Booking Your Appointment...!!!");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                dismiss();
                progressDialog.show();
                    mref2 = FirebaseDatabase.getInstance().getReference("APPOINTMENTS").child(shoptype).child(shopname).child(finaldate);
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
                                            int bookingId = dataSnapshot.getValue(Integer.class);
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
                                                                BookedAppointmentInfo bookedAppointmentInfo = new BookedAppointmentInfo(finaldate,Common.CurrentTime,shopuid,myUserinfo,finalBookingId,shoptype);
                                                                mref.child("UpcomingAppointment").child(String.valueOf(finalBookingId)).setValue(bookedAppointmentInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful())
                                                                        {
                                                                            Common.CurrentTime = null;
                                                                            Toast.makeText(c, "Appointment Booked", Toast.LENGTH_LONG).show();
                                                                            progressDialog.dismiss();
                                                                            successDialog();
                                                                        }
                                                                        else
                                                                        {
                                                                            userData.child(shopname).child(finaldate).child(myUserinfo).removeValue();
                                                                            Toast.makeText(c, "Something Went Wrong", Toast.LENGTH_LONG).show();

                                                                        }

                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(c, "Something Went Wrong", Toast.LENGTH_LONG).show();
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

                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
    }




    //update booking id
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
                Intent intent = new Intent(c, MenuPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                c.startActivity(intent);
            }
        });
        epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicDialog.show();
    }

}


