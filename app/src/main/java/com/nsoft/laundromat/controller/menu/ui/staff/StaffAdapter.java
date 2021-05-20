package com.nsoft.laundromat.controller.menu.ui.staff;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.OnMultiClickListener;

import java.util.ArrayList;


public class StaffAdapter extends ArrayAdapter<StaffView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<StaffView> _mainStaffInfoView = null;

    private MyClickListener mListener;

    public StaffAdapter(@NonNull Context context, int resource, ArrayList<StaffView> data, MyClickListener listener) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainStaffInfoView = data;
        this.mListener = listener;
    }

    static class staffInfoViewHolder
    {
        ImageView imgCustomerLogo;
        TextView txtName;
        TextView txtRole;
        TextView txtTime;
        TextView txtTimeIn;
        TextView txtTimeOut;
        ImageView imgPhone;
        ImageView imgMessage;
        View viewType;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        staffInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new staffInfoViewHolder();
            holder.imgCustomerLogo = row.findViewById(R.id.img_user_photo);
            holder.txtName = row.findViewById(R.id.txt_staff_name);
            holder.txtRole = row.findViewById(R.id.txt_role);
            holder.txtTime = row.findViewById(R.id.txt_date_time);
            holder.txtTimeIn = row.findViewById(R.id.txt_time_in);
            holder.txtTimeOut = row.findViewById(R.id.txt_time_out);
            holder.imgPhone = row.findViewById(R.id.img_phone);
            holder.viewType = row.findViewById(R.id.view_type);
            holder.imgPhone.setOnClickListener(mListener);
            holder.imgPhone.setTag(position);
            holder.imgMessage = row.findViewById(R.id.img_message);
            holder.imgMessage.setOnClickListener(mListener);
            holder.imgMessage.setTag(position);
            row.setTag(holder);
        }

        StaffView resultItem = _mainStaffInfoView.get(position);

        holder.txtName.setText(resultItem.name);
        holder.txtRole.setText(resultItem.role);
        holder.txtTime.setText(resultItem.shiftNo);
        holder.txtTimeIn.setText(resultItem.timeIn);
        holder.txtTimeOut.setText(resultItem.timeOut);

        if (resultItem.timeIn.equals("") && resultItem.timeOut.equals("")){
            holder.viewType.setBackground(_context.getResources().getDrawable(R.drawable.radius_circle_gray));
        }
        else if (!resultItem.timeIn.equals("") && resultItem.timeOut.equals("")){
            holder.viewType.setBackground(_context.getResources().getDrawable(R.drawable.radius_circle));
        }
        else{
            holder.viewType.setBackground(_context.getResources().getDrawable(R.drawable.radius_circle_red));
        }

        return row;
    }

    public static abstract class MyClickListener extends OnMultiClickListener {
        @Override
        public void onMultiClick(View v) {
            myBtnOnClick((Integer) v.getTag(), v);
        }
        public abstract void myBtnOnClick(int position, View v);
    }
}
