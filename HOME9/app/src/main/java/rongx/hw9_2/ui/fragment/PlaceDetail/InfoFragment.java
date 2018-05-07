package rongx.hw9_2.ui.fragment.PlaceDetail;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import rongx.hw9_2.R;
import rongx.hw9_2.ui.Place;
import rongx.hw9_2.ui.PlaceDetail;
import rongx.hw9_2.ui.fragment.BaseFragment;

public class InfoFragment extends BaseFragment {
    private TextView Address_PDT;
    private TextView Phone_Number_PD;
    private TextView Phone_Number_PDT;
    private TextView Price_Level_PDT;
    private RatingBar Rating_PDT;
    private TextView Google_Page_PD;
    private TextView Google_Page_PDT;
    private TextView Website_PD;
    private TextView Website_PDT;
    private TextView Price_Level_PD;
    private TextView Rating_PD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        Address_PDT = (TextView)v.findViewById(R.id.Address_PDT);
        Phone_Number_PD = (TextView)v.findViewById(R.id.Phone_Number_PD);
        Phone_Number_PDT = (TextView)v.findViewById(R.id.Phone_Number_PDT);
        Price_Level_PDT = (TextView)v.findViewById(R.id.Price_Level_PDT);
        Rating_PDT = (RatingBar)v.findViewById(R.id.Rating_PDT);
        Google_Page_PD = (TextView) v.findViewById(R.id.Rating_PD);
        Google_Page_PDT = (TextView)v.findViewById(R.id.Google_Page_PDT);
        Website_PD = (TextView)v.findViewById(R.id.Website_PD);
        Website_PDT = (TextView)v.findViewById(R.id.Website_PDT);
        Price_Level_PD = (TextView)v.findViewById(R.id.Price_Level_PD);
        Rating_PD = (TextView)v.findViewById(R.id.Rating_PD);

        //get data from previous activity
        PlaceDetail placeDetail = (PlaceDetail) getActivity().getIntent().getExtras().getSerializable("placeDetail");
        Address_PDT.setText(placeDetail.getFormatted_address());
        if(placeDetail.getRating()!=-1){
            Phone_Number_PDT.setText(placeDetail.getGetFormatted_phone_number());
        }
        else{
            Phone_Number_PD.setVisibility(View.GONE);
            Phone_Number_PDT.setVisibility(View.GONE);
        }


        //Phone_Number_PDT.setMovementMethod(LinkMovementMethod.getInstance());
        String price_lel="";
        if(placeDetail.getPrice_level()!=-1){
            for(int i =0;i<placeDetail.getPrice_level();i++){
                price_lel+="$";
            }
            Price_Level_PDT.setText(price_lel);
        }
        else{
            Price_Level_PD.setVisibility(View.GONE);
            Price_Level_PDT.setVisibility(View.GONE);
        }
        if(placeDetail.getRating()!=-1){
            Rating_PDT.setRating((float) placeDetail.getRating());
        }
        else{
            Rating_PD.setVisibility(View.GONE);
            Rating_PDT.setVisibility(View.GONE);
        }
        if(placeDetail.getUrl()!=""){
            Google_Page_PDT.setText(placeDetail.getUrl());
            //Google_Page_PDT.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else{
            Google_Page_PD.setVisibility(View.GONE);
            Google_Page_PDT.setVisibility(View.GONE);
        }
        if(placeDetail.getWebsite()!=""){
            Website_PDT.setText(placeDetail.getWebsite());
        }
        else{
            Website_PD.setVisibility(View.GONE);
            Website_PDT.setVisibility(View.GONE);
        }
        //Website_PDT.setMovementMethod(LinkMovementMethod.getInstance());
        return v;
    }


    @Override
    protected void loadData() {
        Toast.makeText(mContent,"Fragment要闻加载数据", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected View initView() {
        TextView mView=new TextView(mContent);
        return mView;
    }

}
