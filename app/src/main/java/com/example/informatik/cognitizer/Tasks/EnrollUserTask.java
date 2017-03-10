package com.example.informatik.cognitizer.Tasks;

import android.os.AsyncTask;

import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationClient;
import com.microsoft.cognitive.speakerrecognition.contract.EnrollmentStatus;
import com.microsoft.cognitive.speakerrecognition.contract.identification.EnrollmentOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.OperationLocation;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.example.informatik.cognitizer.Tasks.Constants.TASK_DELAY;

/**
 * A Task which tries to enroll a new user using his voice. Calls the {@link SpeakerIdentificationClient}.enroll and uses a callback to inform you once the identification finished
 */
public class EnrollUserTask extends AsyncTask<File, Void, EnrollUserTaskResult> {
    private Exception exception;
    private SpeakerIdentificationClient speakerIdentificationClient;
    private PostExecuteCallback callback;

    public EnrollUserTask(SpeakerIdentificationClient speakerIdentificationClient, PostExecuteCallback callback)
    {
        this.speakerIdentificationClient = speakerIdentificationClient;
        this.callback = callback;
    }

    @Override
    protected EnrollUserTaskResult doInBackground(File... params) {
        EnrollUserTaskResult result;

        try {
            UUID createUserUUID = speakerIdentificationClient.createProfile("en-US").identificationProfileId;

            OperationLocation enrollResult = speakerIdentificationClient.enroll(new FileInputStream(params[0]), createUserUUID, true);

            EnrollmentStatus enrollmentStatus = null;
            do {
                //Let Microsoft think before we retry
                Thread.sleep(TASK_DELAY);
                EnrollmentOperation enrollmentStatusResult = speakerIdentificationClient.checkEnrollmentStatus(enrollResult);

                //Wait until Microsoft finishes analysing
                if((enrollmentStatusResult.status == com.microsoft.cognitive.speakerrecognition.contract.identification.Status.SUCCEEDED
                        || enrollmentStatusResult.status == com.microsoft.cognitive.speakerrecognition.contract.identification.Status.FAILED)
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
        void onSuccess(EnrollUserTaskResult result);
        void onError(Exception e);
    }
}
