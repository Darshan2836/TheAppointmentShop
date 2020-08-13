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

import com.example.appointment.Common;
import com.example.menu.MenuPage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OTPverification extends AppCompatActivity {


    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private EditText Textcode;
    private Button verify;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private Bundle extras;
    private ProgressDialog pdialog;
    private FirebaseAuth mAuth;
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


        //initialize progress dialog
        pdialog = new ProgressDialog(OTPverification.this);
        pdialog.setMessage("Signing In");

        //initialize firebase auth
        mAuth = FirebaseAuth.getInstance();


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(OTPverification.this, gso);


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
                    if(Common.GoogleVerification == 1)
                    {
                        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("USER").child(Common.GoogleUid);
                        mref.child("mobile").setValue(finalnumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Common.GoogleUid = null;
                                    delete();
                                }
                            }
                        });
                    }
                    else
                    {
                        delete();
                    }

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
                if(task.isSuccessful()) {
                    if (Common.GoogleVerification == 1) {
                        Common.GoogleVerification = 0;
                        GoogleResignin();

                    } else {
                        Intent intent = new Intent(OTPverification.this, SignUp.class);
                        intent.putExtra("Number", finalnumber);
                        progressDialog.dismiss();
                        startActivity(intent);
                    }
                }
                else {
                    progressDialog.dismiss();

                    Toast.makeText(OTPverification.this,"Verfication failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void GoogleResignin() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        pdialog.show();
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Toast.makeText(LoginPage.this,"Successfully Signed In",Toast.LENGTH_LONG).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                pdialog.dismiss();
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                //Toast.makeText(LoginPage.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pdialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(OTPverification.this,"Successfully Signed In",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(OTPverification.this, MenuPage.class);
                            progressDialog.dismiss();
                            startActivity(intent);
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(OTPverification.this,"Something went wrong..!!",Toast.LENGTH_LONG).show();
                            // updateUI(null);
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

