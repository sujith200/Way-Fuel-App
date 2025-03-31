package com.example.wayfuel.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.wayfuel.Utils.InputValidator;
import com.example.wayfuel.Utils.MyPrefs;
import com.example.wayfuel.Utils.NetworkController;
import com.example.wayfuel.databinding.ActivityEditPriofileBinding;
import com.example.wayfuel.databinding.ActivityLoginBinding;
import com.example.wayfuel.databinding.PopupSuccessBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPriofileActivity extends AppCompatActivity {

    ActivityEditPriofileBinding binding;
    Activity activity;
    AlertDialog dialogLoading;
    String phone = "", password = "", name = "", email = "", confirmPass = "", etLicense = "";
    PopupSuccessBinding popupSuccessBinding;
    AlertDialog popup;
    Handler handler = new Handler();


    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, null);

            try {

                if (apiStatus == APIStatus.SUCCESS) {


                    if (tag.equalsIgnoreCase("view")) {

                        if (response.getBoolean("status")) {

                            JSONObject object = response.getJSONObject("Data");
                            JSONObject object1 = object.getJSONObject("customerData");

                            binding.etUsername.setText(object1.getString("CustName"));
                            binding.etPhone.setText(object1.getString("CustPhoneNum"));
                            binding.etLicense.setText(object1.getString("CustLicenseNum"));
                            binding.etEmail.setText(object1.getString("CustEmail"));

                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                    if (tag.equalsIgnoreCase("updateAcc")) {

                        if (response.getBoolean("status")) {

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
        binding = ActivityEditPriofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        dialogLoading = DialogUtils.createLoading(activity);
        //alert box:
        popupSuccessBinding = PopupSuccessBinding.inflate(getLayoutInflater());
        popupSuccessBinding.text.setText("Updated Successfully");
        popup = DialogUtils.getCustomAlertDialog(activity, popupSuccessBinding.getRoot());
        popup.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(popupSuccessBinding.getRoot().getContext(), R.drawable.background));

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        viewAPI();


        binding.submitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = binding.etUsername.getText().toString();
                phone = binding.etPhone.getText().toString();
                email = binding.etEmail.getText().toString();
                etLicense = binding.etLicense.getText().toString();

                if (name.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Enter Name", Toast.LENGTH_SHORT).show();
                } else if (phone.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                } else if (phone.length() < 10) {
                    Toast.makeText(activity, "Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
                } else if (email.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Enter E-Mail", Toast.LENGTH_SHORT).show();
                } else if (!InputValidator.isValidEmail(email)) {
                    Toast.makeText(activity, "Enter Valid E-Mail", Toast.LENGTH_SHORT).show();
                } else {
                    registerAPI();
                }
            }
        });

    }

    private void viewAPI() {
        Map<String, String> map = new HashMap<>();
        map.put("CustID", MyPrefs.getInstance(activity).getString(UserData.KEY_USER_ID));
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "ViewCustomer", map, "view", new Bundle(), apiCallbacks);
    }

    private void registerAPI() {
        Bundle bundle = new Bundle();
        Map<String, String> map = new HashMap<>();
        map.put("CustID", MyPrefs.getInstance(activity).getString(UserData.KEY_USER_ID));
        map.put("CustName", name);
        map.put("CustEmail", email);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "UpdateCustomer", map, "updateAcc", bundle, apiCallbacks);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
        finish();
    }
}