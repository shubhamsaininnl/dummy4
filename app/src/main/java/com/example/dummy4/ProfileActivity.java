package com.example.dummy4;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements SensorEventListener {

    float[] mGravity;
    float[] mGeomagnetic;
    private FirebaseAuth mAuth;
    private SensorManager sensorManager;
    Button btn2;
    EditText txt3,txt4;
    public static final String NODE_USERS= "users";
    String USER_TOKEN= "";
    String USER_EMAIL= "";

    User user= new User();

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAx3VTKNk:APA91bGGc22Nl3naTBuqe2yVMonSYttkezc8ucEJA0U_XBkpfehyykwvCOzIRBnIOzgqpqWCO471LUA417V13sWNbWNxX1_GvpJmxEy59ReEHvI3YsnEK-lMZktdBqGEhzPBEZ902tcX";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btn2= (Button) findViewById(R.id.btn2);
        txt3= (EditText) findViewById(R.id.txt3);
        txt4= (EditText) findViewById(R.id.txt4);

        mAuth= FirebaseAuth.getInstance();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    String a,b;
                    a=user.email;
                    b=user.token;

                    Log.d("email", a + " " + b);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //FirebaseMessaging.getInstance().subscribeToTopic("updates");
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()) {
                    String token= task.getResult().getToken();
                    saveToken(token);
                }
                else {

                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TOPIC = "updates"; //topic must match with what the receiver subscribed to
                NOTIFICATION_TITLE = txt3.getText().toString();
                NOTIFICATION_MESSAGE = txt4.getText().toString();

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);

                    notification.put("to", TOPIC);
                    notification.put("notification", notifcationBody);
                } catch (JSONException e) {
                    Log.d(TAG, "onCreate: " + e.getMessage() );
                }
                sendNotification(notification);
            }
        });
    }

    private void saveToken(String token)
    {
        USER_EMAIL= mAuth.getCurrentUser().getEmail();
        USER_TOKEN= token;
        float a= 0;
        user.email= USER_EMAIL;
        user.token= USER_TOKEN;
        user.azimuth= a;


        //DatabaseReference dbUsers= FirebaseDatabase.getInstance().getReference(NODE_USERS);
        databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ProfileActivity.this, "Token Saved!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        txt3.setText("");
                        txt4.setText("");

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "Request error", Toast.LENGTH_LONG).show();
                Log.i(TAG, "onErrorResponse: Didn't work");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()== null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

                // orientation contains azimut, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                final float azimuth = orientation[0];
                float rotation = -azimuth * 360 / (2 * 3.14159f);
                Log.d("rotation123", String.valueOf(rotation));
                //user.email= USER_EMAIL;
                //user.token= USER_TOKEN;
                user.azimuth= azimuth;
                databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(user);

                databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d("azimuth", String.valueOf(dataSnapshot.child("azimuth")) );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
