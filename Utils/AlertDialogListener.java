package com.example.wayfuel.Utils;

public interface AlertDialogListener {

    void onYesClick(String requestCode);
    void onNoClick(String requestCode);
    void onNeutralClick(String requestCode);
}
