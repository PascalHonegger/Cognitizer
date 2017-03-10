package com.example.informatik.cognitizer;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.example.informatik.cognitizer.helper.ExceptionHandler;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public abstract class ImageUsingFragmentBase extends Fragment {
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 1;

    // The URI of the chosen file
    protected Uri mUriPhotoTaken;

    // Deal with the result of selection of the photos and faces.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_TAKE_PHOTO:
            case REQUEST_SELECT_IMAGE_IN_ALBUM:
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {
                        mUriPhotoTaken = data.getData();
                    }
                }
                break;
            default:
                break;
        }
    }

    // When the button of "Take a Photo with Camera" is pressed.
    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Save the photo taken to a temporary file.
            File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File file = File.createTempFile("IMG_", ".jpg", storageDir);
                mUriPhotoTaken = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                ExceptionHandler.handleException(getContext(), e);
            }
        }
    }

    // When the button of "Select a Photo in Album" is pressed.
    public void selectImageInAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM);
        }
    }

    /**
     * set event listener for camara and file-picker buttons
     * @param view current view
     */
    protected void setupViewEvents(View view) {
        view.findViewById(R.id.cameraPicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        view.findViewById(R.id.filePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageInAlbum();
            }
        });
    }
}
