package com.example.expensestracker;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class verticalSpacingitemDecoration extends RecyclerView.ItemDecoration {
    private final int verticalSpacing;

    public verticalSpacingitemDecoration(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;

    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.top = verticalSpacing; // Adjust the spacing between items
    }

}
