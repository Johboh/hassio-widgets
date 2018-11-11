package com.fjun.hassiowidgets;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * API for hass.io
 * A API key is required.
 */
public interface HassApi {
    /**
     * Do a generic API call to hass.io.
     *
     * @param url the relative path against host. e.g. /api/services/scene/turn_on
     * @param body raw json to send (or empty), e.g. {"entity_id" : "scene.movie"}
     * @param apiKey the api key to use.
     */
    @POST
    Call<ResponseBody> generic(@Url String url, @Body RequestBody body, @Header("x-ha-access") String apiKey);
}
