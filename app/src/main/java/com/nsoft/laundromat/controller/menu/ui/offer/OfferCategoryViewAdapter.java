package com.nsoft.laundromat.controller.menu.ui.offer;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.nsoft.laundromat.R;

import java.util.ArrayList;
import java.util.List;

public class OfferCategoryViewAdapter extends RecyclerView.Adapter<OfferCategoryViewAdapter.ViewHolder> {

    private List<String> categoryName;
    private ArrayList<OfferCategoryView> categoryViewArrayList;
    private LayoutInflater mInflater;
    private static OfferFragment.ItemClickListener mClickListener;

    // data is passed into the constructor
    public OfferCategoryViewAdapter(Context context, ArrayList<OfferCategoryView> categoryViewArrayList, OfferFragment.ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.categoryViewArrayList = categoryViewArrayList;
        this.mClickListener = itemClickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_offer_category, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String strCategoryName = categoryViewArrayList.get(position).categoryName;
        holder.btnCategoryName.setText(strCategoryName);
        holder.btnCategoryName.setTag("category" + position);
        if (categoryViewArrayList.get(position).selectedStatus)
            holder.btnCategoryName.setSelected(true);
        else
            holder.btnCategoryName.setSelected(false);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return categoryViewArrayList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button btnCategoryName;

        public ViewHolder(View itemView) {
            super(itemView);
            btnCategoryName = itemView.findViewById(R.id.btn_name);
            btnCategoryName.setOnClickListener(this);
    }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return categoryName.get(id);
    }
}

