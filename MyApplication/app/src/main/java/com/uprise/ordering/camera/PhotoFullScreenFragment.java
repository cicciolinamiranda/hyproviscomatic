package com.uprise.ordering.camera;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uprise.ordering.R;
import com.uprise.ordering.rest.service.RestCallServices;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

/**
 * Created by cicciolina on 10/16/16.
 */
public class PhotoFullScreenFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String RESULT_CODE = "resultCode";

    // TODO: Rename and change types of parameters
    public static String imageUri;
    public static int imageIndex;
    private int resultCode;

    private OnFragmentInteractionListener mListener;

    private OnDeletePhotoListener deletePhotoListener;
    public interface OnDeletePhotoListener{
        public void onDeletePhoto(boolean deleted, String imgUri);
    }

    public PhotoFullScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoFullScreenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoFullScreenFragment newInstance(int param1, String param2, int resultCode) {
        PhotoFullScreenFragment fragment = new PhotoFullScreenFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(RESULT_CODE, resultCode);
        fragment.setArguments(args);
        return fragment;
    }

    public OnDeletePhotoListener getDeletePhotoListener() {
        return deletePhotoListener;
    }

    public void setDeletePhotoListener(OnDeletePhotoListener deletePhotoListener) {
        this.deletePhotoListener = deletePhotoListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageIndex = getArguments().getInt(ARG_PARAM1);
            String s = getArguments().getString(ARG_PARAM2);
            imageUri = s;//"wasabi_temp" + s + ".jpg";
            resultCode = getArguments().getInt(RESULT_CODE);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_full_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageViewTouch imageViewTouch = (ImageViewTouch) view.findViewById(R.id.image);
        imageViewTouch.setImageBitmap(RestCallServices.getBitmapFrom(imageUri, resultCode));
        imageViewTouch.setDisplayType(DisplayType.FIT_TO_SCREEN);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_photo, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


}
