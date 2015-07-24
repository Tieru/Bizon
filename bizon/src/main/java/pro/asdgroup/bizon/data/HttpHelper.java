package pro.asdgroup.bizon.data;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.model.FeedEntry;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Status;
import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class HttpHelper {

    public static String BASE_URL = "http://vps-1069545.vpshome.pro/index.php/api/";

    public static String DATA_TAG = "data";
    public static int DEFAULT_COUNT = 30;

    public static boolean silienceMod;

    private static HttpHelper mInstance;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context mCtx;

    private HttpHelper(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
    }

    public static RestAdapter getRestAdapter() {
        Gson gson = (new GsonBuilder())
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .registerTypeAdapter(Profile.class, new Profile.ProfileSerializer())
                .registerTypeAdapter(FeedEntry.class, new FeedEntry.FeedEntrySerializer())
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return (new retrofit.RestAdapter.Builder())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new CustomErrorHandler())
                .build();
    }

    static class CustomErrorHandler implements ErrorHandler {
        @Override public Throwable handleError(RetrofitError cause) {
            if (cause.getResponse() == null || cause.getResponse().getBody() == null){
                sendToast("");
                return cause;
            }

            String message = getErrorString(cause);
            if (!message.isEmpty()) {
                sendToast(message);
                throw new AssertionError();
            }

            return cause;
        }

        private String getErrorString(RetrofitError cause){
            String errorMessage = "";

            try {
                JSONObject jsonObject = new JSONObject(cause.getResponse().getBody().toString());

                if (jsonObject.getInt("status") != 200) {
                    errorMessage = jsonObject.getString("error");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return errorMessage;
        }

        private void sendToast(final String message) {
            if (silienceMod) {
                return;
            }

            final Context ctx = BizonApp.getAppContext();
            final String errorMessage = message.equals("") ? ctx.getString(R.string.server_response_error) : message;

            Handler mainHandler = new Handler(ctx.getMainLooper());

            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
                }
            };
            mainHandler.post(task);
        }
    }

    public static class ItemTypeAdapterFactory implements TypeAdapterFactory {

        public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {

            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

            return new TypeAdapter<T>() {

                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                public T read(JsonReader in) throws IOException {

                    JsonElement jsonElement = elementAdapter.read(in);

                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();

                        if (jsonObject.has("status")) {
                            String status = jsonObject.get("status").getAsString();
                            if (status.equals("200")) {
                                if (jsonObject.has("data")/* && jsonObject.get("data").isJsonObject()*/) {
                                    jsonElement = jsonObject.get("data");
                                }
                            } else {
                                String error = jsonObject.get("error").getAsString();
                                sendToast(error);
                                throw new AssertionError();
                            }
                        } /*else {
                            if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
                                jsonElement = jsonObject;
                            }
                        }*/
                    }

                    return delegate.fromJsonTree(jsonElement);
                }
            }.nullSafe();
        }

        private void sendToast(final String message) {
            if (silienceMod) {
                return;
            }

            final Context ctx = BizonApp.getAppContext();
            final String errorMessage = message.equals("") ? ctx.getString(R.string.server_response_error) : message;

            Handler mainHandler = new Handler(ctx.getMainLooper());

            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
                }
            };
            mainHandler.post(task);
        }
    }

    public static abstract class RestCallback<T> implements Callback<T> {
        public abstract void failure(Status restError);

        @Override
        public void failure(RetrofitError error){
            error.printStackTrace();

            Status restError = null;

            if (error.getResponse() != null){
                TypedInput responseBody = error.getResponse().getBody(); //TODO: fix me
                if (responseBody != null) {
                    try {
                        String json = new String(((TypedByteArray) responseBody).getBytes());
                        Gson gson = new Gson();
                        restError = gson.fromJson(json, Status.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            //Status restError = (Status) error.getBodyAs(Status.class);

            if (restError != null) {
                //sendToast(restError.getError());
                failure(restError);
            } else {
                //sendToast("");
                failure(new Status(error.getMessage()));
            }
        }
/*
        private void sendToast(final String message) {
            final Context ctx = pro.asdgroup.bizon.BizonApp.getAppContext();
            final String errorMessage = message.equals("") ? ctx.getString(R.string.server_response_error) : message;

            Handler mainHandler = new Handler(ctx.getMainLooper());

            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
                }
            };
            mainHandler.post(task);
        }*/
    }


    public static synchronized HttpHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HttpHelper(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
