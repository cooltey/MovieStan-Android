package com.moviestan.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

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

        mLiteDatabase = new LiteDatabase(this);

        // call login
        setupFacebookLogin();

    }

    // setup facebook login button
    private void setupFacebookLogin(){

        // initial callback manager
        mCallbackManager = CallbackManager.Factory.create();

        // setup view
        mLoginButton = (LoginButton) findViewById(R.id.login_button);

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
                MyProgressDialog.procsessing(LaunchActivity.this);

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


                                            // goto next activity
                                            gotoMain();

                                            LogFactory.set("LaunchActivity onSuccess", userId);
                                        }

                                        @Override
                                        public void onFail(String msg) {
                                            LogFactory.set("LaunchActivity onFail", msg);


                                        }
                                    });


                                }catch (Exception e){
                                    LogFactory.set("LaunchActivity Error on API", e);


                                    // goto next activity
                                    gotoMain();
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
