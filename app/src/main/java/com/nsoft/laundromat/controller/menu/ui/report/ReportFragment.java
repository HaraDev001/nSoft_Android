package com.nsoft.laundromat.controller.menu.ui.report;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.menu.ui.offer.OfferViewModel;
import com.nsoft.laundromat.controller.report.ReportActivity;

public class ReportFragment extends Fragment {

    private OfferViewModel offerViewModel;
    private LinearLayout layItemReplenish;
    private ImageView imgHome;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_report, container, false);
        final TextView textView = root.findViewById(R.id.text_share);

        imgHome = root.findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                Intent i = new Intent(getActivity(), MenuActivity.class);
//                startActivity(i);
//                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        gotoReportActivity();

        return root;
    }


    private void gotoReportActivity(){
        Intent intent = new Intent(getContext(), ReportActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
//        getActivity().finish();
    }


}