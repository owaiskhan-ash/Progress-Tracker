package com.progress.tracker;

import android.content.Context;
import android.content.SharedPreferences;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ProjectTracker")
public class ProjectTrackerPlugin extends Plugin {
    static final String DATA_PREFS = "project_tracker_widget_data";
    static final String PROJECTS_KEY = "projects_json";

    @PluginMethod
    public void syncWidgetData(PluginCall call) {
        String projects = call.getString("projects", "[]");
        Context context = getContext();
        SharedPreferences preferences = context.getSharedPreferences(DATA_PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString(PROJECTS_KEY, projects).apply();
        ProjectWidgetProvider.updateAll(context);
        call.resolve(new JSObject());
    }
}
