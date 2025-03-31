package com.example.wayfuel.Adapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wayfuel.Utils.OnItemViewClickListener;
import com.example.wayfuel.databinding.RecyclerBannerBinding;
import java.util.List;

public class Banner_Adapter extends RecyclerView.Adapter<Banner_Adapter.AppViewHolder>{
    Activity activity;
    List<Appitems> list;
    OnItemViewClickListener onItemViewClickListener;

    public Banner_Adapter(Activity activity, List<Appitems> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerBannerBinding binding = RecyclerBannerBinding.inflate(inflater, parent, false);
        return new Banner_Adapter.AppViewHolder(binding.getRoot(), binding);
    }


    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        RecyclerBannerBinding binding = holder.binding;
        Banner_Adapter.Appitems lists = list.get(position);

        binding.bannerImg.setImageResource(lists.getImg());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Appitems{
        int img;

        public Appitems(int img) {
            this.img = img;
        }

        public int getImg() {
            return img;
        }

        public void setImg(int img) {
            this.img = img;
        }
    }
    public class AppViewHolder extends RecyclerView.ViewHolder {
        RecyclerBannerBinding binding;

        public AppViewHolder(@NonNull View itemView, RecyclerBannerBinding binding) {
            super(itemView);
            this.binding = binding;

        }

    }
}
