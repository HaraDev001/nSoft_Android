package com.nsoft.laundromat.controller.menu.ui.dashboard;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nsoft.laundromat.R;

import java.util.List;

public class DashboardMachineViewAdapter extends RecyclerView.Adapter<DashboardMachineViewAdapter.ViewHolder> {

    private List<String> machineNo;
    private List<String> machineStatus;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    DashboardMachineViewAdapter(Context context, List<String> machineNo, List<String> machineStatus) {
        this.mInflater = LayoutInflater.from(context);
        this.machineNo = machineNo;
        this.machineStatus = machineStatus;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_machines, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String strMachineNo = machineNo.get(position);
        String strMachineStatus = machineStatus.get(position);
        if (strMachineStatus.equals("AVAILABLE")){
            holder.layMachine.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.icon_machine_available));
        }
        holder.txtMachineStatus.setText(strMachineStatus);
        holder.txtMachineNo.setText(strMachineNo);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return machineStatus.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout layMachine;
        TextView txtMachineNo;
        TextView txtMachineStatus;

        ViewHolder(View itemView) {
            super(itemView);
            layMachine = itemView.findViewById(R.id.lay_machine);
            txtMachineNo = itemView.findViewById(R.id.txt_machine_number);
            txtMachineStatus = itemView.findViewById(R.id.txt_machine_status);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return machineStatus.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

