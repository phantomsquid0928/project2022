package com.squid0928.project.fragments;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squid0928.project.R;
import com.squid0928.project.placeholder.PlaceholderContent.PlaceholderItem;
import com.squid0928.project.databinding.FragmentFriendTabBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    private OnItemClicked onClick;

    public interface OnItemClicked {
        void onItemClick(View v, int position);
    }

    public void setClickListener(OnItemClicked itemClicked) {
        this.onClick = itemClicked;
    }

    public MyItemRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentFriendTabBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        /*Bitmap bitmap = mValues.get(position).userImage;
        if (bitmap == null) {
            //add Default bitmap or change bitmap to other class
        }*/
        //holder.mPhotoView.setImageBitmap(bitmap);
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //public final ImageView mPhotoView;
        public final TextView mIdView;
        public final TextView mContentView;
        public PlaceholderItem mItem;

        public ViewHolder(FragmentFriendTabBinding binding) {
            super(binding.getRoot());
            binding.getRoot().getRootView().setOnClickListener(this);
            //mPhotoView = binding.itemPhoto;
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            //binding.getRoot().setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onClick(View view) {
            if (onClick != null) onClick.onItemClick(view, getBindingAdapterPosition());
        }
    }
}