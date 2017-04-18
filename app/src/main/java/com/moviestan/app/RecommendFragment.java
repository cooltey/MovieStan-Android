package com.moviestan.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.moviestan.app.data.MovieSerializer;
import com.moviestan.app.util.Cloud;
import com.moviestan.app.util.MyProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


public class RecommendFragment extends Fragment {


    // parameter string
    public static final String RECOMMENDATION_TYPE = "recommend_type";

    // general
    private Handler mHandler = new Handler();
    private ImageLoader mImageLoader;
    private String mRecType = "";

    public RecommendFragment() {
        // Required empty public constructor
    }

    public static RecommendFragment newInstance(String type) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putString(RECOMMENDATION_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecType = getArguments().getString(RECOMMENDATION_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        mImageLoader = Cloud.initImageLoader(getActivity());

        // show list
        downloadMyRecommendation(view, mRecType);

        return view;
    }


    // download my recommendation by type
    private void downloadMyRecommendation(final View view, final String type){

        MyProgressDialog.procsessing(getActivity());

        Cloud.getRecommendList(getActivity(), type, new Cloud.RecommendListener() {
            @Override
            public Handler getHandler() {
                MyProgressDialog.cancel();
                return mHandler;
            }

            @Override
            public void onSuccess(ArrayList<MovieSerializer> data) {
                TextView recommendTypeView = (TextView) view.findViewById(R.id.recommend_type);
                LinearLayout recommendListView = (LinearLayout) view.findViewById(R.id.list_zone);


                if(mRecType.equals("1")){
                    recommendTypeView.setText(getString(R.string.app_title_recommend_1));
                }else if(mRecType.equals("2")){
                    recommendTypeView.setText(getString(R.string.app_title_recommend_2));
                }else{
                    recommendTypeView.setText(getString(R.string.app_title_recommend_3));
                }

                // add views
                recommendListView.removeAllViewsInLayout();

                for(final MovieSerializer getData : data){
                    View rootView = getActivity().getLayoutInflater().inflate(R.layout.item_list_movie, null);

                    ImageView moviePoster       = (ImageView) rootView.findViewById(R.id.list_img);
                    TextView movieTitle         = (TextView) rootView.findViewById(R.id.list_title);
                    TextView movieReleaseDate   = (TextView) rootView.findViewById(R.id.list_release);
                    RatingBar movieRating       = (RatingBar) rootView.findViewById(R.id.rating);

                    // set data
                    mImageLoader.displayImage(getData.poster_path, moviePoster);
                    movieTitle.setText(getData.title);
                    movieReleaseDate.setText(getString(R.string.movie_release_date_prefix) + getData.release_date);
                    movieRating.setNumStars(10);
                    movieRating.setRating(Float.parseFloat(getData.vote_average));

                    // setup onclick
                    rootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // go to movie
                            ((MainActivity)getActivity()).displayMovieView(getData);
                        }
                    });

                    // add into view
                    recommendListView.addView(rootView);
                }



            }

            @Override
            public void onFail(String msg) {

            }
        });
    }




    @Override
    public void onDetach() {
        super.onDetach();
    }

}
