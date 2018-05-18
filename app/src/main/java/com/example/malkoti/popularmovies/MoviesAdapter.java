package com.example.malkoti.popularmovies;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.malkoti.popularmovies.model.Movie;
import com.example.malkoti.popularmovies.utils.MovieApiRetrofitInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

class MoviesAdapter extends Adapter<MoviesAdapter.MovieViewHolder> {
    private List<Movie> mMovies;
    private MovieAdapterOnClickHandler mClickHandler;

    /**
     * Interface to allow activities to handle clicks on movie item
     */
    public interface MovieAdapterOnClickHandler {
        void onItemClick(Movie movie);
    }

    public MoviesAdapter(MovieAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.movie_item, viewGroup, false);

        return new MovieViewHolder(view);
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
    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView mPoster;

        MovieViewHolder(View itemView) {
            super(itemView);

            this.mPoster = itemView.findViewById(R.id.poster_img);

            mPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Movie movie = mMovies.get(getAdapterPosition());
                mClickHandler.onItemClick(movie);
                }
            });
        }
    }
}
