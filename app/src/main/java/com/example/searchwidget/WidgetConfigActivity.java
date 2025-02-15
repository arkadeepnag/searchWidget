package com.example.searchwidget;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import yuku.ambilwarna.AmbilWarnaDialog;

public class WidgetConfigActivity extends AppCompatActivity {
    private int appWidgetId;
    private int bgColor = Color.WHITE;
    private int iconColor = Color.BLACK;
    private int radius = 0;
    private int transparency = 255;
    private String selectedAppPackage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        // Get the widget ID from the intent
        Intent intent = getIntent();
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        // Initialize UI elements
        Button btnBgColor = findViewById(R.id.btn_bg_color);
        Button btnIconColor = findViewById(R.id.btn_icon_color);
        SeekBar sbRadius = findViewById(R.id.sb_radius);
        SeekBar sbTransparency = findViewById(R.id.sb_transparency);
        Button btnSelectApp = findViewById(R.id.btn_select_app);
        Button btnSave = findViewById(R.id.btn_save);

        // Background Color Picker
        btnBgColor.setOnClickListener(v -> {
            new AmbilWarnaDialog(this, bgColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    bgColor = color;
                }
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {}
            }).show();
        });

        // Icon Color Picker
        btnIconColor.setOnClickListener(v -> {
            new AmbilWarnaDialog(this, iconColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    iconColor = color;
                }
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {}
            }).show();
        });

        // Corner Radius Slider
        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int maxRadius = getMaxRadius(WidgetConfigActivity.this, appWidgetId); // Pass context
                Log.d("WidgetConfig", "Max Radius: " + maxRadius);
                Log.d("WidgetConfig", "Progress Radius: " + progress);
                radius = 50; // Prevent excessive curvature
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

        // App Selection
        btnSelectApp.setOnClickListener(v -> {
            Intent intentApp = new Intent(Intent.ACTION_PICK_ACTIVITY);
            startActivityForResult(intentApp, 1);
        });

        // Save Button
        btnSave.setOnClickListener(v -> {
            // Save preferences
            SharedPreferences prefs = getSharedPreferences("WidgetPrefs_" + appWidgetId, MODE_PRIVATE);
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
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        return appWidgetManager.getAppWidgetOptions(widgetId)
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 50); // Default to 50dp
    }

    // Function to calculate max allowed radius based on widget height
    private int getMaxRadius(Context context, int widgetId) {
        int widgetHeight = getWidgetHeight(context, widgetId); // Fetch widget height dynamically
        return widgetHeight;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedAppPackage = data.getComponent().getPackageName();
        }
    }
}