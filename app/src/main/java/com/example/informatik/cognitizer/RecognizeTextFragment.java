package com.example.informatik.cognitizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.informatik.cognitizer.Tasks.RecognizeTextTask;
import com.example.informatik.cognitizer.helper.ExceptionHandler;
import com.example.informatik.cognitizer.helper.ImageHelper;
import com.example.informatik.cognitizer.helper.UserFeedbackHelper;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;

public class RecognizeTextFragment extends ImageUsingFragmentBase {
    // The image selected to detect.
    private Bitmap mBitmap;

    // The edit to show status and result.
    private TextView resultText;

    private ImageView imageView;

    private VisionServiceClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (client==null){
            client = new VisionServiceRestClient(getString(R.string.computerVisionKey));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_recognize_text, container, false);

        setupViewEvents(view);

        resultText = (TextView)view.findViewById(R.id.resultText);
        imageView = (ImageView)view.findViewById(R.id.selectedImage);

        return view;
    }

    // Called when image selection is done.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If image is selected successfully, set the image URI and bitmap.
        mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                mUriPhotoTaken, getContext().getContentResolver());
        if (mBitmap != null) {
            // Show the image on screen.
            imageView.setImageBitmap(mBitmap);

            // Add detection log.
            Log.d("AnalyzeActivity", "Image: " + mUriPhotoTaken + " resized to " + mBitmap.getWidth()
                    + "x" + mBitmap.getHeight());

            doRecognize();
        }
    }

    public void doRecognize() {
        ProgressDialog dialog = UserFeedbackHelper.showProgress(getContext(), getString(R.string.analysing_image));

        try {
            resultText.setText(new RecognizeTextTask(client).execute(mBitmap).get());
        } catch (Exception e)
        {
            ExceptionHandler.handleException(getContext(), e);
        } finally {
            dialog.dismiss();
        }
    }

}
