package com.nsoft.laundromat.controller.menu.ui.customer;

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


public class CustomerAdapter extends ArrayAdapter<CustomerView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<CustomerView> _mainCustomerInfoView = null;

    private MyClickListener mListener;

    public CustomerAdapter(@NonNull Context context, int resource, ArrayList<CustomerView> data, MyClickListener listener) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainCustomerInfoView = data;
        this.mListener = listener;
    }

    static class customerInfoViewHolder
    {
        ImageView imgCustomerLogo;
        TextView txtName;
        TextView txtPhoneNumber;
        TextView txtAmount;
        ImageView imgPhone;
        ImageView imgMessage;
        TextView txtDayNumber;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CustomerAdapter.customerInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new CustomerAdapter.customerInfoViewHolder();
            holder.imgCustomerLogo = (ImageView) row.findViewById(R.id.img_user_photo);
            holder.txtName = (TextView) row.findViewById(R.id.txt_customer_name);
            holder.txtPhoneNumber = (TextView) row.findViewById(R.id.txt_phone_number);
            holder.txtAmount = (TextView) row.findViewById(R.id.txt_amount);
            holder.imgPhone = row.findViewById(R.id.img_phone);
            holder.imgPhone.setOnClickListener(mListener);
            holder.imgPhone.setTag(position);
            holder.imgMessage = row.findViewById(R.id.img_message);
            holder.imgMessage.setOnClickListener(mListener);
            holder.imgMessage.setTag(position);
            holder.txtDayNumber = row.findViewById(R.id.txt_day_number);
            row.setTag(holder);
        }

        CustomerView resultItem = _mainCustomerInfoView.get(position);

        holder.txtName.setText(resultItem.name);
        holder.txtPhoneNumber.setText(resultItem.phoneNumber);
        holder.txtAmount.setText(resultItem.amount);
        holder.txtDayNumber.setText(resultItem.dayNumber + "");
        if (resultItem.dayNumber < 9){
            holder.txtDayNumber.setBackground(_context.getResources().getDrawable(R.drawable.oval_green));
        }
        else if (resultItem.dayNumber < 17){
            holder.txtDayNumber.setBackground(_context.getResources().getDrawable(R.drawable.oval_yellow));
        }
        else {
            holder.txtDayNumber.setBackground(_context.getResources().getDrawable(R.drawable.oval_orange));
        }

        return row;
    }


    public static abstract class MyClickListener extends OnMultiClickListener {
        /*@Override
        public void onClick(View v) {
            myBtnOnClick((Integer) v.getTag(), v);
        }*/
        @Override
        public void onMultiClick(View v) {
            myBtnOnClick((Integer) v.getTag(), v);
        }
        public abstract void myBtnOnClick(int position, View v);
    }
}
