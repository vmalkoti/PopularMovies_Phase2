package com.example.malkoti.popularmovies.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.support.design.widget.BottomNavigationView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.malkoti.popularmovies.BuildConfig;
import com.example.malkoti.popularmovies.FavoritesViewModel;
import com.example.malkoti.popularmovies.NetworkStateChangeReceiver;
import com.example.malkoti.popularmovies.R;
import com.example.malkoti.popularmovies.adapters.MoviesAdapter;
import com.example.malkoti.popularmovies.databinding.ActivityMainBinding;
import com.example.malkoti.popularmovies.model.MovieResult;
import com.example.malkoti.popularmovies.network.ApiClient;

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
    Observer<List<MovieResult.Movie>> liveDataObserver = new Observer<List<MovieResult.Movie>>() {
        @Override
        public void onChanged(@Nullable List<MovieResult.Movie> movies) {
            mAdapter.changeData(movies);
        }
    };
    LiveData<List<MovieResult.Movie>> favoritesLiveData;
    LiveData<List<MovieResult.Movie>> moviesLiveData;

    private int recyclerPosition = 0;
    private int selectedVisibleTab = -1;
    private boolean forceLoad = false;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String apiKey = BuildConfig.apiKey;

    private ActivityMainBinding binding;

    private NetworkStateChangeReceiver networkReciever;
    private NetworkStateChangeReceiver.ConnectionChangeHandler connectionChangeHandler;
    private boolean networkOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Stetho to view database
        //Stetho.initializeWithDefaults(MainActivity.this);

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
                if(findViewById(group.getCheckedRadioButtonId()).isPressed()) {
                    // fire only if the radio button was selected manually by user
                    // and not from code selection
                    Log.d(LOG_TAG, "Checked change listener");
                    loadMovieList(checkedId);
                }
            }
        });



        binding.bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(LOG_TAG, "Triggered nav option " + item.getItemId());
                loadAppScreen(item.getItemId());
                return true;
            }
        });

        /*
        Display display = getWindowManager().getDefaultDisplay();
        Point point =  new Point();
        display.getSize(point);
        int widthPx = point.x;
        binding.bottomNavBar.setMinimumWidth(widthPx);
        */

        binding.searchMovieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchField =  findViewById(R.id.search_movie_tv);
                String searchText = searchField.getText().toString();
                searchMovies(searchText);
                viewModel.setSearchKeywords(searchText);
            }
        });

        connectionChangeHandler = new NetworkStateChangeReceiver.ConnectionChangeHandler() {
            @Override
            public void performAction(boolean isConnected) {
                networkOnline = isConnected;
                Toast.makeText(MainActivity.this, "Network connected " + isConnected, Toast.LENGTH_SHORT).show();
            }
        };
        networkReciever = new NetworkStateChangeReceiver(connectionChangeHandler);
        /*
        if(savedInstanceState != null) {
            Parcelable parcel = savedInstanceState.getParcelable("ListState");
            binding.recyclerView.getLayoutManager().onRestoreInstanceState(parcel);
        }
        */


        viewModel = ViewModelProviders.of(MainActivity.this).get(FavoritesViewModel.class);
        favoritesLiveData = viewModel.getFavoriteMovies();
        moviesLiveData = viewModel.getMovies();
        /*
        moviesLiveData.observe(MainActivity.this, liveDataObserver);
        */
        Log.d(LOG_TAG, "Default radio button option " + binding.tabButtons.getCheckedRadioButtonId());
        //binding.tabButtons.check(R.id.most_popular_btn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_app_info_action:
                showAppCredits();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ListState", binding.recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putInt("BottomNavOption", binding.bottomNavBar.getSelectedItemId());
        outState.putInt("TabOption", binding.tabButtons.getCheckedRadioButtonId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "In onRestoreInstanceState ");

        if(savedInstanceState != null) {
            int navId = savedInstanceState.getInt("BottomNavOption");
            int tabId = savedInstanceState.getInt("TabOption");
            Log.d(LOG_TAG, "Saved IDs " + navId + ", " + tabId);

            binding.bottomNavBar.setSelectedItemId(navId);
            if(navId == R.id.search_movie_action) {
                Log.d(LOG_TAG, "Selected Movie tab");
                loadMovieList(tabId);
            } else if(navId == R.id.view_favorites_action) {
                Log.d(LOG_TAG, "Selected Favorites tab");
                favoritesLiveData.observe(MainActivity.this, liveDataObserver);
            }
        } else {
            Log.d(LOG_TAG, "No Saved IDs");
            // Default selection is Most Popular
            binding.tabButtons.check(R.id.most_popular_btn);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkReciever, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReciever);
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

        Log.d(LOG_TAG, "Loading movie list from top radio buttons");

        switch (optionSelected) {
            case R.id.most_popular_btn:
                selectedVisibleTab = binding.tabButtons.getCheckedRadioButtonId();
                call = ApiClient.getApiInterface().getPopularMovies(apiKey);
                viewModel.setMovieType(FavoritesViewModel.MovieListTypes.POPULAR);
                break;
            case R.id.top_rated_btn:
                selectedVisibleTab = binding.tabButtons.getCheckedRadioButtonId();
                call = ApiClient.getApiInterface().getTopRatedMovies(apiKey);
                viewModel.setMovieType(FavoritesViewModel.MovieListTypes.TOP_RATED);
                break;
            case R.id.upcoming_btn:
                selectedVisibleTab = binding.tabButtons.getCheckedRadioButtonId();
                call = ApiClient.getApiInterface().getUpcomingMovies(apiKey);
                viewModel.setMovieType(FavoritesViewModel.MovieListTypes.UPCOMING);
                break;
            case R.id.now_playing_btn:
                selectedVisibleTab = binding.tabButtons.getCheckedRadioButtonId();
                call = ApiClient.getApiInterface().getNowPlayingMovies(apiKey);
                viewModel.setMovieType(FavoritesViewModel.MovieListTypes.NOW_PLAYING);
                break;
            default:
                selectedVisibleTab = binding.tabButtons.getCheckedRadioButtonId();
                mAdapter.changeData(null);
                return;
                //call = ApiClient.getApiInterface().getPopularMovies(apiKey);
                //viewModel.setMovieType(FavoritesViewModel.MovieListTypes.EMPTY);
                //break;
        }

        Log.d(LOG_TAG, "Loading online movie list");

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
        Log.d(LOG_TAG, "in loadAppScreen with option " + menuOptionSelected);

        switch (menuOptionSelected) {
            case R.id.view_movies_action:
                showSearchFields(false);
                favoritesLiveData.removeObserver(liveDataObserver);
                //binding.recyclerView.setVisibility(View.VISIBLE);
                binding.tabButtons.setVisibility(View.VISIBLE);
                loadMovieList(binding.tabButtons.getCheckedRadioButtonId());
                Log.d(LOG_TAG, "Checked item is " + binding.tabButtons.getCheckedRadioButtonId());
                setTitle(getString(R.string.movies_menu));
                break;
            case R.id.search_movie_action:
                showSearchFields(true);
                favoritesLiveData.removeObserver(liveDataObserver);
                /*
                viewModel.setMovieType(FavoritesViewModel.MovieListTypes.EMPTY);
                 */
                //binding.recyclerView.setVisibility(View.INVISIBLE);
                mAdapter.changeData(null);
                binding.tabButtons.setVisibility(View.GONE);
                setTitle(getString(R.string.search_menu));
                break;
            case R.id.view_favorites_action:
                // code to add observer and show favorites
                showSearchFields(false);
                //binding.recyclerView.setVisibility(View.VISIBLE);
                binding.tabButtons.setVisibility(View.GONE);
                mAdapter.changeData(null);
                favoritesLiveData.observe(MainActivity.this, liveDataObserver);
                /*
                viewModel.setMovieType(FavoritesViewModel.MovieListTypes.FAVORITES);
                 */
                setTitle(getString(R.string.favorites_menu_item));
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

            viewModel.setSearchKeywords(keywords);
            viewModel.setMovieType(FavoritesViewModel.MovieListTypes.SEARCH);

            /* Won't need anything below this */
            Call<MovieResult> call = ApiClient.getApiInterface().getMovieSearchResults(apiKey, keywords);
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
        intent.putExtra(DetailsActivity.MOVIE_ID_KEY, movie.getMovieId());
        intent.putExtra(DetailsActivity.MOVIE_KEY, movie);
        startActivity(intent);
    }

}