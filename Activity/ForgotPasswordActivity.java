package com.example.wayfuel.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.wayfuel.MainActivity;
import com.example.wayfuel.R;
import com.example.wayfuel.Utils.APPConstants;
import com.example.wayfuel.Utils.DialogUtils;
import com.example.wayfuel.Utils.NetworkController;
import com.example.wayfuel.databinding.ActivityForgotPasswordBinding;
import com.example.wayfuel.databinding.PopupSuccessBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    Activity activity;
    String phone = "", password = "", confirmPass = "";
    PopupSuccessBinding popupSuccessBinding;
    AlertDialog popup;
    AlertDialog dialogLoading;
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


                    if (tag.equalsIgnoreCase("UpdatePassword")) {

                        if (response.getBoolean("status")) {


                            popup.show();
                            start();

                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }


                    if (tag.equalsIgnoreCase("ForgetPassword")) {

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
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        dialogLoading = DialogUtils.createLoading(activity);

      /*  Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            phone = bundle.getString("phone");
        }*/

        //alert box:
        popupSuccessBinding = PopupSuccessBinding.inflate(getLayoutInflater());
        popupSuccessBinding.text.setText("Password Updated");
        popup = DialogUtils.getCustomAlertDialog(activity, popupSuccessBinding.getRoot());
        popup.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(popupSuccessBinding.getRoot().getContext(), R.drawable.background));


        binding.submitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPass = binding.etConfirmPassword.getText().toString();
                password = binding.etPassword.getText().toString();
                phone = binding.etPhone.getText().toString();

                if (phone.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                } else if (phone.length() < 10) {
                    Toast.makeText(activity, "Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(activity, "Enter Password", Toast.LENGTH_SHORT).show();
                } else if (confirmPass.isEmpty()) {
                    Toast.makeText(activity, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (!confirmPass.matches(password)) {
                    Toast.makeText(activity, "Password does not match", Toast.LENGTH_SHORT).show();
                } else {
                    forgetPassAPI();
                    PassAPI();
                }
            }

        });

    }

    private void forgetPassAPI() {
        Bundle bundle = new Bundle();
        Map<String, String> map = new HashMap<>();
        map.put("newpassword", password);
        map.put("phone", phone);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "UpdatePassword", map, "UpdatePassword", bundle, apiCallbacks);
    }
    private void PassAPI() {
        Bundle bundle = new Bundle();
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "ForgetPassword", map, "ForgetPassword", bundle, apiCallbacks);
    }

    private void start() {
        handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                popup.dismiss();
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(r, 2000);
    }

}