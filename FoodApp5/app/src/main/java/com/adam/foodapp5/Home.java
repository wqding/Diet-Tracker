package com.adam.foodapp5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class Home extends AppCompatActivity {

    private float dailyCalories = -1;
    private float dailyCarbs = -1;
    private float dailyFat = -1;
    private float dailyProtein = -1;

    private HashMap macrosEaten = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().clear().commit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        boolean ate = false;
        if(getIntent().getSerializableExtra("macrosEaten") != null){
            macrosEaten = (HashMap) getIntent().getSerializableExtra("macrosEaten");
            ate = true;
        }

        Button scanFoodBtn = (Button) findViewById(R.id.scanFoodBtn);
        Button searchBtn = (Button) findViewById(R.id.searchFoodBtn);

        scanFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Home.this, SearchFood.class);
                startActivity(intent);
            }
        });

        retrieveTotalMacros();

        if(ate){
            updateTotalMacros(macrosEaten);
        }


        TextView dailyCaloriesText = (TextView) findViewById(R.id.caloriesTextView);
        dailyCaloriesText.setText("Calories: " + dailyCalories);
        TextView dailyCarbsText = (TextView) findViewById(R.id.carbsTextView);
        dailyCarbsText.setText("Carbohydrates: " + dailyCarbs);
        TextView dailyFatText = (TextView) findViewById(R.id.fatTextView);
        dailyFatText.setText("Fat: " + dailyFat);
        TextView dailyProteinText = (TextView) findViewById(R.id.proteinTextView);
        dailyProteinText.setText("Protein: " + dailyProtein);
    }

    public void retrieveTotalMacros(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        dailyCalories = preferences.getFloat("dailyCalories", -1);
        if(dailyCalories == -1)
        {
            dailyCalories = initMacro("dailyCalories");
        }

        dailyCarbs = preferences.getFloat("dailyCarbs", -1);
        if(dailyCarbs == -1)
        {
            dailyCarbs = initMacro( "dailyCarbs");
        }

        dailyFat = preferences.getFloat("dailyFat", -1);
        if(dailyFat == -1)
        {
            dailyFat = initMacro("dailyFat");
        }

        dailyProtein = preferences.getFloat("dailyProtein", -1);
        if(dailyProtein == -1)
        {
            dailyProtein = initMacro("dailyProtein");
        }
    }
    
    public float initMacro(String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, 0);
        editor.commit();
        return 0;
    }


    public void updateTotalMacros(HashMap macrosEaten){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        dailyCalories += (float) macrosEaten.get("calories");
        editor.putFloat("dailyCalories", dailyCalories);

        dailyCarbs += (float) macrosEaten.get("carbs");
        editor.putFloat("dailyCarbs", dailyCarbs);

        dailyFat += (float) macrosEaten.get("fat");
        editor.putFloat("dailyFat", dailyFat);

        dailyProtein += (float) macrosEaten.get("protein");
        editor.putFloat("dailyProtein", dailyProtein);

        editor.commit();
    }
}
