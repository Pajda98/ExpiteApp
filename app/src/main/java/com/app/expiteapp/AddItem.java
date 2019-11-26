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

public class AddItem extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1001;

    private static final int WRITE_STORAGE_PERMISSION = 1;
    private static final int READ_STORAGE_PERMISSION = 2;

    final static String ADDITEM_EAN = "additemean";

    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;
    EditText ExpiryDate;

    Uri uri = Uri.EMPTY;

    Product loadedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        final Activity activity = this;
        final String EAN = getIntent().getStringExtra(ADDITEM_EAN);
        final TextView EANtext = findViewById(R.id.ean);
        EANtext.setText(EAN);

        setupToolbar();
        myCalendar = Calendar.getInstance();
        setupDatePicker();

        ImageView ProductImage = findViewById(R.id.product_image);
        ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_view_click));

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION);
                } else {
                    GetProductImage();
                }
            }
        });

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
                        loadedProduct.Name = elements.select("h3[class=product-title]").select("a").text();


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

    private File createImageFile(String EAN){
        try {
            String imageFileName;
            if (EAN.toString().equals("")) {
                imageFileName = UUID.randomUUID().toString();
            } else {
                imageFileName = "EAN" + EAN;
            }
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(imageFileName,
                    ".jpg",
                    storageDir
            );
        }
        catch(IOException e){
            return null;
        }
    }

    private void GetProductImage(){
        TextView EANtext = findViewById(R.id.ean);
        if (getIntent().resolveActivity(getPackageManager()) != null) {
            uri = Uri.fromFile(createImageFile(EANtext.getText().toString()));
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetProductImage();
                } else {
                    Toast.makeText(this, R.string.write_storage_permision, Toast.LENGTH_SHORT).show();
                }
                return;
            case READ_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UpdateProductPhoto();
                } else {
                    Toast.makeText(this, R.string.read_storage_permision, Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }


    public void setupDatePicker(){
        ExpiryDate = findViewById(R.id.expiry_date);
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

        };
        final Context context = this;

        ExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar(context, date);
            }
        });

        ExpiryDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    showCalendar(context, date);
                }
            }
        });
    }

    private void showCalendar(Context context,DatePickerDialog.OnDateSetListener date){
        new DatePickerDialog(context, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        ExpiryDate.setText(format.format(myCalendar.getTime()));
    }

    public void setupToolbar() {
        //top toolbar
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
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

    private void UpdateProductPhoto(){
        ImageView image = findViewById(R.id.product_image);
        image.setImageURI(uri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            UpdateProductPhoto();
        }
    }
}
