package com.nsoft.laundromat.controller.report.ui.hourly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.controller.model.BranchObject;

import java.util.ArrayList;

public class ConsolidateBranchViewAdapter extends RecyclerView.Adapter<ConsolidateBranchViewAdapter.ViewHolder> {

//    private List<String> mShopNames;
//    private List<String> mBranchNames;
//    private List<String> mSelectedStatus;
    private ArrayList<BranchObject> mBranchObjects;
    private LayoutInflater mInflater;
    private static ConsolidatedFragment.ItemClickListener mClickListener;

    // data is passed into the constructor
    ConsolidateBranchViewAdapter(Context context, ArrayList<BranchObject> branchObjects, ConsolidatedFragment.ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mBranchObjects = branchObjects;
        this.mClickListener = itemClickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_branch, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String shopName = mBranchObjects.get(position).getShopName();
        String branchName = mBranchObjects.get(position).getBranchName();
        String selectedStatus = mBranchObjects.get(position).getStatus();
        holder.myBranchTime.setText(shopName);
        holder.myShopName.setText(branchName);
        holder.layBranch.setTag("category" + position);
        if (selectedStatus.equals("true")){
            holder.layBranch.setSelected(true);
//            holder.myView.setImageResource(R.drawable.home_clor_icon);
        }
        else {
            holder.layBranch.setSelected(false);
//            holder.myView.setImageResource(R.drawable.icon_home_white);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mBranchObjects.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout layBranch;
        ImageView myView;
        TextView myShopName;
        TextView myBranchTime;

        ViewHolder(View itemView) {
            super(itemView);
            layBranch = itemView.findViewById(R.id.lay_branch);
            myView = itemView.findViewById(R.id.img_shop_logo);
            myShopName = itemView.findViewById(R.id.txt_shop_name);
            myBranchTime = itemView.findViewById(R.id.txt_branch_name);
            layBranch.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

}

