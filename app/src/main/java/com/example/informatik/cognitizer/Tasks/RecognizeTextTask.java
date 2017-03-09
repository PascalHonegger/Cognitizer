package com.example.informatik.cognitizer.Tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class RecognizeTextTask extends AsyncTask<Bitmap, Void, String> {
    // Store error message
    private Exception e = null;
    private VisionServiceClient visionServiceClient;

    public RecognizeTextTask(VisionServiceClient visionServiceClient) {
        this.visionServiceClient = visionServiceClient;
    }

    @Override
    protected String doInBackground(Bitmap... args) {
        try {
            // Put the image into an input stream for detection.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            args[0].compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

            OCR analysisResult = visionServiceClient.recognizeText(inputStream, "en-US", true);


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
            this.e = e;    // Store error
        }

        return null;
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        if (e != null) {
            //TODO Exception handler?
            e.printStackTrace();
        }
    }
}
