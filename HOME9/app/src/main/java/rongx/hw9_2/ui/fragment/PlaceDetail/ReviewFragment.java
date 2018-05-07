package rongx.hw9_2.ui.fragment.PlaceDetail;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import rongx.hw9_2.R;
import rongx.hw9_2.ui.Place;
import rongx.hw9_2.ui.PlaceDetailReview;
import rongx.hw9_2.ui.activity.PlaceDetailActivity;
import rongx.hw9_2.ui.adapter.DetailReviewAdapter;
import rongx.hw9_2.ui.adapter.SearchListAdapter;
import rongx.hw9_2.ui.fragment.BaseFragment;

public class ReviewFragment extends BaseFragment  implements DetailReviewAdapter.ItemClickListener{
    private Spinner spinner1;
    private Spinner spinner2;
    DetailReviewAdapter detailReviewAdapter;
    private ArrayList<PlaceDetailReview> List;
    private ArrayList<PlaceDetailReview> defaultList;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_review, container, false);
        String [] SpinnerValues1 = {"Google reviews", "Yelp reviews"};
        String [] SpinnerValues2 = {"Default order","Highest rating","Lowest rating","Most recent", "Least recent"};
        spinner1 = (Spinner) v.findViewById(R.id.review_spinner1);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, SpinnerValues1);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        getActivity().findViewById(R.id.recycler_review).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.center_nofound).setVisibility(View.GONE);
                        break;
                    case 1:
                        getActivity().findViewById(R.id.recycler_review).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.center_nofound).setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2 = (Spinner) v.findViewById(R.id.review_spinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, SpinnerValues2);
        adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        detailReviewAdapter = new DetailReviewAdapter(getActivity(), defaultList);
                        recyclerView.setAdapter(detailReviewAdapter);
                        break;
                    case 1:
                        Collections.sort(List, new asendrate());
                        detailReviewAdapter = new DetailReviewAdapter(getActivity(), List);
                        recyclerView.setAdapter(detailReviewAdapter);
                        break;
                    case 2:
                        Collections.sort(List, new dsendrate());
                        detailReviewAdapter = new DetailReviewAdapter(getActivity(), List);
                        recyclerView.setAdapter(detailReviewAdapter);
                        break;
                    case 3:
                        Collections.sort(List, new asendtime());
                        detailReviewAdapter = new DetailReviewAdapter(getActivity(), List);
                        recyclerView.setAdapter(detailReviewAdapter);
                        break;
                    case 4:
                        Collections.sort(List, new dsendtime());
                        detailReviewAdapter = new DetailReviewAdapter(getActivity(), List);
                        recyclerView.setAdapter(detailReviewAdapter);
                        break;
                }
            }
            class asendrate implements Comparator<PlaceDetailReview>
            {
                // Used for sorting in ascending order of
                // roll number
                public int compare(PlaceDetailReview a, PlaceDetailReview b)
                {
                    return b.getRating() - a.getRating();
                }
            }
            class dsendrate implements Comparator<PlaceDetailReview>
            {
                // Used for sorting in ascending order of
                // roll number
                public int compare(PlaceDetailReview a, PlaceDetailReview b)
                {
                    return a.getRating() - b.getRating();
                }
            }
            class asendtime implements Comparator<PlaceDetailReview>
            {
                // Used for sorting in ascending order of
                // roll number
                public int compare(PlaceDetailReview a, PlaceDetailReview b)
                {
                    return (int)(b.getTime() - a.getTime());
                }
            }

            class dsendtime implements Comparator<PlaceDetailReview>
            {
                // Used for sorting in ascending order of
                // roll number
                public int compare(PlaceDetailReview a, PlaceDetailReview b)
                {
                    return (int)(a.getTime() - b.getTime());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        List = (ArrayList<PlaceDetailReview>) getActivity().getIntent().getExtras().getSerializable("reviews");
        defaultList = List;
        // set up the RecyclerView
        recyclerView = v.findViewById(R.id.recycler_review);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        detailReviewAdapter = new DetailReviewAdapter(getActivity(), List);
        detailReviewAdapter.setClickListener(this);
        recyclerView.setAdapter(detailReviewAdapter);


        return v;
    }
    @Override
    public void onItemClick(View view, int position, String author_url) {
        Uri uri = Uri.parse(List.get(position).getAuthor_url()); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void loadData() {
        //Toast.makeText(mContent,"Fragment要闻加载数据", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected View initView() {
        TextView mView=new TextView(mContent);
        return mView;
    }
}
