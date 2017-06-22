package com.bomeans.sample.acremotesample;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.bomeans.IRKit.IRKit;

/**
 * Bomeans Design 2016
 *
 * Sample code: using Bomeans IRKit SDK for AC remote controller.
 * Please read the in-code comments for the tricky part of AC remote controllers.
 *
 */
public class MainActivity extends AppCompatActivity {

    // apply an SDK key from Bomeans and apply it below
    private String API_KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check API Key
        if (API_KEY == null || API_KEY.isEmpty()) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Invalid API Key!")
                    .setMessage("No valid API key is assigned!\n\nTo apply an API key:\nhttp://www.bomeans.com/Mainpage/Apply/apikey")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Apply Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bomeans.com/Mainpage/Apply/apikey"));
                            startActivity(browserIntent);
                            MainActivity.this.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        } else {

            // initialize IRKit
            IRKit.setup(API_KEY, getApplicationContext());  // use application context

            // setup the IR Blaster, write your own IR Blaster code by creating a class implements BIRIrHW or BIRIRBlaster
            // BIRIrHW is for general IR waveform data format, BIRIRBlaster is for special format suitable for Bomeans IR Blaster MCU.
            DummyIrBlaster myIrBlaster = new DummyIrBlaster();
            IRKit.setIRHW(myIrBlaster);

            // create remote controller
            Button buttonDemo1 = (Button) findViewById(R.id.button_demo1);
            buttonDemo1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AcDemo1Activity.class);
                    startActivity(intent);
                }
            });

            Button buttonDemo2 = (Button) findViewById(R.id.button_demo2);
            buttonDemo2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AcDemo2Activity.class);
                    startActivity(intent);
                }
            });

            Button buttonDemo3 = (Button) findViewById(R.id.button_demo3);
            buttonDemo3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AcDemo3Activity.class);
                    startActivity(intent);
                }
            });

        }

    }


}
