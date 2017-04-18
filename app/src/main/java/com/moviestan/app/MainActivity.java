package com.moviestan.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.moviestan.app.data.MovieSerializer;
import com.moviestan.app.util.Cloud;
import com.moviestan.app.util.LiteDatabase;
import com.moviestan.app.util.LogFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // init
    private AlertDialog.Builder mAlertDialog;
    private ImageLoader mImageLoader;
    private LiteDatabase mLiteDatabase;
    private Handler mHandler = new Handler();

    // view
    private View mHeaderLayout;
    private TextView mHeaderUsernameView;
    private CircleImageView mHeaderImageView;
    public static ImageButton mRightBtnView;

    // fragment
    private FragmentTransaction mFragmentTransaction;
    private Stack<String> mTitleStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // init
        mImageLoader = Cloud.initImageLoader(this);
        mLiteDatabase = new LiteDatabase(this);
        mAlertDialog  = new AlertDialog.Builder(this);

        // get tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get view
        mRightBtnView = (ImageButton) findViewById(R.id.right_btn);

        // get drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // setup header layout
        setupHeaderLayout(navigationView);

        // download
        downloadMovieGenres();

        // display view
        displayView(0);
    }

    // download movie genres
    private void downloadMovieGenres(){
        Cloud.getMovieGenres(getApplicationContext(), new Cloud.SimpleListener() {
            @Override
            public Handler getHandler() {
                return mHandler;
            }

            @Override
            public void onSuccess(String msg) {

            }

            @Override
            public void onFail(String msg) {

            }
        });
    }


    private void setupHeaderLayout(NavigationView navigationView){
        // setup header layout
        mHeaderLayout = navigationView.getHeaderView(0);
        mHeaderUsernameView = (TextView) mHeaderLayout.findViewById(R.id.nav_profile_username);
        mHeaderImageView = (CircleImageView) mHeaderLayout.findViewById(R.id.nav_profile_img);

        // setup image
        mImageLoader.displayImage("https://graph.facebook.com/" + mLiteDatabase.get(mLiteDatabase.FACEBOOK_ID) + "/picture?type=large", mHeaderImageView);

        // setup username
        mHeaderUsernameView.setText(mLiteDatabase.get(mLiteDatabase.FACEBOOK_NAME));

        // setup username action
        mHeaderUsernameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayView(R.id.nav_profile_username);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            LogFactory.set("onBackPressed", "1");
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            LogFactory.set("count", count);

            if (count == 0) {
                LogFactory.set("onBackPressed", "2");

                mAlertDialog.setMessage(getString(R.string.alert_logout));
                mAlertDialog.setPositiveButton(getString(R.string.alert_yes_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                mAlertDialog.setNegativeButton(getString(R.string.alert_no_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mAlertDialog.setCancelable(false);
                mAlertDialog.show();

            } else {
                LogFactory.set("onBackPressed", "3");

                mRightBtnView.setVisibility(View.GONE);

//                mTitleStack.pop();
                getSupportActionBar().setTitle(getString(R.string.app_title_movie_stan));

                getSupportFragmentManager().popBackStack();
            }
        }
    }


    public void displayView(int viewId) {

        mRightBtnView.setVisibility(View.GONE);

        Fragment fragment = null;
        String title = getString(R.string.app_name);
        Bundle bundle = new Bundle();

        switch (viewId) {
            case R.id.nav_home:
                fragment = MainFragment.newInstance();
                title  = getString(R.string.app_title_movie_stan);
                break;

            case R.id.nav_recommend:
                fragment = RecommendFragment.newInstance("1");
                title  = getString(R.string.app_title_recommend_1);
                break;

            case R.id.nav_recommend_2:
                fragment = RecommendFragment.newInstance("2");
                title  = getString(R.string.app_title_recommend_2);
                break;

            case R.id.nav_recommend_3:
                fragment = RecommendFragment.newInstance("3");
                title  = getString(R.string.app_title_recommend_3);
                break;


            case R.id.nav_friends:
                fragment = new FriendsFragment();
                title  = getString(R.string.app_title_friends);
                break;

            case R.id.nav_about:
                fragment = new AboutFragment();
                title  = getString(R.string.app_title_about);
                break;

            case R.id.nav_logout:
                mAlertDialog.setMessage(getString(R.string.alert_logout_facebook));
                mAlertDialog.setPositiveButton(getString(R.string.alert_yes_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logOut();
                        mLiteDatabase.clear();

                        // go to launch
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, LaunchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
                mAlertDialog.setNegativeButton(getString(R.string.alert_no_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mAlertDialog.setCancelable(false);
                mAlertDialog.show();
                break;

            default:

                fragment = new MainFragment();
                title  = getString(R.string.app_title_movie_stan);
                break;
        }

        if (fragment != null) {
            mFragmentTransaction= getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.replace(R.id.content_frame, fragment);
            mFragmentTransaction.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            mTitleStack.add(title);
            getSupportActionBar().setTitle(title);
        }

        // go to profile view
        if(viewId == R.id.nav_profile_img || viewId == R.id.nav_profile_username){
            // get profile view
            displayProfileView(mLiteDatabase.get(mLiteDatabase.APP_USER_ID));
        }

    }


//    public void showProfileSettings(String user_id){
//        if(!user_id.equals(mLiteDatabase.get(mLiteDatabase.APP_USER_ID))) {
//            // display view
//            mRightBtnView.setVisibility(View.VISIBLE);
//            mRightBtnView.setImageResource(R.drawable.ic_friend_add);
//            // add favorite
//            mRightBtnView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // add favorite
//                }
//            });
//        }
//    }

    public void displayProfileView(final String user_id){

        // display view
        mRightBtnView.setVisibility(View.GONE);

        Fragment fragment = null;

        fragment = ProfileFragment.newInstance(user_id);
        String title  = getString(R.string.app_title_profile);

        if (fragment != null) {

            if(!user_id.equals(mLiteDatabase.get(mLiteDatabase.APP_USER_ID))) {

                Cloud.checkMyFriend(getApplicationContext(), user_id, new Cloud.SimpleListener() {
                    @Override
                    public Handler getHandler() {
                        return mHandler;
                    }

                    @Override
                    public void onSuccess(String data) {
                        if(data.equals("0")){
                            // display view
                            mRightBtnView.setVisibility(View.VISIBLE);
                            mRightBtnView.setImageResource(R.drawable.ic_friend_add);
                            // add favorite
                            mRightBtnView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    // send friend request
                                    Cloud.addFriend(getApplicationContext(), user_id, new Cloud.SimpleListener() {
                                        @Override
                                        public Handler getHandler() {
                                            return mHandler;
                                        }

                                        @Override
                                        public void onSuccess(String msg) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.friends_request_sent), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFail(String msg) {

                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });


            }

            mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.replace(R.id.content_frame, fragment);
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            mTitleStack.add(title);
            getSupportActionBar().setTitle(title);
        }
    }

    public void displayMovieView(final MovieSerializer data){

        Fragment fragment = null;

        fragment = MovieFragment.newInstance(data);
        String title  = data.title;

        if (fragment != null) {

            // display view
            mRightBtnView.setVisibility(View.VISIBLE);
            mRightBtnView.setImageResource(R.drawable.ic_favorite_empty);
            // add favorite
            mRightBtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // add favorite
                    setupFavorite(data.id);
                }
            });

            mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.replace(R.id.content_frame, fragment);
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            mTitleStack.add(title);
            getSupportActionBar().setTitle(title);
        }
    }

    // setup favorite
    private void setupFavorite(String movie_id){


        Cloud.setMyFavorite(getApplicationContext(), movie_id, new Cloud.SimpleListener() {
            @Override
            public Handler getHandler() {
                return mHandler;
            }

            @Override
            public void onSuccess(String status) {
                if(status.equals("INSERT")){
                    // null
                    mRightBtnView.setImageResource(R.drawable.ic_favorite_added);
                }else{
                    // exist
                    mRightBtnView.setImageResource(R.drawable.ic_favorite_empty);
                }
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displayView(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
