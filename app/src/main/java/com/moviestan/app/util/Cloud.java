package com.moviestan.app.util;

import android.content.Context;
import android.os.Handler;

import com.moviestan.app.R;
import com.moviestan.app.data.FriendsSerializer;
import com.moviestan.app.data.General;
import com.moviestan.app.data.MovieSerializer;
import com.moviestan.app.data.RatingSerializer;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Cloud {

    private final static String URL_PREFIX = "http://moviestan.azurewebsites.net/api/";
    private static OkHttpClient mClient = new OkHttpClient();
    private static final ExecutorService mExecutors = Executors.newSingleThreadExecutor();

    public interface RegisterDeviceListener {
        Handler getHandler();
        void onSuccess(String userId);
        void onFail(String msg);
    }

    public interface LoginListener {
        Handler getHandler();
        void onSuccess(String userId);
        void onFail(String msg);
    }

    public interface MovieListener {
        Handler getHandler();
        void onSuccess(boolean has_next, String page, ArrayList<MovieSerializer> data);
        void onFail(String msg);
    }

    public interface RatingListener {
        Handler getHandler();
        void onSuccess(ArrayList<RatingSerializer> data);
        void onFail(String msg);
    }

    public interface FavoriteListener {
        Handler getHandler();
        void onSuccess(String fb_id, String user_name, ArrayList<MovieSerializer> data);
        void onFail(String msg);
    }

    public interface FriendsListener {
        Handler getHandler();
        void onSuccess(ArrayList<FriendsSerializer> data);
        void onFail(String msg);
    }

    public interface RecommendListener {
        Handler getHandler();
        void onSuccess(ArrayList<MovieSerializer> data);
        void onFail(String msg);
    }
    public interface SimpleListener {
        Handler getHandler();
        void onSuccess(String msg);
        void onFail(String msg);
    }


    // universal imageloader
    public static ImageLoader initImageLoader(Context content){
        ImageLoader imageLoader 	= ImageLoader.getInstance();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
			/*.showImageOnLoading(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)*/
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(content)
                .defaultDisplayImageOptions(options)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        imageLoader.init(config);

        return imageLoader;
    }

    // set basic register
    static class RegisterRunnable implements Runnable {
        // config
        Context mContext;
        RegisterDeviceListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        // data
        String mFacebookId;
        String mFbName;
        String mEmail;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mSuccessMsg);
                }
            }
        };

        public RegisterRunnable(Context context, String facebook_id, String fb_name, String email, RegisterDeviceListener listener) {
            mListener   = listener;
            mContext    = context;
            mFacebookId = facebook_id;
            mFbName     = fb_name;
            mEmail      = email;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "MemberRegister.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("FacebookId", mFacebookId)
                        .addFormDataPart("Name", mFbName)
                        .addFormDataPart("Email", mEmail)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("RegisterRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // return id
                    mSuccessMsg = responseJson.getString("MemberId");

                }else if(getStatusCode.equals("500")){

                    // skip register
                    mSuccessMsg = responseJson.getString("MemberId");
                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("RegisterRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // set basic login
    static class LoginRunnable implements Runnable {
        // config
        Context mContext;
        LoginListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        // data
        String mFacebookId;
        String mEmail;
        String mMemberId;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mSuccessMsg);
                }
            }
        };

        public LoginRunnable(Context context, String facebook_id, String email, String member_id, LoginListener listener) {
            mListener   = listener;
            mContext    = context;
            mFacebookId = facebook_id;
            mEmail      = email;
            mMemberId   = member_id;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "MemberLogin.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("FacebookId", mFacebookId)
                        .addFormDataPart("Email", mEmail)
                        .addFormDataPart("MemberId", mMemberId)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("LoginRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
                    mSuccessMsg = responseJson.getString("LoginToken");

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("LoginRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // get movie genre list
    static class GetMovieGenreRunnable implements Runnable {
        // config
        Context mContext;
        SimpleListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        // litedatabase
        LiteDatabase mLiteDatabase;


        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mSuccessMsg);
                }
            }
        };

        public GetMovieGenreRunnable(Context context, SimpleListener listener) {
            mListener   = listener;
            mContext    = context;
            mLiteDatabase = new LiteDatabase(context);
        }
        @Override
        public void run() {


            String url = "https://api.themoviedb.org/3/genre/movie/list?api_key=3eb234903c8417da0d448fde2f9cc02e";

            try {


                // request
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                 LogFactory.set("GetMovieGenreRunnable", responseJson.toString());


                // get list
                JSONArray jsonArray = new JSONArray(responseJson.getString("genres"));
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject tmpObj = jsonArray.getJSONObject(i);

                    String saveName = General.MOVIE_GENRE + "_" + tmpObj.getInt("id");
                    mLiteDatabase.save(saveName, tmpObj.getString("name"));
                }


                mSuccessMsg = mContext.getString(R.string.api_success);

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("GetMovieGenreRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // get movie list
    static class GetMovieListRunnable implements Runnable {
        // config
        Context mContext;
        MovieListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        // data
        boolean mHasNext = false;
        String mPage;
        ArrayList<MovieSerializer> mData = new ArrayList<MovieSerializer>();

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mHasNext, mPage, mData);
                }
            }
        };

        public GetMovieListRunnable(Context context, String page, MovieListener listener) {
            mListener   = listener;
            mContext    = context;
            mPage       = page;
        }
        @Override
        public void run() {


            String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=3eb234903c8417da0d448fde2f9cc02e&page=" + mPage;

            try {


                // request
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

//                 LogFactory.set("GetMovieListRunnable", responseJson.toString());

                // get current page
                int currentPage = responseJson.getInt("page");

                // get total page
                int totalPage = responseJson.getInt("total_pages");

                // get list
                JSONArray jsonArray = new JSONArray(responseJson.getString("results"));
                for(int i = 0; i < jsonArray.length(); i++){
                    MovieSerializer tmpData = MovieSerializer.fromJSON(jsonArray.getString(i));
                    mData.add(tmpData);
                }

                if(currentPage < totalPage){
                    mHasNext = true;
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("RegisterRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // set rating runnable
    static class RatingRunnable implements Runnable {
        // config
        Context mContext;
        SimpleListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        // data
//        MemberId: Get from MemberLogin.php API
//        LoginToken: Get from MemberLogin.php API
//        MovieId: Get from Movie List API
//        Score: The rating score (1-10)
//        Comments: The Comments

        LiteDatabase mLiteDatabase;
        String mMemberId;
        String mLoginToken;
        String mMovieId;
        String mScore;
        String mComments;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mSuccessMsg);
            }
            }
        };

        public RatingRunnable(Context context, String movie_id, String score, String comments, SimpleListener listener) {
            mListener       = listener;
            mContext        = context;
            mLiteDatabase   = new LiteDatabase(context);
            mLoginToken     = mLiteDatabase.get(mLiteDatabase.APP_USER_TOKEN);
            mMemberId       = mLiteDatabase.get(mLiteDatabase.APP_USER_ID);
            mMovieId        = movie_id;
            mScore          = score;
            mComments       = comments;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "RatingAdd.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("MemberId", mMemberId)
                        .addFormDataPart("LoginToken", mLoginToken)
                        .addFormDataPart("MovieId", mMovieId)
                        .addFormDataPart("Score", mScore)
                        .addFormDataPart("Comments", mComments)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("RatingRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
                    mSuccessMsg = responseJson.getString("StatusCode");

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("RatingRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // get rating list runnable
    static class GetRatingRunnable implements Runnable {
        // config
        Context mContext;
        RatingListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        ArrayList<RatingSerializer> mData = new ArrayList<RatingSerializer>();

        // data
//        MemberId: Get from MemberLogin.php API
//        LoginToken: Get from MemberLogin.php API
//        MovieId: Get from Movie List API
//        Score: The rating score (1-10)
//        Comments: The Comments

        LiteDatabase mLiteDatabase;
        String mMemberId;
        String mLoginToken;
        String mMovieId;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mData);
                }
            }
        };

        public GetRatingRunnable(Context context, String movie_id, RatingListener listener) {
            mListener       = listener;
            mContext        = context;
            mLiteDatabase   = new LiteDatabase(context);
            mLoginToken     = mLiteDatabase.get(mLiteDatabase.APP_USER_TOKEN);
            mMemberId       = mLiteDatabase.get(mLiteDatabase.APP_USER_ID);
            mMovieId        = movie_id;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "RatingList.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("MemberId", mMemberId)
                        .addFormDataPart("LoginToken", mLoginToken)
                        .addFormDataPart("MovieId", mMovieId)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("GetRatingRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
                    mSuccessMsg = responseJson.getString("StatusCode");

                    // get list
                    JSONArray jsonArray = new JSONArray(responseJson.getString("List"));
                    for(int i = 0; i < jsonArray.length(); i++){
                        RatingSerializer tmpData = RatingSerializer.fromJSON(jsonArray.getString(i));
                        mData.add(tmpData);
                    }

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("GetRatingRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // set browse history runnable
    static class BrowseHistoryRunnable implements Runnable {
        // config
        Context mContext;
        SimpleListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        // data

        LiteDatabase mLiteDatabase;
        String mMemberId;
        String mLoginToken;
        String mMovieId;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mSuccessMsg);
                }
            }
        };

        public BrowseHistoryRunnable(Context context, String movie_id, SimpleListener listener) {
            mListener       = listener;
            mContext        = context;
            mLiteDatabase   = new LiteDatabase(context);
            mLoginToken     = mLiteDatabase.get(mLiteDatabase.APP_USER_TOKEN);
            mMemberId       = mLiteDatabase.get(mLiteDatabase.APP_USER_ID);
            mMovieId        = movie_id;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "BrowserAdd.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("MemberId", mMemberId)
                        .addFormDataPart("LoginToken", mLoginToken)
                        .addFormDataPart("MovieId", mMovieId)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("BrowseHistoryRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
                    mSuccessMsg = responseJson.getString("StatusCode");

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("BrowseHistoryRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // set favorite runnable
    static class FavoriteRunnable implements Runnable {
        // config
        Context mContext;
        SimpleListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        // data

        LiteDatabase mLiteDatabase;
        String mMemberId;
        String mLoginToken;
        String mMovieId;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mSuccessMsg);
                }
            }
        };

        public FavoriteRunnable(Context context, String movie_id, SimpleListener listener) {
            mListener       = listener;
            mContext        = context;
            mLiteDatabase   = new LiteDatabase(context);
            mLoginToken     = mLiteDatabase.get(mLiteDatabase.APP_USER_TOKEN);
            mMemberId       = mLiteDatabase.get(mLiteDatabase.APP_USER_ID);
            mMovieId        = movie_id;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "FavoriteAdd.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("MemberId", mMemberId)
                        .addFormDataPart("LoginToken", mLoginToken)
                        .addFormDataPart("MovieId", mMovieId)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("FavoriteRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
                    mSuccessMsg = responseJson.getString("Action");

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("FavoriteRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // check favorite runnable
    static class CheckFavoriteRunnable implements Runnable {
        // config
        Context mContext;
        SimpleListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        // data

        LiteDatabase mLiteDatabase;
        String mMemberId;
        String mLoginToken;
        String mMovieId;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mSuccessMsg);
                }
            }
        };

        public CheckFavoriteRunnable(Context context, String movie_id, SimpleListener listener) {
            mListener       = listener;
            mContext        = context;
            mLiteDatabase   = new LiteDatabase(context);
            mLoginToken     = mLiteDatabase.get(mLiteDatabase.APP_USER_TOKEN);
            mMemberId       = mLiteDatabase.get(mLiteDatabase.APP_USER_ID);
            mMovieId        = movie_id;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "FavoriteCheck.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("MemberId", mMemberId)
                        .addFormDataPart("LoginToken", mLoginToken)
                        .addFormDataPart("MovieId", mMovieId)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("CheckFavoriteRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
                    mSuccessMsg = responseJson.getString("FavoriteId");

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("CheckFavoriteRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }


    // get favorite list runnable
    static class GetFavoriteRunnable implements Runnable {
        // config
        Context mContext;
        FavoriteListener mListener;

        String mErrorMsg;
        String mFacebookId;
        String mUserName;

        ArrayList<MovieSerializer> mData = new ArrayList<MovieSerializer>();

        // data

        LiteDatabase mLiteDatabase;
        String mMemberId;
        String mLoginToken;
        String mFetchMemberId;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mFacebookId, mUserName, mData);
                }
            }
        };

        public GetFavoriteRunnable(Context context, String user_id, FavoriteListener listener) {
            mListener       = listener;
            mContext        = context;
            mLiteDatabase   = new LiteDatabase(context);
            mLoginToken     = mLiteDatabase.get(mLiteDatabase.APP_USER_TOKEN);
            mMemberId       = mLiteDatabase.get(mLiteDatabase.APP_USER_ID);
            mFetchMemberId  = user_id;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "FavoriteList.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("MemberId", mMemberId)
                        .addFormDataPart("LoginToken", mLoginToken)
                        .addFormDataPart("FetchMemberId", mFetchMemberId)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("GetFavoriteRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
//                    mSuccessMsg = responseJson.getString("StatusCode");
                    mFacebookId = responseJson.getJSONObject("Member").getString("UserFB");
                    mUserName = responseJson.getJSONObject("Member").getString("UserName");

                    // get list
                    JSONArray jsonArray = new JSONArray(responseJson.getString("List"));
                    for(int i = 0; i < jsonArray.length(); i++){
                        MovieSerializer tmpData = MovieSerializer.fromJSON(jsonArray.getString(i));
                        mData.add(tmpData);
                    }

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("GetFavoriteRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }


    // Add friend runnable
    static class AddFriendRunnable implements Runnable {
        // config
        Context mContext;
        SimpleListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        LiteDatabase mLiteDatabase;
        String mMemberId;
        String mLoginToken;
        String mFriendMemberId;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mSuccessMsg);
                }
            }
        };

        public AddFriendRunnable(Context context, String friend_id, SimpleListener listener) {
            mListener       = listener;
            mContext        = context;
            mLiteDatabase   = new LiteDatabase(context);
            mLoginToken     = mLiteDatabase.get(mLiteDatabase.APP_USER_TOKEN);
            mMemberId       = mLiteDatabase.get(mLiteDatabase.APP_USER_ID);
            mFriendMemberId = friend_id;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "FriendAdd.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("MemberId", mMemberId)
                        .addFormDataPart("LoginToken", mLoginToken)
                        .addFormDataPart("FriendMemberId", mFriendMemberId)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("AddFriendRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
                    mSuccessMsg = responseJson.getString("StatusCode");

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("AddFriendRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // Confirm friend runnable
    static class ConfirmFriendRunnable implements Runnable {
        // config
        Context mContext;
        SimpleListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        LiteDatabase mLiteDatabase;
        String mMemberId;
        String mLoginToken;
        String mFriendMemberId;
        String mAccept;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mSuccessMsg);
                }
            }
        };

        public ConfirmFriendRunnable(Context context, String friend_id, String accept, SimpleListener listener) {
            mListener       = listener;
            mContext        = context;
            mLiteDatabase   = new LiteDatabase(context);
            mLoginToken     = mLiteDatabase.get(mLiteDatabase.APP_USER_TOKEN);
            mMemberId       = mLiteDatabase.get(mLiteDatabase.APP_USER_ID);
            mFriendMemberId = friend_id;
            mAccept         = accept;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "FriendConfirm.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("MemberId", mMemberId)
                        .addFormDataPart("LoginToken", mLoginToken)
                        .addFormDataPart("FriendMemberId", mFriendMemberId)
                        .addFormDataPart("Accept", mAccept)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("ConfirmFriendRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
                    mSuccessMsg = responseJson.getString("StatusCode");

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("ConfirmFriendRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // get friend list runnable
    static class GetFriendsRunnable implements Runnable {
        // config
        Context mContext;
        FriendsListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        ArrayList<FriendsSerializer> mData = new ArrayList<FriendsSerializer>();

        // data

        LiteDatabase mLiteDatabase;
        String mMemberId;
        String mLoginToken;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mData);
                }
            }
        };

        public GetFriendsRunnable(Context context, FriendsListener listener) {
            mListener       = listener;
            mContext        = context;
            mLiteDatabase   = new LiteDatabase(context);
            mLoginToken     = mLiteDatabase.get(mLiteDatabase.APP_USER_TOKEN);
            mMemberId       = mLiteDatabase.get(mLiteDatabase.APP_USER_ID);
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "FriendsList.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("MemberId", mMemberId)
                        .addFormDataPart("LoginToken", mLoginToken)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("GetFriendsRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
                    // get list
                    JSONArray jsonArray = new JSONArray(responseJson.getString("List"));
                    for(int i = 0; i < jsonArray.length(); i++){
                        FriendsSerializer tmpData = FriendsSerializer.fromJSON(jsonArray.getString(i));
                        mData.add(tmpData);
                    }

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("GetFriendsRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // check friend runnable
    static class CheckFriendRunnable implements Runnable {
        // config
        Context mContext;
        SimpleListener mListener;

        String mErrorMsg;
        String mSuccessMsg;

        // data

        LiteDatabase mLiteDatabase;
        String mMemberId;
        String mLoginToken;
        String mFriendMemberId;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mSuccessMsg);
                }
            }
        };

        public CheckFriendRunnable(Context context, String friend_id, SimpleListener listener) {
            mListener       = listener;
            mContext        = context;
            mLiteDatabase   = new LiteDatabase(context);
            mLoginToken     = mLiteDatabase.get(mLiteDatabase.APP_USER_TOKEN);
            mMemberId       = mLiteDatabase.get(mLiteDatabase.APP_USER_ID);
            mFriendMemberId = friend_id;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "FriendCheck.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("MemberId", mMemberId)
                        .addFormDataPart("LoginToken", mLoginToken)
                        .addFormDataPart("FriendMemberId", mFriendMemberId)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("CheckFriendRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
                    mSuccessMsg = responseJson.getString("FriendIndex");

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("CheckFriendRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // get recommend list runnable
    static class GetRecommendsRunnable implements Runnable {
        // config
        Context mContext;
        RecommendListener mListener;

        String mErrorMsg;

        ArrayList<MovieSerializer> mData = new ArrayList<MovieSerializer>();

        // data

        LiteDatabase mLiteDatabase;
        String mMemberId;
        String mLoginToken;
        String mRecommendType;

        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (mErrorMsg != null) {
                    mListener.onFail(mErrorMsg);
                } else {
                    mListener.onSuccess(mData);
                }
            }
        };

        public GetRecommendsRunnable(Context context, String type, RecommendListener listener) {
            mListener       = listener;
            mContext        = context;
            mLiteDatabase   = new LiteDatabase(context);
            mLoginToken     = mLiteDatabase.get(mLiteDatabase.APP_USER_TOKEN);
            mMemberId       = mLiteDatabase.get(mLiteDatabase.APP_USER_ID);
            mRecommendType  = type;
        }
        @Override
        public void run() {


            String url = URL_PREFIX + "RecommendList.php";

            try {


                // setup request body
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("MemberId", mMemberId)
                        .addFormDataPart("LoginToken", mLoginToken)
                        .addFormDataPart("Type", mRecommendType)
                        .build();

                // request
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();

                // get response
                Response response = mClient.newCall(request).execute();

                // get json format

                JSONObject responseJson = new JSONObject(response.body().string());

                LogFactory.set("GetRecommendsRunnable", responseJson.toString());

                String getStatusCode = responseJson.getString("StatusCode");

                // success
                if(getStatusCode.equals("200")){

                    // get login token
                    // get list
                    JSONArray jsonArray = new JSONArray(responseJson.getString("List"));
                    for(int i = 0; i < jsonArray.length(); i++){
                        MovieSerializer tmpData = MovieSerializer.fromJSON(jsonArray.getString(i));
                        mData.add(tmpData);
                    }

                }else{
                    // fail
                    mErrorMsg = mContext.getString(R.string.api_error) + response.body().string();
                }

            }catch (Exception e){
                mErrorMsg = mContext.getString(R.string.api_error) + e;
                LogFactory.set("GetRecommendsRunnable", e);
            }


            mListener.getHandler().post(mCompleteRunnable);

        }
    }


    public static void registerDevice(Context context, String facebook_id, String fb_name, String email, RegisterDeviceListener listener) {
        mExecutors.execute(new RegisterRunnable(context, facebook_id, fb_name, email, listener));
    }

    public static void loginAction(Context context, String facebook_id, String email, String member_id, LoginListener listener) {
        mExecutors.execute(new LoginRunnable(context, facebook_id, email, member_id, listener));
    }


    public static void getMovieGenres(Context context, SimpleListener listener) {
        mExecutors.execute(new GetMovieGenreRunnable(context, listener));
    }

    public static void getMovieList(Context context, String page, MovieListener listener) {
        mExecutors.execute(new GetMovieListRunnable(context, page, listener));
    }

    public static void addRating(Context context, String movie_id, String score, String comments, SimpleListener listener) {
        mExecutors.execute(new RatingRunnable(context, movie_id, score, comments, listener));
    }

    public static void getRatingList(Context context, String movie_id, RatingListener listener) {
        mExecutors.execute(new GetRatingRunnable(context, movie_id, listener));
    }

    public static void addBrowseHistory(Context context, String movie_id, SimpleListener listener) {
        mExecutors.execute(new BrowseHistoryRunnable(context, movie_id, listener));
    }

    public static void setMyFavorite(Context context, String movie_id, SimpleListener listener) {
        mExecutors.execute(new FavoriteRunnable(context, movie_id, listener));
    }

    public static void checkMyFavorite(Context context, String movie_id, SimpleListener listener) {
        mExecutors.execute(new CheckFavoriteRunnable(context, movie_id, listener));
    }

    public static void getFavoriteList(Context context, String user_id, FavoriteListener listener) {
        mExecutors.execute(new GetFavoriteRunnable(context, user_id, listener));
    }

    public static void addFriend(Context context, String friend_id, SimpleListener listener) {
        mExecutors.execute(new AddFriendRunnable(context, friend_id, listener));
    }

    public static void confirmFriend(Context context, String friend_id, String accept, SimpleListener listener) {
        mExecutors.execute(new ConfirmFriendRunnable(context, friend_id, accept, listener));
    }

    public static void getFriendList(Context context, FriendsListener listener) {
        mExecutors.execute(new GetFriendsRunnable(context, listener));
    }

    public static void checkMyFriend(Context context, String friend_id, SimpleListener listener) {
        mExecutors.execute(new CheckFriendRunnable(context, friend_id, listener));
    }

    public static void getRecommendList(Context context, String type, RecommendListener listener) {
        mExecutors.execute(new GetRecommendsRunnable(context, type, listener));
    }
}
