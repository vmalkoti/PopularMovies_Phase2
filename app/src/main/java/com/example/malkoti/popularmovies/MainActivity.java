package com.example.malkoti.popularmovies;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.support.design.widget.BottomNavigationView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malkoti.popularmovies.model.Movie;
import com.example.malkoti.popularmovies.model.SearchResult;
import com.example.malkoti.popularmovies.utils.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mMoviesListView;
    private EditText mSearchText;
    private ImageButton mSearchButton;
    private RadioGroup mTabRadioGroup;
    private MoviesAdapter mAdapter;


    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String apiKey = BuildConfig.apiKey;

    public static final String MOVIE_INTENT_KEY = "MOVIE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int orientation = getResources().getConfiguration().orientation;
        int gridColumns = orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;

        mSearchText = findViewById(R.id.search_movie_tv);
        mSearchButton = findViewById(R.id.search_movie_btn);
        mMoviesListView = findViewById(R.id.recyclerView);
        mTabRadioGroup = findViewById(R.id.tab_buttons);

        showSearchFields(false);

        mMoviesListView.setHasFixedSize(true);
        mMoviesListView.setLayoutManager(new GridLayoutManager(MainActivity.this, gridColumns));
        mAdapter = new MoviesAdapter(MainActivity.this, null);
        mMoviesListView.setAdapter(mAdapter);

        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            loadMovieList(checkedId);
            }
        });

        // load when activity starts
        loadMovieList(R.id.most_popular_btn);

        BottomNavigationView bottomNavBar = findViewById(R.id.bottom_nav_bar);
        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            loadAppScreen(item.getItemId());
            return true;
            }
        });

        mSearchButton = findViewById(R.id.search_movie_btn);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchField =  findViewById(R.id.search_movie_tv);
                String searchText = searchField.getText().toString();
                Log.d(LOG_TAG, "Searching for " + searchText);
                searchMovies(searchText);
            }
        });

    }


    /**
     * Display fields needed to search movies
     * @param show true to show; false to hide fields
     */
    private void showSearchFields(boolean show) {
        int visibility;

        if(!show) {
            visibility = View.GONE;
            mSearchText.requestFocus();
        } else {
            visibility = View.VISIBLE;
        }
        mSearchText.setVisibility(visibility);
        mSearchButton.setVisibility(visibility);
    }


    /**
     * Load list of movies based on radio button selection on top
     * @param optionSelected ID of radio button selected
     */
    private void loadMovieList(final int optionSelected) {
        Call<SearchResult> call;

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

        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                SearchResult searchResult = response.body();
                //int page = searchResult.pageNum;
                List<Movie> movies = searchResult.moviesList;
                mAdapter.changeData(movies);
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error getting list of movies from internet", Toast.LENGTH_LONG).show();
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
                mMoviesListView.setVisibility(View.VISIBLE);
                mTabRadioGroup.setVisibility(View.VISIBLE);
                // reload movies list as we are coming from a different screen
                loadMovieList(mTabRadioGroup.getCheckedRadioButtonId());
                break;
            case R.id.search_movie_action:
                showSearchFields(true);
                mMoviesListView.setVisibility(View.INVISIBLE);
                mTabRadioGroup.setVisibility(View.GONE);
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

            Call<SearchResult> call = ApiClient.getApiInterface().getSearchResults(apiKey, keywords);
            Log.d(LOG_TAG, "URL formed " + call.request().url().toString());

            call.enqueue(new Callback<SearchResult>() {
                @Override
                public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                    SearchResult searchResult = response.body();
                    //int page = searchResult.pageNum;
                    List<Movie> movies = searchResult.moviesList;  // pass it to mAdapter
                    mAdapter.changeData(movies);
                    if(movies != null && movies.size()>0) {
                        mMoviesListView.setVisibility(View.VISIBLE);
                    } else {
                        mMoviesListView.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<SearchResult> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error getting search results", Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, "Error getting search results "
                            + ". \nERROR : " + t.getMessage());
                }
            });
        }
    }

    /**
     * Show app credits - TMDB specifically, per API agreement
     * This method currently shows an AlertDialog, but may be changed to future to load a fragment/activity instead
     */
    private void showAppCredits() {
        String dialogTitle = "Credits";
        String dialogContent = "Popular Movies app for Udacity Android Nanodegree course.\n"
                + "This product uses the TMDb API but is not endorsed or certified by TMDb.";
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
}

