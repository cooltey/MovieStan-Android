package com.moviestan.app.util;

import android.content.Context;
import android.os.Handler;

import com.moviestan.app.R;
import com.moviestan.app.data.General;
import com.moviestan.app.data.MovieSerializer;
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


    public interface AppInformationListener {
        Handler getHandler();
        void onSuccess(String email, String about_us);
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

        public RegisterRunnable(Context context, String facebook_id, String email, RegisterDeviceListener listener) {
            mListener   = listener;
            mContext    = context;
            mFacebookId = facebook_id;
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

                // LogFactory.set("GetMovieListRunnable", responseJson.toString());

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

    public static void registerDevice(Context context, String facebook_id, String email, RegisterDeviceListener listener) {
        mExecutors.execute(new RegisterRunnable(context, facebook_id, email, listener));
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

}
