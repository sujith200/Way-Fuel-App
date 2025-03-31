package com.example.wayfuel;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.example.wayfuel.API.APICallbacks;
import com.example.wayfuel.API.APIStatus;
import com.example.wayfuel.API.UserData;
import com.example.wayfuel.Activity.EditPriofileActivity;
import com.example.wayfuel.Activity.LoginActivity;
import com.example.wayfuel.Activity.Notification_Activity;
import com.example.wayfuel.Adapter.Banner_Adapter;
import com.example.wayfuel.Utils.APPConstants;
import com.example.wayfuel.Utils.CommonFunctions;
import com.example.wayfuel.Utils.DialogUtils;
import com.example.wayfuel.Utils.MyForegroundService;
import com.example.wayfuel.Utils.MyPrefs;
import com.example.wayfuel.Utils.NetworkController;
import com.example.wayfuel.Utils.OnItemViewClickListener;
import com.example.wayfuel.databinding.ActivityMainBinding;
import com.example.wayfuel.databinding.DialogLogoutBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public static Activity activity;
    AlertDialog dialogLoading;
    int currentPage = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 44;
    Banner_Adapter bannerAdapter;
    List<Banner_Adapter.Appitems> bannerList = new ArrayList<>();
    Handler handler = new Handler(Looper.getMainLooper());
    DialogLogoutBinding dialogLogoutBinding;
    AlertDialog logoutDialog;
    double latitude = 0.0, longitude = 0.0;
    private FusedLocationProviderClient fusedLocationClient;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            currentPage++;
            if (currentPage == bannerList.size()) {
                currentPage = 0;
            }
            binding.recyclerBanner.smoothScrollToPosition(currentPage);
            handler.postDelayed(runnable, 5000);
        }
    };

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, null);

            try {

                if (apiStatus == APIStatus.SUCCESS) {

                    if (tag.equalsIgnoreCase("home")) {

                        if (response.getBoolean("status")) {

                            JSONObject object = response.getJSONObject("Data");
                            JSONObject object1 = object.getJSONObject("customer");

                            binding.userName.setText(object1.getString("CustName"));
                            binding.phone.setText(object1.getString("CustPhoneNum"));

                            binding.petrol.setText(object.getString("petrolprice"));
                            binding.diesel.setText(object.getString("dieselprice"));

                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }


                    if (tag.equalsIgnoreCase("Alert")) {

                        if (response.getBoolean("status")) {

                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        dialogLoading = DialogUtils.createLoading(activity);

        MyPrefs.getInstance(activity).putString(UserData.LOGIN, "true");

        dialogLogoutBinding = DialogLogoutBinding.inflate(getLayoutInflater());
        logoutDialog = DialogUtils.getCustomAlertDialog(activity, dialogLogoutBinding.getRoot());
        logoutDialog.setCancelable(false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        checkLocationPermissionAndGPS();


        bannerList.clear();
        bannerList.add(new Banner_Adapter.Appitems(R.drawable.banner1));
        bannerList.add(new Banner_Adapter.Appitems(R.drawable.banner2));
        bannerList.add(new Banner_Adapter.Appitems(R.drawable.banner3));
        bannerList.add(new Banner_Adapter.Appitems(R.drawable.banner4));
        bannerList.add(new Banner_Adapter.Appitems(R.drawable.banner5));

        bannerAdapter = new Banner_Adapter(activity, bannerList, new OnItemViewClickListener() {
            @Override
            public void onClick(View v, int i) throws JSONException {

            }
        });
        binding.recyclerBanner.setAdapter(bannerAdapter);

        binding.pagerIndicatorBanner.attachToRecyclerView(binding.recyclerBanner);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(binding.recyclerBanner);
        handler.postDelayed(runnable, 5000);
        binding.recyclerBanner.setAdapter(bannerAdapter);


        binding.notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(activity, Notification_Activity.class);
                bundle.putDouble("latitude",latitude );
                bundle.putDouble("longitude",longitude );
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditPriofileActivity.class);
                startActivity(intent);
                finish();

            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logoutDialog.show();
            }
        });

        dialogLogoutBinding.tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyPrefs.getInstance(activity.getApplicationContext()).putString(UserData.LOGIN, "");
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        dialogLogoutBinding.tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
            }
        });


        binding.alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertAPI();
            }
        });

        homeAPI();
    }

    private void homeAPI() {
        Bundle bundle = new Bundle();
        Map<String, String> map = new HashMap<>();
        map.put("CustID", MyPrefs.getInstance(activity).getString(UserData.KEY_USER_ID));
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "Home", map, "home", bundle, apiCallbacks);
    }

    private void alertAPI() {
        Bundle bundle = new Bundle();
        Map<String, String> map = new HashMap<>();
        map.put("CustID", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("latitude", latitude + "");
        map.put("longitude", longitude + "");
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "Alert", map, "Alert", bundle, apiCallbacks);
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setCancelable(false);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });


        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private boolean isLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            // For devices running Android versions below Marshmallow, location permission is granted at installation time
            return true;
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Check location permission and GPS status
    private void checkLocationPermissionAndGPS() {
        if (!isLocationPermissionGranted()) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            if (!isGPSEnabled()) {
                showGPSSettingsAlert();
            } else {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(activity, location -> {
                                if (location != null) {

                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();

                                }
                            });

                }
                startForegroundService();
            }
        }
    }

    private void showGPSSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, check GPS status
                checkLocationPermissionAndGPS();
            } else {
                // Location permission denied, show a message or handle it accordingly
                Toast.makeText(activity, "Location permission denied", Toast.LENGTH_SHORT).show();

            }
        }
    }

    // Method to start the foreground service
    private void startForegroundService() {
        MyPrefs.getInstance(getApplicationContext()).putBoolean("ordPic", true);
        Intent serviceIntent = new Intent(activity, MyForegroundService.class);
        serviceIntent.putExtra("activity_package", getPackageName());
        serviceIntent.putExtra("activity_class", MainActivity.class.getName());
        ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
    }


}