package com.example.malkoti.popularmovies;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.malkoti.popularmovies.databinding.MovieItemBinding;
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
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        MovieItemBinding binding = MovieItemBinding.inflate(inflater, viewGroup, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        holder.bind(movie);
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
        //ImageView mPoster;
        private MovieItemBinding binding;

        MovieViewHolder(MovieItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Method to bind viewholder to a Movie object
         * @param movie Movie to be displayed in grid and details
         */
        public void bind(Movie movie) {
            String posterUrl = MovieApiRetrofitInterface.IMG_BASE_URL
                    + MovieApiRetrofitInterface.IMG_POSTER_SIZE
                    + movie.getPosterPath();

            Picasso.get()
                    .load(posterUrl)
                    .placeholder(R.mipmap.poster_placeholder)
                    .into(binding.posterImg);

            binding.posterImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movie movie = mMovies.get(getAdapterPosition());
                    mClickHandler.onItemClick(movie);
                }
            });

            binding.executePendingBindings();
        }
    }
}
