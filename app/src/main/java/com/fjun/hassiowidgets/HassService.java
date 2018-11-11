package com.fjun.hassiowidgets;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final String channelId = getString(R.string.app_name);
            final NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_MIN);
            notificationChannel.setDescription(channelId);
            notificationChannel.setSound(null, null);

            notificationManager.createNotificationChannel(notificationChannel);
            startForeground(1, new Notification.Builder(this, channelId).build());
        }

        if (intent == null) {
            Log.d(HassService.class.getName(), "No intent available.");
            stop();
            return;
        }

        final int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        final String url = intent.getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(url)) {
            Log.d(HassService.class.getName(), "Missing url.");
            stop();
            return;
        }
        final String payload = intent.getStringExtra(EXTRA_PAYLOAD);

        final Call<ResponseBody> hassApi = HassApiHelper.create(this, url, payload);
        if (hassApi == null) {
            Log.d(HassService.class.getName(), "No hassApi available. Are you missing a valid hostname?");
            stop();
            return;
        }
        try {
            HassAppWidgetProvider.updateAppWidget(this, AppWidgetManager.getInstance(this), widgetId, true);
            hassApi.execute();
            Log.d(MainActivity.class.getName(), "Retofit succeeded");
        } catch (IOException e) {
            Log.e(MainActivity.class.getName(), "Retofit failed: " + e.getMessage());
        }

        HassAppWidgetProvider.updateAppWidget(this, AppWidgetManager.getInstance(this), widgetId, false);
        stop();
    }

    private void stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
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
