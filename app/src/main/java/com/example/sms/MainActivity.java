package com.example.sms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText phno,smss;
    int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    String sent ="SMS_Sent";
    String delivered ="SMS_delivered";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phno = (EditText) findViewById(R.id.PhoneNo);
        smss = (EditText) findViewById(R.id.sms);

        sentPI = PendingIntent.getBroadcast(this,0,new Intent(sent),0);
        deliveredPI = PendingIntent.getBroadcast(this,0,new Intent(delivered),0);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               switch (getResultCode())
               {
                   case Activity.RESULT_OK:
                       Toast.makeText(MainActivity.this,"Message Sent!",Toast.LENGTH_SHORT).show();
                       break;
                   case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                       Toast.makeText(MainActivity.this,"Failure.",Toast.LENGTH_SHORT).show();
                       break;
                   case SmsManager.RESULT_ERROR_NO_SERVICE:
                       Toast.makeText(MainActivity.this,"No Service",Toast.LENGTH_SHORT).show();
                       break;
                   case SmsManager.RESULT_ERROR_NULL_PDU:
                       Toast.makeText(MainActivity.this,"Null PDU",Toast.LENGTH_SHORT).show();
                       break;
                   case SmsManager.RESULT_ERROR_RADIO_OFF:
                       Toast.makeText(MainActivity.this,"Radio Off",Toast.LENGTH_SHORT).show();
                       break;
               }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(MainActivity.this,"SMS Delivered!",Toast.LENGTH_SHORT);
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(MainActivity.this,"SMS Not Delivered!",Toast.LENGTH_SHORT);
                        break;
                }
            }
        };

        registerReceiver(smsSentReceiver,new IntentFilter(sent));
        registerReceiver(smsDeliveredReceiver,new IntentFilter(delivered));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(smsDeliveredReceiver);
        unregisterReceiver(smsSentReceiver);
    }

    public void send(View v)
    {
        String message = smss.getText().toString();
        String number = phno.getText().toString();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        else
        {
            SmsManager msg = SmsManager.getDefault();
            msg.sendTextMessage(number,null,message,sentPI,deliveredPI);
        }
    }
}