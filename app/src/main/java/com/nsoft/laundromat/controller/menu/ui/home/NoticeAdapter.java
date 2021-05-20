package com.nsoft.laundromat.controller.menu.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.model.NoticeObject;

import java.util.ArrayList;

import static com.nsoft.laundromat.common.Global.NOTICE_MESSAGE;
import static com.nsoft.laundromat.common.Global.NOTICE_NOTICE;
import static com.nsoft.laundromat.common.Global.NOTICE_REQUEST;
import static com.nsoft.laundromat.common.Global.NOTICE_WARNING;


public class NoticeAdapter extends ArrayAdapter<NoticeObject> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<NoticeObject> _mainStaffInfoView = null;
    private MyClickListener mListener;

    public NoticeAdapter(@NonNull Context context, int resource, ArrayList<NoticeObject> data, MyClickListener listener) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainStaffInfoView = data;
        this.mListener = listener;
    }

    static class noticeInfoViewHolder
    {
        LinearLayout layItemNotice;
        TextView txtTitle;
        TextView txtIntroduction;
        ImageView imgNoticeType;
        TextView btnFirst;
        TextView btnSecond;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        noticeInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new noticeInfoViewHolder();
            holder.layItemNotice = row.findViewById(R.id.lay_item_notice);
            holder.txtTitle = row.findViewById(R.id.txt_notice_title);
            holder.txtIntroduction = row.findViewById(R.id.txt_notice_introduction);
            holder.imgNoticeType = row.findViewById(R.id.img_notice_type);
            holder.btnFirst = row.findViewById(R.id.btn_first);
            holder.btnFirst.setOnClickListener(mListener);
            holder.btnFirst.setTag(position);
            holder.btnSecond = row.findViewById(R.id.btn_second);
            holder.btnSecond.setOnClickListener(mListener);
            holder.btnSecond.setTag(position);

            row.setTag(holder);
        }

        NoticeObject resultItem = _mainStaffInfoView.get(position);

        holder.txtTitle.setText(resultItem.getTitle());
        if (!resultItem.getViewStatus().equals("True")){
            holder.txtTitle.setTypeface(null, Typeface.BOLD);
        }

        if (resultItem.getContent().length() > 22){
            holder.txtIntroduction.setText(resultItem.getContent().substring(0,22) + "...");
        }
        else{
            holder.txtIntroduction.setText(resultItem.getContent());
        }
        if (resultItem.getType().equals(NOTICE_REQUEST)){
            holder.imgNoticeType.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_notice_question));
            if (!resultItem.getActionStatus().equals("True")){
                holder.btnFirst.setVisibility(View.VISIBLE);
                holder.btnFirst.setText("Yes");
                holder.btnSecond.setText("No");
            }
            else {
                holder.btnFirst.setVisibility(View.GONE);
                holder.btnSecond.setText("HIDE");
            }
        }
        else if (resultItem.getType().equals(NOTICE_NOTICE)){
            holder.imgNoticeType.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_notice_bell));
            holder.btnFirst.setVisibility(View.GONE);
            holder.btnSecond.setText("HIDE");
        }
        else if (resultItem.getType().equals(NOTICE_MESSAGE)){
            holder.imgNoticeType.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_notice_post));
            holder.btnFirst.setVisibility(View.GONE);
            holder.btnSecond.setText("HIDE");
        }
        else if (resultItem.getType().equals(NOTICE_WARNING)){
            holder.imgNoticeType.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_notice_remark));
            holder.btnFirst.setVisibility(View.GONE);
            holder.btnSecond.setText("HIDE");
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
