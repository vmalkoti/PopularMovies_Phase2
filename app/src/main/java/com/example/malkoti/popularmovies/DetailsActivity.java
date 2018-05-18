package com.example.malkoti.popularmovies;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malkoti.popularmovies.model.Movie;
import com.example.malkoti.popularmovies.utils.ApiClient;
import com.example.malkoti.popularmovies.utils.MovieApiRetrofitInterface;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {
    private final String apiKey = BuildConfig.apiKey;

    private final String LOG_TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        int selectedMovieId = getIntent().getIntExtra(MainActivity.MOVIE_INTENT_KEY, 0);;
        loadMovieDetails(selectedMovieId);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // emulate back button pressed behavior to
        // go back to the movies list view that launched details
        if(id==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovieDetails(final int movieId) {
        // URL for details page, in onclick of mAdapter
        // https://api.themoviedb.org/3/movie/{movie_id}?api_key={api_key}
        // To fetch more details use append_to_response
        // https://api.themoviedb.org/3/movie/{movie_id}?api_key={api_key}&append_to_response=videos

        final TextView title = findViewById(R.id.dtl_movie_title_tv);
        final TextView ratingText = findViewById(R.id.rating_tv);
        final RatingBar ratingBar  = findViewById(R.id.rating_bar);
        final TextView tagline = findViewById(R.id.tagline_tv);
        final TextView releaseDate = findViewById(R.id.release_date_tv);
        final TextView summary = findViewById(R.id.summary_tv);
        final ImageView backdrop = findViewById(R.id.dtl_backdrop_img);
        final ImageView poster = findViewById(R.id.dtl_poster_img);

        Call<Movie> call = ApiClient.getApiInterface().getMovieDetails(movieId, apiKey);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                float ratingVal = movie.getRating()/2;
                String posterUrl = MovieApiRetrofitInterface.IMG_BASE_URL
                        +MovieApiRetrofitInterface.IMG_POSTER_SIZE
                        + movie.getPosterPath();
                String backdropUrl = MovieApiRetrofitInterface.IMG_BASE_URL
                        + MovieApiRetrofitInterface.IMG_BKDROP_SIZE
                        + movie.getBackdropPath();

                setTitle(movie.getTitle());

                title.setText(movie.getTitle());
                ratingText.setText(String.valueOf(ratingVal));
                ratingBar.setRating(ratingVal);
                tagline.setText(movie.getTagline());
                releaseDate.setText(movie.getReleaseDate());
                summary.setText(movie.getOverview());

                Picasso.get()
                        .load(posterUrl)
                        .placeholder(R.mipmap.poster_placeholder)
                        .into(poster);

                Picasso.get()
                        .load(backdropUrl)
                        .placeholder(R.mipmap.backdrop_placeholder)
                        .into(backdrop);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Error getting movie details", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error getting movie details " + movieId
                    + ". \nERROR : " + t.getMessage());
            }
        });
    }

}
