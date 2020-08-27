package com.example.appointmentnav;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;

public class FireBaseViewHolderPreviousAppointment extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView time;
    public TextView date;
    public TextView id;



    public FireBaseViewHolderPreviousAppointment(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.previousshop);
        date = itemView.findViewById(R.id.previousdate);
        time = itemView.findViewById(R.id.previoustime);
        id = itemView.findViewById(R.id.bookingiddata);
    }
}
