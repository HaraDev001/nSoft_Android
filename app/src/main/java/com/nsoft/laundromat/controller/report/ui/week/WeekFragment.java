package com.nsoft.laundromat.controller.report.ui.week;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.menu.MenuActivity;

public class WeekFragment extends Fragment {

    private ImageView imgHome;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_week, container, false);
        imgHome = root.findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                Intent i = new Intent(getActivity(), MenuActivity.class);
//                startActivity(i);
//                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });
        gotoMenuActivity();
        return root;
    }

    private void gotoMenuActivity(){
        Intent intent = new Intent(getContext(), MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

}