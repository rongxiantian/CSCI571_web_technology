package rongx.hw9_2.ui.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rongx.hw9_2.R;
import rongx.hw9_2.ui.Place;
import rongx.hw9_2.ui.activity.SearchListActivity;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder>{
    private List<Place> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    // data is passed into the constructor
    public SearchListAdapter(Context context, ArrayList<Place> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_search_list_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Place curP = mData.get(position);
        Picasso.get().load(curP.getIcon()).into(holder.place_image);//holder.place_image.
        holder.place_name.setText(curP.getName());
        holder.place_address.setText(curP.getVicinity());
        if(SearchListActivity.fa_map.containsKey(curP.getPlace_id())){
            holder.place_favor.setImageResource(R.drawable.heart_fill_red);
        }
        else{
            holder.place_favor.setImageResource(R.drawable.heart_outline_black);
        }
        holder.place_favor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(SearchListActivity.fa_map.containsKey(curP.getPlace_id())){
                    holder.place_favor.setImageResource(R.drawable.heart_outline_black);
                    SearchListActivity.fa_map.remove(curP.getPlace_id());
                    Toast.makeText(context,curP.getName()+" was added to favorites",Toast.LENGTH_SHORT).show();
                }
                else{
                    holder.place_favor.setImageResource(R.drawable.heart_fill_red);
                    SearchListActivity.fa_map.put(curP.getPlace_id(),curP);
                    Toast.makeText(context,curP.getName()+" was removed from favorites",Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.place_id = curP.getPlace_id();//use by detail search
        holder.place = curP;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }




    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView place_image;
        TextView place_name;
        TextView place_address;
        ImageView place_favor;
        String place_id;
        Place place;
        ViewHolder(View itemView) {
            super(itemView);
            place_image = itemView.findViewById(R.id.place_image);
            place_name = itemView.findViewById(R.id.place_name);
            place_address = itemView.findViewById(R.id.place_address);
            place_favor = itemView.findViewById(R.id.place_favor);
            place_id = "";
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(),place_id,place);
        }
    }

    // convenience method for getting data at click position
    public Place getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, String place_id, Place place);
    }
}
