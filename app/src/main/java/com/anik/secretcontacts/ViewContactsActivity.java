package com.anik.secretcontacts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewContactsActivity extends AppCompatActivity {

    ListView CustomList;
    String getCell;
    ImageView imgNoData;
    private ProgressDialog loading;

    Button btnSearch;
    EditText txtSearch;

    int MAX_SIZE=999;
    public String userID[]=new String[MAX_SIZE];
    public String userName[]=new String[MAX_SIZE];
    public String userCell[]=new String[MAX_SIZE];
    public String userEmail[]=new String[MAX_SIZE];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        CustomList=(ListView)findViewById(R.id.contact_list);
        //imgNoData=(ImageView)findViewById(R.id.imgNoData);

        btnSearch=findViewById(R.id.btn_search);
        txtSearch=findViewById(R.id.txt_search);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Contacts");

        //Fetching cell from shared preferences
        SharedPreferences sharedPreferences;
        sharedPreferences =getSharedPreferences(MyKey.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        getCell = sharedPreferences.getString(MyKey.CELL_SHARED_PREF, "Not Available");

        //Log
        Log.d("SP_CELL",getCell);


        //call function to get data
        getData("");

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String search=txtSearch.getText().toString();

                if (search.isEmpty())
                {
                    Toast.makeText(ViewContactsActivity.this, "Please enter name!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    getData(search);
                }



            }
        });





    }


    private void getData(String text) {


        //for showing progress dialog
        loading = new ProgressDialog(ViewContactsActivity.this);
        loading.setIcon(R.drawable.wait_icon);
        loading.setTitle("Loading");
        loading.setMessage("Please wait....");
        loading.show();

        String URL = MyKey.CONTACT_VIEW_URL+getCell+"&text="+text;

        StringRequest stringRequest = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        loading.dismiss();
                        Toast.makeText(ViewContactsActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(ViewContactsActivity.this);
        requestQueue.add(stringRequest);
    }



    private void showJSON(String response) {

        //Create json object for receiving jason data
        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(MyKey.JSON_ARRAY);


            if (result.length()==0)
            {
                Toast.makeText(ViewContactsActivity.this, "No Data Available!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ViewContactsActivity.this, HomeActivity.class);

                startActivity(intent);
                //imgNoData.setImageResource(R.drawable.nodata);
            }

            else {

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String id = jo.getString(MyKey.KEY_ID);
                    String name = jo.getString(MyKey.KEY_NAME);
                    String cell = jo.getString(MyKey.KEY_CELL);
                    String email = jo.getString(MyKey.KEY_EMAIL);

                    //insert data into array for put extra
                    userID[i] = id;
                    userName[i] = name;
                    userCell[i] = cell;
                    userEmail[i] = email;

                    //put value into Hashmap
                    HashMap<String, String> user_data = new HashMap<>();
                    user_data.put(MyKey.KEY_NAME, name);
                    user_data.put(MyKey.KEY_CELL, cell);
                    user_data.put(MyKey.KEY_EMAIL, email);

                    list.add(user_data);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(
                ViewContactsActivity.this, list, R.layout.contact_list_items,
                new String[]{MyKey.KEY_NAME, MyKey.KEY_CELL,MyKey.KEY_EMAIL},
                new int[]{R.id.txt_name, R.id.txt_cell,R.id.txt_email});
        CustomList.setAdapter(adapter);



        CustomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                Intent intent=new Intent(ViewContactsActivity.this,ContactsDetailsActivity.class);
                intent.putExtra("id",userID[position]);
                intent.putExtra("name",userName[position]);
                intent.putExtra("cell",userCell[position]);
                intent.putExtra("email",userEmail[position]);

                //for logcat
                Log.d("ID",userID[position]);
                Log.d("NAME",userName[position]);
                Log.d("CELL",userCell[position]);
                Log.d("EMAIL",userEmail[position]);

                startActivity(intent);



            }
        });



    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}