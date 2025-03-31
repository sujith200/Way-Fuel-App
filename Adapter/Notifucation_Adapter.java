package com.example.wayfuel.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wayfuel.Utils.CommonFunctions;
import com.example.wayfuel.Utils.OnItemViewClickListener;
import com.example.wayfuel.databinding.RecyclerLocationlistBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Notifucation_Adapter extends RecyclerView.Adapter<Notifucation_Adapter.AppViewHolder> {

    Activity activity;
    List<JSONObject> list = new ArrayList<>();
    OnItemViewClickListener onItemViewClickListener;
    List<JSONObject> listBunk = new ArrayList<>();
    boolean granted = false;
    String phone = "";
    AdapterPetrolBunk adapterPetrolBunk;

    public Notifucation_Adapter(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerLocationlistBinding binding = RecyclerLocationlistBinding.inflate(inflater, parent, false);
        return new Notifucation_Adapter.AppViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        RecyclerLocationlistBinding binding = holder.binding;
        JSONObject object = list.get(position);

        try {

            binding.name.setText(object.getString("CustName"));
            binding.phone.setText(object.getString("CustPhoneNum"));

            adapterPetrolBunk = new AdapterPetrolBunk(activity, listBunk, new OnItemViewClickListener() {
                @Override
                public void onClick(View v, int i) throws JSONException {

                }
            });

            binding.recyclerBunk.setAdapter(adapterPetrolBunk);

            listBunk.clear();
            CommonFunctions.setJSONArray(object, "petrol_bunks", listBunk, adapterPetrolBunk);


            binding.callBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    try {

                        phone = object.getString("CustPhoneNum");

                        if (!granted) {
                            openDialer();
                        } else {
                            checkPermission();
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


            binding.locationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        onItemViewClickListener.onClick(view, position);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
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

    void openDialer() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        activity.startActivity(intent);
    }


    void checkPermission() {

        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            granted = false;
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CALL_PHONE},
                    99);
        } else {
            granted = true;
        }
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {
        RecyclerLocationlistBinding binding;

        public AppViewHolder(@NonNull View itemView, RecyclerLocationlistBinding binding) {
            super(itemView);
            this.binding = binding;

        }

    }
}
