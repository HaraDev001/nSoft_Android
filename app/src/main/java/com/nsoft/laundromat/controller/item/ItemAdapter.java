package com.nsoft.laundromat.controller.item;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.OnMultiClickListener;

import java.util.ArrayList;

import static com.nsoft.laundromat.common.Global.itemViewCategoryArrayList;

public class ItemAdapter extends ArrayAdapter<ItemView> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<ItemView> _mainCustomerInfoView = null;
    private MyClickListener myClickListener;
    private ItemInfoViewHolder mViewHolder;

    //定义成员变量mTouchItemPosition,用来记录手指触摸的EditText的位置
    private int mTouchItemPosition = -1;

    public ItemAdapter(@NonNull Context context, int resource, ArrayList<ItemView> data, MyClickListener listener) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainCustomerInfoView = data;
        this.myClickListener = listener;
    }

    @Override
    public int getCount() {
        return _mainCustomerInfoView.size();
    }

    @Override
    public ItemView getItem(int position) {
        return _mainCustomerInfoView.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ItemInfoViewHolder
    {
        TextView txtItemName;
        TextView txtItemUnit;
        EditText edtQuantity;
        Button btnExpiry;
        ImageView imgCalendar;
        ImageView imgAdd;
        MyTextWatcher mTextWatcher;

        //动态更新TextWathcer的position
        public void updatePosition(int position) {
            mTextWatcher.updatePosition(position);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            mViewHolder = new ItemInfoViewHolder();
            mViewHolder.txtItemName = row.findViewById(R.id.txt_item_name);
            mViewHolder.txtItemUnit = row.findViewById(R.id.txt_unit);
            mViewHolder.edtQuantity = (EditText) row.findViewById(R.id.edt_qty);
            mViewHolder.btnExpiry = row.findViewById(R.id.btn_expiry);
            mViewHolder.btnExpiry.setTag(position);
            mViewHolder.imgCalendar = row.findViewById(R.id.img_calendar);
            mViewHolder.imgCalendar.setTag(position);
            mViewHolder.imgCalendar.setOnClickListener(myClickListener);
            mViewHolder.imgAdd = row.findViewById(R.id.img_add);
            mViewHolder.imgAdd.setTag(position);
            mViewHolder.imgAdd.setOnClickListener(myClickListener);

            mViewHolder.edtQuantity.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    //注意，此处必须使用getTag的方式，不能将position定义为final，写成mTouchItemPosition = position
                    mTouchItemPosition = (Integer) view.getTag();

                    //触摸的是EditText并且当前EditText可以滚动则将事件交给EditText处理；否则将事件交由其父类处理
                    if ((view.getId() == R.id.edt_qty && canVerticalScroll((EditText)view))) {
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }

                    return false;
                }
            });

            // 让ViewHolder持有一个TextWathcer，动态更新position来防治数据错乱；不能将position定义成final直接使用，必须动态更新
            mViewHolder.mTextWatcher = new MyTextWatcher();
            mViewHolder.edtQuantity.addTextChangedListener(mViewHolder.mTextWatcher);
            mViewHolder.updatePosition(position);

            row.setTag(mViewHolder);
        }
        
        ItemView resultItem = _mainCustomerInfoView.get(position);

        mViewHolder.txtItemName.setText(resultItem.itemName);
        mViewHolder.txtItemUnit.setText(resultItem.itemUnit);
        mViewHolder.edtQuantity.setText(resultItem.itemQty);
        if (resultItem.expiredDate != null && !resultItem.expiredDate.equals("000")){
            mViewHolder.btnExpiry.setText(resultItem.expiredDate);
        }
        mViewHolder.edtQuantity.setTag(position);

        if (mTouchItemPosition == position) {
            mViewHolder.edtQuantity.requestFocus();
            mViewHolder.edtQuantity.setSelection(mViewHolder.edtQuantity.getText().length());
        } else {
            mViewHolder.edtQuantity.clearFocus();
        }

        return row;
    }

    /**
     * EditText竖直方向是否可以滚动
     * @param editText 需要判断的EditText
     * @return true：可以滚动 false：不可以滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if(scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    public static abstract class MyClickListener extends OnMultiClickListener {
        @Override
        public void onMultiClick(View v) {
            myBtnOnClick((Integer) v.getTag(), v);
        }
        public abstract void myBtnOnClick(int position, View v);
    }

    class MyTextWatcher implements TextWatcher {
        //由于TextWatcher的afterTextChanged中拿不到对应的position值，所以自己创建一个子类
        private int mPosition;

        public void updatePosition(int position) {
            mPosition = position;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            itemViewCategoryArrayList.get(mPosition).itemQty = s.toString();
        }
    };
}
