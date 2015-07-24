package pro.asdgroup.bizon.gcm;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;

/**
 * Created by Tieru on 27.05.2015.
 */
public class RegisterGsmIdTask extends AsyncTask<Void, Void, Void>{

    private Context mContext;

    public RegisterGsmIdTask(Context context){
        mContext = context.getApplicationContext();
    }

    @Override
    protected Void doInBackground(Void... params) {

        //InstanceID instanceID = InstanceID.getInstance(mContext);
        try {
/*
            String token = instanceID.getToken(mContext.getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
*/
            GoogleCloudMessaging  gcm = GoogleCloudMessaging.getInstance(mContext);
            String token = gcm.register(mContext.getString(R.string.gcm_defaultSenderId));
            sendRegistrationIdToBackend(token);
            User.saveGcmToken(token);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void sendRegistrationIdToBackend(String regid){
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.sendPushToken(User.currentUser().getUserId(), regid);
    }

}
