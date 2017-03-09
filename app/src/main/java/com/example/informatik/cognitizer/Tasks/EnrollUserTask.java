package com.example.informatik.cognitizer.Tasks;

import android.os.AsyncTask;

import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationClient;
import com.microsoft.cognitive.speakerrecognition.contract.EnrollmentStatus;
import com.microsoft.cognitive.speakerrecognition.contract.identification.EnrollmentOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.OperationLocation;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

import static com.example.informatik.cognitizer.Tasks.Constants.TASK_DELAY;

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

            EnrollmentStatus enrollmentStatus = null;
            com.microsoft.cognitive.speakerrecognition.contract.identification.Status operationStatus = com.microsoft.cognitive.speakerrecognition.contract.identification.Status.NOTSTARTED;
            do {
                //Let Microsoft think before we retry
                Thread.sleep(TASK_DELAY);
                EnrollmentOperation enrollmentStatusResult = speakerIdentificationClient.checkEnrollmentStatus(enrollResult);

                //Wait until Microsoft finishes analysing
                if((operationStatus == com.microsoft.cognitive.speakerrecognition.contract.identification.Status.SUCCEEDED
                        || operationStatus == com.microsoft.cognitive.speakerrecognition.contract.identification.Status.FAILED)
                        && enrollmentStatusResult.processingResult != null) {
                    enrollmentStatus = enrollmentStatusResult.processingResult.enrollmentStatus;
                }
            }while(enrollmentStatus == null);

            result = new EnrollUserTaskResult(enrollmentStatus);
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
