package com.example.website;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class MainActivity extends AppCompatActivity {

    private TextView all_record;
    private TextView stu_name;
    private TextView stu_id;
    private Button button;
    private EditText get_student_id_frontend;

    private Integer counter;
    private Integer Respo_len;
    private String get_stu_name;
    private String get_stu_id;
    private Integer get_receipt_amount;
    private TableView tableView;

    void myMethod() {
        stu_name.setText("Fetching...");
        all_record.setText("");
        String data_table [] [] = {};
        tableView.setDataAdapter(new SimpleTableDataAdapter(this,data_table));
    }

    private void myTableData(String[][] data_table) {
        stu_name.setVisibility(View.VISIBLE);
        stu_id.setVisibility(View.VISIBLE);
        stu_name.setTextColor(getResources().getColor(R.color.black));
        stu_name.setText(get_stu_name);
        stu_id.setText(get_stu_id);
        tableView.setDataAdapter(new SimpleTableDataAdapter(this,data_table));
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().hide();


        button = findViewById(R.id.button);
        stu_name = findViewById(R.id.stu_name);
        stu_id = findViewById(R.id.stu_id);
        all_record = findViewById(R.id.all_record);
        get_student_id_frontend = findViewById(R.id.get_student_id_frontend);

        tableView = findViewById(R.id.table_data_view);
        String[] headers={"Date", "Receipts", "Amount"};
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this,headers));

        Respo_len = 0;

        stu_name.setVisibility(View.INVISIBLE);
        stu_id.setVisibility(View.INVISIBLE);





        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myMethod();
                DecimalFormat formatter = new DecimalFormat("#,###");

                String get_student_id = get_student_id_frontend.getText().toString();

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://indus.rubick.org/accounts/get_data_api.php?stu_id='" + get_student_id + "'", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        counter = 0;

                        get_stu_name = null;
                        String [][] data_table;
                        Respo_len = response.length();
                        data_table = new String[response.length()+1][3];
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonobject = null;
                            try {
                                jsonobject = response.getJSONObject(i);
                                String stu_date = jsonobject.getString("Date");
                                String separated[] = stu_date.split("-");

                                String stu_name = jsonobject.getString("Name");
                                String stu_id = jsonobject.getString("ID");
                                String stu_amount = jsonobject.getString("Amount");
                                get_receipt_amount = Integer.parseInt(stu_amount);

                                counter += Integer.parseInt(stu_amount);
                                get_stu_name = stu_name;
                                get_stu_id = stu_id;
                                if (separated[0].length() < 2) {
                                    data_table[i][0] = "0" + stu_date;
                                    data_table[i][1] = jsonobject.getString("Receipts");
                                    data_table[i][2] = (formatter.format(get_receipt_amount));
                                }
                                else {
                                    data_table[i][0] = stu_date;
                                    data_table[i][1] = jsonobject.getString("Receipts");
                                    data_table[i][2] = (formatter.format(get_receipt_amount));
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        data_table[Respo_len][0] = "";
                        data_table[Respo_len][1] = "TOTAL";
                        data_table[Respo_len][2] = (formatter.format(counter)) + "\\-";
                        myTableData(data_table);
                        if (get_stu_name == null) {
                            stu_name.setText("No Student Found :(");
                            stu_id.setText(get_student_id);
                            stu_name.setTextColor(getResources().getColor(R.color.red));
                        }


                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", "Some_Thing_Went_Wrong");
                    }
                });

                requestQueue.add(jsonArrayRequest);

            }


        });



    }


}