package com.example.malkoti.popularmovies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.malkoti.popularmovies.BuildConfig;
import com.example.malkoti.popularmovies.data.FavoritesViewModel;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity
        implements TrailerAdapter.TrailerAdapterOnClickHandler {
    public static final String MOVIE_KEY = "MOVIE";
    private String TRAILERS_STATE = "TrailerSavedState";
    private String REVIEWS_STATE = "ReviewSavedState";

    private final String LOG_TAG = DetailsActivity.class.getSimpleName();
    private final String API_KEY = BuildConfig.apiKey;

    ActivityDetailsBinding binding;
    private MovieResult.Movie movie;
    private boolean isFavorite;

    private ShareActionProvider mShareActionProvider;

    private TrailerAdapter trailerAdapter = new TrailerAdapter(this);
    private ReviewAdapter reviewAdapter = new ReviewAdapter();

    private FavoritesViewModel viewModel;
    private Observer<Integer> favoriteObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer value) {
            isFavorite = (value > 0);
            setFavoriteIcon(isFavorite);
        }
    };
    private Observer<TrailerResult> trailerObserver = new Observer<TrailerResult>() {
        @Override
        public void onChanged(@Nullable TrailerResult trailerResult) {
            List<TrailerResult.Trailer> trailers = trailerResult.getTrailersList();
            trailerAdapter.setData(trailers);
            if(trailers.size() > 0) {
                binding.trailersTv.setVisibility(View.VISIBLE);
            }
        }
    };
    private Observer<ReviewResult> reviewObserver = new Observer<ReviewResult>() {
        @Override
        public void onChanged(@Nullable ReviewResult reviewResult) {
            List<ReviewResult.Review> reviews = reviewResult.getReviews();
            reviewAdapter.setData(reviews);
            if(reviews.size() > 0) {
                binding.reviewsTv.setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        movie = getIntent().getParcelableExtra(MOVIE_KEY);
        loadMovieDetailsIntoViews(movie);

        viewModel = ViewModelProviders.of(DetailsActivity.this).get(FavoritesViewModel.class);
        viewModel.isFavorite(movie).observe(DetailsActivity.this, favoriteObserver);

        /* Trailers and Reviews using viewModel -- START */
        binding.trailers.setAdapter(trailerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(DetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.trailers.setLayoutManager(layoutManager);
        viewModel.getTrailers(movie.getMovieId()).observe(DetailsActivity.this, trailerObserver);

        binding.reviews.setAdapter(reviewAdapter);
        binding.reviews.setLayoutManager(
                new LinearLayoutManager(DetailsActivity.this));
        viewModel.getReviews(movie.getMovieId()).observe(DetailsActivity.this, reviewObserver);
        /* Trailers and Reviews using viewModel -- END */

        //loadTrailers(movie.getMovieId());
        //loadReviews(movie.getMovieId());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        MenuItem item = menu.findItem(R.id.share_movie_action);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareSubject = getString(R.string.share_action_subject);
        StringBuilder builder = new StringBuilder();
        builder.append(movie.getMovieTitle());
        builder.append("\n\n");
        builder.append(movie.getOverview());
        builder.append("\n\n");
        builder.append(getString(R.string.share_action_footer));
        builder.append(getString(R.string.play_store_url));
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, builder.toString());

        mShareActionProvider.setShareIntent(sharingIntent);

        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRAILERS_STATE, binding.trailers.getLayoutManager().onSaveInstanceState());
        outState.putParcelable(REVIEWS_STATE, binding.reviews.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Doesn't seem to restore recyclerview scroll state
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "In onRestoreInstanceState ");

        if(savedInstanceState != null) {
            Parcelable trailersParcelable = savedInstanceState.getParcelable(TRAILERS_STATE);
            binding.trailers.getLayoutManager().onRestoreInstanceState(trailersParcelable);
            Parcelable reviewsParcelable = savedInstanceState.getParcelable(REVIEWS_STATE);
            binding.reviews.getLayoutManager().onRestoreInstanceState(reviewsParcelable);
        }

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
        binding.languageTv.setText(movie.getLanguage().toUpperCase());
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

    /* Not needed anymore
     * Getting data using viewmodel now
     */
    /*
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
                Log.d(LOG_TAG, "Trailer call url " + call.request().url());
                TrailerResult result = response.body();
                List<TrailerResult.Trailer> trailers = result.getTrailersList();
                adapter.setData(trailers);
                if(trailers.size() > 0) {
                    binding.trailersTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<TrailerResult> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Error getting movie trailers", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error getting movie details " + movieId
                        + ". \nERROR : " + t.getMessage());
            }
        });
    }
    */

    /* Not needed anymore
     * Getting data using viewmodel now
     */
    /*
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
                List<ReviewResult.Review> reviews = result.getReviews();
                adapter.setData(reviews);
                if(reviews.size() > 0) {
                    binding.reviewsTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ReviewResult> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Error getting movie reviews", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error getting movie details " + movieId
                        + ". \nERROR : " + t.getMessage());
            }
        });
    }
    */


}
