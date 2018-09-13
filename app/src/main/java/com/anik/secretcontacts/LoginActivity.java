package com.anik.secretcontacts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

public class LoginActivity extends AppCompatActivity {

    //EditText object declaration
    EditText etxtCell,etxtPassword;
    //Button object declaration
    Button btnSignUp,btnLogin;
    //ProgressDialog object declaration
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Link up to java object to xml Button id
        btnSignUp=(Button)findViewById(R.id.btn_signup);
        btnLogin=(Button)findViewById(R.id.btn_login);


        //Link up to java object to xml EditText id
        etxtCell=(EditText)findViewById(R.id.etxt_cell);
        etxtPassword=(EditText)findViewById(R.id.etxt_password);

        //Click listener in Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Call login function
                login();

            }
        });

        //Click listener in Signup Button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }


    //Login function
    private void login() {
        //Getting values from edit texts
        final String cell = etxtCell.getText().toString().trim();
        final String password = etxtPassword.getText().toString().trim();


        //Checking usercell field/validation
        if (cell.isEmpty()) {
            etxtCell.setError("Please enter cell !");
            requestFocus(etxtCell);
        }



        //Checking password field/validation
        else if (password.isEmpty()) {
            etxtPassword.setError("Password can't be empty!");
            requestFocus(etxtPassword);
        }

        //showing progress dialog

        else {

            loading = new ProgressDialog(this);
            // loading.setIcon(R.drawable.wait_icon);
            loading.setTitle("Login");
            loading.setMessage("Please wait....");
            loading.show();

            //Creating a string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("Response",""+response);
                            //If we are getting success from server
                            if (response.equals("success")) {
                                //Creating a shared preference

                                SharedPreferences sp = LoginActivity.this.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                                //Creating editor to store values to shared preferences
                                SharedPreferences.Editor editor = sp.edit();
                                //Adding values to editor
                                editor.putString(Constant.CELL_SHARED_PREF, cell);

                                //Saving values to editor
                                editor.commit();

                                loading.dismiss();
                                //Starting Home activity
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            }




                            else if(response.equals("failure")) {
                                //If the server response is not success
                                //Displaying an error message on toast
                                Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_LONG).show();
                                loading.dismiss();
                            }

                            else {
                                //If the server response is not success
                                //Displaying an error message on toast
                                Toast.makeText(LoginActivity.this, "Invalid user cell or password", Toast.LENGTH_LONG).show();
                                loading.dismiss();
                            }
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                            Toast.makeText(LoginActivity.this, "There is an error !!!", Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put(Constant.KEY_CELL, cell);
                    params.put(Constant.KEY_PASSWORD, password);

                    //returning parameter
                    return params;
                }
            };

            //Adding the string request to the queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

    }

    //for request focus
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}