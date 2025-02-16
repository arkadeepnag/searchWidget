package com.example.searchwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.RemoteViews;

public class SearchWidget extends AppWidgetProvider {
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences("WidgetPrefs_" + appWidgetId, Context.MODE_PRIVATE);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        applySettings(context, views, prefs);

        // Get the selected app package name
        String packageName = prefs.getString("selected_app", null);
        Intent intent;

        if (packageName != null) {
            intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        } else {
            // Fallback: Open the widget configuration if no app is selected
            intent = new Intent(context, WidgetConfigActivity.class);
        }

        // Ensure intent is not null before setting click action
        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void applySettings(Context context, RemoteViews views, SharedPreferences prefs) {
        int bgColor = prefs.getInt("bg_color", Color.WHITE);
        int transparency = prefs.getInt("transparency", 255);
        int iconColor = prefs.getInt("icon_color", Color.BLACK);

        // Adjust transparency
        bgColor = Color.argb(transparency, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor));

        // Set the background color
        views.setInt(R.id.widget_container, "setBackgroundColor", bgColor);

        // Apply icon color
        views.setInt(R.id.iv_search, "setColorFilter", iconColor);
        views.setInt(R.id.iv_mic, "setColorFilter", iconColor);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}
