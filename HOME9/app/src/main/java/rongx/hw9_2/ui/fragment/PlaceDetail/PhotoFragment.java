package rongx.hw9_2.ui.fragment.PlaceDetail;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

import rongx.hw9_2.R;
import rongx.hw9_2.ui.Place;
import rongx.hw9_2.ui.PlaceDetail;
import rongx.hw9_2.ui.adapter.DetailPhotoAdapter;
import rongx.hw9_2.ui.adapter.SearchListAdapter;
import rongx.hw9_2.ui.fragment.BaseFragment;

public class PhotoFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    DetailPhotoAdapter adapter;

    private String place_id;
    public PlaceDetail placeDetail;
    GoogleApiClient mGoogleApiClient;
    private ArrayList<Bitmap> List;
    private TextView center_nofound;
    private RecyclerView recycler_photo;
    private boolean nofound = false;
    private boolean running = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo, container, false);
        place_id = (String) getActivity().getIntent().getExtras().getSerializable("place_id");
        ArrayList<Bitmap> List = new ArrayList<>();
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        photo_to_adapter(v);
        mGoogleApiClient.connect();
        center_nofound = v.findViewById(R.id.center_nofound);
        recycler_photo = v.findViewById(R.id.recycler_photo);
        return v;
    }

    private void photo_to_adapter(final View v){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List = new ArrayList<>();
                PlacePhotoMetadataResult result = Places.GeoDataApi
                        .getPlacePhotos(mGoogleApiClient, place_id).await();
                // Get a PhotoMetadataBuffer instance containing a list of photos (PhotoMetadata).
                if (result != null && result.getStatus().isSuccess()) {
                    PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                    for (int i = 0; i < photoMetadataBuffer.getCount(); i++) {
                        List.add(photoMetadataBuffer.get(i).getPhoto(mGoogleApiClient).await().getBitmap());
                    }
                    if(photoMetadataBuffer.getCount()==0){
                        nofound = true;
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        // set up the RecyclerView
                        RecyclerView recyclerView = v.findViewById(R.id.recycler_photo);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        adapter = new DetailPhotoAdapter(getActivity(), List);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }
    @Override
    protected void loadData() {
        Toast.makeText(mContent, "Fragment要闻加载数据", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected View initView() {
        TextView mView = new TextView(mContent);
        mView.setText("Fragment要闻");
        mView.setGravity(Gravity.CENTER);
        mView.setTextSize(18);
        mView.setTextColor(Color.BLACK);
        return mView;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }
}