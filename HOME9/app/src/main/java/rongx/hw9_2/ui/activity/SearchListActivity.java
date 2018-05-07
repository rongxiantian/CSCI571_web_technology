package rongx.hw9_2.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rongx.hw9_2.R;
import rongx.hw9_2.ui.Place;
import rongx.hw9_2.ui.PlaceDetail;
import rongx.hw9_2.ui.PlaceDetailReview;
import rongx.hw9_2.ui.adapter.SearchListAdapter;

public class SearchListActivity extends AppCompatActivity implements SearchListAdapter.ItemClickListener {

    SearchListAdapter adapter;

    private String place_id;
    public PlaceDetail placeDetail;
    public ArrayList<PlaceDetailReview> placeDetailReview;
    public static final HashMap<String, Place> fa_map = new HashMap<>();//store favourite places
    public  ArrayList<ArrayList<Place>> page = new ArrayList<ArrayList<Place>>();
    public int curpage = 0;
    public String page_token="";
    private RecyclerView recyclerView;
    private ProgressDialog pd;
    private ProgressDialog pdnext;
    private Button previous;
    private Button next;
    private static boolean threadorder = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        //back navigation button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //get data from previous activity
        ArrayList<Place> List = (ArrayList<Place>) getIntent().getExtras().getSerializable("List");
        page_token = (String ) getIntent().getExtras().getSerializable("next_page_token");
        for(int i=0;i<3;i++){
            page.add(new ArrayList<Place>());
        }
        page.set(0,List);//for page 1;
        curpage = 0;
        // set up the RecyclerView
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchListAdapter(this, List);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        previous = findViewById(R.id.prepage);
        next = findViewById(R.id.nextpage);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new SearchListAdapter(SearchListActivity.this, page.get(--curpage));
                adapter.setClickListener(SearchListActivity.this);
                recyclerView.setAdapter(adapter);
                if(curpage==0){
                    previous.setEnabled(false);
                }
                next.setEnabled(true);
            }
        });
        if(page_token=="" || page.get(0).size()==0){
            next.setEnabled(false);
            if(page.get(0).size()==0){
                TextView tmp = findViewById(R.id.center_nofound);
                LinearLayout Search_Linear = findViewById(R.id.Search_Linear);
                tmp.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                Search_Linear.setVisibility(View.GONE);
            }
        }
        else{
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(page.get(curpage+1).size()==0 && page_token!=""){
                        pdnext.setMessage("Fetching results");
                        pdnext.show();
                        httpRequest_next(page_token);
                    }
                    else{
                        threadorder = false;
                    }
                    while(threadorder);

                    threadorder = true;
                    adapter = new SearchListAdapter(SearchListActivity.this, page.get(++curpage));
                    adapter.setClickListener(SearchListActivity.this);
                    recyclerView.setAdapter(adapter);
                    if(curpage==(page.size()-1)){//curpage==2
                        next.setEnabled(false);
                    }
                    else if (page.get(curpage+1).size()==0 && page_token==""){//
                        next.setEnabled(false);
                    }
                    else{
                        next.setEnabled(true);
                    }
                    previous.setEnabled(true);
                }
            });

        }


        pd = new ProgressDialog(this);
        pdnext = new ProgressDialog(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onItemClick(View view, int position, String place_id, Place place) {
        pd.setMessage("Fetching results");
        pd.show();
        httpRequest(place_id, place);
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onPause() {
        super.onPause();
        pd.dismiss();
    }

/*    @Override
    public void onResume(){
        super.onResume();

    }*/

    private void httpRequest(final String place_id, final Place place) {
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
                    placeDetail = JSONparsePlaceDetail(responseData);
                    placeDetailReview = JSONparse2(responseData);
                    Intent myIntent = new Intent( SearchListActivity.this, PlaceDetailActivity.class);
                    myIntent.putExtra("placeDetail",placeDetail);
                    myIntent.putExtra("place_id",place_id);
                    myIntent.putExtra("reviews",placeDetailReview);
                    myIntent.putExtra("place",place);
                    startActivity(myIntent);


                }catch (IOException e){
                    System.out.println(e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }).start();

    }

    private void httpRequest_next(final String next_page_token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String params = "http://rongxiat-hw7.us-west-1.elasticbeanstalk.com/next?"+"pagetoken="+next_page_token;
                //Request request = new Request.Builder().get().url("http://rongxiat-hw7.us-west-1.elasticbeanstalk.com/location_cur?Keyword=pizza&Category=Default&Distance=10&location_lat=34.0266&location_lng=-118.2831").build();
                Request request = new Request.Builder().get().url(params).build();
                Response response = null;
                String responseData;
                ArrayList<Place> List = new ArrayList<>();

                try {
                    response = client.newCall(request).execute();
                    responseData = response.body().string();
                    List = JSONparseplace(responseData);
                    page_token = JSONparseNextpage(responseData);
                    page.set(curpage+1,List);
                }catch (IOException e){
                    System.out.println(e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                threadorder = false;
                pdnext.dismiss();
            }
        }).start();

    }
    private ArrayList JSONparseplace(String JSONstring) throws JSONException {
        JSONObject reader = new JSONObject(JSONstring);
        ArrayList<Place> res = new ArrayList<>();
        JSONArray results = reader.getJSONArray("results");
        for(int i = 0;i<results.length();i++){
            JSONObject c = results.getJSONObject(i);
            double[] temp = new double[]{c.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),c.getJSONObject("geometry").getJSONObject("location").getDouble("lng")};
            int price_level = -1;
            double rating = -1;
            if(c.has("price_level")){
                price_level=c.getInt("price_level");
            }
            if(c.has("rating")){
                rating = c.getDouble("rating");
            }
            Place place = new Place(temp,
                    c.getString("icon"),
                    c.getString("name"),
                    c.getString("place_id"),
                    price_level,
                    rating,
                    c.getString("vicinity"));
            res.add(place);
        }
        return res;
    }
    private String JSONparseNextpage(String JSONstring) throws JSONException {
        JSONObject reader = new JSONObject(JSONstring);
        if(reader.has("next_page_token")){
            return reader.getString("next_page_token");
        }
        else{
            return "";
        }
    }


    private PlaceDetail JSONparsePlaceDetail(String JSONstring) throws JSONException {
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
        if(result.has("reviews")){
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
        else{
            return new ArrayList<PlaceDetailReview>();
        }

    }

}