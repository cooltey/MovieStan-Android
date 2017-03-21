package com.moviestan.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moviestan.app.data.MovieSerializer;
import com.moviestan.app.util.Cloud;
import com.moviestan.app.util.LogFactory;
import com.moviestan.app.util.MyProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


public class MainFragment extends Fragment {

    // general
    private Handler mHandler = new Handler();
    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;

    // list
    private ArrayList<MovieSerializer> mData = new ArrayList<>();
    private ListAdapter mListAdapter;
    private boolean mHasNext;
    private final static int PRELOAD_DISTANCE = 3;
    private String mCurrentPage = "1";
    private ListView mListView;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // initial
        mLayoutInflater = getActivity().getLayoutInflater();
        mImageLoader  = Cloud.initImageLoader(getActivity());

        // setup view
        mListView = (ListView) view.findViewById(R.id.list_layout);

        // download movie content
        if(mData.size() <= 0) {
            downloadMovieContent(mCurrentPage);
        }

        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        // set on click listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity) getActivity()).displayMovieView(mData.get(i));
            }
        });


        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    // download content
    private void downloadMovieContent(String page){

        MyProgressDialog.procsessing(getActivity());

        Cloud.getMovieList(getActivity(), page, new Cloud.MovieListener() {
            @Override
            public Handler getHandler() {
                MyProgressDialog.cancel();
                return mHandler;
            }

            @Override
            public void onSuccess(boolean has_next, String page, ArrayList<MovieSerializer> data) {
                mData.addAll(data);
                mHasNext = has_next;
                mListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFail(String msg) {
                LogFactory.set("downloadMovieContent onFail", msg);
            }
        });
    }

    // for movie list
    private class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mData.size();
        }
        @Override
        public Object getItem(int position) {
            if (null == mData || position < 0 || position >= mData.size()) {
                return null;
            }
            return mData.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = mLayoutInflater.inflate(R.layout.item_list_movie, null);

            RelativeLayout itemLayout = (RelativeLayout) convertView;

            // download data
            if (position > (getCount() - PRELOAD_DISTANCE)) {
                if(mHasNext) {
                    mCurrentPage = Integer.parseInt(mCurrentPage) + 1 + "";
                    downloadMovieContent(mCurrentPage);
                }
            }

            // get data
            if(mData.size() <= position){
                return itemLayout;
            }

            MovieSerializer getData = mData.get(position);

            if(getData == null){
                return itemLayout;
            }

            ImageView moviePoster       = (ImageView) itemLayout.findViewById(R.id.list_img);
            TextView movieTitle         = (TextView) itemLayout.findViewById(R.id.list_title);
            TextView movieReleaseDate   = (TextView) itemLayout.findViewById(R.id.list_release);
            RatingBar movieRating       = (RatingBar) itemLayout.findViewById(R.id.rating);

            // set data
            mImageLoader.displayImage(getData.poster_path, moviePoster);
            movieTitle.setText(getData.title);
            movieReleaseDate.setText(getString(R.string.movie_release_date_prefix) + getData.release_date);
            movieRating.setNumStars(10);
            movieRating.setRating(Float.parseFloat(getData.vote_average));


            return itemLayout;
        }
    }

}
