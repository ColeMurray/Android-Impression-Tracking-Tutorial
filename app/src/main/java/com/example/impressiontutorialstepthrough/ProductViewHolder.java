package com.example.impressiontutorialstepthrough;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by User on 1/18/16.
 */
public class ProductViewHolder extends RecyclerView.ViewHolder {
    public final TextView mTitleTextView;

    public ProductViewHolder(View itemView) {
        super(itemView);
        mTitleTextView = (TextView) itemView.findViewById(R.id.title_textview);
    }
}