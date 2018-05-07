package rongx.hw9_2.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Menu;

import rongx.hw9_2.R;
import rongx.hw9_2.Utils.CommentUtils;
import rongx.hw9_2.ui.Place;
import rongx.hw9_2.ui.PlaceDetail;
import rongx.hw9_2.ui.fragment.BaseFragment;
import rongx.hw9_2.ui.fragment.Main.FragmentFactory;
import rongx.hw9_2.ui.fragment.PlaceDetail.FragmentFactory_PD;

public class PlaceDetailActivity extends AppCompatActivity  {
    private TabLayout mTab;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail_text);

        //back navigation button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        initData();
    }
    private void initData() {
        ImagePagerAdapter adapter = new ImagePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTab.setupWithViewPager(mViewPager);
    }
    private void initView() {
        mTab = (TabLayout) findViewById(R.id.tab_placedetail);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_placedetail);
        Place place =  (Place) this.getIntent().getExtras().getSerializable("place");
        setTitle(place.getName());
    }

    private class ImagePagerAdapter extends FragmentPagerAdapter {
        public int[] mTitleImage;
        public  String[] mTitle;

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
            mTitleImage = new int[]{R.drawable.info_outline, R.drawable.photos,
                    R.drawable.maps, R.drawable.review};
            mTitle= getResources().getStringArray(R.array.tab_long_Title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable = ContextCompat.getDrawable(PlaceDetailActivity.this, mTitleImage[position]);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            SpannableString spannableString = new SpannableString(" "+mTitle[position]);
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

        @Override
        public BaseFragment getItem(int position) {
            BaseFragment fragment = FragmentFactory_PD.createFragment(position);
            return fragment;
        }
        @Override
        public int getCount() {
            return CommentUtils.TAB_LONG_COUNT;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        MenuItem item = menu.findItem(R.id.detail_favourite);
        String place_id = (String)this.getIntent().getExtras().getSerializable("place_id");
        if(SearchListActivity.fa_map.containsKey(place_id)){
            item.setIcon(R.drawable.heart_fill_white);
        }
        else{
            item.setIcon(R.drawable.heart_outline_white);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PlaceDetail placeDetail = (PlaceDetail) this.getIntent().getExtras().getSerializable("placeDetail");
        String place_id = (String)this.getIntent().getExtras().getSerializable("place_id");
        Place place = (Place)this.getIntent().getExtras().getSerializable("place");
        switch (item.getItemId()) {
            case R.id.detail_share:

                String url= "https://twitter.com/intent/tweet?text=Check out ";
                url+=placeDetail.getName()+" locate at "+placeDetail.getFormatted_address()+". Website:"+placeDetail.getWebsite()+" #TravelAndEntertainmentSearch";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            case R.id.detail_favourite:
                if(SearchListActivity.fa_map.containsKey(place_id)){
                    item.setIcon(R.drawable.heart_outline_white);
                    SearchListActivity.fa_map.remove(place_id);
                }
                else{
                    item.setIcon(R.drawable.heart_fill_white);
                    SearchListActivity.fa_map.put(place_id,place);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
