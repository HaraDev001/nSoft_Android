package com.nsoft.laundromat.controller.menu.ui.transactions;

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

import java.util.ArrayList;


public class TransactionAdapter extends ArrayAdapter<TransactionView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<TransactionView> _mainCustomerInfoView = null;

    public TransactionAdapter(@NonNull Context context, int resource, ArrayList<TransactionView> data) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainCustomerInfoView = data;
    }

    static class customerInfoViewHolder
    {
        ImageView imgTransactionLogo;
        TextView txtUserName;
        TextView txtOperationId;
        TextView txtAmount;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TransactionAdapter.customerInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new TransactionAdapter.customerInfoViewHolder();
            holder.imgTransactionLogo = (ImageView) row.findViewById(R.id.img_transaction);
            holder.txtUserName = (TextView) row.findViewById(R.id.txt_user_name);
            holder.txtOperationId = (TextView) row.findViewById(R.id.txt_operation_id);
            holder.txtAmount = (TextView) row.findViewById(R.id.txt_amount);
            row.setTag(holder);
        }

        TransactionView resultItem = _mainCustomerInfoView.get(position);

        holder.txtUserName.setText(resultItem.name);
        holder.txtOperationId.setText(resultItem.operationId);
        holder.txtAmount.setText(resultItem.amount);

        return row;
    }


}
