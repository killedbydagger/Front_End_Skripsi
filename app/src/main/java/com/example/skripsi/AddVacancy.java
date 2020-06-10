package com.example.skripsi;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.engine.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class AddVacancy extends AppCompatActivity {

    private RequestQueue requestQueue;

    EditText et_title, et_gaji, et_deskripsi;

    TextView tv_dueDate;

    private SimpleDateFormat dateFormatter, dateFormatter2;

    Spinner sp_kategori, sp_location, sp_position;

    Button btn_add;

    Map<String, Boolean> validationChecks = new HashMap<>();
    HashMap<String, Integer> compared_position = new HashMap<>();
    ArrayList<String> positionArray = new ArrayList<>();

    private DatePickerDialog datePickerDialog;
    private String tanggal;

    static int PRIVATE_MODE = 0;
    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vacancy);

        sp_kategori = findViewById(R.id.sp_kategori);
        sp_location = findViewById(R.id.sp_location);
        sp_position = findViewById(R.id.sp_position);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        tv_dueDate = findViewById(R.id.tv_dueDate);
        tv_dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        et_title = findViewById(R.id.et_title);
        et_gaji = findViewById(R.id.et_gaji);
        et_deskripsi = findViewById(R.id.et_deskripsi);
        btn_add = findViewById(R.id.btn_add);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();

        try {
            sharedPreferences = sessionManager.context.getSharedPreferences("LOGIN", PRIVATE_MODE);
            editor = sharedPreferences.edit();
            setCategorySpinner(user.get(sessionManager.CATEGORY_DATA));
            setLocationSpinner(user.get(sessionManager.LOCATION_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sp_kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                compared_position.clear();
                positionArray.clear();
                sp_position.setAdapter(null);
                if (position == 0) {
                    et_gaji.setText("");
                    et_gaji.setEnabled(false);
                    et_gaji.setBackgroundResource(R.drawable.edit_text_card_gray);
                    sp_position.setEnabled(false);
                    sp_position.setBackgroundResource(R.drawable.edit_text_card_gray);
                } else if (position == 6) {
                    et_gaji.setText("0");
                    et_gaji.setEnabled(false);
                    et_gaji.setBackgroundResource(R.drawable.edit_text_card_gray);
                    sp_position.setEnabled(false);
                    sp_position.setBackgroundResource(R.drawable.edit_text_card_gray);
                } else {
                    et_gaji.setText("");
                    et_gaji.setEnabled(true);
                    et_gaji.setBackgroundResource(R.drawable.edit_text_card);
                    sp_position.setEnabled(true);
                    sp_position.setBackgroundResource(R.drawable.edit_text_card);
                    try {
                        loadPositionData(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        HashMap<String, String> business = sessionManager.getBusinessDetail();
        final String businessId = business.get(sessionManager.BUSINESS_ID);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCategory();
                validateTitle();
                validateLocation();
                validateSalary();
                validateDescription();
                validateDate();

                if (!validationChecks.containsValue(false)) {
                    int tampung;
                    if (sp_kategori.getSelectedItemPosition() != 6) {
                        tampung = compared_position.get(sp_position.getSelectedItem().toString());
                    } else {
                        tampung = 7;
                    }
                    try {
                        String deskripsi = et_deskripsi.getText().toString();
                        deskripsi = deskripsi.replace(System.getProperty("line.separator"),"/n");
                        add(businessId, sp_kategori.getSelectedItemPosition(), et_title.getText().toString(), deskripsi, et_gaji.getText().toString(), sp_location.getSelectedItemPosition(), tampung, tanggal);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void showDateDialog() {

        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                String date = dateFormatter.format(newDate.getTime());
                SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
                Date oneWayTripDate = null;
                try {
                    oneWayTripDate = input.parse(date);
                    tv_dueDate.setText(output.format(oneWayTripDate));
                    System.out.println("ini tanggalnya: " + oneWayTripDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                tanggal = dateFormatter2.format(newDate.getTime());
                validateDate();
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void validatePosition() {
        if (sp_position.getSelectedItemPosition() == 0) {
            ((TextView) sp_position.getSelectedView()).setError("Please choose your position");
            validationChecks.put("Position", false);
        } else {
            ((TextView) sp_position.getSelectedView()).setError(null);
            validationChecks.put("Position", true);
        }
    }

    private void validateDate() {
        if (tv_dueDate.getText().toString().isEmpty()) {
            tv_dueDate.setError("Field can't be empty");
            validationChecks.put("Due_Date", false);
        } else {
            tv_dueDate.setError(null);
            validationChecks.put("Due_Date", true);
        }
    }

    private void validateCategory() {
        if (sp_kategori.getSelectedItemPosition() == 0) {
            ((TextView) sp_kategori.getSelectedView()).setError("Please choose your category");
            validationChecks.put("Category", false);
        } else {
            ((TextView) sp_kategori.getSelectedView()).setError(null);
            validationChecks.put("Category", true);

            if (sp_kategori.getSelectedItemPosition() != 6) {
                validatePosition();
            }
        }
    }

    private void validateTitle() {
        if (et_title.getText().toString().isEmpty()) {
            et_title.setError("Field can't be empty");
            validationChecks.put("Title", false);
        } else {
            validationChecks.put("Title", true);
        }
    }

    private void validateLocation() {
        if (sp_location.getSelectedItemPosition() == 0) {
            ((TextView) sp_location.getSelectedView()).setError("Please choose your location");
            validationChecks.put("Location", false);
        } else {
            ((TextView) sp_location.getSelectedView()).setError(null);
            validationChecks.put("Location", true);
        }
    }

    private void validateSalary() {
        if (et_gaji.getText().toString().isEmpty()) {
            et_gaji.setError("Field can't be empty");
            validationChecks.put("Salary", false);
        } else {
            validationChecks.put("Salary", true);
        }
    }

    private void validateDescription() {
        if (et_deskripsi.getText().toString().isEmpty()) {
            et_deskripsi.setError("Field can't be empty");
            validationChecks.put("Description", false);
        } else {
            validationChecks.put("Description", true);
        }
    }

    private void setLocationSpinner(String json) throws JSONException {
        ArrayList<String> locationArray = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray locationJSON = jsonObject.getJSONArray("data");
        JSONObject object;
        locationArray.add("--- Choose Location ---");
        for (int i = 0; i < locationJSON.length(); i++) {
            object = locationJSON.getJSONObject(i);
            locationArray.add(object.getString("location_name"));
        }
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationArray);
        locationArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_location.setAdapter(locationArrayAdapter);
    }

    private void setCategorySpinner(String json) throws JSONException {
        ArrayList<String> locationArray = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray locationJSON = jsonObject.getJSONArray("data");
        JSONObject object;
        locationArray.add("--- Choose category ---");
        for (int i = 0; i < locationJSON.length(); i++) {
            object = locationJSON.getJSONObject(i);
            locationArray.add(object.getString("category_name"));
        }
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationArray);
        locationArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_kategori.setAdapter(locationArrayAdapter);
    }

    private void loadPositionData(int categoryId) throws JSONException {
        System.out.println(categoryId);
        if (sp_position.getSelectedItemPosition() != 0) {
            String URL = "https://springjava-1591708327203.azurewebsites.net/CategoryPosition/getCategoryPosition";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("category_id", categoryId);

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String status = response.getString("status");
                        if (status.equals("Success")) {
                            System.out.println("MASUK SUKSES");
                            positionArray.add("--- Choose position ---");
                            JSONArray positionJSON = response.getJSONArray("data");
                            JSONObject object;
                            for (int i = 0; i < positionJSON.length(); i++) {
                                object = positionJSON.getJSONObject(i);
                                JSONObject object1 = object.getJSONObject("position");
                                positionArray.add(object1.getString("position_name"));
                                compared_position.put(object1.getString("position_name"), object1.getInt("position_id"));
                            }
                            ArrayAdapter<String> positionArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, positionArray);
                            positionArrayAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            sp_position.setAdapter(positionArrayAdapter);
                        } else {
                            // Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> params = new HashMap<String, String>();
                    params.put("Context-Type", "application/json");
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        }
    }

    private void add(String businessId, int categoryId, String title, String description, String salary, int locationId, int positionId, String date) throws JSONException {
        Context mContext = AddVacancy.this;
        String URL = "https://springjava-1591708327203.azurewebsites.net/Vacancy/addNewVacancy";
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("business_id", businessId);
        jsonBody.put("category_id", categoryId);
        jsonBody.put("title", title);
        jsonBody.put("description", description);
        jsonBody.put("salary", salary);
        jsonBody.put("location_id", locationId);
        jsonBody.put("position_id", positionId);
        jsonBody.put("due_date", date);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Succes to add New Vacancy", Toast.LENGTH_LONG).show();
                        finish();
                    } else if (status.equals("Reached limit")) {
                        Toast.makeText(getApplicationContext(), "Already reach maximum limit posting", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to add New Vacancy", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("Context-Type", "applicatiom/json");
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }
}
