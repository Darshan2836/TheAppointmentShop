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

    private RecyclerView recyclerView;
    private ArrayList<shopuserinfo> arrayList;
    private FirebaseRecyclerOptions<shopuserinfo> options;
    private FirebaseRecyclerAdapter<shopuserinfo, FireBaseViewHolder> adapter;
    private DatabaseReference ref,ref1;
    private ProgressDialog dialog;


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        //check internet connection
        if(!isNetworkAvailable())
        {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }

        //progress dialog
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..!!");
        dialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.home_navigation,container,false);

        //find view by ID
        recyclerView = (RecyclerView) RootView.findViewById(R.id.recyclerview);

        //database refrence
        ref = FirebaseDatabase.getInstance().getReference().child("SHOP");
        ref.keepSynced(true);

        //recycler view
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        //array list
        arrayList = new ArrayList<shopuserinfo>();
        options = new FirebaseRecyclerOptions.Builder<shopuserinfo>().setQuery(ref,shopuserinfo.class).build();






        //adapter
        adapter = new FirebaseRecyclerAdapter<shopuserinfo, FireBaseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FireBaseViewHolder holder, int position, @NonNull final shopuserinfo model) {

                String imguri = null;
                //picasso
                Picasso.get().setLoggingEnabled(true);
                holder.shopname.setText(model.getShopname());
                holder.locality.setText(model.getLocality());
                holder.time.setText(model.getTime());
                imguri = model.getImage();
                Picasso.get().load(imguri).resize(100,100).into(holder.image);
                dialog.dismiss();
                final String finalImguri = imguri;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), RecyclerviewHolderEg.class);
                        intent.putExtra("shopnametext",model.getShopname());
                        intent.putExtra("addresstext",model.getAddress());
                        intent.putExtra("numbertext",model.getNumber());
                        intent.putExtra("timetext",model.getTime());
                        intent.putExtra("shopimage",model.getImage());
                        intent.putExtra("seatstext",model.getSeat());
                        intent.putExtra("shopuid",model.getUid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FireBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FireBaseViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.cardview,parent,false));
            }
        };


        recyclerView.setAdapter(adapter);

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
