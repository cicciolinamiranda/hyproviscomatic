package com.uprise.ordering.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class HorizontalListView extends HorizontalScrollView {

    private String TAG = "HorizontalListView";
    private ViewGroup mContainer = null;
    private Context mContext = null;
    private ListAdapter mAdapter = null;
    private OnListItemClickListener mListItemClickListener = null;

    /**
     * OnListItemClickListener interface
     * Interface definition for a callback to be invoked when a list item is clicked.
     */
    public interface OnListItemClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         * @param position The position if the item that was clicked
         */
        void onClick(View v, int position);
    }

    /**
     * Register a listener for list item click.
     * @param listItemClickListener
     */
    public void registerListItemClickListener(OnListItemClickListener listItemClickListener) {
        mListItemClickListener = listItemClickListener;
    }

    /**
     * Custom OnClickListener for list item
     */
    public class CustomOnClickListener implements OnClickListener {
        private int mPosition;
        public CustomOnClickListener(int position) {
            mPosition = position;
        }
        @Override
        public void onClick(View v) {
            if (mListItemClickListener != null) {
                mListItemClickListener.onClick(v, mPosition);
            }
        }
    }

    /**
     * HorizontalListView constructor.
     * @param context
     * @param attributeSet
     */
    public HorizontalListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContext = context;
        // Init child view
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mContainer = container;
       addView(mContainer);

        // Remove horizontal scrollbar
        setHorizontalScrollBarEnabled(false);
    }

    /**
     * Set the adapter which will be used to build the list item views.
     * @param adapter
     */
    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;

        if (getChildCount() == 0 || adapter == null)
            return;

        mContainer.removeAllViews();

        for (int i = 0; i < adapter.getCount(); i++) {
            View v = adapter.getView(i, null, mContainer);
            if (v != null) {
                 v.setOnClickListener(new CustomOnClickListener(i));
                mContainer.addView(v);
            }
        }
    }
}