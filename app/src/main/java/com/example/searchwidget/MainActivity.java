package com.example.searchwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnEditWidget = findViewById(R.id.btn_edit_widget);

        btnEditWidget.setOnClickListener(v -> {
            // Find the first existing widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            ComponentName widgetComponent = new ComponentName(this, SearchWidget.class);
            int[] widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);

            if (widgetIds.length > 0) {
                // Open the WidgetConfigActivity with the first widget ID
                Intent configIntent = new Intent(this, WidgetConfigActivity.class);
                configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetIds[0]);
                startActivity(configIntent);
            }
        });
    }
}
