package com.app.expiteapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final int REQUEST_CODE_READ_CODE = 1002;
    private static final int REQUEST_ADD_ITEM_CODE = 1003;
    private static final int CAMERA_PERMISSION = 1;

    private Class<?> mClss;
    public static ExpiryDbHelper DB_HELPER;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB_HELPER = new ExpiryDbHelper(this);

        UpdateProductList();

    }

    private void UpdateProductList(){
        //Adapter
        List<Product> products = Product.getList(DB_HELPER.getReadableDatabase());
        ListView pl = (ListView)findViewById(R.id.product_list);
        ProductAdapter adapter = new ProductAdapter(this, R.layout.item_product_layout, products);

        pl.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_product){
            Class<?> clss = ScannerActivity.class;
            mClss = clss;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            } else {
                Intent intent = new Intent(this, clss);
                startActivityForResult(intent, REQUEST_CODE_READ_CODE);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivityForResult(intent, REQUEST_CODE_READ_CODE);
                    }
                } else {
                    Toast.makeText(this, R.string.barcode_permision, Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main_activity, menu);
            return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_READ_CODE){
            if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                return;
            }

            Intent intent = new Intent(this, AddItem.class);
            if (resultCode == AppCompatActivity.RESULT_OK) {
                intent.putExtra(AddItem.ADDITEM_EAN, data.getStringExtra(ScannerActivity.SCANNER_RESULT_VALUE));
            }
            else if (resultCode == ScannerActivity.RESULT_SKIP){
                intent.putExtra(AddItem.ADDITEM_EAN, "");
            }
            startActivityForResult(intent, REQUEST_ADD_ITEM_CODE);
        }
        if (requestCode == REQUEST_ADD_ITEM_CODE && resultCode == AppCompatActivity.RESULT_OK){
            UpdateProductList();
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

    @Override
    protected void onDestroy() {
        DB_HELPER.close();
        super.onDestroy();
    }
}
