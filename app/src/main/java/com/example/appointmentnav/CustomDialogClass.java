package com.example.appointmentnav;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.appointment.Common;
import com.example.login.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public String shopname,date,appointmentinfo,customeruid,bookingid;
    public ProgressDialog progressDialog;

    public CustomDialogClass(Activity a,String bookingid,String shopname,String customeruid,String date,String appointmentinfo) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.bookingid = bookingid;
        this.shopname = shopname;
        this.customeruid = customeruid;
        this.date =date;
        this.appointmentinfo =appointmentinfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cancel_appointment_dialogbox);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

         progressDialog = new ProgressDialog(c);
         progressDialog.setMessage("Canceling Appointment...!!");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                dismiss();
                progressDialog.show();
                DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("APPOINTMENTS").child(shopname).child(date).child(appointmentinfo);
                mref.removeValue();
                DatabaseReference mref1 = FirebaseDatabase.getInstance().getReference().child("USER").child(customeruid).child("UpcomingAppointment").child(bookingid);
                mref1.removeValue();
                progressDialog.dismiss();
                c.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }


}
