package com.example.menu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homenav.FireBaseViewHolder;
import com.example.homenav.RecyclerviewHolderEg;
import com.example.homenav.shopuserinfo;
import com.example.login.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdaptar extends RecyclerView.Adapter<MyAdaptar.MyAdaptarViewHolder> {

    public Context c;
    public ArrayList<shopuserinfo> arrayList;
    public String shoptype;

    public MyAdaptar(Context c, ArrayList<shopuserinfo> arrayList,String shoptype) {
        this.c = c;
        this.arrayList = arrayList;
        this.shoptype = shoptype;
    }

    @NonNull
    @Override
    public MyAdaptarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
        return new MyAdaptarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdaptarViewHolder holder, int position) {

        final shopuserinfo shopuserinfoo = arrayList.get(position);
        String imguri = null;
        //picasso
        Picasso.get().setLoggingEnabled(true);
        holder.shopname.setText(shopuserinfoo.getShopname());
        holder.locality.setText(shopuserinfoo.getLocality());
        holder.time.setText(shopuserinfoo.getTime());
        imguri = shopuserinfoo.getImage();
        Picasso.get().load(imguri).resize(100,100).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(c, RecyclerviewHolderEg.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("shopnametext",shopuserinfoo.getShopname());
                intent.putExtra("addresstext",shopuserinfoo.getAddress());
                intent.putExtra("numbertext",shopuserinfoo.getNumber());
                intent.putExtra("timetext",shopuserinfoo.getTime());
                intent.putExtra("shopimage",shopuserinfoo.getImage());
                intent.putExtra("seatstext",shopuserinfoo.getSeat());
                intent.putExtra("shopuid",shopuserinfoo.getUid());
                intent.putExtra("shoptype",shoptype);
                c.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyAdaptarViewHolder extends RecyclerView.ViewHolder
    {
        public TextView shopname,locality,time;
        public ImageView image;

        public MyAdaptarViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.salonimg);
            shopname = itemView.findViewById(R.id.shopname);
            locality = itemView.findViewById(R.id.locality);
            time = itemView.findViewById(R.id.time);
        }
    }
}
