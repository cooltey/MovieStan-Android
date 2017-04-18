package com.moviestan.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moviestan.app.data.FriendsSerializer;
import com.moviestan.app.util.Cloud;
import com.moviestan.app.util.MyProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {

    // general
    private Handler mHandler = new Handler();
    private ImageLoader mImageLoader;
    private String mUserId;

    // view
    private AutoCompleteTextView mSearchFriendView;
    private Button mSearchBtnView;
    private LinearLayout mFriendsZoneView;

    // data
    private ArrayList<FriendsSerializer> mData = new ArrayList<>();

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mImageLoader = Cloud.initImageLoader(getActivity());

        // setup view
        mSearchFriendView   = (AutoCompleteTextView) view.findViewById(R.id.search_friend);
        mSearchBtnView      = (Button) view.findViewById(R.id.search_btn);
        mFriendsZoneView    = (LinearLayout) view.findViewById(R.id.friends_zone);

        // search
        searchFriend();

        // download friend
        downloadMyFriendsList();

        return view;
    }


    private void searchFriend(){
        mSearchBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get keyword

                String getKeyword = mSearchFriendView.getText().toString();


                ArrayList<FriendsSerializer> searchData = new ArrayList<FriendsSerializer>();

                if(getKeyword != null){
                    // filter search

                    for(FriendsSerializer getData : mData){

                        if(getData.name.toLowerCase().contains(getKeyword.toLowerCase())){
                            // add
                            searchData.add(getData);
                        }
                    }

                    // show list
                    showFriendList(searchData);
                }
            }
        });
    }

    private void showFriendList(ArrayList<FriendsSerializer> data){

        // clear view
        mFriendsZoneView.removeAllViewsInLayout();

        for(final FriendsSerializer getData : data){

            // show views
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.item_list_friends, null);

            CircleImageView profileImgView          = (CircleImageView) rootView.findViewById(R.id.list_img);
            TextView profileUserNameView            = (TextView) rootView.findViewById(R.id.list_username);
            TextView profileDateView                = (TextView) rootView.findViewById(R.id.list_date);
            LinearLayout profileRequestZoneView     = (LinearLayout) rootView.findViewById(R.id.friend_request_block);
            Button requestAcceptBtnView             = (Button) rootView.findViewById(R.id.friend_accept_btn);
            Button requestDeclineBtnView            = (Button) rootView.findViewById(R.id.friend_decline_btn);

            if(getData.accpetance.equals("2")){ // // 2 == await, 1 == accept, 0 == decline
                profileDateView.setVisibility(View.GONE);
                profileRequestZoneView.setVisibility(View.VISIBLE);

                requestAcceptBtnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        addFriendAction(getData.user_id, "1");

                    }
                });

                requestDeclineBtnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        addFriendAction(getData.user_id, "0");
                    }
                });

            }else{
                // accept
                profileDateView.setVisibility(View.VISIBLE);
                profileRequestZoneView.setVisibility(View.GONE);

                // cut date
                String[] newDate = getData.date.split(" ");

                profileDateView.setText(getString(R.string.friends_since_prefix) + newDate[0]);
            }

            // view
            // setup image
            mImageLoader.displayImage("https://graph.facebook.com/" + getData.fb_id + "/picture?type=large", profileImgView);

            // setup username
            profileUserNameView.setText(getData.name);


            // long press
            rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getActivity());
                    mAlertDialog.setMessage(getString(R.string.friend_remove));
                    mAlertDialog.setPositiveButton(getString(R.string.alert_yes_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            addFriendAction(getData.user_id, "0");
                        }
                    });
                    mAlertDialog.setNegativeButton(getString(R.string.alert_no_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    mAlertDialog.setCancelable(false);
                    mAlertDialog.show();

                    return false;
                }
            });

            // click and view profile
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)getActivity()).displayProfileView(getData.user_id);
                }
            });

            mFriendsZoneView.addView(rootView);

        }
    }

    private void addFriendAction(String user_id, String acceptance){
        Cloud.confirmFriend(getActivity(), user_id, acceptance, new Cloud.SimpleListener() {
            @Override
            public Handler getHandler() {
                return mHandler;
            }

            @Override
            public void onSuccess(String status) {
                // refresh view
                downloadMyFriendsList();
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }

    // download my friends list and member information
    private void downloadMyFriendsList(){

        MyProgressDialog.procsessing(getActivity());

        Cloud.getFriendList(getActivity(), new Cloud.FriendsListener() {
            @Override
            public Handler getHandler() {
                MyProgressDialog.cancel();
                return mHandler;
            }

            @Override
            public void onSuccess(ArrayList<FriendsSerializer> data) {

                mData = data;

                showFriendList(data);
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
