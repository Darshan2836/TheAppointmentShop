package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.menu.MenuPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    private EditText textname, textemail, textpassword;
    private Button create;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference userData;
    private Bundle extras;
    private int x=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textname = (EditText) findViewById(R.id.editTextname);
        textemail = (EditText) findViewById(R.id.editTextmail);
        textpassword = (EditText) findViewById(R.id.editTextpass);
        create = (Button) findViewById(R.id.buttoncreate);


        //Auth instance
        mAuth = FirebaseAuth.getInstance();

        //Database conectivity
        userData = FirebaseDatabase.getInstance().getReference("USER");


        //Progress Dialog created
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..!!");


        //get number from OTP activity
        extras = getIntent().getExtras();
        final String number = extras.getString("Number");

        //email check
        textemail.addTextChangedListener(createvisibility);


        //Create Button
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = textname.getText().toString();
                String email = textemail.getText().toString();
                String password = textpassword.getText().toString();

                if (!name.equals("")) {
                    if (emailcheck(email)) {
                        String newemail = email + "@dsmail.com";
                        if (passwordcheck(password)) {
                            signup(newemail, password, number, name);

                        }
                    }
                } else {
                    textname.setError("Enter a Name");
                    textname.requestFocus();
                }
            }
        });

    }

    //check password
    private boolean passwordcheck(String password) {
        if (password.equals("")) {
            textpassword.setError("Enter a password");
            textpassword.requestFocus();
            return false;
        } else if (password.contains(" ")) {
            textpassword.setError("Please remove blank spaces");
            textpassword.requestFocus();
            return false;
        } else if (password.length() < 8) {
            textpassword.setError("Use 8 characters or more for your password");
            textpassword.requestFocus();
            return false;
        }
        return true;
    }


    //Check email
    private boolean emailcheck(String email) {
        if (email.equals("")) {
            textemail.setError("Please Enter a Value");
            textemail.requestFocus();
            return false;
        } else if (email.contains(" ")) {
            textemail.setError("Please remove blank spaces");
            textemail.requestFocus();
            return false;
        }
        else if(x==1)
        {
            textemail.requestFocus();
            return false;
        }
        return true;
    }


    //Signup Function
    private void signup(final String email, final String password, final String number,
                        final String name) {

        progressDialog.show();
        //create account
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //save user data to database
                    FirebaseUser currentuser = mAuth.getCurrentUser();                      //currentUser object created
                    String uid = currentuser.getUid();                                      //Get User Id

                    UserInfo userInfo = new UserInfo(name, email, number, password);           //Created object of UserInfo

                    userData.child(uid).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {  //Data uploaded
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(SignUp.this, "SignedUp Successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignUp.this, MenuPage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignUp.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignUp.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    //textwather
        private TextWatcher createvisibility = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                x=0;
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("USER");
                userRef
                        .orderByChild("email")
                        .equalTo(s+"@dsmail.com")
                        .addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    //it means user already registered
                                    textemail.setError("Email ID already Exist(Type a different Email ID)");
                                    textemail.requestFocus();
                                    x++;
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressDialog.dismiss();
                                Toast.makeText(SignUp.this,databaseError.getMessage().toString(),Toast.LENGTH_LONG).show();

                            }
                        });

            }
    };
}




