package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PhoneNumber extends AppCompatActivity {

    private Button proceed;
    private EditText number;
private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number1);

        //findviewbyid
        proceed = (Button) findViewById(R.id.buttonproceed);
        number = (EditText) findViewById(R.id.editTextnumber);


        //progress dialog
        progressDialog = new ProgressDialog(PhoneNumber.this);
        progressDialog.setMessage("Please Wait...!!");

        //button visibility
        number.addTextChangedListener(numbertext);



        //proceed method
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Number = "+91" + number.getText().toString();

                progressDialog.show();
                //check mobile number exist
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("USER");
                userRef
                        .orderByChild("mobile")
                        .equalTo(Number)
                        .addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                progressDialog.dismiss();
                                if (dataSnapshot.getValue() != null) {
                                    //it means user already registered
                                    //Add code to show your prompt
                                    number.setError("Mobile Number already registed");
                                } else {

                                    //mobile number does not exist
                                    Intent intent = new Intent(PhoneNumber.this, OTPverification.class);
                                    intent.putExtra("number", Number);
                                    startActivity(intent);
                                    finish();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressDialog.dismiss();
                                Toast.makeText(PhoneNumber.this,databaseError.getMessage().toString(),Toast.LENGTH_LONG).show();

                            }
                        });

            }

        });
    }

    /*Intent intent = new Intent(PhoneNumber.this, OTPverification.class);
                            intent.putExtra("number", Number);
                            startActivity(intent);
                            finish();*/




    //Text Watcher
    private TextWatcher numbertext = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        String no = number.getText().toString();
        if(no.length() == 10)
        {
            proceed.setText("CONTINUE");
            proceed.setEnabled(true);
            proceed.setAlpha(1);
        }
        else
        {
            proceed.setEnabled(false);
            proceed.setText("ENTER PHONE NUMBER");
            proceed.setAlpha((float) 0.4);
        }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
