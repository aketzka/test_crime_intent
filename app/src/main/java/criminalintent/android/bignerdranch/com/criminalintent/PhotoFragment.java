package criminalintent.android.bignerdranch.com.criminalintent;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by User on 19.01.2018.
 */

public class PhotoFragment extends DialogFragment {
    private static final String ARG_PHOTO = "com.bignerdranch.android.criminalintent.photo_file";
    ImageView mImageView;
    File mPhotoFile;

    public static PhotoFragment newInstanse(File photoFile){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PHOTO, photoFile);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPhotoFile = (File) getArguments().getSerializable(ARG_PHOTO);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        mImageView = (ImageView) view.findViewById(R.id.photo_imageview);
        mImageView.setImageBitmap(null);
        Dialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mImageView.setImageBitmap(PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity()));
            }
        });
        return dialog;
    }
}
