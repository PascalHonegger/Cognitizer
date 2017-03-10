package com.example.informatik.cognitizer.Tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

public class DescribeImageTask extends AsyncTask<Bitmap, Void, AnalysisResult> {
    // Store error message
    private Exception exception = null;
    private VisionServiceClient visionServiceClient;
    private PostExecuteCallback callback;

    public DescribeImageTask(VisionServiceClient visionServiceClient, PostExecuteCallback callback) {
        this.visionServiceClient = visionServiceClient;
        this.callback = callback;
    }

    @Override
    protected AnalysisResult doInBackground(Bitmap... args) {
        try {
            // Put the image into an input stream for detection.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            args[0].compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

            AnalysisResult analysisResult = visionServiceClient.describe(inputStream, 1);

            Log.i("result", analysisResult.description.captions.get(0).text);

            return analysisResult;
        } catch (Exception e) {
            this.exception = e;    // Store error
        }

        return null;
    }

    @Override
    protected void onPostExecute(AnalysisResult data) {
        super.onPostExecute(data);
        if(exception != null) {
            callback.onError(exception);
        } else {
            try {
                callback.onSuccess(get());
            } catch (InterruptedException | ExecutionException e) {
                callback.onError(e);
            }
        }
    }

    public interface PostExecuteCallback {
        void onSuccess(AnalysisResult result);
        void onError(Exception e);
    }
}
