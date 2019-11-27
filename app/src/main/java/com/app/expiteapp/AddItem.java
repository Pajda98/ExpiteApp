package com.app.expiteapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddItem extends ProductBaseActivity {

    final static String ADDITEM_EAN = "additemean";
    Product loadedProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String EAN = getIntent().getStringExtra(ADDITEM_EAN);
        EANText.setText(EAN);

        setupToolbar();

        if(!EAN.equals("") && !EAN.contains("EAN")){
            loadedProduct = Product.search(MainActivity.DB_HELPER.getReadableDatabase(), ExpiryContract.ProductEntry.EAN13, EAN);
            if(loadedProduct == null){
                GetMakroProduct(EAN);
            }
            if (loadedProduct != null) {
                EditText NameText = findViewById(R.id.name);
                NameText.setText(loadedProduct.Name);

                uri = Uri.parse(loadedProduct.ThumbnailSource);
                UpdateProductPhoto();
            }
        }

        //Show datepicker on start
        showCalendar(this, date);
    }

    private void GetMakroProduct(final String EAN){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    Document makroDocument = Jsoup.connect("https://sortiment.makro.cz/cs/search/?q=" + EAN).get();
                    Elements elements = makroDocument.select("div[class=product product-incart]");
                    if (elements.size() > 0) {
                        loadedProduct = new Product();
                        String name = elements.select("h3[class=product-title]").select("a").text();
                        Pattern pattern = Pattern.compile("\\s\\d");
                        Matcher matcher = pattern.matcher(name);
                        // Check all occurrences
                        while (matcher.find()) {
                            name = name.substring(0, matcher.start());
                        }
                        loadedProduct.Name = name;

                        URL url = new URL(elements.select("a[class=product-photo]").select("img").attr("src"));
                        InputStream in = new BufferedInputStream(url.openStream());
                        File image = createImageFile(EAN);
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(image));

                        for ( int i; (i = in.read()) != -1; ) {
                            out.write(i);
                        }
                        in.close();
                        out.close();

                        loadedProduct.ThumbnailSource = Uri.fromFile(image).toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
        }
        catch(InterruptedException e){
        }
    }


    public void setupToolbar() {
        //top toolbar
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.add_product_title);
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
        EditText NameText = findViewById(R.id.name);
        TextView EANText = findViewById(R.id.ean);
        EditText NotesText = findViewById(R.id.notes_field);

        Long productId = 0l;
        if (loadedProduct == null || loadedProduct.Id == 0l) {
            Product p = new Product();
            String ean = EANText.getText().toString();
            if (ean.equals("")){
                ean = "EAN" + UUID.randomUUID().toString();
            }
            p.Name = NameText.getText().toString();
            p.EAN13 = ean;
            p.ThumbnailSource = uri.toString();
            productId = p.insert(MainActivity.DB_HELPER.getWritableDatabase());
        }else{
            loadedProduct.Name = NameText.getText().toString();
            loadedProduct.EAN13 = EANText.getText().toString();
            loadedProduct.ThumbnailSource = uri.toString();
            loadedProduct.update(MainActivity.DB_HELPER.getWritableDatabase());
            productId = loadedProduct.Id;
        }

        ExpiryProduct ep = new ExpiryProduct();
        ep.Notes = NotesText.getText().toString();
        ep.setExpiryDate(myCalendar.getTime());
        ep.ProductId = productId;
        ep.insert(MainActivity.DB_HELPER.getWritableDatabase());
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
