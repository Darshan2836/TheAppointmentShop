package com.example.homenav;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;

public class FireBaseViewHolder extends RecyclerView.ViewHolder{

     public TextView shopname,locality,time;
     public ImageView image;

    public FireBaseViewHolder(@NonNull View itemView) {
        super(itemView);

        image = itemView.findViewById(R.id.salonimg);
        shopname = itemView.findViewById(R.id.shopname);
        locality = itemView.findViewById(R.id.locality);
        time = itemView.findViewById(R.id.time);
    }
}
