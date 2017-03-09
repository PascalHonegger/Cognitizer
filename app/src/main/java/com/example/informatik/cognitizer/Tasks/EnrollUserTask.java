package com.example.informatik.cognitizer.Tasks;

import android.os.AsyncTask;

import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationClient;
import com.microsoft.cognitive.speakerrecognition.contract.EnrollmentStatus;
import com.microsoft.cognitive.speakerrecognition.contract.identification.EnrollmentOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.OperationLocation;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

public class EnrollUserTask extends AsyncTask<File, Void, EnrollUserTaskResult> {

    private static UUID createUserUUID;

    private Exception exception;
    private SpeakerIdentificationClient speakerIdentificationClient;

    public EnrollUserTask(SpeakerIdentificationClient speakerIdentificationClient)
    {
        this.speakerIdentificationClient = speakerIdentificationClient;
    }

    @Override
    protected EnrollUserTaskResult doInBackground(File... params) {
        EnrollUserTaskResult result;

        try {
            //Create a new user if the register button is pressed for the first time
            if(createUserUUID == null) {
                createUserUUID = speakerIdentificationClient.createProfile("en-US").identificationProfileId;
            }

            OperationLocation enrollResult = speakerIdentificationClient.enroll(new FileInputStream(params[0]), createUserUUID, true);

            EnrollmentOperation enrollmentStatusResult = speakerIdentificationClient.checkEnrollmentStatus(enrollResult);

            EnrollmentStatus status = enrollmentStatusResult.processingResult != null ? enrollmentStatusResult.processingResult.enrollmentStatus : null;
            result = new EnrollUserTaskResult(status);
        } catch (Exception e) {
            this.exception = e;

            result = new EnrollUserTaskResult(e);
        }

        return result;
    }

    protected void onPostExecute(EnrollUserTaskResult result) {
        // TODO: check this.exception
        // TODO: do something with the feed
        if(exception != null) {
            //TODO Exception-Handler?
            exception.printStackTrace();
        }
    }
}
