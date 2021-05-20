package com.nsoft.laundromat.controller.report.ui.xreading;

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
import com.nsoft.laundromat.controller.model.ReportObject;

import java.util.ArrayList;

public class ReportThreeAdapter extends ArrayAdapter<ReportObject> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<ReportObject> _mainStaffInfoView = null;

    private MyClickListener mListener;

    public ReportThreeAdapter(@NonNull Context context, int resource, ArrayList<ReportObject> data) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainStaffInfoView = data;
    }

    static class offerInfoViewHolder
    {
        TextView txtTitle;
        TextView txtSubTitle;
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
            holder.txtTitle = row.findViewById(R.id.txt_title);
            holder.txtAmount = row.findViewById(R.id.txt_amount);
            holder.txtSubTitle = row.findViewById(R.id.txt_sub_title);
            row.setTag(holder);
        }
        ReportObject resultItem = _mainStaffInfoView.get(position);

        holder.txtTitle.setText(resultItem.getTitle());
        holder.txtAmount.setText(resultItem.getAmount());
        holder.txtSubTitle.setText(resultItem.getSubTitle());

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
