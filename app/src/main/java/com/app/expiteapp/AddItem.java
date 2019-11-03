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

import com.app.expiteapp.models.Product;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
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

    Product makroProduct;

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

        if(!EAN.equals("")){
            GetMakroProduct(EAN);

            if (makroProduct != null) {
                EditText NameText = findViewById(R.id.name);
                NameText.setText(makroProduct.Name);

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
                    makroProduct = new Product();
                    Elements elements = makroDocument.select("div[class=product product-incart]");
                    makroProduct.Name = elements.select("h3[class=product-title]").select("a").text();
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

    private void GetProductImage(){
        TextView EANtext = findViewById(R.id.ean);
        if (getIntent().resolveActivity(getPackageManager()) != null) {
            try {
                String imageFileName;
                if (EANtext.getText().toString().equals("")) {
                    imageFileName = UUID.randomUUID().toString();
                } else {
                    imageFileName = "EAN" + EANtext.getText().toString();
                }
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(imageFileName,
                        ".jpg",
                        storageDir
                );

                uri = Uri.fromFile(image);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), R.string.add_product_saveImage, Toast.LENGTH_SHORT).show();
            }
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
                EditText NameText = findViewById(R.id.name);
                TextView EANText = findViewById(R.id.ean);
                EditText DescriptionText = findViewById(R.id.description_field);


                Product prod = new Product();
                prod.Name = NameText.getText().toString();
                prod.Description = DescriptionText.getText().toString();
                prod.EAN13 = EANText.getText().toString();
                prod.setExpiryDate(myCalendar.getTime());
                prod.ThumbnailSource = uri.toString();
                prod.insert(MainActivity.DB_HELPER.getWritableDatabase());
                setResult(RESULT_OK);
                finish();
            }
        });
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
