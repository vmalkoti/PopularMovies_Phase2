package com.example.malkoti.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.malkoti.popularmovies.databinding.ActivityDetailsBinding;
import com.example.malkoti.popularmovies.model.Movie;
import com.example.malkoti.popularmovies.utils.ApiClient;
import com.example.malkoti.popularmovies.utils.MovieApiRetrofitInterface;
import com.squareup.picasso.Picasso;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {
    public static final String MOVIE_INTENT_KEY = "MOVIE_ID";
    private final String LOG_TAG = DetailsActivity.class.getSimpleName();

    ActivityDetailsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        int selectedMovieId = getIntent().getIntExtra(MOVIE_INTENT_KEY, 0);;
        getMovieDetails(selectedMovieId);
    }

    /**
     * Get movie details from the API
     * @param movieId ID of movie to fetch
     */
    private void getMovieDetails(final int movieId) {
        String apiKey = BuildConfig.apiKey;
        Call<Movie> call = ApiClient.getApiInterface().getMovieDetails(movieId, apiKey);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                loadMovieDetailsIntoViews(movie);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Error getting movie details", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error getting movie details " + movieId
                    + ". \nERROR : " + t.getMessage());
            }
        });
    }

    /**
     * Load movie information into Views
     * @param movie Movie object to map to views
     */
    private void loadMovieDetailsIntoViews(Movie movie) {
        float ratingVal = movie.getRating()/2;
        String posterUrl = MovieApiRetrofitInterface.IMG_BASE_URL
                +MovieApiRetrofitInterface.IMG_POSTER_SIZE
                + movie.getPosterPath();
        String backdropUrl = MovieApiRetrofitInterface.IMG_BASE_URL
                + MovieApiRetrofitInterface.IMG_BKDROP_SIZE
                + movie.getBackdropPath();

        setTitle(movie.getTitle());

        binding.dtlMovieTitleTv.setText(movie.getTitle());
        binding.ratingTv.setText(String.valueOf(ratingVal));
        binding.ratingBar.setVisibility(View.VISIBLE);
        binding.ratingBar.setRating(ratingVal);
        binding.taglineTv.setText(movie.getTagline());
        binding.releaseDateTv.setText(movie.getReleaseDate());
        binding.summaryTv.setText(movie.getOverview());

        Picasso.get()
                .load(posterUrl)
                .placeholder(R.mipmap.poster_placeholder)
                .into(binding.dtlPosterImg);

        Picasso.get()
                .load(backdropUrl)
                .placeholder(R.mipmap.backdrop_placeholder)
                .into(binding.dtlBackdropImg);
    }

}
