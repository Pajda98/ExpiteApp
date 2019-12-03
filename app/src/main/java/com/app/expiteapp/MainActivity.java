package com.app.expiteapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.expiteapp.adapters.ProductAdapter;
import com.app.expiteapp.database.ExpiryDbHelper;
import com.app.expiteapp.notifications.NotificationManager;
import com.app.expiteapp.services.ProductAlarmReceiver;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private static final int REQUEST_CODE_READ_CODE = 1002;
    private static final int REQUEST_ADD_ITEM_CODE = 1003;
    private static final int REQUEST_PRODCUT_ALARM = 1004;
    private static final int CAMERA_PERMISSION = 1;

    private Class<?> mClss;
    public static ExpiryDbHelper DB_HELPER;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB_HELPER = new ExpiryDbHelper(this);
        //UpdateProductList();

//        if(!isMyServiceRunning(ProductsService.class)){
////            Intent intent = new Intent(this, ProductsService.class);
////            startService(intent);
//        }

        startProductAlarm();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_shopping_list)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    private void startProductAlarm(){
        Intent intent = new Intent(this, ProductAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_PRODCUT_ALARM, intent, PendingIntent. FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + getMilliseonsToStart(), AlarmManager.INTERVAL_DAY, pendingIntent);
//        alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,    System.currentTimeMillis() + 10000, 20000, pendingIntent);
    }


    private long getMilliseonsToStart(){
        Date now = new Date();
        final Calendar cal = Calendar.getInstance();
        cal.set(now.getYear() + 1900, now.getMonth(), now.getDate() + 1, 3 ,0, 0);

        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date());
        return cal.getTimeInMillis() - cal2.getTimeInMillis();
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
        else if(item.getItemId() == R.id.check_notification){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            NotificationManager nm = new NotificationManager(this, intent);
            nm.checkAllNotifications();
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
//            FragmentManager fm = getSupportFragmentManager();
//            for(Fragment fr : fm.getFragments()){
//                if(fr instanceof HomeFragment){
//                    HomeFragment fragment = (HomeFragment)fr;
//                    fragment.UpdateProductList();
//                }
//            }
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

//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
}
