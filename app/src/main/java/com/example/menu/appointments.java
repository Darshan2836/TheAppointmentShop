package com.example.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointmentnav.AppointmentBill;
import com.example.appointmentnav.BookedUserInfo;
import com.example.appointmentnav.FireBaseViewHolderUpcomingAppointment;
import com.example.homenav.FireBaseViewHolder;
import com.example.homenav.NoInternet;
import com.example.homenav.RecyclerviewHolderEg;
import com.example.homenav.shopuserinfo;
import com.example.login.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class appointments extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference ref;
    private ArrayList<BookedUserInfo> arrayList;
    private FirebaseRecyclerOptions<BookedUserInfo> options;
    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseRecyclerAdapter<BookedUserInfo, FireBaseViewHolderUpcomingAppointment> adapter;
    private String shopname;
    private ProgressDialog progressDialog;
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.appointment_navigation,container,false);


        //find view by ID
        recyclerView = (RecyclerView) RootView.findViewById(R.id.recycleupcomingappointment);

        //Auth instance
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        uid = currentuser.getUid();


        //initialize progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading");
        progressDialog.show();
        //database refrence
        ref = FirebaseDatabase.getInstance().getReference().child("USER").child(uid).child("UpcomingAppointment");
        ref.keepSynced(true);

        //recycler view
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        //array list
        arrayList = new ArrayList<BookedUserInfo>();
        options = new FirebaseRecyclerOptions.Builder<BookedUserInfo>().setQuery(ref,BookedUserInfo.class).build();


        //adapter
        adapter = new FirebaseRecyclerAdapter<BookedUserInfo, FireBaseViewHolderUpcomingAppointment>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final FireBaseViewHolderUpcomingAppointment holder, int position, @NonNull final BookedUserInfo model) {

                final String datedata = model.getDate();
                final String timedata = model.getTime();
                final int bookingiddata = model.getBookingid();

                Date initDate = null;
                try {
                    initDate = new SimpleDateFormat("dd-MM-yyyy").parse(datedata);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
                final String parsedDate = formatter.format(initDate);
                holder.time.setText(timedata);
                holder.date.setText(parsedDate);
                holder.id.setText(String.valueOf(bookingiddata));


                final String shopuid = model.getShopuid();
                DatabaseReference myRef =FirebaseDatabase.getInstance().getReference().child("SHOP").child(shopuid);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        shopname = dataSnapshot.child("shopname").getValue().toString();
                        holder.name.setText(shopname);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), AppointmentBill.class);
                        intent.putExtra("bookingid",String.valueOf(bookingiddata));
                        intent.putExtra("timetext",timedata);
                        intent.putExtra("datetext",datedata);
                        intent.putExtra("uidtext",shopuid);
                        intent.putExtra("appointmentinfotext",model.getAppointmentinfo());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FireBaseViewHolderUpcomingAppointment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FireBaseViewHolderUpcomingAppointment(LayoutInflater.from(getActivity()).inflate(R.layout.cardview_appointment,parent,false));
            }
        };



        recyclerView.setAdapter(adapter);

        return  RootView;
    }
}
