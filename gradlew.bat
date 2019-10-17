package com.example.radovan.tamz_pondeli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;


public class MainActivity extends Activity implements DatePicker.OnDateChangedListener {

    DatePicker myDate;
    TextView myText;
    TextView myText2;
    ImageView myImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDate = findViewById(R.id.myDatePicker);
        Date actualDate = new Date();
        myDate.init(actualDate.getTime(), 0, 1, this);

        myText = findViewById(R.id.textView);
        myText.setText("Zadej Datum");

        myImage = findViewById(R.id.imageView);

        myText2 = findViewById(R.id.textView2);
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth){

        Log.d("mesic ", " " + monthOfYear);
        myImage.setImageResource(R.drawable.beran04);
        myText2.setText("Beran");
    }

    public void ShowZodiacDescription(View view) {
        Intent intent = new Intent(this, ZodiacDescription.class);
        intent.putExtra("key", "Pozdrav z prvni aktivity");
        //intent.getAction();
        startActivity(intent);
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               