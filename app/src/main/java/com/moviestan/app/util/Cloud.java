package com.moviestan.app.util;

import android.content.Context;
import android.os.Handler;

import com.moviestan.app.R;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONObject;

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

    public static void registerDevice(Context context, String facebook_id, String email, RegisterDeviceListener listener) {
        mExecutors.execute(new RegisterRunnable(context, facebook_id, email, listener));
    }



}
