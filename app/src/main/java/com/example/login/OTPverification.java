package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OTPverification extends AppCompatActivity {

    private EditText Textcode;
    private Button verify;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private Bundle extras;
    private FirebaseAuth auth;
    private String verificationCode, userCode, finalnumber;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_pverification);


        //progress dialog1
        progressDialog = new ProgressDialog(OTPverification.this);
        progressDialog.setMessage("Verifying....!!");




        //viewByID
        Textcode = (EditText) findViewById(R.id.editTextcode);
        verify = (Button) findViewById(R.id.buttonverify);

        //get number from phoneNumber
        extras = getIntent().getExtras();
        final String number = extras.getString("number");
        finalnumber = number;


        //verify_button visible
        Textcode.addTextChangedListener(verifyvisibility);





        //callback
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //Callback function called
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {   //Auto OTP verification
                progressDialog.show();
                Toast.makeText(OTPverification.this, "Verification Successful", Toast.LENGTH_LONG);
                signup(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(OTPverification.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) { //Manual Otp Verification
                super.onCodeSent(s, forceResendingToken);
                progressDialog.dismiss();
                verificationCode = s;
                Toast.makeText(OTPverification.this, "Code sent", Toast.LENGTH_SHORT).show();
            }
        };


        //Otp verfication
        progressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,                     // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                OTPverification.this,        // Activity (for callback binding)
                mCallback);                      // OnVerificationStateChangedCallbacks


        //Verify Button
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                userCode = Textcode.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, userCode);
                signup(credential);


            }
        });
    }


    //Sign Up method called
    private void signup(PhoneAuthCredential credential) {
        auth = FirebaseAuth.getInstance();
        auth.signInWithCredential(credential).addOnCompleteListener(OTPverification.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(OTPverification.this, "Verification Successful", Toast.LENGTH_LONG).show();
                    delete();

                } else {
                    Toast.makeText(OTPverification.this, "Incorrect OTP", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void delete() {

        FirebaseUser currentuser = auth.getCurrentUser();
        currentuser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(OTPverification.this,"Verfication Successul",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(OTPverification.this,SignUp.class);
                    intent.putExtra("Number",finalnumber);
                    progressDialog.dismiss();
                    startActivity(intent);
                    finish();
                }
                else {
                    progressDialog.dismiss();

                    Toast.makeText(OTPverification.this,"Verfication failed",Toast.LENGTH_LONG).show();
                }
            }
        });
   }


    private  TextWatcher verifyvisibility = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() < 6) {
                verify.setAlpha((float) 0.4);
            } else {
                verify.setAlpha(1);
                verify.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }


    };


    }

