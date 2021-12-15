package com.fjun.hassiowidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.fjun.hassiowidgets.Constants.KEY_WIDGET_NAME;
import static com.fjun.hassiowidgets.Constants.KEY_WIDGET_PAYLOAD;
import static com.fjun.hassiowidgets.Constants.KEY_WIDGET_URL;
import static com.fjun.hassiowidgets.Constants.PREFS_NAME;

public class HassAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, false, null);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, boolean running, @Nullable Boolean successful) {
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return;
        }

        final SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Create an Intent to start background service
        final String name = sharedPreferences.getString(KEY_WIDGET_NAME + appWidgetId, "");
        final String url = sharedPreferences.getString(KEY_WIDGET_URL + appWidgetId, "");
        final String payload = sharedPreferences.getString(KEY_WIDGET_PAYLOAD + appWidgetId, "");

        int additionalIntentFlag = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            additionalIntentFlag = PendingIntent.FLAG_MUTABLE;
        }

        final PendingIntent pendingIntentAction;
        {
            final Intent intent = HassService.createIntent(context, url, payload);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                pendingIntentAction = PendingIntent.getForegroundService(context, appWidgetId, intent, FLAG_UPDATE_CURRENT + additionalIntentFlag);
            } else {
                pendingIntentAction = PendingIntent.getService(context, appWidgetId, intent, FLAG_UPDATE_CURRENT + additionalIntentFlag);
            }
        }

        final PendingIntent pendingIntentConfiguration;
        {
            final Intent intent = new Intent(context, WidgetConfigurationActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntentConfiguration = PendingIntent.getActivity(context, appWidgetId, intent, FLAG_UPDATE_CURRENT + additionalIntentFlag);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);

        // Set name to widget.
        views.setTextViewText(R.id.name, name);

        if (!running) {
            if (!TextUtils.isEmpty(url)) {
                views.setOnClickPendingIntent(R.id.name, pendingIntentAction);
            } else {
                views.setOnClickPendingIntent(R.id.name, pendingIntentConfiguration);
            }

            views.setOnClickPendingIntent(R.id.top_container, pendingIntentConfiguration);
        }

        views.setViewVisibility(R.id.progress, running ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.result, successful != null ? View.VISIBLE : View.GONE);
        views.setBoolean(R.id.root, "setEnabled", !running);
        views.setBoolean(R.id.settings, "setEnabled", !running);

        if (successful != null && successful) {
            views.setImageViewResource(R.id.result, R.mipmap.baseline_thumb_up_black);
        } else {
            views.setImageViewResource(R.id.result, R.mipmap.baseline_thumb_down_black);
        }

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
