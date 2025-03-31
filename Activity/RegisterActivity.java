package com.example.wayfuel.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.wayfuel.MainActivity;
import com.example.wayfuel.R;
import com.example.wayfuel.Utils.APPConstants;
import com.example.wayfuel.Utils.DialogUtils;
import com.example.wayfuel.Utils.InputValidator;
import com.example.wayfuel.Utils.NetworkController;
import com.example.wayfuel.databinding.ActivityRegisterBinding;
import com.example.wayfuel.databinding.PopupSuccessBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    Activity activity;
    AlertDialog dialogLoading;
    String phone = "", password = "", name = "", email = "", confirmPass = "",etLicense = "",checklicence = "0";

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


                    if (tag.equalsIgnoreCase("register")) {

                        if (response.getBoolean("status")) {



                            popup.show();
                            start();

                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                    if (tag.equalsIgnoreCase("LicenseData")) {

                        if (response.getBoolean("status")) {

                            JSONObject object = response.getJSONObject("Data");
                            binding.licname.setText(object.getString("Name"));
                            binding.licname.setVisibility(View.VISIBLE);
                            checklicence = "1";
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            binding.licname.setVisibility(View.GONE);
                            checklicence = "0";

                            binding.errorlicence.setVisibility(View.VISIBLE);
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
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        dialogLoading = DialogUtils.createLoading(activity);

        //alert box:
        popupSuccessBinding = PopupSuccessBinding.inflate(getLayoutInflater());
        popupSuccessBinding.text.setText("Registered Successfully");
        popup = DialogUtils.getCustomAlertDialog(activity, popupSuccessBinding.getRoot());
        popup.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(popupSuccessBinding.getRoot().getContext(), R.drawable.background));

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

        binding.visible1.setImageResource(R.drawable.ic_baseline_visibility_24);

        binding.visible1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etConfirmPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    binding.etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.visible1.setImageResource(R.drawable.ic_baseline_visibility_24);
                    binding.etConfirmPassword.setSelection(binding.etConfirmPassword.getText().length());
                } else {
                    binding.etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    binding.visible1.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                    binding.etConfirmPassword.setSelection(binding.etConfirmPassword.getText().length());
                }
            }
        });

        binding.etLicense.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.errorlicence.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 16) {
                    etLicense = binding.etLicense.getText().toString();
                    lisenceCheckAPI();
                }

            }
        });
        binding.submitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = binding.etUsername.getText().toString();
                phone = binding.etPhone.getText().toString();
                email = binding.etEmail.getText().toString();
                confirmPass = binding.etConfirmPassword.getText().toString();
                password = binding.etPassword.getText().toString();
                etLicense = binding.etLicense.getText().toString();

                if (name.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Enter Name", Toast.LENGTH_SHORT).show();
                } else if (phone.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                } else if (phone.length() < 10) {
                    Toast.makeText(activity, "Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
                } else if (etLicense.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Enter License Number", Toast.LENGTH_SHORT).show();
                }  else if (email.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Enter E-Mail", Toast.LENGTH_SHORT).show();
                } else if (!InputValidator.isValidEmail(email)) {
                    Toast.makeText(activity, "Enter Valid E-Mail", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(activity, "Enter Password", Toast.LENGTH_SHORT).show();
                }else if (confirmPass.isEmpty()) {
                    Toast.makeText(activity, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (!confirmPass.matches(password)) {
                    Toast.makeText(activity, "Password does not match", Toast.LENGTH_SHORT).show();
                } else if (checklicence.equalsIgnoreCase("0")) {
                    Toast.makeText(activity, "Enter valid License Number", Toast.LENGTH_SHORT).show();
                } else {
                    registerAPI();
                }
            }
        });

    }

    private void registerAPI() {
        Bundle bundle = new Bundle();
        Map<String, String> map = new HashMap<>();
        map.put("Name", name);
        map.put("PhoneNum", phone);
        map.put("LicenseNum", etLicense);
        map.put("Email", email);
        map.put("Password", password);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "AddCustomer", map, "register", bundle, apiCallbacks);
    }

    private void lisenceCheckAPI() {
        Bundle bundle = new Bundle();
        Map<String, String> map = new HashMap<>();
        map.put("licenseno", etLicense);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "LicenseData", map, "LicenseData", bundle, apiCallbacks);
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