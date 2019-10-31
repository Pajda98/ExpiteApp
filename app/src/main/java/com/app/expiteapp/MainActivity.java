package com.app.expiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.expiteapp.adapters.ProductAdapter;
import com.app.expiteapp.database.ExpiryDbHelper;
import com.app.expiteapp.models.Product;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

    ExpiryDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new ExpiryDbHelper(this);



        //Adapter
        List<Product> products = Product.getList(dbHelper.getReadableDatabase());
        ListView pl = (ListView)findViewById(R.id.ProductList);

        ProductAdapter adapter = new ProductAdapter(this,
                R.layout.item_product_layout, products);


        pl.setAdapter(adapter);

//        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
//                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
//                        .build();
//
//        if(!detector.isOperational()){
//            txtView.setText("Could not set up the detector!");
//            return;
//        }
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

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
