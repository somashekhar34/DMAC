package com.google.dmac;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.dmac.R;
import com.google.dmac.DatabaseHelper;
import com.google.dmac.BarcodeFragment;
import com.google.dmac.ProductListFragment;
import com.google.dmac.Product;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Account extends AppCompatActivity implements BarcodeFragment.ScanRequest {

    private Context context ;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static final String BARCODE_KEY = "BARCODE";
    private Barcode barcodeResult,divide;
    String[] divison,comparison,exp_comparison;
    String th,coupon_code,expiry_code,load_link;
    private final String TAG = MainActivity.class.getSimpleName() ;
    private final int MY_PERMISSION_REQUEST_CAMERA = 1001;
    private ItemScanned itemScanned ;
    ProductAdapter p ;
    String dateFormat1;
    final String last_date="Coupon Expired!";
     int  day,mon,year,exp_day,exp_mon,exp_year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        context = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BarcodeFragment(), "Barcode Scanner");
        adapter.addFragment(new ProductListFragment(), "My vouchers");
        adapter.addFragment(new dmacstore(),"DMAC store");
        viewPager.setAdapter(adapter);
    }

    public String getScanTime() {
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a" , Locale.getDefault());
        return  timeFormat.format(new Date());
    }

    public String getScanDate(String expiry_date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy",Locale.getDefault());
        dateFormat1 = dateFormat.toString();
        exp_comparison= expiry_date.split("/");
        exp_day = Integer.parseInt(exp_comparison[0]);
        exp_mon = Integer.parseInt(exp_comparison[1]);
        exp_year= Integer.parseInt(exp_comparison[2]);
        comparison = dateFormat1.split("/");
        day=Integer.parseInt(comparison[0]);
        mon=Integer.parseInt(comparison[1]);
        year=Integer.parseInt(comparison[2]);
        if(exp_day>day && exp_mon>mon && exp_year>year)
        {

        }
        return dateFormat.format(new Date());
    }

    @Override
    public void scanBarcode() {
        /** This method will listen the button clicked passed form the fragment **/
        checkPermission();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menusidetop,menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.profile1:
                openShare();
                break;
            case R.id.logout:
                openRate();
                break ;
        }

        return super.onOptionsItemSelected(item);
    }


    private void openRate() {
       // String k = getIntent().getExtras().getString("Name");
       //Log.i("currentuser",k);
        FirebaseAuth.getInstance().signOut();
        Intent i =new Intent(Account.this,MainActivity.class);
        i.putExtra("finish",true);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //startActivity(i);
        //ActivityCompat.finishAffinity(this);
        //finish();
    }

    private void openShare() {
       Intent i =new Intent(Account.this,profile.class);
       startActivity(i);
    }


    private void showDialog(final String scanContent, final String currentTime, final String currentDate, final String load_link) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);
        builder.setMessage(scanContent)
                .setTitle(R.string.dialog_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(context);
                    databaseHelper.addProduct(new Product(scanContent,currentTime,currentDate,load_link));
                    Toast.makeText(Account.this, "Saved!", Toast.LENGTH_SHORT).show();
                    viewPager.setCurrentItem(1);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(Account.this, "Not Saved", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);
        builder.setMessage("Are You Sure? ")
                .setTitle(R.string.exit_title);
        builder.setPositiveButton(R.string.ok_title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Account.this.finish();
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG , getResources().getString(R.string.camera_permission_granted));
            startScanningBarcode();
        } else {
            requestCameraPermission();

        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            ActivityCompat.requestPermissions(Account.this,  new String[] {Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CAMERA);

        } else{
            ActivityCompat.requestPermissions(Account.this,new String[] {Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    private void startScanningBarcode() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(Account.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode;
                        th =barcode.rawValue;
                        divison=th.split(",");
                        coupon_code=divison[0];
                        expiry_code=divison[1];
                        load_link=divison[2];
                        // getScanDate(expiry_code);
                        //p =new ProductAdapter(getApplicationContext(),coupon_code,expiry_code);
                        showDialog(coupon_code, getScanTime(),expiry_code,load_link);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MY_PERMISSION_REQUEST_CAMERA && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanningBarcode();
        } else {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.sorry_for_not_permission), Snackbar.LENGTH_SHORT)
                    .show();
        }

    }


    public interface  ItemScanned{
        void itemUpdated();
    }






}
/*
                        String k;
                        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy",Locale.getDefault());
                        dateFormat1 = dateFormat.toString();
                        comparison = dateFormat1.split("/");
                        day=Integer.parseInt(comparison[0]);
                        mon=Integer.parseInt(comparison[1]);
                        year=Integer.parseInt(comparison[2]);
                        exp_comparison= expiry_code.split("/");
                        exp_day = Integer.parseInt(exp_comparison[0]);
                        exp_mon = Integer.parseInt(exp_comparison[1]);
                        exp_year= Integer.parseInt(exp_comparison[2]);
                        if(exp_day>day && exp_mon>mon && exp_year>year)
                        {
                          k=last_date;
                        }
                        else
                        {
                          k =expiry_code;
                        }
 */