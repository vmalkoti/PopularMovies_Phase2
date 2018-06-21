package com.example.malkoti.popularmovies.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.malkoti.popularmovies.databinding.ReviewItemBinding;
import com.example.malkoti.popularmovies.model.ReviewResult;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<ReviewResult.Review> mReviews;

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ReviewItemBinding binding = ReviewItemBinding.inflate(inflater, viewGroup, false);
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewResult.Review review = mReviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return mReviews==null? 0 : mReviews.size();
    }

    /**
     * Swap data for adapter and refresh
     * @param data
     */
    public void setData(List<ReviewResult.Review> data) {
        this.mReviews = data;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for movie reviews
     */
    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private ReviewItemBinding binding;

        public ReviewViewHolder(ReviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ReviewResult.Review review) {
            binding.reviewAuthorTv.setText(review.getAuthor());
            binding.reviewContentTv.setText(review.getContent());
            binding.executePendingBindings();
        }
    }
}
