package com.nsoft.laundromat.controller.menu.ui.offer;


import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.nsoft.laundromat.R;

// stores and recycles views as they are scrolled off screen
public class OfferViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public Button btnCategoryName;
    public ItemClickListener mClickListener;

    public OfferViewHolder(View itemView) {
        super(itemView);
        btnCategoryName = itemView.findViewById(R.id.btn_name);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mClickListener != null) mClickListener.onItemClick(view);
    }

    public interface ItemClickListener {
        void onItemClick(View view);
    }
}
