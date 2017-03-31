package com.moviestan.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.moviestan.app.data.MovieSerializer;
import com.moviestan.app.util.Cloud;
import com.moviestan.app.util.MyProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    // parameter string
    public static final String USER_ID = "user_id";

    // general
    private Handler mHandler = new Handler();
    private ImageLoader mImageLoader;
    private String mUserId;

    // views

    // setup views
    private CircleImageView mProfileImgView;
    private TextView mProfileNameView;
    private RecyclerView mProfileFavMoviesView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String user_id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_ID, user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mUserId = getArguments().getString(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mImageLoader = Cloud.initImageLoader(getActivity());

        // setup views
        mProfileImgView         = (CircleImageView) view.findViewById(R.id.profile_img);
        mProfileNameView        = (TextView) view.findViewById(R.id.profile_username);
        mProfileFavMoviesView   = (RecyclerView) view.findViewById(R.id.fav_movies);

        // setup add button
//        ((MainActivity)getActivity()).showProfileSettings(mUserId);

        // download data
        downloadMyFavorite(mUserId);

        return view;
    }

    // download my favorite list and member information
    private void downloadMyFavorite(final String user_id){

        MyProgressDialog.procsessing(getActivity());

        Cloud.getFavoriteList(getActivity(), user_id, new Cloud.FavoriteListener() {
            @Override
            public Handler getHandler() {
                MyProgressDialog.cancel();
                return mHandler;
            }

            @Override
            public void onSuccess(String fb_id, String username, ArrayList<MovieSerializer> data) {

                // setup image
                mImageLoader.displayImage("https://graph.facebook.com/" + fb_id + "/picture?type=large", mProfileImgView);

                // setup username
                mProfileNameView.setText(username);

                // setup adapter
                mProfileFavMoviesView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(getActivity(), data);
                mProfileFavMoviesView.setAdapter(adapter);
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


    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private ArrayList<MovieSerializer> mData = new ArrayList<>();
        private ImageLoader mImageLoader;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View listItemView;
            public ViewHolder(View v) {
                super(v);
                listItemView = v;
            }
        }

        public MyRecyclerViewAdapter(Context context, ArrayList<MovieSerializer> data) {
            mData = data;
            mImageLoader = Cloud.initImageLoader(context);
        }

        @Override
        public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_fav_movie, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final MovieSerializer getData = mData.get(position);

            TextView movieTitle = (TextView) holder.listItemView.findViewById(R.id.list_title);
            ImageView moviePoster = (ImageView) holder.listItemView.findViewById(R.id.list_img);
            RatingBar movieRating = (RatingBar) holder.listItemView.findViewById(R.id.list_rating);

            // set data
            mImageLoader.displayImage(getData.poster_path, moviePoster);
            movieTitle.setText(getData.title);
            movieRating.setNumStars(10);
            movieRating.setRating(Float.parseFloat(getData.vote_average));

            // setup click event
            holder.listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // go to movie
                    ((MainActivity)getActivity()).displayMovieView(getData);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

    }
}
