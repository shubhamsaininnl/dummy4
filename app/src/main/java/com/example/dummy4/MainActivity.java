package com.example.dummy4;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    Button btn1;
    EditText txt1, txt2;
    FirebaseAuth mAuth;
    public  static final String CHANNEL_ID= "simplified_coding";
    private  static final String CHANNEL_NAME= "Simplified Coding";
    private  static final String CHANNEL_DESC= "Simplified Coding Notifications";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1= (Button) findViewById(R.id.btn1);
        txt1= (EditText) findViewById(R.id.txt1);
        txt2= (EditText) findViewById(R.id.txt2);
        mAuth= FirebaseAuth.getInstance();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }



        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    createUser();
            }
        });

    }

    private  void createUser(){
        final String email= txt1.getText().toString().trim();
        final String password= txt2.getText().toString().trim();

        if(email.isEmpty())
        {
            txt1.setError("Email Required!");
            txt1.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            txt2.setError("password Required!");
            txt2.requestFocus();
            return;
        }

        if(password.length() < 6)
        {
            txt2.setError("password length should be atleast 6");
            txt2.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startProfileActivity();
                    }

                    else
                    {
                        if(task.getException() instanceof FirebaseAuthUserCollisionException)
                        {
                                userLogin(email, password);
                        }

                        else
                        {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            }
        });
    }

    private void userLogin(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    startProfileActivity();
                }

                else{
                    Toast.makeText(MainActivity.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()!= null) {
            startProfileActivity();
        }
    }
    private  void startProfileActivity()
    {
        Intent intent= new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
