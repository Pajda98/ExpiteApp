package com.app.expiteapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.expiteapp.database.ExpiryContract;
import com.app.expiteapp.models.ExpiryProduct;
import com.app.expiteapp.models.Product;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.UUID;

public class EditProduct extends ProductBaseActivity {

    public final static String EDITITEM_ID = "edititemid";

    ExpiryProduct expiryProduct;
    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        expiryProductId = getIntent().getLongExtra(EDITITEM_ID,0);

        setupToolbar();

        expiryProduct = ExpiryProduct.get(expiryProductId, MainActivity.DB_HELPER.getReadableDatabase());
        product = Product.get(expiryProduct.ProductId, MainActivity.DB_HELPER.getReadableDatabase());


        NameText.setText(product.Name);
        EANText.setText(product.EAN13);
        if(product.EAN13.contains("EAN")) EANText.setVisibility(View.INVISIBLE);
        NotesText.setText(expiryProduct.Notes);
        ExpiryDate.setText(expiryProduct.ExpiryDate);

//        uri = Uri.parse(product.ThumbnailSource);
        uri = product.ThumbnailSource;
        UpdateProductPhoto();
    }


    public void setupToolbar() {
        //top toolbar
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.edit_product_title);
        }

        //bottom toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.bottom_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpiryProduct();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void saveExpiryProduct(){
        product.Name = NameText.getText().toString();
        product.ThumbnailSource = uri.toString();
        product.update(MainActivity.DB_HELPER.getWritableDatabase());

        expiryProduct.Notes = NotesText.getText().toString();
        expiryProduct.setExpiryDate(myCalendar.getTime());
        expiryProduct.update(MainActivity.DB_HELPER.getWritableDatabase());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
