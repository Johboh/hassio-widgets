package com.fjun.hassiowidgets;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.fjun.hassiowidgets.Constants.KEY_PREFS_TOKEN;
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
        final EditText tokenEditText = findViewById(R.id.token);
        findViewById(R.id.save).setOnClickListener(view -> {
            sharedPreferences.edit()
                    .putString(KEY_PREFS_HOST, hostEditText.getText().toString().trim())
                    .putString(KEY_PREFS_TOKEN, tokenEditText.getText().toString().trim())
                    .apply();
            Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
        });

        // Set current saved host and api key.
        hostEditText.setText(sharedPreferences.getString(KEY_PREFS_HOST, ""));
        tokenEditText.setText(sharedPreferences.getString(KEY_PREFS_TOKEN, ""));
    }
}
