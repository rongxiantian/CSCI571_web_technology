package rongx.hw9_2.ui.fragment.Main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rongx.hw9_2.R;
import rongx.hw9_2.ui.Place;
import rongx.hw9_2.ui.PlaceDetail;
import rongx.hw9_2.ui.PlaceDetailReview;
import rongx.hw9_2.ui.activity.MainActivity;
import rongx.hw9_2.ui.activity.PlaceDetailActivity;
import rongx.hw9_2.ui.activity.SearchListActivity;
import rongx.hw9_2.ui.adapter.DetailReviewAdapter;
import rongx.hw9_2.ui.adapter.FavouriteListAdapter;
import rongx.hw9_2.ui.adapter.SearchListAdapter;
import rongx.hw9_2.ui.fragment.BaseFragment;

import static android.support.v4.view.PagerAdapter.POSITION_NONE;

/**
 * Created by LW on 2017/4/21.
 */
public class FavouriteFragment extends BaseFragment implements FavouriteListAdapter.ItemClickListener{
    FavouriteListAdapter adapter;
    private ArrayList<Place> List;
    public PlaceDetail placeDetail;
    public ArrayList<PlaceDetailReview> placeDetailReview;
    private ProgressDialog pd;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favourite, container, false);

        List = new ArrayList<Place>(SearchListActivity.fa_map.values());
        // set up the RecyclerView
        recyclerView = v.findViewById(R.id.recycler_favourite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FavouriteListAdapter(getActivity(), List);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        pd = new ProgressDialog(getActivity());
        return v;
    }
    @Override
    public void onItemClick(View view, int position, String place_id) {
        pd.setMessage("Fetching results");
        pd.show();
        httpRequest(place_id);
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onPause() {
        super.onPause();
        pd.dismiss();
    }

    @Override
    public void onResume(){
        super.onResume();
        adapter = new FavouriteListAdapter(getActivity(), List);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }



    private void httpRequest(final String place_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                //RequestBody requestBody = new FormBody.Builder().add("latitude","34.0223519").add("longitude","-118.285117").add("distance","16090").add("category","cafe").add("keyword","usc").build();
                String params = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
                params+=place_id+"&key=AIzaSyCaVHSWA2FTNKYLOecEY05UcAz9yo1krzA";

                //Request request = new Request.Builder().get().url("https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJOaegwbTHwoARg7zN_9nq5Uc&key=AIzaSyCaVHSWA2FTNKYLOecEY05UcAz9yo1krzA").build();
                Request request = new Request.Builder().get().url(params).build();
                Response response = null;
                String responseData;


                try {
                    response = client.newCall(request).execute();
                    responseData = response.body().string();
                    placeDetail = JSONparse(responseData);
                    placeDetailReview = JSONparse2(responseData);
                    Intent myIntent = new Intent( getActivity(), PlaceDetailActivity.class);
                    myIntent.putExtra("placeDetail",placeDetail);
                    myIntent.putExtra("place_id",place_id);
                    myIntent.putExtra("reviews",placeDetailReview);
                    startActivity(myIntent);


                }catch (IOException e){
                    System.out.println(e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }).start();

    }

    private PlaceDetail JSONparse(String JSONstring) throws JSONException {
        JSONObject reader = new JSONObject(JSONstring);
        JSONObject result = reader.getJSONObject("result");
        String format_add = "";
        String format_pho = "";
        int price_level = -1;
        double rating = -1;
        String url = "";
        String website="";
        String name="";
        double[] location=new double[2];
        if(result.has("formatted_address")){
            format_add=result.getString("formatted_address");
        }
        if(result.has("formatted_phone_number")){
            format_pho=result.getString("formatted_phone_number");
        }
        if(result.has("price_level")){
            price_level=result.getInt("price_level");
        }
        if(result.has("rating")){
            rating=result.getInt("rating");
        }
        if(result.has("url")){
            url=result.getString("url");
        }
        if(result.has("website")){
            website=result.getString("website");
        }
        if(result.has("name")){
            name=result.getString("name");
        }
        if(result.has("geometry")){
            if(result.getJSONObject("geometry").has("location")){
                location[0]=result.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                location[1]=result.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            }
        }
//        ArrayList<String> photoarr = new ArrayList<>();
//        if(result.has("photos")){
//            JSONArray Jarr = result.getJSONArray("photos");
//            for(int j = 0;j<Jarr.length();j++){
//                photoarr.add(Jarr.getJSONObject(j).getString("photo_reference"));
//            }
//        }
        PlaceDetail placeDetail = new PlaceDetail(format_add,
                format_pho,
                price_level,
                rating,
                url,
                website,
                name,
                location/*,
                photoarr*/);

        return placeDetail;
    }
    private ArrayList<PlaceDetailReview> JSONparse2(String JSONstring) throws JSONException {
        JSONObject reader = new JSONObject(JSONstring);
        JSONObject result = reader.getJSONObject("result");
        JSONArray reviews = result.getJSONArray("reviews");
        ArrayList<PlaceDetailReview> res =  new ArrayList<PlaceDetailReview>();
        for(int i = 0; i< reviews.length(); i++){
            JSONObject review = reviews.getJSONObject(i);
            PlaceDetailReview placeDetailReview = new PlaceDetailReview(review.getString("profile_photo_url"),
                    review.getString("author_name"),
                    review.getInt("rating"),
                    review.getLong("time"),
                    review.getString("text"),
                    review.getString("author_url"));
            res.add(placeDetailReview);
        }
        return(res);
    }
        @Override
    protected void loadData() {
        Toast.makeText(mContent,"Fragment要闻加载数据", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected View initView() {
        TextView mView=new TextView(mContent);
        mView.setText("Fragment要闻");
        mView.setGravity(Gravity.CENTER);
        mView.setTextSize(18);
        mView.setTextColor(Color.BLACK);
        return mView;
    }
}
