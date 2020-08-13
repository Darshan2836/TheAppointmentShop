package com.example.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.homenav.FireBaseViewHolder;
import com.example.homenav.NoInternet;
import com.example.homenav.RecyclerviewHolderEg;
import com.example.homenav.shopuserinfo;
import com.example.login.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HairSalon extends AppCompatActivity {

    private EditText searchbar;
    private View linear1,linear2;
    private ImageView close,search;
    private RecyclerView recyclerView;
    private ArrayList<shopuserinfo> arrayList;
    private ArrayList<shopuserinfo> arrayList2;
    private FirebaseRecyclerOptions<shopuserinfo> options;
    private FirebaseRecyclerAdapter<shopuserinfo, FireBaseViewHolder> adapter;
    private DatabaseReference ref;
    private ProgressDialog dialog;
    private Bundle extras;
    private String shoptype;


    @Override
    public void onStart() {
        super.onStart();
        //check internet connection
        if(!isNetworkAvailable())
        {
            Intent intent = new Intent(HairSalon.this, NoInternet.class);
            startActivity(intent);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hair_salon);

        //find view by id
        searchbar = (EditText) findViewById(R.id.search_bar);
        linear1 = (View) findViewById(R.id.linearlayout1);
        linear2 = (View) findViewById(R.id.linearlayout2);
        close = (ImageView) findViewById(R.id.close_img);
        search = (ImageView) findViewById(R.id.search_icon);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewshop);


        //search button on click listner
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear1.setVisibility(View.GONE);
                linear2.setVisibility(View.VISIBLE);
                searchbar.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchbar, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        //close button on click listner
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear1.setVisibility(View.VISIBLE);
                linear2.setVisibility(View.GONE);
            }
        });



        //initialize array
        arrayList2 = new ArrayList<>();

        //getshop type
        extras = getIntent().getExtras();
        shoptype = extras.getString("shoptype");


        //database refrence
        ref = FirebaseDatabase.getInstance().getReference().child("SHOP").child(shoptype);
        ref.keepSynced(true);

        //searchbar text listner
        searchbar.addTextChangedListener(searchText);


        //recycler view
        LinearLayoutManager llm = new LinearLayoutManager(HairSalon.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        //array list
        arrayList = new ArrayList<shopuserinfo>();
        options = new FirebaseRecyclerOptions.Builder<shopuserinfo>().setQuery(ref,shopuserinfo.class).build();

        //progress dialog
        dialog = new ProgressDialog(HairSalon.this);
        dialog.setMessage("Loading..!!");
        dialog.show();
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
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(HairSalon.this, RecyclerviewHolderEg.class);
                        intent.putExtra("shopnametext",model.getShopname());
                        intent.putExtra("addresstext",model.getAddress());
                        intent.putExtra("numbertext",model.getNumber());
                        intent.putExtra("timetext",model.getTime());
                        intent.putExtra("shopimage",model.getImage());
                        intent.putExtra("seatstext",model.getSeat());
                        intent.putExtra("shopuid",model.getUid());
                        intent.putExtra("shoptype",shoptype);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FireBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FireBaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false));
            }
        };


        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    //text watcher
    private TextWatcher searchText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!editable.toString().isEmpty())
            {
                updateView(editable.toString());
            }
            else
            {
                updateView("");
            }

        }
    };

    private void updateView(String toString) {
        String nameCapitalized;
        if(!toString.equals("")) {
            String s1 = toString.substring(0, 1).toUpperCase();
             nameCapitalized = s1 + toString.substring(1);
        }
        else {
             nameCapitalized = toString;
        }


        Query firebaseQuery = ref.orderByChild("locality").startAt(nameCapitalized).endAt(nameCapitalized+"\uf8ff");
        firebaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    arrayList2.clear();
                    for(DataSnapshot dss:dataSnapshot.getChildren())
                    {
                        final shopuserinfo shopuserinfooo = dss.getValue(shopuserinfo.class);
                        arrayList2.add(shopuserinfooo);
                    }

                    arrayList2.size();
                    MyAdaptar myAdaptar = new MyAdaptar(getApplicationContext(),arrayList2,shoptype);
                    recyclerView.setAdapter(myAdaptar);
                    myAdaptar.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //To check internet connections
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}