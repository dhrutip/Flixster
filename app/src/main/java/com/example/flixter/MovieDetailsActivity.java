package com.example.flixter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixter.databinding.ActivityMovieDetailsBinding;
import com.example.flixter.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;
    String url;
    public static final String TAG = "MovieDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        url = "https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos?api_key=9d001a90e38ee3da32a41de9884fbee7";

        // set the title and overview
        binding.tvTitleMovie.setText(movie.getTitle());
        binding.tvOverviewMovie.setText(movie.getOverview());

        String imageUrl;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageUrl = movie.getBackdropPath();
        } else {
            imageUrl = movie.getPosterPath();
        }

        int radius = 25; // corner radius, higher value = more rounded
        int margin = 7; // crop margin, set to 0 for corners with no crop
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.mipmap.placeholder_foreground)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(binding.igView);

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        binding.rbVoteAverage.setRating(voteAverage / 2.0f);

        binding.igView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(url, new JsonHttpResponseHandler() { // movie database returns json
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d(TAG, "onSuccess");
                        Log.d(TAG, json.toString());
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            String trailerKey = jsonArray.getJSONObject(0).getString("key");
                            //send this key over to the movie trailer activity via an intent
                            Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class); //make intent
                            intent.putExtra("key", trailerKey); //pass in values
                            startActivity(intent); //start activity with the intent
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String s, Throwable throwable) {
                        Log.d(TAG, "onFailure");
                    }
                });
            }
        });
    }
}