package fr.voxeet.sdk.sample.oauth;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by kevinleperf on 23/07/2018.
 */

public interface OAuthCalls {
    /**
     * Get the current thirdparty accessToken
     *
     * @return a callable webservice object
     */
    @GET("/api/token")
    Call<String> retrieveAccessToken();

    /**
     * Refresh the current thirdparty accessToken
     * <p>
     * Any call to this method which were not preceeded from {@link #retrieveRefreshToken()} will
     * invoke a 401 or 403 error code
     *
     * @return a callable webservice object
     */
    @GET("/api/refresh")
    Call<String> retrieveRefreshToken();
}
