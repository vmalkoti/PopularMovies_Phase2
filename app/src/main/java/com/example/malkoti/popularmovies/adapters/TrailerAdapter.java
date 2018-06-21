package com.example.malkoti.popularmovies.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.malkoti.popularmovies.R;
import com.example.malkoti.popularmovies.model.TrailerResult.Trailer;

import com.example.malkoti.popularmovies.databinding.TrailerItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private TrailerAdapterOnClickHandler mHandler;
    private List<Trailer> mTrailers;

    /**
     * Interface for handling click events
     */
    public interface TrailerAdapterOnClickHandler {
        void onItemClick(String trailerKey);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler handler) {
        this.mHandler = handler;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        TrailerItemBinding binding = TrailerItemBinding.inflate(inflater, viewGroup, false);
        return new TrailerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = mTrailers.get(position);
        holder.bind(trailer);
    }

    @Override
    public int getItemCount() {
        return mTrailers==null ? 0 : mTrailers.size();
    }

    /**
     * Swap data for adapter and refresh
     * @param data
     */
    public void setData(List<Trailer> data) {
        this.mTrailers = data;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for videos
     */
    class TrailerViewHolder extends RecyclerView.ViewHolder {
        private TrailerItemBinding mBinding;

        public TrailerViewHolder(TrailerItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        /**
         * Bind viewHolder to a trailer
         * @param trailer
         */
        public void bind(final Trailer trailer) {
            String thumbnailUrl = "https://img.youtube.com/vi/" + trailer.getKey() + "/default.jpg";

            Picasso.get()
                    .load(thumbnailUrl)
                    .placeholder(R.mipmap.backdrop_placeholder)
                    .error(R.mipmap.backdrop_placeholder)
                    .into(mBinding.trailerImg);

            mBinding.trailerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.onItemClick(trailer.getKey());
                }
            });

            mBinding.executePendingBindings();
        }
    }
}
