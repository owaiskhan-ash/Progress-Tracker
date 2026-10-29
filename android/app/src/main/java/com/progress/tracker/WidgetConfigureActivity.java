package com.progress.tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WidgetConfigureActivity extends Activity {
    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        widgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        SharedPreferences data = getSharedPreferences(ProjectTrackerPlugin.DATA_PREFS, Context.MODE_PRIVATE);
        try {
            JSONArray projects = new JSONArray(data.getString(ProjectTrackerPlugin.PROJECTS_KEY, "[]"));
            for (int i = 0; i < projects.length(); i++) {
                JSONObject project = projects.optJSONObject(i);
                if (project == null) continue;
                String id = project.optString("id", "");
                if (id.isEmpty()) continue;
                int progress = ProjectWidgetProviderProgress.value(project);
                ids.add(id);
                labels.add(project.optString("name", "Project") + "    " + progress + "%");
            }
        } catch (JSONException ignored) {
        }

        if (ids.isEmpty()) {
            SharedPreferences settings = getSharedPreferences(ProjectWidgetProvider.WIDGET_PREFS, Context.MODE_PRIVATE);
            settings.edit().putString(ProjectWidgetProvider.PROJECT_PREFIX + widgetId, "").apply();
            finishConfiguration();
            Toast.makeText(this, "Create a project, then tap this widget to choose it.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }

        String[] choices = labels.toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle("Choose a project")
                .setItems(choices, (dialog, which) -> selectProject(ids.get(which)))
                .setOnCancelListener(dialog -> finish())
                .show();
    }

    private void selectProject(String projectId) {
        SharedPreferences settings = getSharedPreferences(ProjectWidgetProvider.WIDGET_PREFS, Context.MODE_PRIVATE);
        settings.edit().putString(ProjectWidgetProvider.PROJECT_PREFIX + widgetId, projectId).apply();
        finishConfiguration();
    }

    private void finishConfiguration() {
        Intent result = new Intent();
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, result);
        ProjectWidgetProvider.updateWidget(this, AppWidgetManager.getInstance(this), widgetId);
        finish();
    }

    private static final class ProjectWidgetProviderProgress {
        static int value(JSONObject project) {
            JSONArray milestones = project.optJSONArray("milestones");
            int progress = 0;
            if (milestones != null) {
                for (int i = 0; i < milestones.length(); i++) {
                    JSONObject milestone = milestones.optJSONObject(i);
                    if (milestone != null) progress = Math.max(progress, milestone.optInt("percent", 0));
                }
            }
            return Math.max(0, Math.min(progress, 100));
        }
    }
}
