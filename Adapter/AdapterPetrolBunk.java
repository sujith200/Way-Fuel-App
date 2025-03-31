package com.example.wayfuel.Adapter;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wayfuel.Utils.CommonFunctions;
import com.example.wayfuel.Utils.OnItemViewClickListener;
import com.example.wayfuel.databinding.AdapterPetrolBinding;
import com.google.android.gms.location.FusedLocationProviderClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdapterPetrolBunk extends RecyclerView.Adapter<AdapterPetrolBunk.ViewHolder> {

    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 44;
    double latitude = 0.0, longitude = 0.0;
    JSONObject object = new JSONObject();


    public AdapterPetrolBunk(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AdapterPetrolBunk.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterPetrolBinding binding = AdapterPetrolBinding.inflate(CommonFunctions.getAdapterInflater(parent), parent, false);
        return new AdapterPetrolBunk.ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPetrolBunk.ViewHolder holder, int position) {

        object = list.get(position);
        AdapterPetrolBinding binding = holder.binding;

        try {

            binding.tvBunkName.setText(object.getString("PetrolBunkName"));
            binding.tvBunkAddress.setText(object.getString("PetrolBunkAddress"));


            binding.imgLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        checkGPSAndProceed1();
                    } else {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void checkGPSAndProceed1() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled, show dialog to enable it
            showEnableGPSDialog();
        } else {

            try {

                latitude = object.getDouble("CustLatitude");
                longitude = object.getDouble("CustLongitude");

                String uri = String.format("geo:%f,%f", latitude, longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                activity.startActivity(intent);

            } catch (Exception e) {
                Toast.makeText(activity, "Location is Empty", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showEnableGPSDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Enable GPS")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open location settings
                        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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


    public class ViewHolder extends RecyclerView.ViewHolder {

        AdapterPetrolBinding binding;

        public ViewHolder(@NonNull View itemView, AdapterPetrolBinding binding) {
            super(itemView);

            this.binding = binding;
        }
    }
}
