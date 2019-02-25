package com.agmapp.agm.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agmapp.agm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    protected EditText edtEmail, edtPass;
    protected Button btnlgn;
    private String email,pass,userty;
    private Boolean chkbol;
    private FirebaseAuth mAuth;
    private DatabaseReference mdata;
    protected ProgressDialog progressDialog;
    private static final String AGM_PREF = "agm.conf";
    private SharedPreferences mshred;
    private SharedPreferences.Editor editor;
    protected TextView txtr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtEmail = (EditText)findViewById(R.id.input_email);
        edtPass = (EditText)findViewById(R.id.input_password);
        btnlgn = (Button)findViewById(R.id.btn_login);
        txtr = (TextView)findViewById(R.id.txtreg);
        mAuth = FirebaseAuth.getInstance();
        mdata = FirebaseDatabase.getInstance().getReference().child("Users");
        mshred = getSharedPreferences(AGM_PREF, Context.MODE_PRIVATE);
        editor = mshred.edit();
        btnlgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                varInit();
                checkErr();
                if (chkbol)
                {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setTitle("AGM");
                    progressDialog.setMessage("Logging into account....");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    LogClass(email,pass);
                }
            }
        });

        txtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent regInt = new Intent(LoginActivity.this, RegActivity.class);
//                startActivity(regInt);
            }
        });

    }

    public void varInit()
    {
        email = edtEmail.getText().toString();
        pass = edtPass.getText().toString();
    }

    public void checkErr(){
        chkbol = true;
        if (email.isEmpty())
        {
            edtEmail.setError("Invalid email!");
            chkbol = false;
        }
        if (pass.isEmpty()){
            edtPass.setError("Invalid password!");
            chkbol = false;
        }
    }

    private void LogClass(final String a, final String b){
        mAuth.signInWithEmailAndPassword(a,b).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful())
                {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user.isEmailVerified())
                    {
                        mdata.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                userty = dataSnapshot.child("usertype").getValue().toString();
                                editor.putString("user_type",userty);
                                editor.apply();
                                if (userty.contains("member"))
                                {
                                    //member menu
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                else if (userty.contains("sudo")){
                                    //sudo
                                    Intent intent = new Intent(LoginActivity.this,AdminActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Please verify your email to access your account",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this,"Login failed please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
