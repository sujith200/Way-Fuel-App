package com.example.wayfuel.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.wayfuel.API.APICallbacks;
import com.example.wayfuel.API.APIStatus;
import com.example.wayfuel.API.UserData;
import com.example.wayfuel.Adapter.Notifucation_Adapter;
import com.example.wayfuel.R;
import com.example.wayfuel.Utils.APPConstants;
import com.example.wayfuel.Utils.CommonFunctions;
import com.example.wayfuel.Utils.DialogUtils;
import com.example.wayfuel.Utils.MyPrefs;
import com.example.wayfuel.Utils.NetworkController;
import com.example.wayfuel.Utils.OnItemViewClickListener;
import com.example.wayfuel.databinding.ActivityNotificationBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notification_Activity extends AppCompatActivity {

    ActivityNotificationBinding binding;
    Activity activity;
    AlertDialog dialogLoading;
    List<JSONObject> list = new ArrayList<>();
    Notifucation_Adapter notificationAdapter;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 44;
    double latitude = 0.0, longitude = 0.0,listLat = 0.0,listLong = 0.0;
    private FusedLocationProviderClient fusedLocationClient;
    JSONObject object = new JSONObject();


    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, null);

            try {

                if (apiStatus == APIStatus.SUCCESS) {

                    if (tag.equalsIgnoreCase("notification")) {

                        if (response.getBoolean("status")) {

                            list.clear();
                            CommonFunctions.setJSONArray(response, "Data", list, notificationAdapter);

                            if (list.size() == 0) {
                                binding.tvNo.setVisibility(View.VISIBLE);
                            } else {
                                binding.tvNo.setVisibility(View.GONE);
                            }

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
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        dialogLoading = DialogUtils.createLoading(activity);



        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkGPSAndProceed();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }


        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            listLat = bundle.getDouble("latitude");
            listLong = bundle.getDouble("longitude");

        }
        notificationAPI();


        notificationAdapter = new Notifucation_Adapter(activity, list, new OnItemViewClickListener() {
            @Override
            public void onClick(View v, int i) throws JSONException {

                if (v.getId() == R.id.locationBtn) {

                    object = list.get(i);

                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        checkGPSAndProceed1();
                    } else {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                }
            }
        });

        binding.notiRecycler.setAdapter(notificationAdapter);



    }

    private void checkGPSAndProceed() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled, show dialog to enable it
            showEnableGPSDialog();

        } else {

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(activity, location -> {
                            if (location != null) {

                                listLat = location.getLatitude();
                                listLong = location.getLongitude();


                            }
                        });

            }
        }
    }

    private void checkGPSAndProceed1() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled, show dialog to enable it
            showEnableGPSDialog();
        } else {

            try {
                // GPS is enabled, proceed with location-based functionality
                latitude = object.getDouble("CustLatitude");
                longitude = object.getDouble("CustLongitude");

                String uri = String.format("geo:%f,%f", latitude, longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            } catch (Exception e) {
                Toast.makeText(activity, "Location is Empty", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showEnableGPSDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open location settings
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkGPSAndProceed();
            } else {
                //Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Enable Location")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Open location settings
                                //startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                i.addCategory(Intent.CATEGORY_DEFAULT);
                                i.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(i);
                            }
                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }

    }

    public void notificationAPI() {
        Bundle bundle = new Bundle();
        Map<String, String> map = new HashMap<>();
        map.put("CustID", MyPrefs.getInstance(activity).getString(UserData.KEY_USER_ID));
        map.put("latitude", listLat + "");
        map.put("longitude", listLong + "");
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "Notification", map, "notification", bundle, apiCallbacks);
    }
}