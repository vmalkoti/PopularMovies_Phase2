package com.example.malkoti.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.malkoti.popularmovies.model.Movie;
import com.example.malkoti.popularmovies.utils.MovieApiRetrofitInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

class MoviesAdapter extends Adapter<MoviesAdapter.MovieViewHolder> {
    private List<Movie> mMovies;
    private Context mContext;

    public MoviesAdapter(Context context, List<Movie> movies) {
        this.mContext = context;
        this.mMovies = movies;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.movie_item, parent, false);

        return new MovieViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        String posterUrl = MovieApiRetrofitInterface.IMG_BASE_URL
                + MovieApiRetrofitInterface.IMG_POSTER_SIZE
                + movie.getPosterPath();

        Picasso.get()
                .load(posterUrl)
                .placeholder(R.mipmap.poster_placeholder)
                .into(holder.mPoster);

    }

    @Override
    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

    /**
     * Method to change data in the adapter
     * @param movies List of movie objects to use for RecyclerView
     */
    public void changeData(List<Movie> movies) {
        this.mMovies = movies;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder implementation class for the adapter
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView mPoster;

        public MovieViewHolder(final Context context, View itemView) {
            super(itemView);

            this.mPoster = itemView.findViewById(R.id.poster_img);

            // for click on item
            mPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movie movie = mMovies.get(getAdapterPosition());
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra(MainActivity.MOVIE_INTENT_KEY, movie.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
