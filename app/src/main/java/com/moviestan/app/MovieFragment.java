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
import com.moviestan.app.util.Cloud;
import com.moviestan.app.util.LiteDatabase;
import com.nostra13.universalimageloader.core.ImageLoader;


public class MovieFragment extends Fragment {
    // parameter string
    private static final String MOVIE_DATA = "movie_data";

    // movie data
    private MovieSerializer mData;


    // general
    private Handler mHandler = new Handler();
    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;
    private LiteDatabase mLiteDatabase;


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
        LinearLayout commentsArea  = (LinearLayout) view.findViewById(R.id.comment_area);


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
                startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }



}
