package com.example.evcharging.data.repository;

import com.example.evcharging.http.AuthApi;
import com.example.evcharging.http.HttpCallback;
import com.example.evcharging.http.AuthRetrofitProvider;
import com.example.evcharging.model.LoginSuccessDTO;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class AuthRepository {

    private final AuthApi api;

    public AuthRepository() {
        api = AuthRetrofitProvider.getInstance().create(AuthApi.class);
    }

    public interface LoginCallback {
        void onSuccess(LoginSuccessDTO loginResponse);
        void onError(String errorMessage);
    }

    public void login(String email, String password, LoginCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("password", password);

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                jsonObject.toString()
        );

        Call<LoginSuccessDTO> call = api.login(requestBody);

        call.enqueue(new HttpCallback<LoginSuccessDTO>() {
            @Override
            public void onResult(LoginSuccessDTO result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public interface RegisterCallback {
        void onSuccess(LoginSuccessDTO response);
        void onError(String errorMessage);
    }

    public void register(String name, String email, String nic, String password, RegisterCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("nic", nic);
        jsonObject.addProperty("password", password);

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                jsonObject.toString()
        );

        Call<LoginSuccessDTO> call = api.register(requestBody);

        call.enqueue(new HttpCallback<LoginSuccessDTO>() {
            @Override
            public void onResult(LoginSuccessDTO result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}
