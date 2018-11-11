package com.fjun.hassiowidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
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
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Create an Intent to start background service
        final String name = sharedPreferences.getString(KEY_WIDGET_NAME + appWidgetId, "");
        final String url = sharedPreferences.getString(KEY_WIDGET_URL + appWidgetId, "");
        final String payload = sharedPreferences.getString(KEY_WIDGET_PAYLOAD + appWidgetId, "");

        // Create on click intent. Valid uri?
        final PendingIntent pendingIntent;
        if (!TextUtils.isEmpty(url)) {
            final Intent intent = HassService.createIntent(context, url, payload);
            pendingIntent = PendingIntent.getService(context, appWidgetId, intent, FLAG_UPDATE_CURRENT);
        } else {
            final Intent intent = new Intent(context, WidgetConfigurationActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, FLAG_UPDATE_CURRENT);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);

        // Set name to widget.
        views.setTextViewText(R.id.name, name);

        // Get the layout for the App Widget and attach an on-click listener to the root view.
        views.setOnClickPendingIntent(R.id.root, pendingIntent);

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
