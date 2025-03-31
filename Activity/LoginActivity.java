package com.example.wayfuel.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.wayfuel.MainActivity;
import com.example.wayfuel.R;
import com.example.wayfuel.Utils.APPConstants;
import com.example.wayfuel.Utils.DialogUtils;
import com.example.wayfuel.Utils.MyPrefs;
import com.example.wayfuel.Utils.NetworkController;
import com.example.wayfuel.databinding.ActivityLoginBinding;
import com.example.wayfuel.databinding.PopupSuccessBinding;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    Activity activity;
    AlertDialog dialogLoading;
    String phone = "", password = "";
    PopupSuccessBinding popupSuccessBinding;
    AlertDialog popup;
    Handler handler = new Handler();
    String firebase_token = "";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 44;


    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, null);

            try {

                if (apiStatus == APIStatus.SUCCESS) {

                    if (tag.equalsIgnoreCase("login")) {

                        if (response.getBoolean("status")) {

                            JSONObject object = response.getJSONObject("Data");

                            String userId = object.getString("userid");
                            MyPrefs.getInstance(activity).putString(UserData.KEY_USER_ID, userId);

                            popup.show();
                            start();

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
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        dialogLoading = DialogUtils.createLoading(activity);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebase_token = task.getResult();
                Log.i("Token", firebase_token);
            }
        });

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkGPSAndProceed();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }


        if (Build.VERSION.SDK_INT >= 32) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                    == PackageManager.PERMISSION_GRANTED)
                return;
            ActivityResultLauncher<String> launcher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted -> {

                    }
            );
            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }


        //alert box:
        popupSuccessBinding = PopupSuccessBinding.inflate(getLayoutInflater());
        popupSuccessBinding.text.setText("Verified");
        popup = DialogUtils.getCustomAlertDialog(activity, popupSuccessBinding.getRoot());
        popup.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(popupSuccessBinding.getRoot().getContext(), R.drawable.background));

        binding.visible.setImageResource(R.drawable.ic_baseline_visibility_24);

        binding.visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.visible.setImageResource(R.drawable.ic_baseline_visibility_24);
                    binding.etPassword.setSelection(binding.etPassword.getText().length());
                } else {
                    binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    binding.visible.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                    binding.etPassword.setSelection(binding.etPassword.getText().length());
                }
            }
        });


        binding.submitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phone = binding.etPhone.getText().toString();
                password = binding.etPassword.getText().toString();

                if (phone.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                } else if (phone.length() < 10) {
                    Toast.makeText(activity, "Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(activity, "Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    loginAPI();
                }
            }
        });

        binding.forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkGPSAndProceed() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled, show dialog to enable it
            showEnableGPSDialog();

        } else {


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
    public void loginAPI() {

        Bundle bundle = new Bundle();
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("password", password);
        map.put("fbtoken", firebase_token);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "login", map, "login", bundle, apiCallbacks);


    }

    private void start() {
        handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                popup.dismiss();
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(r, 2000);
    }

}