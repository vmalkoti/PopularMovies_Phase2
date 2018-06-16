package com.example.malkoti.popularmovies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.malkoti.popularmovies.BuildConfig;
import com.example.malkoti.popularmovies.FavoritesViewModel;
import com.example.malkoti.popularmovies.R;
import com.example.malkoti.popularmovies.adapters.ReviewAdapter;
import com.example.malkoti.popularmovies.adapters.TrailerAdapter;
import com.example.malkoti.popularmovies.databinding.ActivityDetailsBinding;
import com.example.malkoti.popularmovies.model.MovieResult;
import com.example.malkoti.popularmovies.model.ReviewResult;
import com.example.malkoti.popularmovies.model.TrailerResult;
import com.example.malkoti.popularmovies.network.ApiClient;
import com.example.malkoti.popularmovies.network.MovieApiRetrofitInterface;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {
    public static final String MOVIE_INTENT_KEY = "MOVIE_ID";
    public static final String MOVIE_FAVORITE_KEY = "MOVIE_FAVORITE";
    private final String LOG_TAG = DetailsActivity.class.getSimpleName();
    private final String API_KEY = BuildConfig.apiKey;

    ActivityDetailsBinding binding;

    private FavoritesViewModel viewModel;
    private Observer<Integer> observer = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer value) {
            isFavorite = (value > 0);
            setFavoriteIcon(isFavorite);
        }
    };

    private MovieResult.Movie movie;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        int selectedMovieId = getIntent().getIntExtra(MOVIE_INTENT_KEY, 0);
        isFavorite = getIntent().getBooleanExtra(MOVIE_FAVORITE_KEY, false);

        getMovieDetails(selectedMovieId);
        loadTrailers(selectedMovieId);
        loadReviews(selectedMovieId);

        viewModel = ViewModelProviders.of(DetailsActivity.this).get(FavoritesViewModel.class);


        binding.favoriteIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set image resource to border or filled image
                // and insert or delete movie from db
                if(!isFavorite) {
                    viewModel.insertFavorite(movie);

                } else {
                    viewModel.deleteFavorite(movie);
                }
            }
        });
    }

    /**
     * Get movie details from the API
     * @param movieId ID of movie to fetch
     */
    private void getMovieDetails(final int movieId) {
        Call<MovieResult.Movie> call = ApiClient.getApiInterface().getMovieDetails(movieId, API_KEY);

        call.enqueue(new Callback<MovieResult.Movie>() {
            @Override
            public void onResponse(Call<MovieResult.Movie> call, Response<MovieResult.Movie> response) {
                movie = response.body();
                loadMovieDetailsIntoViews(movie);
                viewModel.isFavorite(movie).observe(DetailsActivity.this, observer);
            }

            @Override
            public void onFailure(Call<MovieResult.Movie> call, Throwable t) {
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
    private void loadMovieDetailsIntoViews(MovieResult.Movie movie) {
        float ratingVal = movie.getMovieRating()/2;
        String posterUrl = MovieApiRetrofitInterface.IMG_BASE_URL
                +MovieApiRetrofitInterface.IMG_POSTER_SIZE
                + movie.getPosterPath();
        String backdropUrl = MovieApiRetrofitInterface.IMG_BASE_URL
                + MovieApiRetrofitInterface.IMG_BKDROP_SIZE
                + movie.getBackdropPath();

        setTitle(movie.getMovieTitle());

        binding.dtlMovieTitleTv.setText(movie.getMovieTitle());
        binding.ratingTv.setText(String.valueOf(ratingVal));
        binding.ratingBar.setVisibility(View.VISIBLE);
        binding.ratingBar.setRating(ratingVal);
        binding.taglineTv.setText(movie.getTagline());
        binding.languageTv.setText(movie.getLanguage());
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

    private void setFavoriteIcon(boolean isFavorite) {
        int imgId = isFavorite ? R.drawable.ic_baseline_favorite_filled : R.drawable.ic_baseline_favorite_border;
        binding.favoriteIconImg.setBackgroundResource(imgId);
    }

    private void loadTrailers(final int movieId) {
        //  https://api.themoviedb.org/3/movie/{movie_id}/videos?api_key={api_key}
        TrailerAdapter.TrailerAdapterOnClickHandler clickHandler = new TrailerAdapter.TrailerAdapterOnClickHandler() {
            @Override
            public void onItemClick(String trailerKey) {
                String videoUrl = "https://www.youtube.com/watch?v=" + trailerKey;

                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                PackageManager manager = DetailsActivity.this.getPackageManager();

                if(appIntent.resolveActivity(manager) != null) {
                    startActivity(appIntent);
                } else {
                    Toast.makeText(DetailsActivity.this,
                            "No app available for showing trailer video",
                            Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "No app available for showing trailer video");
                }

            }
        };
        final TrailerAdapter adapter = new TrailerAdapter(clickHandler);
        binding.trailers.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(DetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.trailers.setLayoutManager(layoutManager);

        Call<TrailerResult> call = ApiClient.getApiInterface().getMovieTrailers(movieId, API_KEY);
        call.enqueue(new Callback<TrailerResult>() {
            @Override
            public void onResponse(Call<TrailerResult> call, Response<TrailerResult> response) {
                TrailerResult result = response.body();
                adapter.setData(result.getTrailersList());
                //Log.d(LOG_TAG, "Items = " + result.getTrailersList().size());
            }

            @Override
            public void onFailure(Call<TrailerResult> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Error getting movie trailers", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error getting movie details " + movieId
                        + ". \nERROR : " + t.getMessage());
            }
        });
    }

    private void loadReviews(final int movieId) {
        final ReviewAdapter adapter = new ReviewAdapter();
        binding.reviews.setAdapter(adapter);
        binding.reviews.setLayoutManager(
                new LinearLayoutManager(DetailsActivity.this));

        Call<ReviewResult> call = ApiClient.getApiInterface().getMovieReviews(movieId, API_KEY);
        call.enqueue(new Callback<ReviewResult>() {
            @Override
            public void onResponse(Call<ReviewResult> call, Response<ReviewResult> response) {
                ReviewResult result = response.body();
                adapter.setData(result.getReviews());
            }

            @Override
            public void onFailure(Call<ReviewResult> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Error getting movie reviews", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error getting movie details " + movieId
                        + ". \nERROR : " + t.getMessage());
            }
        });
    }
}
