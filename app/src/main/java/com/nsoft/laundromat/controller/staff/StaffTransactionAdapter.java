package com.nsoft.laundromat.controller.staff;

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


public class StaffTransactionAdapter extends ArrayAdapter<StaffTransactionView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<StaffTransactionView> _mainCustomerInfoView = null;


    public StaffTransactionAdapter(@NonNull Context context, int resource, ArrayList<StaffTransactionView> data) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainCustomerInfoView = data;
    }

    static class customerInfoViewHolder
    {
        LinearLayout layTransaction;
        ImageView imgCalendar;
        TextView txtDate;
        TextView txtNo;
        TextView txtTimeIn;
        TextView txtTimeOut;
//        View viewType;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StaffTransactionAdapter.customerInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new StaffTransactionAdapter.customerInfoViewHolder();
//            holder.layTransaction = row.findViewById(R.id.lay_transaction);
//            holder.imgCalendar = row.findViewById(R.id.img_calendar);
            holder.txtDate = row.findViewById(R.id.txt_date);
            holder.txtNo = row.findViewById(R.id.txt_no);
            holder.txtTimeIn =  row.findViewById(R.id.txt_time_in);
            holder.txtTimeOut =  row.findViewById(R.id.txt_time_out);
//            holder.viewType = row.findViewById(R.id.view_type);
            row.setTag(holder);
        }

        StaffTransactionView resultItem = _mainCustomerInfoView.get(position);

        holder.txtDate.setText(resultItem.date);
        holder.txtNo.setText(resultItem.no);
        holder.txtTimeIn.setText(resultItem.timeIn);
        holder.txtTimeOut.setText(resultItem.timeOut);
//        if (resultItem.inOut.equals("TIME-OUT")){
////            holder.imgCalendar.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_calendar_white));
////            holder.layTransaction.setBackground(_context.getResources().getDrawable(R.drawable.item_pressed_red));
//            holder.txtDate.setTextColor(_context.getResources().getColor(R.color.white));
//            holder.txtDay.setTextColor(_context.getResources().getColor(R.color.white));
//            holder.txtTimeIn.setTextColor(_context.getResources().getColor(R.color.white));
//            holder.viewType.setSelected(true);
//        }

        return row;
    }

}
