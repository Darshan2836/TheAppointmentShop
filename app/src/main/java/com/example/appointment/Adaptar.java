package com.example.appointment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;

import java.util.List;

public class Adaptar extends RecyclerView.Adapter<Adaptar.ViewHolder> {
    List<String> timelist;
    List<String> availabilitylist;
    LayoutInflater inflater;
    int selectedPosition=-1;

    public Adaptar(List<String> timelist, List<String> availabilitylist , Context ctx) {
        this.timelist = timelist;
        this.availabilitylist = availabilitylist;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public Adaptar.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.grid_cardview_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adaptar.ViewHolder holder, final int position) {


        holder.availability.setText(availabilitylist.get(position));
        holder.time.setText(timelist.get(position));
        if(availabilitylist.get(position).equals("Full"))
        {
            holder.linear.setBackgroundColor(Color.parseColor("#CFCFCF"));
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        if(selectedPosition==position) {
            Common.CurrentTime = timelist.get(position);
            if(availabilitylist.get(position).equals("Full"))
            {
                Common.CurrentAvaliability = 1;
            }
            else
            {
                Common.CurrentAvaliability = 0;
                holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            holder.itemView.setBackgroundColor(Color.parseColor("#F3CA20"));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition= position;
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return timelist.size();
    }


    
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView time;
        TextView availability;
        View linear;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linear = itemView.findViewById(R.id.linearcard);
            time = itemView.findViewById(R.id.timecard);
            availability = itemView.findViewById(R.id.availabilitycard);

        }
    }


}
