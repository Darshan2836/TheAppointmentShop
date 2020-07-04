package com.example.homenav;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appointment.AppointmentBooking;
import com.example.login.R;
import com.squareup.picasso.Picasso;

public class RecyclerviewHolderEg extends AppCompatActivity {
    private  Bundle bundle;
    private TextView shopname,address,number,time;
    private ImageView shopimage;
    private Button bookappointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_holder_eg);


        //find view by id
        shopname = (TextView) findViewById(R.id.shoptext);
        address = (TextView) findViewById(R.id.addresstext);
        number = (TextView) findViewById(R.id.numbertext);
        time = (TextView) findViewById(R.id.timetext);
        shopimage = (ImageView) findViewById(R.id.shopimg);
        bookappointment = (Button) findViewById(R.id.bookappointment);

        //get intent from
         bundle = getIntent().getExtras();
         shopname.setText(bundle.getString("shopnametext"));
         address.setText(bundle.getString("addresstext"));
         number.setText(bundle.getString("numbertext"));
         time.setText(bundle.getString("timetext"));
        Picasso.get().load(bundle.getString("shopimage")).into(shopimage);

        bookappointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecyclerviewHolderEg.this, AppointmentBooking.class);
                String timedata = bundle.getString("timetext");
                String namedata = bundle.getString("shopnametext");
                String seatdata = bundle.getString("seatstext");
                String addressdata = bundle.getString("addresstext");
                String localitydata = bundle.getString("localitytext");
                intent.putExtra("time",timedata);
                intent.putExtra("nameshop",namedata);
                intent.putExtra("seat",seatdata);
                intent.putExtra("address",addressdata);
                intent.putExtra("locality",localitydata);
                intent.putExtra("uriimage",bundle.getString("imageuri"));
                intent.putExtra("phone",bundle.getString("numbertext"));
                startActivity(intent);

            }
        });
    }
}