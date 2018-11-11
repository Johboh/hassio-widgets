package com.fjun.hassiowidgets;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class HassService extends IntentService {

    private static final String EXTRA_URL = "url";
    private static final String EXTRA_PAYLOAD = "payload";

    public HassService() {
        super("HassService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent == null) {
            Log.d(HassService.class.getName(), "No intent available.");
            return;
        }

        final String url = intent.getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(url)) {
            Log.d(HassService.class.getName(), "Missing url.");
            return;
        }
        final String payload = intent.getStringExtra(EXTRA_PAYLOAD);

        final Call<ResponseBody> hassApi = HassApiHelper.create(this, url, payload);
        if (hassApi == null) {
            Log.d(HassService.class.getName(), "No hassApi available. Are you missing a valid hostname?");
        }
        try {
            hassApi.execute();
            Log.d(MainActivity.class.getName(), "Retofit succeeded");
        } catch (IOException e) {
            Log.e(MainActivity.class.getName(), "Retofit failed: " + e.getMessage());
        }
    }

    public static Intent createIntent(
            @NonNull Context context,
            @NonNull String url,
            @Nullable String payload) {
        final Intent intent = new Intent(context, HassService.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_PAYLOAD, payload);
        return intent;
    }
}
