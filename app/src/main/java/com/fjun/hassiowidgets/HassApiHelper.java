package com.fjun.hassiowidgets;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.fjun.hassiowidgets.Constants.DEFAULT_PORT;
import static com.fjun.hassiowidgets.Constants.KEY_PREFS_TOKEN;
import static com.fjun.hassiowidgets.Constants.KEY_PREFS_HOST;
import static com.fjun.hassiowidgets.Constants.PREFS_NAME;

/**
 * Helper class for creating the HassApi based on host and api key.
 */
class HassApiHelper {

    private static final String BEARER_PATTERN = "Bearer %s";

    /**
     * Create a Call.
     *
     * @param url the relative path against host. e.g. /api/services/scene/turn_on
     * @param body raw json to send (or empty), e.g. {"entity_id" : "scene.movie"}
     *
     * @return a call, or null if some of the necessary parameters is missing (e.q. host)
     */
    @Nullable
    static Call<ResponseBody> create(
            @NonNull Context context,
            @NonNull String url,
            @Nullable String body) {
        // Read host and API key.
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String host = sharedPreferences.getString(KEY_PREFS_HOST, "");
        if (TextUtils.isEmpty(host)) {
            return null;
        }

        // No port number? Add default one.
        if (!host.contains(":")) {
            host = String.format(Locale.getDefault(), "%s:%d", host, DEFAULT_PORT);
        }
        // Default to http:// if there is no protocol defined.
        if (!host.startsWith("http://") && !host.startsWith("https://")) {
            host = String.format(Locale.getDefault(), "http://%s", host);
        }

        // Support empty token, if there is no one required.
        final String token = sharedPreferences.getString(KEY_PREFS_TOKEN, "");
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final RequestBody requestBody;
        if (!TextUtils.isEmpty(body)) {
            requestBody = RequestBody.create(MediaType.parse("application/json"), body);
        } else {
            requestBody = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        String bearer = String.format(BEARER_PATTERN, token);
        return retrofit.create(HassApi.class).generic(url, requestBody, bearer);
    }
}
