package com.example.evcharging.data.repository;

import com.example.evcharging.http.AuthApi;
import com.example.evcharging.http.HttpCallback;
import com.example.evcharging.http.AuthRetrofitProvider;
import com.example.evcharging.model.LoginSuccessDTO;
import com.example.evcharging.utils.SpUtil;
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
                jsonObject.toString(),
                MediaType.parse("application/json")
        );

        Call<LoginSuccessDTO> call = api.login(requestBody);

        call.enqueue(new HttpCallback<LoginSuccessDTO>() {
            @Override
            public void onResult(LoginSuccessDTO result) {
                if (result != null && result.getToken() != null) {
                    String accessToken = result.getToken();
                    String userId = result.getEvOwner() != null ? result.getEvOwner().getId() : null;
                    String email = result.getEvOwner() != null ? result.getEvOwner().getEmail() : null;
                    String name = result.getEvOwner() != null ? result.getEvOwner().getName() : null;
                    String nic = result.getEvOwner() != null ? result.getEvOwner().getNic() : null;
                    String userType = "EVOwner"; // Set based on your app's user types

                    SpUtil.saveUserCredentials(accessToken, null, userId, email, name, nic, userType);
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }

            @Override
            protected void onUnauthorized() {
                super.onUnauthorized();
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
                jsonObject.toString(),
                MediaType.parse("application/json")
        );

        Call<LoginSuccessDTO> call = api.register(requestBody);

        call.enqueue(new HttpCallback<LoginSuccessDTO>() {
            @Override
            public void onResult(LoginSuccessDTO result) {
                if (result != null && result.getToken() != null) {
                    String accessToken = result.getToken();
                    String userId = result.getEvOwner() != null ? result.getEvOwner().getId() : null;
                    String userEmail = result.getEvOwner() != null ? result.getEvOwner().getEmail() : null;
                    String userName = result.getEvOwner() != null ? result.getEvOwner().getName() : null;
                    String userNic = result.getEvOwner() != null ? result.getEvOwner().getNic() : null;
                    String userType = "EVOwner";

                    SpUtil.saveUserCredentials(accessToken, null, userId, userEmail, userName, userNic, userType);
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}