package com.example.searchwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import yuku.ambilwarna.AmbilWarnaDialog;

public class WidgetConfigActivity extends AppCompatActivity {
    private int appWidgetId;
    private int bgColor;
    private int iconColor;
    private int radius;
    private int transparency;
    private String selectedAppPackage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        // Get widget ID from intent or find the first existing widget
        Intent intent = getIntent();
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            ComponentName widgetComponent = new ComponentName(this, SearchWidget.class);
            int[] widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);

            if (widgetIds.length > 0) {
                appWidgetId = widgetIds[0]; // Use the first existing widget
            } else {
                finish(); // No widget found, close activity
                return;
            }
        }

        // Load saved preferences
        SharedPreferences prefs = getSharedPreferences("WidgetPrefs_" + appWidgetId, MODE_PRIVATE);
        bgColor = prefs.getInt("bg_color", Color.WHITE);
        iconColor = prefs.getInt("icon_color", Color.BLACK);
        radius = prefs.getInt("radius", 20);
        transparency = prefs.getInt("transparency", 255);
        selectedAppPackage = prefs.getString("selected_app", "");

        // Initialize UI elements
        Button btnBgColor = findViewById(R.id.btn_bg_color);
        Button btnIconColor = findViewById(R.id.btn_icon_color);
        SeekBar sbRadius = findViewById(R.id.sb_radius);
        SeekBar sbTransparency = findViewById(R.id.sb_transparency);
        Button btnSelectApp = findViewById(R.id.btn_select_app);
        Button btnSave = findViewById(R.id.btn_save);

        // Apply current settings to UI
        sbRadius.setProgress(radius);
        sbTransparency.setProgress(transparency);

        // Background Color Picker
        btnBgColor.setOnClickListener(v -> new AmbilWarnaDialog(this, bgColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                bgColor = color;
            }
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}
        }).show());

        // Icon Color Picker
        btnIconColor.setOnClickListener(v -> new AmbilWarnaDialog(this, iconColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                iconColor = color;
            }
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}
        }).show());

        // Corner Radius Slider
        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int maxRadius = getMaxRadius(WidgetConfigActivity.this, appWidgetId);
                radius = Math.min(progress, maxRadius);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Transparency Slider
        sbTransparency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                transparency = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // App Selection (Exclude System Apps but Include Browsers & Google Search)
        btnSelectApp.setOnClickListener(v -> {
            PackageManager pm = getPackageManager();
            List<ResolveInfo> apps = pm.queryIntentActivities(new Intent(Intent.ACTION_MAIN, null)
                    .addCategory(Intent.CATEGORY_LAUNCHER), 0);

            List<String> userApps = new ArrayList<>();
            List<String> appNames = new ArrayList<>();

            // Allowed system apps (Search & Browsers)
            List<String> allowedSystemApps = Arrays.asList(
                    "com.google.android.googlequicksearchbox", // Google Search
                    "com.android.chrome", // Chrome
                    "org.mozilla.firefox", // Firefox
                    "com.opera.browser", // Opera
                    "com.microsoft.emmx", // Edge
                    "com.brave.browser", // Brave Browser
                    "com.duckduckgo.mobile.android" // DuckDuckGo
            );

            for (ResolveInfo app : apps) {
                String packageName = app.activityInfo.packageName;
                boolean isSystemApp = (app.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

                // Allow all user-installed apps + explicitly allow Google Search & browsers
                if (!isSystemApp || allowedSystemApps.contains(packageName)) {
                    userApps.add(packageName);
                    try {
                        appNames.add(pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString());
                    } catch (PackageManager.NameNotFoundException e) {
                        appNames.add(packageName);
                    }
                }
            }

            // Ensure Google Search or Chrome is in the list
            if (!userApps.contains("com.google.android.googlequicksearchbox") && isAppInstalled("com.google.android.googlequicksearchbox", pm)) {
                userApps.add("com.google.android.googlequicksearchbox");
                appNames.add("Google Search");
            }
            if (!userApps.contains("com.android.chrome") && isAppInstalled("com.android.chrome", pm)) {
                userApps.add("com.android.chrome");
                appNames.add("Google Chrome");
            }

            // If no apps are found, show an error message
            if (userApps.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("No Apps Available")
                        .setMessage("No supported apps found.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Select an App")
                    .setItems(appNames.toArray(new String[0]), (dialog, which) -> selectedAppPackage = userApps.get(which))
                    .show();
        });


        // Save Button
        btnSave.setOnClickListener(v -> {
            // Save settings
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("bg_color", bgColor);
            editor.putInt("icon_color", iconColor);
            editor.putInt("radius", radius);
            editor.putInt("transparency", transparency);
            editor.putString("selected_app", selectedAppPackage);
            editor.apply();

            // Update the widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            SearchWidget.updateAppWidget(this, appWidgetManager, appWidgetId);

            // Return result
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(Activity.RESULT_OK, resultValue);
            finish();
        });
    }

    private int getWidgetHeight(Context context, int widgetId) {
        return AppWidgetManager.getInstance(context)
                .getAppWidgetOptions(widgetId)
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 50);
    }

    private int getMaxRadius(Context context, int widgetId) {
        return getWidgetHeight(context, widgetId);
    }
    // Helper method to check if an app is installed
    private boolean isAppInstalled(String packageName, PackageManager pm) {
        try {
            pm.getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
