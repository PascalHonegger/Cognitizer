package com.example.informatik.cognitizer.Tasks;

import android.os.AsyncTask;

import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationClient;
import com.microsoft.cognitive.speakerrecognition.contract.identification.Identification;
import com.microsoft.cognitive.speakerrecognition.contract.identification.IdentificationOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.OperationLocation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.Profile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.example.informatik.cognitizer.Tasks.Constants.TASK_DELAY;

/**
 * A Task which tries to authorize a user using his voice. Calls the {@link SpeakerIdentificationClient}.identify and uses a callback to inform you once the identification finished
 */
public class AuthorizeUserTask extends AsyncTask<File, Void, AuthorizeUserTaskResult> {
    private Exception exception;
    private SpeakerIdentificationClient speakerIdentificationClient;
    private PostExecuteCallback callback;

    public AuthorizeUserTask(SpeakerIdentificationClient speakerIdentificationClient, PostExecuteCallback callback)
    {
        this.speakerIdentificationClient = speakerIdentificationClient;
        this.callback = callback;
    }

    @Override
    protected AuthorizeUserTaskResult doInBackground(File... params) {
        AuthorizeUserTaskResult result;

        try {
            List<Profile> profiles = speakerIdentificationClient.getProfiles();

            List<UUID> allUsers = new ArrayList<>();

            for (Profile p : profiles) {
                allUsers.add(p.identificationProfileId);
            }

            OperationLocation identifyResult = speakerIdentificationClient.identify(new FileInputStream(params[0]), allUsers, true);


            Identification identifiedUser = null;
            do {
                //Let Microsoft think before we retry
                Thread.sleep(TASK_DELAY);

                IdentificationOperation identificationStatusResult = speakerIdentificationClient.checkIdentificationStatus(identifyResult);

                //Wait until Microsoft finishes analysing
                if((identificationStatusResult.status == com.microsoft.cognitive.speakerrecognition.contract.identification.Status.SUCCEEDED
                        || identificationStatusResult.status == com.microsoft.cognitive.speakerrecognition.contract.identification.Status.FAILED)
                        && identificationStatusResult.processingResult != null) {
                    identifiedUser = identificationStatusResult.processingResult;
                }

            }while(identifiedUser == null);

            result = new AuthorizeUserTaskResult(identifiedUser.identifiedProfileId, identifiedUser.confidence);
        } catch (Exception e) {
            this.exception = e;

            result = new AuthorizeUserTaskResult(e);
        }

        return result;
    }

    protected void onPostExecute(AuthorizeUserTaskResult result) {
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
        void onSuccess(AuthorizeUserTaskResult result);
        void onError(Exception e);
    }
}
