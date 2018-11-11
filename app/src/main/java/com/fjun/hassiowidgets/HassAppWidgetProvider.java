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
            updateAppWidget(context, appWidgetManager, appWidgetId, false);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, boolean running) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Create an Intent to start background service
        final String name = sharedPreferences.getString(KEY_WIDGET_NAME + appWidgetId, "");
        final String url = sharedPreferences.getString(KEY_WIDGET_URL + appWidgetId, "");
        final String payload = sharedPreferences.getString(KEY_WIDGET_PAYLOAD + appWidgetId, "");

        final PendingIntent pendingIntentAction;
        {
            final Intent intent = HassService.createIntent(context, url, payload);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                pendingIntentAction = PendingIntent.getForegroundService(context, appWidgetId, intent, FLAG_UPDATE_CURRENT);
            } else {
                pendingIntentAction = PendingIntent.getService(context, appWidgetId, intent, FLAG_UPDATE_CURRENT);
            }
        }

        final PendingIntent pendingIntentConfiguration;
        {
            final Intent intent = new Intent(context, WidgetConfigurationActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            pendingIntentConfiguration = PendingIntent.getActivity(context, appWidgetId, intent, FLAG_UPDATE_CURRENT);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);

        // Set name to widget.
        views.setTextViewText(R.id.name, name);

        if (!running) {
            if (!TextUtils.isEmpty(url)) {
                views.setOnClickPendingIntent(R.id.root, pendingIntentAction);
            } else {
                views.setOnClickPendingIntent(R.id.root, pendingIntentConfiguration);
            }

            views.setOnClickPendingIntent(R.id.settings, pendingIntentConfiguration);
        }

        views.setViewVisibility(R.id.progress, running ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.settings, !running ? View.VISIBLE : View.GONE);
        views.setBoolean(R.id.root, "setEnabled", !running);
        views.setBoolean(R.id.settings, "setEnabled", !running);

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
