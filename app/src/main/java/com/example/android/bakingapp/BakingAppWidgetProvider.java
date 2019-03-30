package com.example.android.bakingapp;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class BakingAppWidgetProvider extends AppWidgetProvider {

    private static String mWidgetIngredientsList;
    private static String mRecipeName;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context,
                                AppWidgetManager appWidgetManager,
                                String recipeName,
                                String ingredients,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget_provider);
        views.setTextViewText(R.id.widget_text_recipe_name, recipeName);
        views.setTextViewText(R.id.widget_ingredients, ingredients);

        //Create an intent to launch the MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                0);

        //Launch the PendingIntent when the TextView "Recipe Name" is clicked on
        views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);

        // Instruct the widget manager to update the widget
        //Note that this method call belongs to the AppWidgetManager class
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    //Called when a new widget is created and every update interval
    //AppWidgetManager class gives access to information about all existin widgets on the homescreen
    //Also provides access to the developer to force an update on all existing widgets
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context,
                    appWidgetManager,
                    mRecipeName,
                    mWidgetIngredientsList,
                    appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void updateBakingAppWidgets (
            Context context,
            AppWidgetManager appWidgetManager,
            String recipeName,
            String ingredientsList,
            int[] appWidgetIds) {

        mWidgetIngredientsList = ingredientsList;

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeName, ingredientsList, appWidgetId);
        }

    }
}

