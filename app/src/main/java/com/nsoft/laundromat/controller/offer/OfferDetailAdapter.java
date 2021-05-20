package com.nsoft.laundromat.controller.offer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.model.OfferDetailObject;

import java.util.ArrayList;

public class OfferDetailAdapter extends ArrayAdapter<OfferDetailObject> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<OfferDetailObject> _mainStaffInfoView = null;

    private MyClickListener mListener;

    public OfferDetailAdapter(@NonNull Context context, int resource, ArrayList<OfferDetailObject> data) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainStaffInfoView = data;
    }

    static class offerInfoViewHolder
    {
        TextView txtNo;
        TextView txtDescription;
        TextView txtCount;
        TextView txtUnit;
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
            holder.txtNo = row.findViewById(R.id.txt_no);
            holder.txtDescription = row.findViewById(R.id.txt_description);
            holder.txtCount = row.findViewById(R.id.txt_count);
            holder.txtUnit = row.findViewById(R.id.txt_unit);
            row.setTag(holder);
        }
        OfferDetailObject resultItem = _mainStaffInfoView.get(position);

        holder.txtNo.setText(resultItem.getNo() + "");
        holder.txtDescription.setText(resultItem.getDescription());
        holder.txtCount.setText(resultItem.getCount());
        holder.txtUnit.setText(resultItem.getUnit());

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
