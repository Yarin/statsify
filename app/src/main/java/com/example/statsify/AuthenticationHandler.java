package com.example.statsify;

import android.app.Activity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class AuthenticationHandler {

    public final String BASE_URL =  "https://accounts.spotify.com/authorize?";
    private String CLIENT_ID;
    private String REDIRECT_URI;
    private String SCOPES;
    private int REQUEST_CODE;


    /**
     * Constructs an AuthenticationHandler instance with the specified parameters.
     *
     * @param CLIENT_ID    The client ID of the Spotify application.
     * @param REDIRECT_URI The redirect URI to handle the authorization response.
     * @param SCOPES       The scopes to request during the authentication process.
     * @param REQUEST_CODE The request code to identify the authorization request.
     */
    public AuthenticationHandler(String CLIENT_ID, String REDIRECT_URI, String SCOPES, int REQUEST_CODE) {
        this.CLIENT_ID = CLIENT_ID;
        this.REDIRECT_URI = REDIRECT_URI;
        this.REQUEST_CODE = REQUEST_CODE;
        this.SCOPES = SCOPES;

    }

    /**
     * Opens the Spotify login activity to initiate the authentication flow.
     *
     * @param activity The activity from which the login activity will be opened.
     */
    public void openAuthentication(Activity activity) {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, request);
    }

    }

