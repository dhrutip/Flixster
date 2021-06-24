package com.example.flixter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flixter.models.Movie;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;

    // the view objects
    TextView tvTitleMovie;
    TextView tvOverviewMovie;
    RatingBar rbVoteAverage;
    ImageView igView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // resolve the view objects
        tvTitleMovie = (TextView) findViewById(R.id.tvTitleMovie);
        tvOverviewMovie = (TextView) findViewById(R.id.tvOverviewMovie);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        igView = (ImageView) findViewById(R.id.igView);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        tvTitleMovie.setText(movie.getTitle());
        tvOverviewMovie.setText(movie.getOverview());

        String imageUrl;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageUrl = movie.getBackdropPath();
        } else {
            imageUrl =movie.getPosterPath();
        }

        int radius = 25; // corner radius, higher value = more rounded
        int margin = 7; // crop margin, set to 0 for corners with no crop
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.mipmap.placeholder_foreground)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(igView);


        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage / 2.0f);
    }
}