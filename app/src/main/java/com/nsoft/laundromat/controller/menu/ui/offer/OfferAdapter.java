package com.nsoft.laundromat.controller.menu.ui.offer;

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


public class OfferAdapter extends ArrayAdapter<OfferView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<OfferView> _mainStaffInfoView = null;

    private MyClickListener mListener;

    public OfferAdapter(@NonNull Context context, int resource, ArrayList<OfferView> data) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainStaffInfoView = data;
    }

    static class offerInfoViewHolder
    {
        ImageView imgItemLogo;
        TextView txtName;
        TextView txtDescription;
        TextView txtAmount;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        offerInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new offerInfoViewHolder();
            holder.imgItemLogo = row.findViewById(R.id.img_item);
            holder.txtName = row.findViewById(R.id.txt_item_name);
            holder.txtDescription = row.findViewById(R.id.txt_item_description);
            holder.txtAmount = row.findViewById(R.id.txt_amount);
            row.setTag(holder);
        }
        OfferView resultItem = _mainStaffInfoView.get(position);

        holder.txtName.setText(resultItem.code);
        holder.txtDescription.setText(resultItem.description);
        holder.txtAmount.setText(resultItem.price);
        if (resultItem.kind.equals("Item")){
            holder.imgItemLogo.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_item));
        }
        else if (resultItem.kind.equals("Service")){
            holder.imgItemLogo.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_service));
        }
        else if (resultItem.kind.equals("Package")){
            holder.imgItemLogo.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_package));
        }
        else {
            holder.imgItemLogo.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_package));
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
