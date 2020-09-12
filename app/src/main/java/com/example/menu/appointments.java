package com.example.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.Adaptar;
import com.example.appointmentnav.AppointmentBill;
import com.example.appointmentnav.AppointmentCompletedBill;
import com.example.appointmentnav.BookedUserInfo;
import com.example.appointmentnav.FireBaseViewHolderPreviousAppointment;
import com.example.appointmentnav.FireBaseViewHolderUpcomingAppointment;
import com.example.appointmentnav.PreviousUserInfo;
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
import java.util.Objects;

public class appointments extends Fragment {
    private RecyclerView recyclerView, recyclerView2;
    private DatabaseReference ref, ref1;
    private ArrayList<BookedUserInfo> arrayList;
    private ArrayList<PreviousUserInfo> arrayList2;
    private FirebaseRecyclerOptions<BookedUserInfo> options;
    private FirebaseRecyclerOptions<PreviousUserInfo> options2;
    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseRecyclerAdapter<BookedUserInfo, FireBaseViewHolderUpcomingAppointment> adapter;
    private FirebaseRecyclerAdapter<PreviousUserInfo, FireBaseViewHolderPreviousAppointment> adapter2;
    private String shopname;
    private ProgressDialog progressDialog;
    private TextView emptyview, emptyview2;

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
       adapter2.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        adapter2.stopListening();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.appointment_navigation, container, false);


        //find view by ID
        recyclerView = (RecyclerView) RootView.findViewById(R.id.recycleupcomingappointment);
        recyclerView2 = RootView.findViewById(R.id.recyclepreviousappointment);
        emptyview2 = (TextView) RootView.findViewById(R.id.empty_view2);
        emptyview = (TextView) RootView.findViewById(R.id.empty_view);


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
        ref1 = FirebaseDatabase.getInstance().getReference().child("USER").child(uid).child("PreviousAppointments");
        ref1.keepSynced(true);
        ref.keepSynced(true);

        // To find if Upcoming Appointments are empty
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyview.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // To find if Previous Appointments are empty
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    recyclerView2.setVisibility(View.GONE);
                    emptyview2.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //recycler view1 layout manager
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        //layout manager
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(llm2);


        //array list
        arrayList = new ArrayList<BookedUserInfo>();
        options = new FirebaseRecyclerOptions.Builder<BookedUserInfo>().setQuery(ref, BookedUserInfo.class).build();

        //array list for previous appointments
        arrayList2 = new ArrayList<PreviousUserInfo>();
        options2 = new FirebaseRecyclerOptions.Builder<PreviousUserInfo>().setQuery(ref1, PreviousUserInfo.class).build();


        //adapter for upcoming appointments
        adapter = new FirebaseRecyclerAdapter<BookedUserInfo, FireBaseViewHolderUpcomingAppointment>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final FireBaseViewHolderUpcomingAppointment holder, int position, @NonNull final BookedUserInfo model) {

                final String datedata = model.getDate();
                final String timedata = model.getTime();
                final int bookingiddata = model.getBookingid();
                final String shoptype = model.getShoptype();

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
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("SHOP").child(shoptype).child(shopuid);
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
                        progressDialog.dismiss();
                        // Failed to read value
                    }
                });
                progressDialog.dismiss();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), AppointmentBill.class);
                        intent.putExtra("bookingid", String.valueOf(bookingiddata));
                        intent.putExtra("timetext", timedata);
                        intent.putExtra("datetext", datedata);
                        intent.putExtra("uidtext", shopuid);
                        intent.putExtra("appointmentinfotext", model.getAppointmentinfo());
                        intent.putExtra("shoptype", shoptype);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FireBaseViewHolderUpcomingAppointment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FireBaseViewHolderUpcomingAppointment(LayoutInflater.from(getActivity()).inflate(R.layout.cardview_appointment, parent, false));
            }
        };
        recyclerView.setAdapter(adapter);


        //adapter 2
        adapter2 = new FirebaseRecyclerAdapter<PreviousUserInfo, FireBaseViewHolderPreviousAppointment>(options2) {
            @Override
            protected void onBindViewHolder(@NonNull final FireBaseViewHolderPreviousAppointment holder, int position, @NonNull PreviousUserInfo model) {
                final String datedata = model.getDate();
                final String timedata = model.getTime();
                final String shopuid = model.getShopuid();
                final String shoptype = model.getShoptype();
                final String bookingid = model.getBookingid();
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
                holder.id.setText(String.valueOf(bookingid));

                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("SHOP").child(shoptype).child(shopuid);
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
                        progressDialog.dismiss();
                        // Failed to read value
                    }
                });
                progressDialog.dismiss();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), AppointmentCompletedBill.class);
                        intent.putExtra("bookingid",bookingid);
                        intent.putExtra("timetext", timedata);
                        intent.putExtra("datetext", datedata);
                        intent.putExtra("uidtext", shopuid);
                        intent.putExtra("shoptype", shoptype);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public FireBaseViewHolderPreviousAppointment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FireBaseViewHolderPreviousAppointment(LayoutInflater.from(getActivity()).inflate(R.layout.cardview_completed_appointment,parent,false));
            }
        };

        recyclerView2.setAdapter(adapter2);

        return RootView;
    }
}
