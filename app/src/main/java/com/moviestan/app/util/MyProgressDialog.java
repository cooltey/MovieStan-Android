package com.moviestan.app.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.moviestan.app.R;

public class MyProgressDialog{

    private static ProgressDialog progressDialog;

    public static void procsessing(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.dialog_processing));
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public static void procsessing(Context context, int position){
        procsessing(context);
        progressDialog.getWindow().setGravity(position);
    }



    public static void cancel(){
        if(progressDialog != null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }


}
