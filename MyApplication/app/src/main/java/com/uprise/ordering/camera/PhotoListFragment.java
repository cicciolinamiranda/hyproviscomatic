package com.uprise.ordering.camera;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uprise.ordering.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cicciolina on 10/16/16.
 */
public class PhotoListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String RESULT_CODE = "resultCode";

    // TODO: Rename and change types of parameters
    private List<String> mImages;
    private String mParam2;
    private int resultCode;

    private OnFragmentInteractionListener mListener;
    PhotosAdapter pAdapter;

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param images Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoListFragment newInstance(List<String> images, String param2, int resultCode) {
        PhotoListFragment fragment = new PhotoListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, new ArrayList<>(images));
        args.putString(ARG_PARAM2, param2);
        args.putInt(RESULT_CODE, resultCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImages = getArguments().getStringArrayList(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            resultCode = getArguments().getInt(RESULT_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rView = (RecyclerView) view.findViewById(R.id.photoGrid);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new GridLayoutManager(getActivity(), 4));

        pAdapter = new PhotosAdapter(getActivity(), R.layout.photo_grid_item,
                mImages, resultCode);
        pAdapter.setItemClickedListener(new OnItemClickedListener() {
            @Override
            public void onClick(int position) {
                replaceFragment(PhotoFullScreenFragment.newInstance(position, mImages.get(position), resultCode));
            }
        });

        rView.setAdapter(pAdapter);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(),
                R.dimen.grid_item_offset);
        rView.addItemDecoration(itemDecoration);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.see_all_container, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mImages = ((CameraImageActivity) getActivity()).getImages();
        pAdapter.notifyDataSetChanged();
    }
}
