package com.nsoft.laundromat.controller.item;

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

public class ItemReplenishAdapter extends ArrayAdapter<ItemView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<ItemView> _mainCustomerInfoView = null;
    private MyClickListener myClickListener;

    public ItemReplenishAdapter(@NonNull Context context, int resource, ArrayList<ItemView> data, MyClickListener listener) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainCustomerInfoView = data;
        this.myClickListener = listener;
    }

    static class itemInfoViewHolder
    {
        TextView txtItemName;
        TextView txtExpiryDate;
        TextView txtItemAmount;
        ImageView imgDelete;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        itemInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new itemInfoViewHolder();
            holder.txtItemName = row.findViewById(R.id.txt_item_name);
            holder.txtItemAmount = row.findViewById(R.id.txt_amount);
            holder.txtExpiryDate = row.findViewById(R.id.txt_expiry_date);
            holder.imgDelete = row.findViewById(R.id.img_delete);
            holder.imgDelete.setTag(position);
            holder.imgDelete.setOnClickListener(myClickListener);
            row.setTag(holder);
        }

        ItemView resultItem = _mainCustomerInfoView.get(position);

        holder.txtItemName.setText(resultItem.itemName);
        if (resultItem.expiredDate != null){
            holder.txtExpiryDate.setText(resultItem.expiredDate.split(" ")[0]);
        }
        holder.txtItemAmount.setText(resultItem.itemQty + resultItem.itemUnit);

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
