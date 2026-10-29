package com.progress.tracker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProjectWidgetProvider extends AppWidgetProvider {
    static final String WIDGET_PREFS = "project_tracker_widget_settings";
    static final String PROJECT_PREFIX = "project_for_widget_";
    private static final int LIME = 0xFFD9FF36;
    private static final int TRACK = 0x66FFFFFF;

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] widgetIds) {
        for (int widgetId : widgetIds) updateWidget(context, manager, widgetId);
    }

    @Override
    public void onDeleted(Context context, int[] widgetIds) {
        SharedPreferences.Editor editor = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE).edit();
        for (int widgetId : widgetIds) editor.remove(PROJECT_PREFIX + widgetId);
        editor.apply();
    }

    static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName component = new ComponentName(context, ProjectWidgetProvider.class);
        int[] ids = manager.getAppWidgetIds(component);
        for (int widgetId : ids) updateWidget(context, manager, widgetId);
    }

    static void updateWidget(Context context, AppWidgetManager manager, int widgetId) {
        SharedPreferences settings = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE);
        String projectId = settings.getString(PROJECT_PREFIX + widgetId, null);
        SharedPreferences data = context.getSharedPreferences(ProjectTrackerPlugin.DATA_PREFS, Context.MODE_PRIVATE);
        String projectsJson = data.getString(ProjectTrackerPlugin.PROJECTS_KEY, "[]");
        JSONObject project = findProject(projectsJson, projectId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.project_widget);

        if (project == null) {
            views.setTextViewText(R.id.widget_project_name, "Choose a project");
            views.setTextViewText(R.id.widget_milestone, "Tap to configure this widget");
            views.setTextViewText(R.id.widget_percent, "0% COMPLETE");
            views.setImageViewBitmap(R.id.widget_ring, progressBitmap(0));
            views.setOnClickPendingIntent(R.id.widget_root, configurationIntent(context, widgetId));
        } else {
            String name = project.optString("name", "Project");
            int progress = projectProgress(project.optJSONArray("milestones"));
            String milestone = latestMilestone(project.optJSONArray("milestones"));
            views.setTextViewText(R.id.widget_project_name, name);
            views.setTextViewText(R.id.widget_milestone, milestone);
            views.setTextViewText(R.id.widget_percent, progress + "% COMPLETE");
            views.setImageViewBitmap(R.id.widget_ring, progressBitmap(progress));
            views.setOnClickPendingIntent(R.id.widget_root, appIntent(context));
        }
        manager.updateAppWidget(widgetId, views);
    }

    private static JSONObject findProject(String projectsJson, String projectId) {
        if (projectId == null) return null;
        try {
            JSONArray projects = new JSONArray(projectsJson);
            for (int i = 0; i < projects.length(); i++) {
                JSONObject project = projects.optJSONObject(i);
                if (project != null && projectId.equals(project.optString("id"))) return project;
            }
        } catch (JSONException ignored) {
        }
        return null;
    }

    private static int projectProgress(JSONArray milestones) {
        int result = 0;
        if (milestones == null) return result;
        for (int i = 0; i < milestones.length(); i++) {
            JSONObject milestone = milestones.optJSONObject(i);
            if (milestone != null) result = Math.max(result, milestone.optInt("percent", 0));
        }
        return Math.max(0, Math.min(result, 100));
    }

    private static String latestMilestone(JSONArray milestones) {
        if (milestones == null || milestones.length() == 0) return "";
        JSONObject latest = null;
        int highest = -1;
        for (int i = 0; i < milestones.length(); i++) {
            JSONObject milestone = milestones.optJSONObject(i);
            if (milestone != null && milestone.optInt("percent", 0) >= highest) {
                highest = milestone.optInt("percent", 0);
                latest = milestone;
            }
        }
        return latest == null ? "" : latest.optString("note", "");
    }

    private static Bitmap progressBitmap(int progress) {
        int size = 220;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint ring = new Paint(Paint.ANTI_ALIAS_FLAG);
        ring.setStyle(Paint.Style.STROKE);
        ring.setStrokeWidth(7f);
        ring.setStrokeCap(Paint.Cap.ROUND);
        float inset = 18f;
        RectF bounds = new RectF(inset, inset, size - inset, size - inset);
        ring.setColor(TRACK);
        canvas.drawArc(bounds, 0, 360, false, ring);
        ring.setColor(LIME);
        canvas.drawArc(bounds, -90, 360f * progress / 100f, false, ring);
        ring.setStyle(Paint.Style.FILL);
        ring.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        ring.setTextAlign(Paint.Align.CENTER);
        ring.setTextSize(62f);
        ring.setColor(0xFFFFFFFF);
        Paint.FontMetrics metrics = ring.getFontMetrics();
        float baseline = size / 2f - (metrics.ascent + metrics.descent) / 2f;
        canvas.drawText(progress + "%", size / 2f, baseline, ring);
        return bitmap;
    }

    private static PendingIntent appIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 100, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static PendingIntent configurationIntent(Context context, int widgetId) {
        Intent intent = new Intent(context, WidgetConfigureActivity.class);
        intent.setData(Uri.parse("projecttracker://widget/" + widgetId));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        return PendingIntent.getActivity(context, widgetId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
