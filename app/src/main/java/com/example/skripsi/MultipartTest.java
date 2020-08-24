package com.example.skripsi;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MultipartTest extends Request<String> {

    private MultipartEntityBuilder entity = MultipartEntityBuilder.create();
    private static final String FILE_PART_NAME = "image";
    //    private static final String STRING_PART_NAME = "user_id";
    private final Map<String, String> bodypart;
    private final Response.Listener<String> mListener;
    private final File file;
    //private final String user_id;
    private HttpEntity httpentity;

    public MultipartTest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, File image, Map<String, String> bodypart) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        this.file = image;
        this.bodypart = bodypart;


        entity.addPart(FILE_PART_NAME, new FileBody(file, ContentType.create("image/jpg"), file.getName()));

        try {
            if (bodypart != null) {
                for (Map.Entry<String, String> entry : bodypart.entrySet()) {
                    entity.addPart(entry.getKey(), new StringBody(entry.getValue()));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpentity = entity.build();
    }

    @Override
    public String getBodyContentType() {
        return httpentity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            httpentity.writeTo(bos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
//          System.out.println("Network Response "+ new String(response.data, "UTF-8"));
            return Response.success(new String(response.data, "UTF-8"),
                    getCacheEntry());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // fuck it, it should never happen though
            return Response.success(new String(response.data), getCacheEntry());
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}

