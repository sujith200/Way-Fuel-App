package com.example.wayfuel.Utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.wayfuel.R;
import com.example.wayfuel.databinding.DialogLoadingBinding;
import com.example.wayfuel.databinding.DialogYesNoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DialogUtils {

    public static AlertDialog createLoading(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        DialogLoadingBinding binding = DialogLoadingBinding.inflate(activity.getLayoutInflater(), null, false);
        builder.setView(binding.getRoot());

        AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /*alertDialog.getWindow().setDimAmount(0);*/
        alertDialog.setCancelable(false);
        return alertDialog;
    }

    public static void setButtonTextColor(Activity activity, android.app.AlertDialog alertDialog) {
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (bq != null)
            bq.setTextColor(ContextCompat.getColor(activity, R.color.black));
        Button bqN = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (bqN != null)
            bqN.setTextColor(ContextCompat.getColor(activity, R.color.black));
        Button bqNE = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        if (bqNE != null)
            bqNE.setTextColor(ContextCompat.getColor(activity, R.color.black));
    }


    public static void setButtonTextColor1(Activity activity, AlertDialog alertDialog) {
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (bq != null)
            bq.setTextColor(ContextCompat.getColor(activity, R.color.black));
        Button bqN = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (bqN != null)
            bqN.setTextColor(ContextCompat.getColor(activity, R.color.black));
        Button bqNE = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        if (bqNE != null)
            bqNE.setTextColor(ContextCompat.getColor(activity, R.color.black));
    }


    public static void dismissLoading(AlertDialog dialogLoading, AlertDialog dialogProgress, SwipeRefreshLayout swipeRefreshLayout) {

        if (dialogLoading != null) {
            if (dialogLoading.isShowing()) {
                dialogLoading.dismiss();
            }
        }
        if (dialogProgress != null) {
            if (dialogProgress.isShowing()) {
                dialogProgress.dismiss();
            }
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    public static void showYesNoAlert1(Activity activity, final String requestCode, String title, String message, boolean cancelable, final AlertYesNoListener yesNoListener) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        yesNoListener.onYesClick(requestCode);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        yesNoListener.onNoClick(requestCode);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        DialogYesNoBinding yesNoBinding = DialogYesNoBinding.inflate(activity.getLayoutInflater(), null, false);
        builder.setView(yesNoBinding.getRoot());
        final AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(title);
        yesNoBinding.textViewMessage.setText(message);
        //builder.setPositiveButton("Yes", dialogClickListener);
        // builder.setNegativeButton("No", dialogClickListener);
        yesNoBinding.buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                yesNoListener.onNoClick(requestCode);
            }
        });
        yesNoBinding.buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                yesNoListener.onYesClick(requestCode);

            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // alertDialog.setMessage(message);
        if (cancelable) {
            alertDialog.setCanceledOnTouchOutside(false);
        } else {
            alertDialog.setCancelable(false);
        }
        //setButtonTextColor(activity, alertDialog);
        alertDialog.show();
    }

    public static void showYesNoAlert(Activity activity, final String requestCode, String message, boolean cancelable, Bundle bundle, final AlertYesNoListener yesNoListener) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yesNoListener.onYesClick(requestCode);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton("Yes", dialogClickListener);
        builder.setNegativeButton("No", dialogClickListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.setMessage(message);
        if (cancelable) {
            alertDialog.setCanceledOnTouchOutside(false);
        } else {
            alertDialog.setCancelable(false);
        }
        alertDialog.show();

    }

/*
    public static void showRentYesNoAlert(Activity activity, final String requestCode, String message, boolean cancelable, Bundle bundle, final AlertRentYesNoListener yesNoListener) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yesNoListener.onYesClick(which, requestCode, bundle);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton("Yes", dialogClickListener);
        builder.setNegativeButton("No", dialogClickListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.setMessage(message);
        if (cancelable) {
            alertDialog.setCanceledOnTouchOutside(false);
        } else {
            alertDialog.setCancelable(false);
        }
        alertDialog.show();

    }
*/


    public static void showRentYesNoAlert(Activity activity, final String requestCode, String message, boolean cancelable, Bundle bundle, final AlertYesNoListener1 yesNoListener) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yesNoListener.onYesClick(which, requestCode, bundle);
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        //    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton("Ok", dialogClickListener);
        // builder.setNegativeButton("No", dialogClickListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.setMessage(message);
        if (cancelable) {
            alertDialog.setCanceledOnTouchOutside(false);
        } else {
            alertDialog.setCancelable(false);
        }
        alertDialog.show();

    }


    public static void showRentYesNoAlert1(Activity activity, final String requestCode, String message, boolean cancelable, Bundle bundle, final AlertYesNoListener1 yesNoListener) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yesNoListener.onYesClick(which, requestCode, bundle);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
        //AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton("Ok", dialogClickListener);


        AlertDialog alertDialog = builder.create();

        alertDialog.setMessage(message);
        if (cancelable) {
            alertDialog.setCanceledOnTouchOutside(false);
        } else {
            alertDialog.setCancelable(false);
        }
        alertDialog.show();

        alertDialog.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                if (positiveButton != null) {
                    positiveButton.setTextColor(activity.getResources().getColor(R.color.black));
                }

                alertDialog.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    public static void showOKAlert(Activity activity, final String requestCode, String message, Bundle bundle, boolean cancelable, final AlertYesNoListener yesNoListener) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yesNoListener.onYesClick(requestCode);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton("OK", dialogClickListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.setMessage(message);
        if (cancelable) {
            alertDialog.setCanceledOnTouchOutside(false);
        } else {
            alertDialog.setCancelable(false);
        }
        alertDialog.show();
    }

    public static BottomSheetDialog getBottomDialog(Activity activity, View view) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(view);
        //bottomSheetDialog.setCanceledOnTouchOutside(false);
        return bottomSheetDialog;
    }

    public static AlertDialog getCustomAlertDialog(Activity activity, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }


}
