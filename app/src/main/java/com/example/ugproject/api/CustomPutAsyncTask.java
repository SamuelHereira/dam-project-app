package com.example.ugproject.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomPutAsyncTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private String url;
    private JSONObject requestBodyJson;
    private List<FileItem> files;
    private OnResultListener resultListener;

    public interface OnResultListener {
        void onSuccess(String result);
        void onError(String result);
    }

    public static class FileItem {
        public File file;
        public String fieldName;
        public String fileName;

        public FileItem(File file, String fieldName, String fileName) {
            this.file = file;
            this.fieldName = fieldName;
            this.fileName = fileName;
        }
    }

    public CustomPutAsyncTask(Context context, String url, JSONObject requestBodyJson,
                               List<FileItem> files, OnResultListener resultListener) {
        this.context = context;
        this.url = url;
        this.requestBodyJson = requestBodyJson;
        this.files = files;
        this.resultListener = resultListener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            OkHttpClient client = new OkHttpClient();

            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);

            // Agregar campos JSON al cuerpo multipart
            Iterator<String> keys = requestBodyJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = requestBodyJson.getString(key);
                requestBodyBuilder.addFormDataPart(key, value);
            }

            // Agregar archivos al cuerpo multipart
            for (FileItem fileItem : files) {
                String fieldName = fileItem.fieldName;
                String fileName = fileItem.fileName;
                RequestBody fileRequestBody = RequestBody.create(
                        MultipartBody.FORM, fileItem.file);
                requestBodyBuilder.addFormDataPart(fieldName, fileName, fileRequestBody);
            }

            RequestBody requestBody = requestBodyBuilder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .put(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            String responseBody = response.body().string();
            Log.d("CustomPostAsyncTask", "Response from server: " + responseBody);

            return responseBody;



        } catch (IOException | JSONException e) {
            Log.e("CustomPostAsyncTask", "Error al realizar la petición PUT: " + e.toString());
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            resultListener.onSuccess(result);
        } else {
            resultListener.onError(result);
        }
    }
}
