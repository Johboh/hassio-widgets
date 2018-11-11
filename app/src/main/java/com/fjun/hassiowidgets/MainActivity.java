package com.fjun.hassiowidgets;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import static com.fjun.hassiowidgets.Constants.KEY_PREFS_API_KEY;
import static com.fjun.hassiowidgets.Constants.KEY_PREFS_HOST;
import static com.fjun.hassiowidgets.Constants.PREFS_NAME;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Support saving settings (host, api key)
        final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final EditText hostEditText = findViewById(R.id.host);
        final EditText apiKeyEditText = findViewById(R.id.api_key);
        findViewById(R.id.save).setOnClickListener(view -> sharedPreferences.edit()
                .putString(KEY_PREFS_HOST, hostEditText.getText().toString().trim())
                .putString(KEY_PREFS_API_KEY, apiKeyEditText.getText().toString().trim())
                .apply());

        // Set current saved host and api key.
        hostEditText.setText(sharedPreferences.getString(KEY_PREFS_HOST, ""));
        apiKeyEditText.setText(sharedPreferences.getString(KEY_PREFS_API_KEY, ""));
    }
}
