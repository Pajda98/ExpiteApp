package com.app.expiteapp;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ProductBaseActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1001;

    private static final int WRITE_STORAGE_PERMISSION = 1;
    private static final int READ_STORAGE_PERMISSION = 2;

//    Uri uri = Uri.EMPTY;
    String uri = "";

    protected  Long productId;
    protected  Long expiryProductId;

    protected EditText NameText;
    protected TextView EANText;
    protected EditText NotesText;
    protected ImageView ProductImage;

    protected DatePickerDialog.OnDateSetListener date;
    protected Calendar myCalendar;
    protected EditText ExpiryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        final Activity activity = this;

        NameText = findViewById(R.id.name);
        EANText = findViewById(R.id.ean);
        NotesText = findViewById(R.id.notes_field);
        ExpiryDate = findViewById(R.id.expiry_date);
        ImageView ProductImage = findViewById(R.id.product_image);

        myCalendar = Calendar.getInstance();
        setupDatePicker();

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
    }

    protected File createImageFile(){
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = null;
                image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
            // Save a file: path for use with ACTION_VIEW intents
            uri = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void GetProductImage(){
        TextView EANtext = findViewById(R.id.ean);
        if (getIntent().resolveActivity(getPackageManager()) != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                photoFile = createImageFile();
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            this.getApplicationContext().getPackageName() + ".provider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
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

    protected void showCalendar(Context context,DatePickerDialog.OnDateSetListener date){
        new DatePickerDialog(context,R.style.DatePickerColor, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        ExpiryDate.setText(format.format(myCalendar.getTime()));
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

    protected void UpdateProductPhoto(){
        ImageView image = findViewById(R.id.product_image);
        if(!uri.toString().equals("")) {
              File file = new File(uri);
              Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(file.getAbsolutePath()),
                        512 ,
                        512);
                image.setImageBitmap(thumbImage);
        }
    }
    public String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = this.getContentResolver().query(contentURI, null,
                null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            try {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            } catch (Exception e) {
                result = "";
            }
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            UpdateProductPhoto();
        }
    }

}
