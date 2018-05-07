package rongx.hw9_2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rongx.hw9_2.R;
import rongx.hw9_2.ui.PlaceDetailReview;

public class DetailReviewAdapter  extends RecyclerView.Adapter<DetailReviewAdapter.ViewHolder>{
    private List<PlaceDetailReview> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    public DetailReviewAdapter(Context context, ArrayList<PlaceDetailReview> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_review_row, parent, false);
        return new DetailReviewAdapter.ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaceDetailReview placeDetailReview = mData.get(position);
        Picasso.get().load(placeDetailReview.getProfile_photo_url()).into(holder.review_profile_photo_url);
        holder.review_name.setText(placeDetailReview.getAuthor_name());
        holder.review_rating_star.setRating(placeDetailReview.getRating());
        holder.review_time.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date(placeDetailReview.getTime()*1000)));
        holder.review_text.setText(placeDetailReview.getText());
        holder.author_url = placeDetailReview.getAuthor_url();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView review_profile_photo_url;
        TextView review_name;
        RatingBar review_rating_star;
        TextView review_time;
        TextView review_text;
        String author_url;

        ViewHolder(View itemView) {
            super(itemView);
            review_profile_photo_url = itemView.findViewById(R.id.review_profile_photo_url);
            review_name = itemView.findViewById(R.id.review_name);
            review_rating_star = itemView.findViewById(R.id.review_rating_star);
            review_time = itemView.findViewById(R.id.review_time);
            review_text = itemView.findViewById(R.id.review_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(author_url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(),author_url);
        }
    }

    // convenience method for getting data at click position
    public PlaceDetailReview getItem(int id) { return mData.get(id); }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, String author_url);
    }
}
