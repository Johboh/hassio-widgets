package com.fjun.hassiowidgets;

import java.util.HashMap;

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
     * @param body key/values to add as json body. e.g. .put("entity_id", "scene.movie")
     * @param apiKey the api key to use.
     */
    @POST
    Call<ResponseBody> generic(@Url String url, @Body HashMap<String, Object> body, @Header("x-ha-access") String apiKey);
}
