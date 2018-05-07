package rongx.hw9_2.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import rongx.hw9_2.R;

public class DetailPhotoAdapter extends RecyclerView.Adapter<DetailPhotoAdapter.ViewHolder>{
    private List<Bitmap> mData;
    private LayoutInflater mInflater;
    GoogleApiClient mGoogleApiClient;
    // data is passed into the constructor
    public DetailPhotoAdapter(Context context, ArrayList<Bitmap> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_photo_row, parent, false);
        return new DetailPhotoAdapter.ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(DetailPhotoAdapter.ViewHolder holder, int position) {
        Bitmap bitImg = mData.get(position);
        holder.detail_photo.setImageBitmap(bitImg);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView detail_photo;

        ViewHolder(View itemView) {
            super(itemView);
            detail_photo = itemView.findViewById(R.id.detail_photo);
            //itemView.setOnClickListener(this);
        }

       /* @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(),place_id);
        }*/
    }

    // convenience method for getting data at click position
    public Bitmap getItem(int id) {
        return mData.get(id);
    }

  /*  // allows clicks events to be caught
    public void setClickListener(SearchListAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
*/
  /*  // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, String place_id);
    }*/
}
