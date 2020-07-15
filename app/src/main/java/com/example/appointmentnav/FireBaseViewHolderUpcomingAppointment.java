package com.example.appointmentnav;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;

import java.text.BreakIterator;

public class FireBaseViewHolderUpcomingAppointment extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView time;
    public TextView date;
    public TextView id;


    public FireBaseViewHolderUpcomingAppointment(@NonNull View itemView) {
        super(itemView);


        name = itemView.findViewById(R.id.upcomingshop);
        date = itemView.findViewById(R.id.upcomingdate);
        time = itemView.findViewById(R.id.upcomingtime);
        id = itemView.findViewById(R.id.bookingidtext);
    }
}
