package com.example.evcharging.viewmodel;

import com.example.evcharging.model.LoginSuccessDTO;
import com.example.evcharging.data.repository.AuthRepository;

public class AuthViewModel extends BaseViewModel {

    private AuthRepository authRepository;

    public AuthViewModel() {
        authRepository = new AuthRepository();
    }

    public interface LoginResultCallback {
        void onLoginSuccess(LoginSuccessDTO loginResponse);
        void onLoginError(String errorMessage);
    }

    public void login(String email, String password, LoginResultCallback callback) {
        authRepository.login(email, password, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess(LoginSuccessDTO loginResponse) {
                callback.onLoginSuccess(loginResponse);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onLoginError(errorMessage);
            }
        });
    }
}
