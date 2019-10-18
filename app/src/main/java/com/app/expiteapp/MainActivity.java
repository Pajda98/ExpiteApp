package com.app.expiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView txtView = findViewById(R.id.textView);

        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();

        if(!detector.isOperational()){
            txtView.setText("Could not set up the detector!");
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this,
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext()),
                REQUEST_CODE_RECOVER_PLAY_SERVICES);
        if (errorDialog != null) {
            errorDialog.show();
        }
    }
}
