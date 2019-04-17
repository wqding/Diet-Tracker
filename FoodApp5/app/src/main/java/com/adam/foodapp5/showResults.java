package com.adam.foodapp5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class showResults extends AppCompatActivity {
    private TextView nameText = null;
    private TextView brandText = null;
    private TextView descriptionText = null;
    private TextView caloriesText = null;
    private TextView carbsText = null;
    private TextView fatText = null;
    private TextView proteinText = null;
    private TextView servSizeText = null;
    private TextView servWeightText = null;
    private EditText amountEatenText = null;
    private String url = "";
    private float servWeight;
    private float calories;
    private float carbs;
    private float fat;
    private float protein;

    private EditText servingWeightEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getIntent().getStringExtra("productId") != null){
            url = "https://api.nutritionix.com/v1_1/item?id=" + getIntent().getStringExtra("productId") + "&appId=0ab9e692&appKey=6d1e9dcefe67a7f3295127cf5bcc3436";
        }
        else if(getIntent().getStringExtra("barcode") != null){
            url = "https://api.nutritionix.com/v1_1/item?upc=" + getIntent().getStringExtra("barcode") + "&appId=0ab9e692&appKey=6d1e9dcefe67a7f3295127cf5bcc3436";
        }
//        url = "https://api.nutritionix.com/v1_1/item?upc=013562610020&appId=0ab9e692&appKey=6d1e9dcefe67a7f3295127cf5bcc3436";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);

        nameText = (TextView) findViewById(R.id.NameTextView);
        brandText = (TextView) findViewById(R.id.BrandTextView);
        descriptionText = (TextView) findViewById(R.id.DescTextView);
        caloriesText = (TextView) findViewById(R.id.CaloriesTextView);
        carbsText = (TextView) findViewById(R.id.CarbsTextView);
        fatText = (TextView) findViewById(R.id.FatTextView);
        proteinText = (TextView) findViewById(R.id.ProteinTextView);
        servSizeText = (TextView) findViewById(R.id.ServingSizeTextView);
        servWeightText = (TextView) findViewById(R.id.ServingWeightTextView);
        amountEatenText = (EditText) findViewById(R.id.amountEaten);

        Button addFoodBtn = (Button) findViewById(R.id.addFoodBtn);
        addFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float amountEaten = Float.parseFloat(amountEatenText.getText().toString());

                HashMap<String, Float> macrosEaten = new HashMap();
                if(servWeight == -1){
                    servWeight = Float.parseFloat(servingWeightEdit.getText().toString());
                }
                macrosEaten.put("calories", calories * (amountEaten/servWeight));
                macrosEaten.put("carbs", carbs * (amountEaten/servWeight));
                macrosEaten.put("fat", fat * (amountEaten/servWeight));
                macrosEaten.put("protein", protein * (amountEaten/servWeight));

                Intent intent = new Intent(showResults.this, Home.class);
                intent.putExtra("macrosEaten", macrosEaten);
                startActivity(intent);
            }
        });
        getResponse(url, this);
    }

    public void setText(String jsonData){
        try {
            JSONObject product = new JSONObject(jsonData);
            if(!product.has(Constants.PRODUCT_NAME)){
                Toast.makeText(getApplicationContext(),"Barcode not found, please scan another or search manually",Toast.LENGTH_LONG).show();
                nameText.setText("Barcode not found, please scan another or search manually");
            }

            else {
                nameText.setText("Name: " + product.getString(Constants.PRODUCT_NAME));
                brandText.setText("Brand: " + product.getString(Constants.PRODUCT_BRAND));
                descriptionText.setText("Description: " + product.getString(Constants.PRODUCT_DESCRIPTION));

                if (product.isNull(Constants.CALORIES)) {
                    calories = 0;
                } else {
                    calories = Float.parseFloat((String) product.getString(Constants.CALORIES));
                }
                caloriesText.setText("Calories: " + calories);

                if (product.isNull(Constants.CARBS)) {
                    carbs = 0;
                } else {
                    carbs = Float.parseFloat((String) product.getString(Constants.CARBS));
                }
                carbsText.setText("Carbohydrates: " + carbs + "g");

                if (product.isNull(Constants.FAT)) {
                    fat = 0;
                } else {
                    fat = Float.parseFloat((String) product.getString(Constants.FAT));
                }
                fatText.setText("Fat: " + fat + "g");

                if (product.isNull(Constants.PROTEIN)) {
                    protein = 0;
                } else {
                    protein = Float.parseFloat((String) product.getString(Constants.PROTEIN));
                }
                proteinText.setText("Protein: " + protein + "g");

                if (product.isNull(Constants.SERVING_WEIGHT)) {
                    servingWeightEdit = findViewById(R.id.servingWeightEditText);
                    servWeightText.setText("Not found, please enter manually in grams: ");
                    servWeight = -1;
                } else {
                    servWeight = Float.parseFloat((String) product.getString(Constants.SERVING_WEIGHT));
                    servWeightText.setText("Serving Weight: " + servWeight + "g");
                }

                servSizeText.setText("Serving Size: " + (String) product.getString(Constants.SERVING_QTY) + " "
                        + product.getString(Constants.SERVING_UNIT));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void getResponse(String url, final Context context){

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nameText.setText("Barcode not found, please scan another or search manually");
                Toast.makeText(getApplicationContext(),"Barcode not found, please scan another or search manually",Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);
    }
}
