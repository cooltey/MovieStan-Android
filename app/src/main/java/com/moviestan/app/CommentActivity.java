package com.moviestan.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moviestan.app.data.MovieSerializer;
import com.moviestan.app.util.Cloud;
import com.moviestan.app.util.LiteDatabase;
import com.moviestan.app.util.MyProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentActivity extends AppCompatActivity{

    // movie data
    private MovieSerializer mData;


    // general
    private Handler mHandler = new Handler();
    private ImageLoader mImageLoader;
    private LiteDatabase mLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // init
        mImageLoader  = Cloud.initImageLoader(this);
        mLiteDatabase = new LiteDatabase(this);

        // get data
        mData = (MovieSerializer) this.getIntent().getSerializableExtra(MovieFragment.MOVIE_DATA);

        // setup view
        ImageView moviePoster   = (ImageView) findViewById(R.id.comment_movie_poster);
        TextView movieTitle     = (TextView) findViewById(R.id.comment_movie_title);
        TextView movieDesc      = (TextView) findViewById(R.id.comment_movie_description);
        final AppCompatSpinner movieRating   = (AppCompatSpinner) findViewById(R.id.comment_rating);
        final EditText movieComment   = (EditText) findViewById(R.id.comment_enter_zone);
        Button movieCommentSend = (Button) findViewById(R.id.comment_send);

        // setup content
        mImageLoader.displayImage(mData.poster_path, moviePoster);
        movieTitle.setText(mData.title);
        movieDesc.setText(mData.overview);

        // send comments
        movieCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(CommentActivity.this);
                mAlertDialog.setMessage(getString(R.string.alert_send_comment));
                mAlertDialog.setPositiveButton(getString(R.string.alert_yes_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String movieId = mData.id;
                        String movieScore = movieRating.getSelectedItem().toString() + "";
                        String movieComments = movieComment.getText().toString();

                        // execute posting process
                        postComment(movieId, movieScore, movieComments);
                    }
                });
                mAlertDialog.setNegativeButton(getString(R.string.alert_no_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                mAlertDialog.setCancelable(false);
                mAlertDialog.show();

            }
        });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    private void postComment(String movie_id, String score, String movie_comment){
        MyProgressDialog.procsessing(CommentActivity.this);

        Cloud.addRating(CommentActivity.this, movie_id, score, movie_comment, new Cloud.SimpleListener() {
            @Override
            public Handler getHandler() {
                MyProgressDialog.cancel();
                return mHandler;
            }

            @Override
            public void onSuccess(String msg) {
                Toast.makeText(getApplicationContext(), getString(R.string.comment_success), Toast.LENGTH_SHORT).show();

                // back to previous
                finish();
            }

            @Override
            public void onFail(String msg) {
                Toast.makeText(getApplicationContext(), getString(R.string.comment_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
