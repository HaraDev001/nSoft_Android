package com.nsoft.laundromat.controller.menu.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nsoft.laundromat.R;

import java.util.List;

import static com.nsoft.laundromat.common.Global.strStaffLogIn;
import static com.nsoft.laundromat.common.Global.strStaffLogOut;

public class DashboardUserViewAdapter extends RecyclerView.Adapter<DashboardUserViewAdapter.ViewHolder> {

    private List<String> mUserStatuses;
    private List<String> mUserNames;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    // data is passed into the constructor
    DashboardUserViewAdapter(Context context, List<String> userStatus, List<String> userNames) {
        this.mInflater = LayoutInflater.from(context);
        this.mUserStatuses = userStatus;
        this.mUserNames = userNames;
        this.mContext = context;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_users, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userStatus = mUserStatuses.get(position);
        String userName = mUserNames.get(position);
//        holder.myUserTime.setText(userStatus);
        holder.myUserName.setText(userName);
        if (userStatus.equals(strStaffLogIn)){
            holder.myUserStatus.setBackground(mContext.getResources().getDrawable(R.drawable.radius_circle_green));
        }
        else if (userStatus.equals(strStaffLogOut)){
            holder.myUserStatus.setBackground(mContext.getResources().getDrawable(R.drawable.radius_circle_red));
        }
        else {
            holder.myUserStatus.setBackground(mContext.getResources().getDrawable(R.drawable.radius_circle_gray));
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mUserNames.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView myView;
        TextView myUserName;
        TextView myUserTime;
        View myUserStatus;

        ViewHolder(View itemView) {
            super(itemView);
            myView = itemView.findViewById(R.id.img_user_photo);
            myUserName = itemView.findViewById(R.id.txt_user_name);
            myUserTime = itemView.findViewById(R.id.txt_user_time);
            myUserStatus = itemView.findViewById(R.id.staff_status);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mUserNames.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

