package com.example.guohouxiao.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by guohouxiao on 2017/4/24.
 */

public class PhotoFragment extends DialogFragment {

    public static final String EXTRA_PHOTO_PATH = "com.example.guohouxiao.criminalintent.photo_path";
    private ImageView mPhotoView;

    //private ViewTreeObserver mTreeObserver;

    public static PhotoFragment newInstance(String photoPath) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PHOTO_PATH, photoPath);

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo,null);

        final Dialog dialog = new Dialog(getActivity(),R.style.CustomDialogTheme);

        dialog.setContentView(v);

        mPhotoView = (ImageView) v.findViewById(R.id.dialog_crime_photo);

/*        mTreeObserver = mPhotoView.getViewTreeObserver();
        mTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                String path = getArguments().getString(EXTRA_PHOTO_PATH);
                Bitmap bitmap = PictureUtils.getScaledBitmap(path, getActivity());
                mPhotoView.setImageBitmap(bitmap);

                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });*/

        String path = getArguments().getString(EXTRA_PHOTO_PATH);
        Bitmap bitmap = PictureUtils.getScaledBitmap(path, getActivity());
        mPhotoView.setImageBitmap(bitmap);

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

       // return new AlertDialog.Builder(getActivity()).setView(v).create();
        return dialog;
    }
}
