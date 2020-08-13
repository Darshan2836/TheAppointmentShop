package com.example.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homenav.FireBaseViewHolder;
import com.example.homenav.NoInternet;
import com.example.homenav.RecyclerviewHolderEg;
import com.example.homenav.shopuserinfo;
import com.example.login.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class home extends Fragment {

private Button button,button2;

    @Override
    public void onStart() {
        super.onStart();
        //check internet connection
        if(!isNetworkAvailable())
        {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.home_navigation,container,false);

        //find view by ID
        button = (Button) RootView.findViewById(R.id.button99);
        button2 = (Button) RootView.findViewById(R.id.button100);



        //salon shop on click listner
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(),HairSalon.class);
                intent.putExtra("shoptype","hairsalon");
                startActivity(intent);
            }
        });


        //beauty palour shop on click listner
        //salon shop on click listner
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(),HairSalon.class);
                intent.putExtra("shoptype","beautypalour");
                startActivity(intent);
            }
        });

        return  RootView;
    }
    //To check internet connections
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
