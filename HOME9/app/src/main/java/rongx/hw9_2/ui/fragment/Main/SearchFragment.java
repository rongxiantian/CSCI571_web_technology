package rongx.hw9_2.ui.fragment.Main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.Filter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rongx.hw9_2.R;
import rongx.hw9_2.ui.Place;
import rongx.hw9_2.ui.activity.MainActivity;
import rongx.hw9_2.ui.activity.SearchListActivity;
import rongx.hw9_2.ui.adapter.CustomAutoCompleteAdapter;
import rongx.hw9_2.ui.fragment.BaseFragment;

/**
 * Created by LW on 2017/4/21.
 */
public class SearchFragment extends BaseFragment  {
    private EditText KeywordInput;
    private TextView KeywordVali;
    private Spinner spinner;
    private RadioButton radioButton;
    private EditText DistanceInput;
    private RadioGroup FromradioGroup;
    private AutoCompleteTextView OthLocInput;
    private TextView OthLocVali;

    public ArrayList<String> List;
    public String next_page_token;
    private ProgressDialog pd;

    private static final String API_KEY = "AIzaSyCaVHSWA2FTNKYLOecEY05UcAz9yo1krzA";
    public static final String TAG = "AutoCompleteActivity";
    private static final int AUTO_COMP_REQ_CODE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);
        //Keyword
        KeywordInput = (EditText) v.findViewById(R.id.KeywordInput);
        KeywordVali = (TextView)v.findViewById(R.id.KeywordVali);
        //Category Spinner  getSelectedItem
        String [] SpinnerValues = {"Default","Airport","Amusement Park","Aquarium","Art_Gallery","Bakery","Bar","Beauty_Salon","Bowling_Alley",
                "Bus_Station","Cafe","Campground","Car_Rental","Casino","Lodging","Movie_Theater","Museum","Night_Club","Park","Parking",
                "Restaurant","Shopping_Mall","Stadium","Subway_Station","Taxi_Stand","Train_Station","Transit_Station","Travel_Agency","Zoo"};
        spinner = (Spinner) v.findViewById(R.id.CategorySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, SpinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        //Distance
        DistanceInput = (EditText) v.findViewById(R.id.DistanceInput);
        //From radio getCheckedRadioButtonId
        FromradioGroup = (RadioGroup)v.findViewById(R.id.FromRadioGroup);
        //other location address
        OthLocInput = (AutoCompleteTextView)v.findViewById(R.id.OthLocInput);
        //OthLocInput.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.fragment_search));
        //OthLocInput.setOnItemClickListener(this);
        CustomAutoCompleteAdapter adapterC =  new CustomAutoCompleteAdapter(v.getContext());
        OthLocInput.setAdapter(adapterC);
        OthLocInput.setOnItemClickListener(onItemClickListener);

        OthLocVali  =(TextView)v.findViewById(R.id.OthLocVali);

        pd = new ProgressDialog(getActivity());

        FromradioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = FromradioGroup.findViewById(checkedId);
                int index = FromradioGroup.indexOfChild(radioButton);
                switch (index){
                    case 0:
                        OthLocInput.setEnabled(false);
                        break;
                    case 1:
                        OthLocInput.setEnabled(true);
                        break;
                }
            }
        });

        //Search button
        Button SearchButton = (Button)v.findViewById(R.id.search_button);
        SearchButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int submit = 0;
                        int selectedId = FromradioGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton)getActivity().findViewById(selectedId);

                        //validation
                        final String KwInput = KeywordInput.getText().toString();
                        if(KwInput.matches("")){
                            Toast.makeText(getActivity(),"Please fix all fields with errors",Toast.LENGTH_SHORT).show();
                            KeywordVali.setVisibility(View.VISIBLE);
                        }
                        else{
                            KeywordVali.setVisibility(View.GONE);
                            submit++;
                        }

                        String test = radioButton.getText().toString();
                        if(radioButton.getText().toString().equals("Other. Specify Location")){
                            final String OLInput = OthLocInput.getText().toString();
                            if(OLInput.matches("")){
                                Toast.makeText(getActivity(),"Please fix all fields with errors",Toast.LENGTH_SHORT).show();
                                OthLocVali.setVisibility(View.VISIBLE);
                            }
                            else{
                                OthLocVali.setVisibility(View.GONE);
                                submit++;
                            }
                        }
                        else{
                            submit++;
                        }
                        //httpRequest get google api list and change activity
                        if(submit==2){
                            pd.setMessage("Fetching results");
                            pd.show();

                            httpRequest();
                        }
                    }
                });

        //Clear button
        Button ClearButton = (Button)v.findViewById(R.id.clear_button);
        ClearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                KeywordInput.setText("");
                KeywordVali.setVisibility(View.GONE);
                spinner.setSelection(0);
                DistanceInput.setText("");
                FromradioGroup.check(R.id.FromRadioButton0);
                OthLocInput.setText("");
                OthLocVali.setVisibility(View.GONE);
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        pd.dismiss();
    }

    private void httpRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                String DisInput = DistanceInput.getText().toString();
                if(DisInput.equals("")){DisInput="10";}
                String params = "http://rongxiat-hw7.us-west-1.elasticbeanstalk.com/location_cur?"+"Keyword="+KeywordInput.getText().toString()+"&Category="+spinner.getSelectedItem().toString()
                        +"&Distance="+DisInput+"&location_lat="+ MainActivity.Lat+"&location_lng="+MainActivity.Lng;
                //Request request = new Request.Builder().get().url("http://rongxiat-hw7.us-west-1.elasticbeanstalk.com/location_cur?Keyword=pizza&Category=Default&Distance=10&location_lat=34.0266&location_lng=-118.2831").build();
                Request request = new Request.Builder().get().url(params).build();
                Response response = null;
                String responseData;


                try {
                    response = client.newCall(request).execute();
                    responseData = response.body().string();
                    List = JSONparse(responseData);
                    next_page_token = JSONparseNextpage(responseData);
                    Intent myIntent = new Intent(getActivity(), SearchListActivity.class);
                    myIntent.putExtra("List",List);
                    myIntent.putExtra("next_page_token",next_page_token);
                    startActivity(myIntent);


                }catch (IOException e){
                    System.out.println(e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }).start();

    }

    private ArrayList JSONparse(String JSONstring) throws JSONException {
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
            return null;
        }
    }



    //as I override onCreateView, this initView becomes useless
    @Override
    protected View initView() {
        TextView mView = new TextView(mContent);
        mView.setText("Fragment头条");
        mView.setGravity(Gravity.CENTER);
        mView.setTextSize(18);
        mView.setTextColor(Color.BLACK);
      return mView;
    }

    @Override
    protected void loadData() {
        Toast.makeText(mContent,"Fragment要闻加载数据", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    /*Toast.makeText(view,
                            "selected place "
                                    + ((zoftino.com.places.Place)adapterView.
                                    getItemAtPosition(i)).getPlaceText()
                            , Toast.LENGTH_SHORT).show();*/
                    //do something with the selection
                    //searchScreen();
                }
            };

}
