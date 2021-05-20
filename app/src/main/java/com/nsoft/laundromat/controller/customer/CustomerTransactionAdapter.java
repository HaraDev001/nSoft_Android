package com.nsoft.laundromat.controller.customer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nsoft.laundromat.R;

import java.util.ArrayList;


public class CustomerTransactionAdapter extends ArrayAdapter<CustomerTransactionView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<CustomerTransactionView> _mainCustomerInfoView = null;


    public CustomerTransactionAdapter(@NonNull Context context, int resource, ArrayList<CustomerTransactionView> data) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainCustomerInfoView = data;
    }

    static class customerInfoViewHolder
    {
        LinearLayout layTransaction;
        ImageView imgStatus;
        TextView txtOperationId;
        TextView txtDateTime;
        TextView txtAmount;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CustomerTransactionAdapter.customerInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new CustomerTransactionAdapter.customerInfoViewHolder();
            holder.layTransaction = row.findViewById(R.id.lay_transaction);
            holder.imgStatus = (ImageView) row.findViewById(R.id.img_status);
            holder.txtOperationId = (TextView) row.findViewById(R.id.txt_operation_id);
            holder.txtDateTime = (TextView) row.findViewById(R.id.txt_date_time);
            holder.txtAmount = (TextView) row.findViewById(R.id.txt_amount);
            row.setTag(holder);
        }

        CustomerTransactionView resultItem = _mainCustomerInfoView.get(position);

        holder.txtOperationId.setText(resultItem.operationId);
        holder.txtDateTime.setText(resultItem.dateTime);
        holder.txtAmount.setText(resultItem.amount);
        if (resultItem.no % 2 == 1){
            holder.layTransaction.setBackground(_context.getResources().getDrawable(R.drawable.item_pressed_gray));
        }
        else
            holder.layTransaction.setBackground(_context.getResources().getDrawable(R.drawable.item_pressed_white));
        if (resultItem.status){
            holder.imgStatus.setImageDrawable(_context.getResources().getDrawable(R.drawable.block_copy_icon));
            holder.layTransaction.setBackground(_context.getResources().getDrawable(R.drawable.item_pressed_red));
            holder.txtOperationId.setTextColor(_context.getResources().getColor(R.color.white));
            holder.txtDateTime.setTextColor(_context.getResources().getColor(R.color.white));
            holder.txtAmount.setTextColor(_context.getResources().getColor(R.color.white));
        }

        return row;
    }

}
