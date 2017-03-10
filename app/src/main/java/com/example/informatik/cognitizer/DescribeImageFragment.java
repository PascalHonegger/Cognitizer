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
import android.widget.ListView;
import android.widget.TextView;

import com.example.informatik.cognitizer.Tasks.DescribeImageTask;
import com.example.informatik.cognitizer.helper.ExceptionHandler;
import com.example.informatik.cognitizer.helper.ImageHelper;
import com.example.informatik.cognitizer.helper.UserFeedbackHelper;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;

import java.util.ArrayList;


public class DescribeImageFragment extends ImageUsingFragmentBase {
    // The image selected to detect.
    private Bitmap mBitmap;

    // The edit to show status and result.
    private ListView listView;

    private TextView descriptionBox;

    private ImageView imageView;

    private VisionServiceClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (client == null) {
            client = new VisionServiceRestClient(getString(R.string.computerVisionKey));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_describe_image, container, false);

        setupViewEvents(view);

        listView = (ListView) view.findViewById(R.id.tagResultList);
        descriptionBox = (TextView) view.findViewById(R.id.imageDescription);
        imageView = (ImageView) view.findViewById(R.id.selectedImage);

        return view;
    }

    public void doAnalyze() {
        final ProgressDialog dialog = UserFeedbackHelper.showProgress(getContext(), getString(R.string.analysing_image));

        new DescribeImageTask(client, new DescribeImageTask.PostExecuteCallback() {
            @Override
            public void onSuccess(AnalysisResult result) {
                dialog.dismiss();
                // Construct the data source
                ArrayList<Tag> arrayOfTags = new ArrayList<>();

                for (String tag : result.description.tags) {
                    arrayOfTags.add(new Tag(tag));
                }

                // Create the adapter to convert the array to views
                CustomAdapter adapter = new CustomAdapter(getContext(), arrayOfTags);
                // Attach the adapter to a ListView
                listView.setAdapter(adapter);

                Caption thisCaption = result.description.captions.get(0);

                descriptionBox.setText(thisCaption.text + " with confidence of " + thisCaption.confidence * 100 + "%");
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                ExceptionHandler.handleException(getContext(), e);
            }
        }).execute(mBitmap);
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

            doAnalyze();
        }
    }

}
