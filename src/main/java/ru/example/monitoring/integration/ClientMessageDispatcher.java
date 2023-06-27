package ru.example.monitoring.integration;

import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

public class ClientMessageDispatcher {

    public void send(String json) {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("http://example.com/api/endpoint")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = Objects.requireNonNull(response.body()).string();
            System.out.println("Response: " + responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
