package com.nsoft.laundromat.controller.menu.ui.home;

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

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ShopAdapter extends ArrayAdapter<ShopView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<ShopView> _mainStaffInfoView = null;

    public ShopAdapter(@NonNull Context context, int resource, ArrayList<ShopView> data) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainStaffInfoView = data;
    }

    static class shopInfoViewHolder
    {
        LinearLayout layItemShop;
        TextView txtName;
        TextView txtBranch;
        TextView txtAmount;
        ImageView imgSelected;
        ImageView imgShopLogo;
        View viewOnlineStatus;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        shopInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new shopInfoViewHolder();
            holder.layItemShop = row.findViewById(R.id.lay_item_shop);
            holder.txtName = row.findViewById(R.id.txt_shop_name);
            holder.txtBranch = row.findViewById(R.id.txt_branch_name);
            holder.txtAmount = row.findViewById(R.id.txt_amount);
            holder.imgSelected = row.findViewById(R.id.img_selected);
            holder.imgShopLogo = row.findViewById(R.id.img_shop_logo);
            holder.viewOnlineStatus = row.findViewById(R.id.view_online_status);
            row.setTag(holder);
        }

        ShopView resultItem = _mainStaffInfoView.get(position);

        holder.txtName.setText(resultItem.name);
        holder.txtBranch.setText(resultItem.branch);
        DecimalFormat df = new DecimalFormat("#,###.00");
        Double amount = Double.parseDouble(resultItem.amount);
        holder.txtAmount.setText(df.format(amount));
        if (resultItem.onlineStatus != 1){
            holder.viewOnlineStatus.setBackground(_context.getResources().getDrawable(R.drawable.radius_circle_gray));
            holder.txtAmount.setText("0.00");
        }
        if (resultItem.selected){
            holder.layItemShop.setBackground(_context.getResources().getDrawable(R.drawable.green_rectangle));
            holder.txtName.setTextColor(_context.getResources().getColor(R.color.white));
            holder.txtBranch.setTextColor(_context.getResources().getColor(R.color.white));
            holder.txtAmount.setTextColor(_context.getResources().getColor(R.color.white));
            holder.imgShopLogo.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_home_white));
        }

        return row;
    }
}
