package com.adam.foodapp5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchFood extends AppCompatActivity {
    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    public SimpleAdapter sa;
    public EditText searchBarText;
    public ListView productList;

    public String searchedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        productList = (ListView)findViewById(R.id.list);
        searchBarText = findViewById(R.id.searchBarText);

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchFood.this, showResults.class);
                String productId = list.get(position).get("id");
                intent.putExtra("productId", productId);
                startActivity(intent);
            }
        });

        Button searchBarBtn = findViewById(R.id.searchBarBtn);
        searchBarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchedProduct = searchBarText.getText().toString();
                getSearchResponse(searchedProduct, getApplicationContext());
            }
        });
    }

    public void getProductResponse(String idOrBarcode){
        String url = "https://api.nutritionix.com/v1_1/item?id=" + idOrBarcode + "&appId=0ab9e692&appKey=6d1e9dcefe67a7f3295127cf5bcc3436";

    }


    public void getSearchResponse(String search, Context context){
        String url = "https://api.nutritionix.com/v1_1/search/" + search + "?results=0%3A20&cal_min=0&cal_max=50000&fields=item_name%2Cbrand_name%2Citem_id%2Cbrand_id&appId=0ab9e692&appKey=6d1e9dcefe67a7f3295127cf5bcc3436";
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        displayList(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    public void displayList(JSONObject response){
        try{
            JSONArray hits = response.getJSONArray("hits");

            HashMap<String,String> item;

            for(int x = 0; x < hits.length(); x++){
                JSONObject fields = hits.getJSONObject(x).getJSONObject("fields");

                item = new HashMap<String,String>();
                item.put( "name", fields.getString(Constants.NAME));
                item.put( "brand", fields.getString(Constants.BRAND));
                item.put( "id", fields.getString(Constants.ID));
                list.add( item );

                //Use adapter to link data to Views
                sa = new SimpleAdapter(this, list, R.layout.activity_listview,
                        new String[] { "name","brand", "id" },
                        new int[] {R.id.productName, R.id.productBrand, R.id.productId});

                //link list to adapter
                productList.setAdapter(sa);

            }

        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}
