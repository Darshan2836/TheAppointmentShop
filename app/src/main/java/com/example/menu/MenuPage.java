package com.example.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.login.LoginPage;
import com.example.login.MainActivity;
import com.example.login.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MenuPage extends AppCompatActivity {

    private Button mbutton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChipNavigationBar bottomNavigationView;


   @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        //find view by ID
        bottomNavigationView = (ChipNavigationBar) findViewById(R.id.bottomnavbar);

        //get instance
        mAuth = FirebaseAuth.getInstance();

        //default home navigation
        if(savedInstanceState==null) {
            bottomNavigationView.setItemSelected(R.id.homenav,true);
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new home()).commit();
        }



        //change activity of user
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
          if (firebaseAuth.getCurrentUser() == null)
          {
              startActivity(new Intent(MenuPage.this, MainActivity.class));
              finish();
          }
            }
        };

        bottomNavigationView.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment frag = null;
                switch(i)
                {
                    case R.id.homenav:
                        frag = new home();
                        break;
                    case R.id.notinav:
                        frag = new appointments();
                        break;
                    case R.id.settingnav:
                        frag = new settings();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,frag).commit();
            }
        });


    }
}
