package com.example.bls;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity
{
    private ToggleButton buttonOnOff;
    private Button led;
    private RadioButton radioLedButton;
    private RadioButton radioFlash;
    private RadioButton radioLed;
    private Runnable ledBlink;
//    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOnOff = findViewById(R.id.buttonOnOff);
        led = findViewById(R.id.led);
        radioLedButton = findViewById(R.id.radioLedButton);
        radioFlash = findViewById(R.id.radioFlash);
        radioLed = findViewById(R.id.radioLed);

        radioFlash.setVisibility(View.INVISIBLE);
        radioLed.setVisibility(View.INVISIBLE);

        ledBlink = new Runnable()
        {
            @Override
            public void run()
            {
                while(buttonOnOff.isChecked())
                {
                    /* Led Button */
                    if (radioLedButton.isChecked())
                    {
                        // Turn on the LED
                        try {
                            led.setBackgroundColor(Color.BLUE);
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Turn off the LED
                        try {
                            led.setBackgroundColor(Color.WHITE);
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    /* Flash */
                    if (radioFlash.isChecked())
                    {
//                        Context context = this;
//
//                        PackageManager pm = context.getPackageManager();
//
//                        // if device support camera?
//                        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
//                        {
//                            Log.e("err", "Device has no camera!");
//                            return;
//                        }

//                        camera = Camera.open();
//
//                        final Camera.Parameters par = camera.getParameters();
//                        par.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                        camera.setParameters(par);
//                        camera.startPreview();
                    }

                    /* Notification Led */
                    if (radioLed.isChecked())
                    {
//                        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        nm.cancel(1234); // clear previous notification
//                        Notification not = new Notification();
//                        not.flags |= Notification.FLAG_SHOW_LIGHTS;
//                        not.ledARGB = Color.WHITE;
//                        not.ledOnMS = 1;
//                        not.ledOffMS = 0;
//                        nm.notify(1234, not);
                    }
                }
            }
        };
    }

    public void onClickBlink(View view)
    {
        if(buttonOnOff.isChecked())
        {
            view.setBackgroundColor(Color.GREEN);

            new Thread(ledBlink).start();
        }
        else
        {
            view.setBackgroundColor(Color.RED);

            led.setBackgroundColor(Color.WHITE);

//            par.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//            camera.setParameters(par);
//            camera.stopPreview();
        }
    }
}
