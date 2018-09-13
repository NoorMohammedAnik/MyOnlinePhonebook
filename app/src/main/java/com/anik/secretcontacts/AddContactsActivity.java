package com.anik.secretcontacts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AddContactsActivity extends AppCompatActivity {



    //Java object declaration
    Button btnSave;
    EditText etxtName,etxtCell,etxtEmail;
    private ProgressDialog loading;

    String getUserCell;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

//Link to java and xml id
        btnSave=(Button)findViewById(R.id.btn_save);
        etxtName=(EditText)findViewById(R.id.etxt_name);
        etxtCell=(EditText)findViewById(R.id.etxt_cell);
        etxtEmail=(EditText)findViewById(R.id.etxt_email);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Add Contacts");//for actionbar title

        //Fetching cell from shared preferences
        SharedPreferences sharedPreferences;
        sharedPreferences =getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        getUserCell = sharedPreferences.getString(Constant.CELL_SHARED_PREF, "Not Available");


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddContactsActivity.this);
                builder.setIcon(R.mipmap.ic_launcher)
                        .setMessage("Want to Save Contact?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                // Perform Your Task Here--When Yes Is Pressed.
                                SaveContact(); //call SaveContact function
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform Your Task Here--When No is pressed
                                dialog.cancel();
                            }
                        }).show();


            }
        });

    }





    //Save contact method
    public void  SaveContact()
    {

        final String name=etxtName.getText().toString();
        final String cell=etxtCell.getText().toString();
        final String email=etxtEmail.getText().toString();


        if (name.isEmpty())
        {
            Toast.makeText(this, "Name Can't Empty", Toast.LENGTH_SHORT).show();
        }
        else if (cell.isEmpty())
        {
            Toast.makeText(this, "Cell Can't Empty", Toast.LENGTH_SHORT).show();
        }

        else if(email.isEmpty())
        {
            Toast.makeText(this, "Email Can't Empty", Toast.LENGTH_SHORT).show();
        }

        else {
            loading = new ProgressDialog(this);
            loading.setIcon(R.drawable.wait_icon);
            loading.setTitle("Adding");
            loading.setMessage("Please wait....");
            loading.show();

            String URL = Constant.SAVE_URL;


            //Creating a string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            //for track response in logcat
                            Log.d("RESPONSE", response);
                            // Log.d("RESPONSE", userCell);


                            //If we are getting success from server
                            if (response.equals("success")) {

                                loading.dismiss();
                                //Starting profile activity

                                Intent intent = new Intent(AddContactsActivity.this, HomeActivity.class);
                                Toast.makeText(AddContactsActivity.this, " Successfully Saved!", Toast.LENGTH_SHORT).show();
                                startActivity(intent);

                            }


                            //If we are getting success from server
                            else if (response.equals("failure")) {

                                loading.dismiss();
                                //Starting profile activity

                                //Intent intent = new Intent(AddContactsActivity.this, HomeActivity.class);
                                Toast.makeText(AddContactsActivity.this, " Save fail!", Toast.LENGTH_SHORT).show();
                                //startActivity(intent);

                            } else {

                                loading.dismiss();
                                Toast.makeText(AddContactsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();

                            }

                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                            Toast.makeText(AddContactsActivity.this, "No Internet Connection or \nThere is an error !!!", Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request


                    params.put(Constant.KEY_USER_CELL, getUserCell);
                    params.put(Constant.KEY_NAME, name);
                    params.put(Constant.KEY_CELL, cell);
                    params.put(Constant.KEY_EMAIL, email);



                    // Log.d("ID", getID);

                    //returning parameter
                    return params;
                }
            };


            //Adding the string request to the queue
            RequestQueue requestQueue = Volley.newRequestQueue(AddContactsActivity.this);
            requestQueue.add(stringRequest);
        }

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