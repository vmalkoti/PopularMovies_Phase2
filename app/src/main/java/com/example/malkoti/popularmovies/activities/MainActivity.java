package com.example.malkoti.popularmovies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.support.design.widget.BottomNavigationView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.malkoti.popularmovies.BuildConfig;
import com.example.malkoti.popularmovies.FavoritesViewModel;
import com.example.malkoti.popularmovies.R;
import com.example.malkoti.popularmovies.adapters.MoviesAdapter;
import com.example.malkoti.popularmovies.databinding.ActivityMainBinding;
import com.example.malkoti.popularmovies.model.MovieResult;
import com.example.malkoti.popularmovies.network.ApiClient;
import com.facebook.stetho.Stetho;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Main or Home screen activity for the app.
 * Shows a grid of movie posters as thumbnails.
 * Clicking on a thumbnail shows its details.
 */
public class MainActivity
        extends AppCompatActivity
        implements MoviesAdapter.MovieAdapterOnClickHandler {

    private MoviesAdapter mAdapter;

    private FavoritesViewModel viewModel;

    int recyclerPosition = 0;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String apiKey = BuildConfig.apiKey;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        int orientation = getResources().getConfiguration().orientation;
        int gridColumns = orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;

        showSearchFields(false);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, gridColumns));
        mAdapter = new MoviesAdapter(this);
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.setHasFixedSize(true);


        binding.tabButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                loadMovieList(checkedId);
            }
        });

        binding.bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                loadAppScreen(item.getItemId());
                return true;
            }
        });

        binding.searchMovieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchField =  findViewById(R.id.search_movie_tv);
                String searchText = searchField.getText().toString();
                searchMovies(searchText);
            }
        });

        // Default selection is Most Popular
        binding.tabButtons.check(R.id.most_popular_btn);

        /*
        if(savedInstanceState != null) {
            Parcelable parcel = savedInstanceState.getParcelable("ListState");
            binding.recyclerView.getLayoutManager().onRestoreInstanceState(parcel);
        }
        */

        viewModel = ViewModelProviders.of(MainActivity.this).get(FavoritesViewModel.class);
        Observer<List<MovieResult.Movie>> observer = new Observer<List<MovieResult.Movie>>() {
            @Override
            public void onChanged(@Nullable List<MovieResult.Movie> movies) {
                mAdapter.changeData(movies);
            }
        };
        /*
        viewModel.getFavoriteMovies().observe(MainActivity.this, observer);
        */

        Stetho.initializeWithDefaults(MainActivity.this);

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("ListState", binding.recyclerView.getLayoutManager().onSaveInstanceState());

    }

    /**
     * Display fields needed to search movies
     * @param show true to show; false to hide fields
     */
    private void showSearchFields(boolean show) {
        int visibility;

        if(!show) {
            visibility = View.GONE;
            binding.searchMovieTv.requestFocus();
        } else {
            visibility = View.VISIBLE;
        }
        binding.searchMovieTv.setVisibility(visibility);
        binding.searchMovieBtn.setVisibility(visibility);
    }


    /**
     * Load list of movies based on radio button selection on top
     * @param optionSelected ID of radio button selected
     */
    private void loadMovieList(final int optionSelected) {
        Call<MovieResult> call;

        switch (optionSelected) {
            case R.id.most_popular_btn:
                call = ApiClient.getApiInterface().getPopularMovies(apiKey);
                break;
            case R.id.top_rated_btn:
                call = ApiClient.getApiInterface().getTopRatedMovies(apiKey);
                break;
            case R.id.upcoming_btn:
                call = ApiClient.getApiInterface().getUpcomingMovies(apiKey);
                break;
            case R.id.now_playing_btn:
                call = ApiClient.getApiInterface().getNowPlayingMovies(apiKey);
                break;
            default:
                call = ApiClient.getApiInterface().getPopularMovies(apiKey);
                break;
        }

        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                MovieResult MovieResult = response.body();
                List<MovieResult.Movie> movies = MovieResult.moviesList;
                mAdapter.changeData(movies);
                binding.recyclerView.smoothScrollToPosition(0);
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "Error getting list of movies from internet " + optionSelected
                        + ". \nERROR : " + t.getMessage());
            }
        });
    }


    /**
     * Load movies based on option selected in Bottom Navigation
     * @param menuOptionSelected ID of item selected
     */
    private void loadAppScreen(int menuOptionSelected) {
        switch (menuOptionSelected) {
            case R.id.view_movies_action:
                showSearchFields(false);
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.tabButtons.setVisibility(View.VISIBLE);
                loadMovieList(binding.tabButtons.getCheckedRadioButtonId());
                break;
            case R.id.search_movie_action:
                showSearchFields(true);
                binding.recyclerView.setVisibility(View.INVISIBLE);
                binding.tabButtons.setVisibility(View.GONE);
                break;
            case R.id.view_app_info_action:
                showAppCredits();
                break;
        }
    }

    /**
     * Search for movies using keywords
     * @param keywords Keywords to pass as query parameter
     */
    private void searchMovies(String keywords) {
        if(keywords != null && !keywords.trim().equals("")) {
            String encodedSearchText = Uri.encode(keywords);
            Log.d(LOG_TAG, "Searching for encoded " + encodedSearchText);

            Call<MovieResult> call = ApiClient.getApiInterface().getMovieResults(apiKey, keywords);
            Log.d(LOG_TAG, "URL formed " + call.request().url().toString());

            call.enqueue(new Callback<MovieResult>() {
                @Override
                public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                    MovieResult MovieResult = response.body();
                    List<MovieResult.Movie> movies = MovieResult.moviesList;  // pass it to mAdapter
                    mAdapter.changeData(movies);
                    if(movies != null && movies.size()>0) {
                        binding.recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        binding.recyclerView.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<MovieResult> call, Throwable t) {
                    Toast.makeText(MainActivity.this, R.string.error_message,
                            Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, R.string.error_message
                            + ". \nERROR : " + t.getMessage());
                }
            });
        }
    }

    /**
     * Show app credits - TMDB specifically, per API agreement
     */
    private void showAppCredits() {
        String dialogTitle = "Credits";
        String dialogContent = getString(R.string.disclaimer_text);
        String dialogButtonText = "OK";

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
        dialog.setTitle(dialogTitle);
        dialog.setMessage(dialogContent);
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, dialogButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Method implemented for onclick interface
     * @param movie Movie object of item clicked
     */
    public void onItemClick(MovieResult.Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.MOVIE_INTENT_KEY, movie.getMovieId());
        startActivity(intent);
    }

}