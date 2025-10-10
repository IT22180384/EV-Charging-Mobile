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

        // First try EVOwner login
        android.util.Log.d("AuthRepository", "Attempting EVOwner login for: " + email);
        Call<LoginSuccessDTO> evOwnerCall = api.evOwnerLogin(requestBody);
        
        evOwnerCall.enqueue(new HttpCallback<LoginSuccessDTO>() {
            @Override
            public void onResult(LoginSuccessDTO result) {
                // EVOwner login successful
                android.util.Log.d("AuthRepository", "EVOwner login successful");
                handleLoginSuccess(result, callback);
            }

            @Override
            public void onError(String errorMessage) {
                // EVOwner login failed, try Operator login
                android.util.Log.d("AuthRepository", "EVOwner login failed, trying Operator login: " + errorMessage);
                tryOperatorLogin(requestBody, callback);
            }

            @Override
            protected void onUnauthorized() {
                // EVOwner login unauthorized, try Operator login
                android.util.Log.d("AuthRepository", "EVOwner login unauthorized, trying Operator login");
                tryOperatorLogin(requestBody, callback);
            }
        });
    }

    private void tryOperatorLogin(RequestBody requestBody, LoginCallback callback) {
        android.util.Log.d("AuthRepository", "Attempting Operator login");
        Call<LoginSuccessDTO> operatorCall = api.operatorLogin(requestBody);
        
        operatorCall.enqueue(new HttpCallback<LoginSuccessDTO>() {
            @Override
            public void onResult(LoginSuccessDTO result) {
                // Operator login successful
                android.util.Log.d("AuthRepository", "Operator login successful");
                handleLoginSuccess(result, callback);
            }

            @Override
            public void onError(String errorMessage) {
                // Both logins failed
                android.util.Log.d("AuthRepository", "Both login attempts failed: " + errorMessage);
                callback.onError("Invalid email or password");
            }

            @Override
            protected void onUnauthorized() {
                // Both logins failed
                android.util.Log.d("AuthRepository", "Both login attempts unauthorized");
                callback.onError("Invalid email or password");
            }
        });
    }

    private void handleLoginSuccess(LoginSuccessDTO result, LoginCallback callback) {
        if (result != null && result.getToken() != null) {
            String accessToken = result.getToken();
            String userId, email, name, nic, userType;
            
            // Check if this is an EVOwner login response
            if (result.getEvOwner() != null) {
                userId = result.getEvOwner().getId();
                email = result.getEvOwner().getEmail();
                name = result.getEvOwner().getName();
                nic = result.getEvOwner().getNic();
                userType = "EVOwner";
            }
            // Check if this is an Operator login response
            else if (result.getOperator() != null) {
                userId = result.getOperator().getUserId();
                email = result.getOperator().getEmail();
                name = result.getOperator().getName();
                nic = null; // Operators might not have NIC
                userType = "StationOperator";
            }
            // Fallback to userType field if available
            else {
                userId = null;
                email = null;
                name = null;
                nic = null;
                userType = result.getUserType() != null ? result.getUserType() : "EVOwner";
            }

            SpUtil.saveUserCredentials(accessToken, null, userId, email, name, nic, userType);
        }
        callback.onSuccess(result);
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