package com.example.informatik.cognitizer.Tasks;

import android.os.AsyncTask;

import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationClient;
import com.microsoft.cognitive.speakerrecognition.contract.EnrollmentStatus;
import com.microsoft.cognitive.speakerrecognition.contract.identification.EnrollmentOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.Identification;
import com.microsoft.cognitive.speakerrecognition.contract.identification.IdentificationOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.OperationLocation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.Profile;
import com.microsoft.cognitive.speakerrecognition.contract.identification.Status;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.informatik.cognitizer.Tasks.Constants.TASK_DELAY;

public class AuthorizeUserTask extends AsyncTask<File, Void, AuthorizeUserTaskResult> {
    private Exception exception;
    private SpeakerIdentificationClient speakerIdentificationClient;

    public AuthorizeUserTask(SpeakerIdentificationClient speakerIdentificationClient)
    {
        this.speakerIdentificationClient = speakerIdentificationClient;
    }

    @Override
    protected AuthorizeUserTaskResult doInBackground(File... params) {
        AuthorizeUserTaskResult result;

        try {
            //TODO Save users locally?
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
        // TODO: check this.exception
        // TODO: do something with the feed
        if(exception != null) {
            //TODO Exception-Handler?
            exception.printStackTrace();
        }
    }
}
