package com.anik.secretcontacts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

public class ContactsDetailsActivity extends AppCompatActivity {



    EditText etxtName, etxtCell, etxtEmail;
    String getID, getName, getCell, getEmail;

    Button btnEdit, btnUpdate, btnDelete;

    private ProgressDialog loading;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_details);

        etxtName = (EditText) findViewById(R.id.etxt_name);
        etxtCell = (EditText) findViewById(R.id.etxt_cell);
        etxtEmail = (EditText) findViewById(R.id.etxt_email);

        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnDelete = (Button) findViewById(R.id.btn_delete);

        //For disable edit text
        etxtName.setEnabled(false);
        etxtCell.setEnabled(false);
        etxtEmail.setEnabled(false);

        getID = getIntent().getExtras().getString("id");
        getName = getIntent().getExtras().getString("name");
        getCell = getIntent().getExtras().getString("cell");
        getEmail = getIntent().getExtras().getString("email");

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Contact Details");//for actionbar title


        etxtName.setText(getName);
        etxtCell.setText(getCell);
        etxtEmail.setText(getEmail);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etxtName.setEnabled(true);
                etxtCell.setEnabled(true);
                etxtEmail.setEnabled(true);


                etxtName.setTextColor(Color.BLUE);
                etxtCell.setTextColor(Color.BLUE);
                etxtEmail.setTextColor(Color.BLUE);


            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(etxtName.isEnabled()) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactsDetailsActivity.this);
                    builder.setIcon(R.drawable.loading)
                            .setMessage("Want to Update Contact?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {


                                    // Perform Your Task Here--When Yes Is Pressed.
                                    UpdateContact();
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

                else
                {
                    Toast.makeText(ContactsDetailsActivity.this, "Please edit data!", Toast.LENGTH_SHORT).show();
                }


            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(ContactsDetailsActivity.this);
                builder.setIcon(R.drawable.loading)
                        .setMessage("Want to Delete Contact?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                // Perform Your Task Here--When Yes Is Pressed.
                                DataDeleteFromServer();
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


    //update contact method
    public void UpdateContact() {

        final String name = etxtName.getText().toString();
        final String cell = etxtCell.getText().toString();
        final String email = etxtEmail.getText().toString();


        if (name.isEmpty()) {
            Toast.makeText(this, "Name Can't Empty", Toast.LENGTH_SHORT).show();
        } else if (cell.isEmpty()) {
            Toast.makeText(this, "Cell Can't Empty", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Email Can't Empty", Toast.LENGTH_SHORT).show();
        } else {
            loading = new ProgressDialog(this);
            // loading.setIcon(R.drawable.wait_icon);
            loading.setTitle("Update");
            loading.setMessage("Please wait....");
            loading.show();

            String URL = Constant.UPDATE_URL;


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

                                Intent intent = new Intent(ContactsDetailsActivity.this, HomeActivity.class);
                                Toast.makeText(ContactsDetailsActivity.this, " Successfully Updated!", Toast.LENGTH_SHORT).show();
                                startActivity(intent);

                            }


                            //If we are getting success from server
                            else if (response.equals("failure")) {

                                loading.dismiss();
                                //Starting profile activity

                                Intent intent = new Intent(ContactsDetailsActivity.this, HomeActivity.class);
                                Toast.makeText(ContactsDetailsActivity.this, " Update fail!", Toast.LENGTH_SHORT).show();
                                //startActivity(intent);

                            } else {

                                loading.dismiss();
                                Toast.makeText(ContactsDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();

                            }

                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                            Toast.makeText(ContactsDetailsActivity.this, "No Internet Connection or \nThere is an error !!!", Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request

                    params.put(Constant.KEY_ID, getID);
                    params.put(Constant.KEY_NAME, name);
                    params.put(Constant.KEY_CELL, cell);
                    params.put(Constant.KEY_EMAIL, email);


                    Log.d("ID", getID);

                    //returning parameter
                    return params;
                }
            };


            //Adding the string request to the queue
            RequestQueue requestQueue = Volley.newRequestQueue(ContactsDetailsActivity.this);
            requestQueue.add(stringRequest);
        }

    }


    //Delete method for deleting contacts
    public void DataDeleteFromServer() {
        loading = new ProgressDialog(this);
        // loading.setIcon(R.drawable.wait_icon);
        loading.setTitle("Delete");
        loading.setMessage("Please wait....");
        loading.show();

        String URL = Constant.DELETE_URL;


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

                            Intent intent = new Intent(ContactsDetailsActivity.this, HomeActivity.class);
                            Toast.makeText(ContactsDetailsActivity.this, " Successfully Deleted!", Toast.LENGTH_SHORT).show();
                            startActivity(intent);

                        }


                        //If we are getting success from server
                        else if (response.equals("failure")) {

                            loading.dismiss();
                            //Starting profile activity

                            Intent intent = new Intent(ContactsDetailsActivity.this, HomeActivity.class);
                            Toast.makeText(ContactsDetailsActivity.this, " Delete fail!", Toast.LENGTH_SHORT).show();
                            //startActivity(intent);

                        } else {

                            loading.dismiss();
                            Toast.makeText(ContactsDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();

                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want

                        Toast.makeText(ContactsDetailsActivity.this, "No Internet Connection or \nThere is an error !!!", Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request

                params.put(Constant.KEY_ID, getID);

                Log.d("ID", getID);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(ContactsDetailsActivity.this);
        requestQueue.add(stringRequest);

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