package com.fjun.hassiowidgets;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.fjun.hassiowidgets.Constants.KEY_WIDGET_NAME;
import static com.fjun.hassiowidgets.Constants.KEY_WIDGET_PAYLOAD;
import static com.fjun.hassiowidgets.Constants.KEY_WIDGET_URL;
import static com.fjun.hassiowidgets.Constants.PREFS_NAME;

public class WidgetConfigurationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appwidget_configuration);

        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        final int widgetId;
        if (extras != null) {
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        } else {
            widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        }
        setResult(RESULT_CANCELED);

        final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final EditText nameEditText = findViewById(R.id.name);
        final EditText urlEditText = findViewById(R.id.url);
        final EditText bodyEditText = findViewById(R.id.body);

        // Set current saved host and api key.
        nameEditText.setText(sharedPreferences.getString(KEY_WIDGET_NAME + widgetId, ""));
        urlEditText.setText(sharedPreferences.getString(KEY_WIDGET_URL + widgetId, ""));
        bodyEditText.setText(sharedPreferences.getString(KEY_WIDGET_PAYLOAD + widgetId, ""));

        findViewById(R.id.save).setOnClickListener(v -> {
            if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                final SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(KEY_WIDGET_NAME + widgetId, nameEditText.getText().toString());
                editor.putString(KEY_WIDGET_URL + widgetId, urlEditText.getText().toString());
                editor.putString(KEY_WIDGET_PAYLOAD + widgetId, bodyEditText.getText().toString());
                editor.apply();

                final Intent resultIntent = new Intent();
                resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                setResult(RESULT_OK, resultIntent);
                HassAppWidgetProvider.updateAppWidget(this, AppWidgetManager.getInstance(this), widgetId, false, null);
            }

            finish();
        });
    }
}
