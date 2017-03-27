package com.moviestan.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.moviestan.app.data.General;
import com.moviestan.app.data.MovieSerializer;
import com.moviestan.app.data.RatingSerializer;
import com.moviestan.app.util.Cloud;
import com.moviestan.app.util.LiteDatabase;
import com.moviestan.app.util.MyProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


public class MovieFragment extends Fragment {
    // parameter string
    public static final String MOVIE_DATA = "movie_data";

    // movie data
    private MovieSerializer mData;
    // rating data
    private ArrayList<RatingSerializer> mRatingData = new ArrayList<>();


    // general
    private Handler mHandler = new Handler();
    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;
    private LiteDatabase mLiteDatabase;

    // view
    private LinearLayout mCommentsArea;


    public MovieFragment() {
        // Required empty public constructor
    }

    public static MovieFragment newInstance(MovieSerializer data) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putSerializable(MOVIE_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = (MovieSerializer) getArguments().getSerializable(MOVIE_DATA);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // init
        mLayoutInflater = getActivity().getLayoutInflater();
        mImageLoader  = Cloud.initImageLoader(getActivity());
        mLiteDatabase = new LiteDatabase(getActivity());

        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        // set view
        ImageView moviePoster   = (ImageView) view.findViewById(R.id.movie_poster);
        TextView movieTitle     = (TextView) view.findViewById(R.id.movie_title);
        TextView movieId        = (TextView) view.findViewById(R.id.movie_id);
        TextView movieRelease   = (TextView) view.findViewById(R.id.movie_release);
        TextView movieDesc      = (TextView) view.findViewById(R.id.movie_description);
        RatingBar movieRating   = (RatingBar) view.findViewById(R.id.movie_rating);
        LinearLayout movieTags  = (LinearLayout) view.findViewById(R.id.movie_tags);
        mCommentsArea  = (LinearLayout) view.findViewById(R.id.comment_area);


        // setup content
        mImageLoader.displayImage(mData.poster_path, moviePoster);
        movieTitle.setText(mData.title);
        movieId.setText(getString(R.string.movie_id_prefix) + mData.id);
        movieRelease.setText(getString(R.string.movie_release_date_prefix) + mData.release_date);
        movieDesc.setText(mData.overview);
        movieRating.setNumStars(10);
        movieRating.setRating(Float.parseFloat(mData.vote_average));

        // add tags
        for(int i = 0; i < mData.genre_ids.size(); i++){
            View rootView = mLayoutInflater.inflate(R.layout.item_movie_tags, null);

            TextView tagsView = (TextView) rootView.findViewById(R.id.text);

            String savedName = General.MOVIE_GENRE + "_" + mData.genre_ids.get(i);
            String getGenreId = mLiteDatabase.get(savedName);
            tagsView.setText(getGenreId);

            movieTags.addView(rootView);
        }


        // go to comment activity
        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.write_comment);

        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), CommentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(MOVIE_DATA, mData);
                startActivity(intent);
            }
        });


        return view;
    }



    // download rating
    private void downloadRating(String movie_id){

        MyProgressDialog.procsessing(getActivity());

        Cloud.getRatingList(getActivity(), movie_id, new Cloud.RatingListener() {
            @Override
            public Handler getHandler() {
                MyProgressDialog.cancel();
                return mHandler;
            }

            @Override
            public void onSuccess(ArrayList<RatingSerializer> data) {

                // update list
                mRatingData.clear();
                mRatingData.addAll(data);

                // update view
                mCommentsArea.removeAllViewsInLayout();

                // add views
                for(RatingSerializer ratingData : mRatingData){
                    // get view
                    View rootView = getActivity().getLayoutInflater().inflate(R.layout.item_list_comment, null);

                    ImageView userImage = (ImageView) rootView.findViewById(R.id.list_img);
                    TextView userComment = (TextView) rootView.findViewById(R.id.list_comment);
                    TextView userInfo = (TextView) rootView.findViewById(R.id.list_info);
                    RatingBar userRating = (RatingBar) rootView.findViewById(R.id.rating);


                    mImageLoader.displayImage("https://graph.facebook.com/" + ratingData.fb_id + "/picture?type=large", userImage);
                    userComment.setText(ratingData.comments);
                    userInfo.setText(ratingData.name + " @ " + ratingData.date);
                    userRating.setNumStars(10);
                    userRating.setRating(Float.parseFloat(ratingData.score));

                    mCommentsArea.addView(rootView);

                }

            }

            @Override
            public void onFail(String msg) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        // load view
        downloadRating(mData.id);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}
