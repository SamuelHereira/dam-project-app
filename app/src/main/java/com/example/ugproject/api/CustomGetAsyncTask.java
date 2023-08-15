package com.example.ugproject.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomGetAsyncTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private OnResultListener resultListener;

    private String url;

    private String search;

    public interface OnResultListener {
        void onSuccess(String result);
        void onError(String result);
    }

    public CustomGetAsyncTask(Context context, String url, String search, OnResultListener resultListener) {
        this.context = context;
        this.resultListener = resultListener;
        this.url = url;
        this.search = search;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            OkHttpClient client = new OkHttpClient();

            url = url + "?search=" + search;

            Log.println(Log.DEBUG, "CustomGetAsyncTask", "URL: " + url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            Log.d("CustomPostAsyncTask", "Response from server: " + responseBody);

            return responseBody;

        } catch (Exception e) {
            Log.e("CustomGetAsyncTask", "Error al realizar la petición GET: " + e.toString());
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            resultListener.onSuccess(result);
        } else {
            resultListener.onError("Error en la solicitud GET");
        }
    }
}
