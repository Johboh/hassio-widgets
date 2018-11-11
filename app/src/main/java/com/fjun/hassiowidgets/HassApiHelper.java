package com.fjun.hassiowidgets;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.fjun.hassiowidgets.Constants.DEFAULT_PORT;
import static com.fjun.hassiowidgets.Constants.KEY_PREFS_API_KEY;
import static com.fjun.hassiowidgets.Constants.KEY_PREFS_HOST;
import static com.fjun.hassiowidgets.Constants.PREFS_NAME;

/**
 * Helper class for creating the HassApi based on host and api key.
 */
class HassApiHelper {

    /**
     * Create a Call.
     *
     * @param url the relative path against host. e.g. /api/services/scene/turn_on
     * @param body key/values to add as json body. e.g. .put("entity_id", "scene.movie")
     *
     * @return a call, or null if some of the necessary parameters is missing (e.q. host)
     */
    @Nullable
    static Call<ResponseBody> create(
            @NonNull Context context,
            @NonNull String url,
            @NonNull HashMap<String, Object> body) {
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

        // Support empty API key, if there is no one required.
        final String apiKey = sharedPreferences.getString(KEY_PREFS_API_KEY, "");
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(HassApi.class).generic(url, body, apiKey);
    }
}
