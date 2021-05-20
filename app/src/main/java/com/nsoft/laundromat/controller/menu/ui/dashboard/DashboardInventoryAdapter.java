package com.nsoft.laundromat.controller.menu.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.model.DashInventoryObject;

import java.util.ArrayList;


public class DashboardInventoryAdapter extends ArrayAdapter<DashInventoryObject> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<DashInventoryObject> _mainStaffInfoView = null;

    private MyClickListener mListener;

    public DashboardInventoryAdapter(@NonNull Context context, int resource, ArrayList<DashInventoryObject> data) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainStaffInfoView = data;
    }

    static class offerInfoViewHolder
    {
        LinearLayout layInventory;
        TextView txtName;
        TextView txtDescription;
        TextView txtUnit;
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
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
            holder.layInventory = row.findViewById(R.id.lay_inventory);
            holder.txtName = row.findViewById(R.id.txt_inventory_name);
            holder.txtDescription = row.findViewById(R.id.txt_description);
            holder.txtUnit = row.findViewById(R.id.txt_unit);
            holder.txtFirst = row.findViewById(R.id.txt_first);
            holder.txtSecond = row.findViewById(R.id.txt_second);
            holder.txtThird = row.findViewById(R.id.txt_third);
            row.setTag(holder);
        }
        DashInventoryObject resultItem = _mainStaffInfoView.get(position);

        holder.txtName.setText(resultItem.getName());
        holder.txtUnit.setText(resultItem.getUnit());
        holder.txtFirst.setText(resultItem.getFirst());
        holder.txtSecond.setText(resultItem.getSecond());
//        int first = 0;
//        if (!resultItem.getFirst().equals("") && resultItem.getFirst() != null){
//            first = Integer.parseInt(resultItem.getFirst());
//        }
//        int second = 0;
//        if (!resultItem.getSecond().equals("") && resultItem.getSecond() != null){
//            second = Integer.parseInt(resultItem.getSecond());
//        }

//        holder.txtThird.setText(resultItem.getThird());
        holder.txtThird.setText(resultItem.getStorage() + "");
        if (resultItem.getCriticalStatus()){
            holder.layInventory.setBackgroundColor(_context.getResources().getColor(R.color.clr_dashboard_light_red));
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
