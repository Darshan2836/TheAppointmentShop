package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.menu.MenuPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button wbutton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private ProgressDialog progressDialog;

     @Override
    protected void onStart() {
        super.onStart();
         FirebaseUser currentUser = mAuth.getCurrentUser();
         updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
         if(currentUser != null)
         {
             startActivity(new Intent(MainActivity.this,MenuPage.class));
             finish();
         }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wbutton = (Button) findViewById(R.id.welcome_button);


        //mAuth instance
        mAuth = FirebaseAuth.getInstance();


         //progress dialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait");



               //login button
                wbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isNetworkAvailable()) {
                            Intent intent = new Intent(MainActivity.this, LoginPage.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "Check your internet connection", Toast.LENGTH_LONG).show();
                        }
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
