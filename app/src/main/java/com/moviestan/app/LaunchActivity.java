package com.moviestan.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.moviestan.app.util.Cloud;
import com.moviestan.app.util.LiteDatabase;
import com.moviestan.app.util.LogFactory;
import com.moviestan.app.util.MyProgressDialog;

import org.json.JSONObject;

import java.util.Arrays;

public class LaunchActivity extends AppCompatActivity{

    // cloud request
    private Handler mHandler = new Handler();

    // view
    private LoginButton mLoginButton;

    // callback
    private CallbackManager mCallbackManager;

    // lite database
    private LiteDatabase mLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        //init
        mLiteDatabase = new LiteDatabase(this);



        // setup view
        mLoginButton = (LoginButton) findViewById(R.id.login_button);


        // check access
        if(!mLiteDatabase.get(mLiteDatabase.FACEBOOK_ID).equals("0")){

            // hide btn
            mLoginButton.setVisibility(View.GONE);

            MyProgressDialog.procsessing(LaunchActivity.this);

            // call token
            getLoginToken(mLiteDatabase.get(mLiteDatabase.FACEBOOK_ID),
                        mLiteDatabase.get(mLiteDatabase.FACEBOOK_EMAIL),
                        mLiteDatabase.get(mLiteDatabase.APP_USER_ID));

        }else{

            // call login
            setupFacebookLogin();
        }

    }

    // get login token
    private void getLoginToken(String fb_id, String email, String user_id){
        // send information to database
        Cloud.loginAction(getApplicationContext(), fb_id, email, user_id, new Cloud.LoginListener() {
            @Override
            public Handler getHandler() {
                return mHandler;
            }

            @Override
            public void onSuccess(String userToken) {

                // save into database
                mLiteDatabase.save(mLiteDatabase.APP_USER_TOKEN, userToken);


                // goto next activity
                gotoMain();

                LogFactory.set("LaunchActivity onSuccess", userToken);
            }

            @Override
            public void onFail(String msg) {
                LogFactory.set("LaunchActivity onFail", msg);


            }
        });
    }

    // setup facebook login button
    private void setupFacebookLogin(){

        // initial callback manager
        mCallbackManager = CallbackManager.Factory.create();

        // setup permission
        mLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        // Callback registration
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LogFactory.set("LaunchActivity ", "Success");
                // App code
                String getToken = loginResult.getAccessToken().getToken();


                // call dialog
                MyProgressDialog.procsessing(LaunchActivity.this, Gravity.BOTTOM);

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                LogFactory.set("LaunchActivity", response.toString());

                                try {
                                    // Application code
                                    String getEmail = object.getString("email");
                                    String getId    = object.getString("id");
                                    String getName  = object.getString("name");

                                    // save into database
                                    mLiteDatabase.save(mLiteDatabase.FACEBOOK_ID, getId);
                                    mLiteDatabase.save(mLiteDatabase.FACEBOOK_NAME, getName);
                                    mLiteDatabase.save(mLiteDatabase.FACEBOOK_EMAIL, getEmail);

                                    // send information to database
                                    Cloud.registerDevice(getApplicationContext(), getId, getEmail, new Cloud.RegisterDeviceListener() {
                                        @Override
                                        public Handler getHandler() {
                                            return mHandler;
                                        }

                                        @Override
                                        public void onSuccess(String userId) {

                                            // save into database
                                            mLiteDatabase.save(mLiteDatabase.APP_USER_ID, userId);

                                            // execute login
                                            getLoginToken(mLiteDatabase.get(mLiteDatabase.FACEBOOK_ID),
                                                            mLiteDatabase.get(mLiteDatabase.FACEBOOK_EMAIL),
                                                            mLiteDatabase.get(mLiteDatabase.APP_USER_ID));

                                            LogFactory.set("LaunchActivity onSuccess", userId);
                                        }

                                        @Override
                                        public void onFail(String msg) {
                                            LogFactory.set("LaunchActivity onFail", msg);


                                        }
                                    });


                                }catch (Exception e){
                                    LogFactory.set("LaunchActivity Error on API", e);
                                    // error
                                    finish();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                LogFactory.set("LaunchActivity Error on API", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                LogFactory.set("LaunchActivity Error on API", "onError " + exception);
            }
        });
    }

    private void gotoMain(){

        MyProgressDialog.cancel();

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // receive callback from API
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
