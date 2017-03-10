package com.example.informatik.cognitizer.Tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

/**
 * A Task which tries to recognize text on an image. Calls the {@link VisionServiceClient}.recognizeText and uses a callback to inform you once the analyzation finished
 */
public class RecognizeTextTask extends AsyncTask<Bitmap, Void, String> {
    // Store error message
    private Exception exception = null;
    private VisionServiceClient visionServiceClient;
    private PostExecuteCallback callback;

    public RecognizeTextTask(VisionServiceClient visionServiceClient, PostExecuteCallback callback) {
        this.visionServiceClient = visionServiceClient;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Bitmap... args) {
        try {
            // Put the image into an input stream for detection.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            args[0].compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

            OCR analysisResult = visionServiceClient.recognizeText(inputStream, LanguageCodes.AutoDetect, true);


            String result = "";

            for (Region reg : analysisResult.regions) {
                for (Line line : reg.lines) {
                    for (Word word : line.words) {
                        result += word.text + " ";
                    }
                    result += "\n";
                }
                result += "\n\n";
            }
            Log.d("result", result);

            return result;
        } catch (Exception e) {
            this.exception = e;    // Store error
        }

        return null;
    }

    protected void onPostExecute(String result) {
        if (exception != null) {
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
        void onSuccess(String result);

        void onError(Exception e);
    }
}
