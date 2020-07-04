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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menu.MenuPage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginPage.this";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private Button login;
    private TextView register;
    private EditText vemail;
    private EditText vpassword;
    private String email,password;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private ProgressDialog pdialog;
    private ImageView google;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference userData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        login = (Button) findViewById(R.id.loginbutton);
        vemail = (EditText) findViewById(R.id.editTextuser);
        vpassword = (EditText) findViewById(R.id.editTextpass);
        register = (TextView) findViewById(R.id.textViewregister);
        pdialog = new ProgressDialog(this);
        google = (ImageView) findViewById(R.id.googlesignin);



        //initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        //Database conectivity
        userData = FirebaseDatabase.getInstance().getReference("USER");

        //initialize progress dialog
        pdialog = new ProgressDialog(LoginPage.this);
        pdialog.setMessage("Signing In");


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(LoginPage.this, gso);



        //Register Button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this,PhoneNumber.class);
                startActivity(intent);
            }
        });



        //google sigin button
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



        //Login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!vemail.getText().toString().equals("") && !vemail.getText().toString().contains(" "))
                {
                    email = vemail.getText().toString() + "@dsmail.com";
                    if(!vpassword.getText().toString().equals("") && !vpassword.getText().toString().contains(" "))
                    {
                        password = vpassword.getText().toString();
                        signin(email, password);
                    }
                    else
                    {
                        vpassword.setError("Enter a valid password");
                        vpassword.requestFocus();
                    }

                }
                else
                {
                    vemail.setError("Invalid email Id");
                    vemail.requestFocus();
                }

            }
        });




        //login button visibility
        vpassword.addTextChangedListener(loginvisibility);
        vemail.addTextChangedListener(loginvisibility);


    }


    //Text watcher
    private TextWatcher loginvisibility = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String username = vemail.getText().toString();
            String password = vpassword.getText().toString();
            if(!username.isEmpty() && !password.isEmpty())
            {
                login.setAlpha(1);
            }
            else
            {
                login.setAlpha((float) 0.4);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };




    //google signin
    private void signIn() {
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
                            Toast.makeText(LoginPage.this,"Successfully Signed In",Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            createaccount(user);
                            Intent n =new Intent(LoginPage.this,MenuPage.class);
                            n.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(n);

                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginPage.this,"Something went wrong..!!",Toast.LENGTH_LONG).show();
                           // updateUI(null);
                        }


                    }
                });
    }

    private void createaccount(final FirebaseUser user) {


        //get email from current user
        final String s = user.getEmail();
        final String name = user.getDisplayName();
        final String phoneNumber = user.getPhoneNumber();



        //check if email exist in data
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("USER");
        userRef
                .orderByChild("email")
                .equalTo(s)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            //it means user already registered
                        }
                        else
                        {
                            String uid = user.getUid();                                      //Get User Id
                            UserInfo userInfo = new UserInfo(name, s, phoneNumber, null);           //Created object of UserInfo
                            userData.child(uid).setValue(userInfo);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(LoginPage.this,databaseError.getMessage().toString(),Toast.LENGTH_LONG).show();

                    }
                });

    }


    //signin Function
    private void signin(String email, String password) {
        pdialog.setMessage("Please wait...");
        pdialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pdialog.dismiss();
                        if (task.isSuccessful())
                        {
                            Toast.makeText(LoginPage.this,"Authentication successful",Toast.LENGTH_LONG).show();
                            Intent n =new Intent(LoginPage.this,MenuPage.class);
                            n.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(n);


                        } else
                            {
                            String error = task.getException().getMessage();
                           Toast.makeText(LoginPage.this, error, Toast.LENGTH_SHORT).show();

                             }

                    }
                });
    }
}



