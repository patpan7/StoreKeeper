package com.example.storekeeper.HelperClasses;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.Models.supplierModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String SETTINGS = "settings";



    public DBHelper(@Nullable Context context) {
        super(context, "storekeeper.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table if not exists " + SETTINGS + "(code INTEGER PRIMARY KEY, ip TEXT, port TEXT, standalone int)";
        db.execSQL(createTable);
        ContentValues cv = new ContentValues();
        cv.put("code", 1);
        cv.put("ip", "192.168.1.10");
        cv.put("port", "1433");
        cv.put("standalone", 1);
        db.insert(SETTINGS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public interface MyCallback {
        void onSuccess(String response);

        void onError(String error);
    }

    //products methods
    public void productAdd(productModel productModel, Context context, MyCallback callback) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productAdd.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    callback.onSuccess(message);
                } else callback.onError(message);
            } catch (JSONException e) {
                callback.onError(e.toString());
            }
        }, error -> callback.onError("error")) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("name", productModel.getName());
                paramV.put("barcode", productModel.getBarcode());
                paramV.put("warranty", String.valueOf(productModel.getWarranty()));
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public void productUpdate(productModel productModel, Context context, MyCallback callback) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productUpdate.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    callback.onSuccess(message);
                } else callback.onError(message);
            } catch (JSONException e) {
                callback.onError(e.toString());
            }
        }, error -> callback.onError("error")) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("code", productModel.getCode() + "");
                paramV.put("name", productModel.getName());
                paramV.put("barcode", productModel.getBarcode());
                paramV.put("warranty", String.valueOf(productModel.getWarranty()));
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public void productNextID(Context context, EditText code) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productNextID.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    int message = response.getInt("message");
                    if (status.equals("success")) code.setText(String.valueOf(message));
                    else code.setText("NULL");
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, Throwable::printStackTrace);
        queue.add(request);
    }

    public void productsGetIncomeSum(Context context, int code, EditText incomeSum) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productsGetIncomeSum.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    int message = jsonObject.getInt("message");
                    if (status.equals("success")) incomeSum.setText(String.valueOf(message));
                    else incomeSum.setText("NULL");
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, error -> {
            //callback.onError("error");
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("code", code + "");
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public void productsGetAvailable(Context context, int code, EditText available) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productsGetAvailable.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    int message = jsonObject.getInt("message");
                    if (status.equals("success")) available.setText(String.valueOf(message));
                    else available.setText("NULL");
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, error -> {
            //callback.onError("error");
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("code", code + "");
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public void productsGetCharged(Context context, int code, EditText charged) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productsGetCharged.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    int message = jsonObject.getInt("message");
                    if (status.equals("success")) charged.setText(String.valueOf(message));
                    else charged.setText("NULL");
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, error -> {
            //callback.onError("error");
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("code", code + "");
                return paramV;
            }
        };
        queue.add(stringRequest);
    }
    //-------------------------------------------------------------------

    //employees methods
    public void employeeAdd(employeesModel employeesModel, Context context, MyCallback callback) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/employees/employeeAdd.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    callback.onSuccess(message);
                } else callback.onError(message);
            } catch (JSONException e) {
                callback.onError(e.toString());
            }
        }, error -> callback.onError("error")) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("name", employeesModel.getName());
                paramV.put("phone", employeesModel.getPhone());
                paramV.put("mobile", employeesModel.getMobile());
                paramV.put("mail", employeesModel.getMail());
                paramV.put("work", employeesModel.getWork());
                paramV.put("id", employeesModel.getId());
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public void employeeUpdate(employeesModel employeesModel, Context context, MyCallback callback) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/employees/employeeUpdate.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    callback.onSuccess(message);
                } else callback.onError(message);
            } catch (JSONException e) {
                callback.onError(e.toString());
            }
        }, error -> callback.onError("error")) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("code", employeesModel.getCode() + "");
                paramV.put("name", employeesModel.getName());
                paramV.put("phone", employeesModel.getPhone());
                paramV.put("mobile", employeesModel.getMobile());
                paramV.put("mail", employeesModel.getMail());
                paramV.put("work", employeesModel.getWork());
                paramV.put("id", employeesModel.getId());
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public void employeeNextID(Context context, EditText code) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/employees/employeeNextID.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    int message = response.getInt("message");
                    if (status.equals("success")) code.setText(String.valueOf(message));
                    else code.setText("NULL");
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, Throwable::printStackTrace);
        queue.add(request);
    }

    public void employeeGetChargedProd(Context context, int code, EditText charged) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/employees/employeeGetChargedProd.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    int message = jsonObject.getInt("message");
                    if (status.equals("success")) charged.setText(String.valueOf(message));
                    else charged.setText("NULL");
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, error -> {
            //callback.onError("error");
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("code", code + "");
                return paramV;
            }
        };
        queue.add(stringRequest);
    }
    //-------------------------------------------------------------------

    //suppliers methods
    public void supplierAdd(supplierModel supplierModel, Context context, MyCallback callback) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/suppliers/supplierAdd.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    callback.onSuccess(message);
                } else callback.onError(message);
            } catch (JSONException e) {
                callback.onError(e.toString());
            }
        }, error -> callback.onError(error.toString())) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("code", supplierModel.getCode() + "");
                paramV.put("name", supplierModel.getName());
                paramV.put("phone", supplierModel.getPhone());
                paramV.put("mobile", supplierModel.getMobile());
                paramV.put("mail", supplierModel.getMail());
                paramV.put("afm", supplierModel.getAfm());
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public void supplierUpdate(supplierModel supplierModel, Context context, MyCallback callback) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/suppliers/supplierUpdate.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    callback.onSuccess(message);
                } else callback.onError(message);
            } catch (JSONException e) {
                callback.onError(e.toString());
            }
        }, error -> callback.onError("error")) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("code", supplierModel.getCode() + "");
                paramV.put("name", supplierModel.getName());
                paramV.put("phone", supplierModel.getPhone());
                paramV.put("mobile", supplierModel.getMobile());
                paramV.put("mail", supplierModel.getMail());
                paramV.put("afm", supplierModel.getAfm());
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public void supplierNextID(Context context, EditText code) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/suppliers/supplierNextID.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    int message = response.getInt("message");
                    if (status.equals("success")) code.setText(String.valueOf(message));
                    else code.setText("NULL");
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, Throwable::printStackTrace);
        queue.add(request);
    }

    public void supplierGetAllIncomeProd(Context context, int code, EditText income) {
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/suppliers/supplierGetAllIncomeProd.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    int message = jsonObject.getInt("message");
                    if (status.equals("success")) income.setText(String.valueOf(message));
                    else income.setText("NULL");
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, error -> {
            //callback.onError("error");
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("code", code + "");
                return paramV;
            }
        };
        queue.add(stringRequest);
    }
//-------------------------------------------------------------------

    //income methods
    public void incomeAddNew(ArrayList<String> serial_numbers, ArrayList<String> serial_numbers_return, int prod_code, String date, int supp_code, int warranty, Context context, MyCallback callback) throws ParseException {
        String income_date_format = formatDateForSQL(date);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(Objects.requireNonNull(sdf.parse(income_date_format)));
        c.add(Calendar.MONTH, warranty);
        String warrantyString = sdf.format(c.getTime());
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/incomes/incomeAddNew.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    callback.onSuccess(message);
                } else callback.onError(message);
            } catch (JSONException e) {
                callback.onError(e.toString());
                Log.e("log",e.toString());
            }
        }, error -> callback.onError("error")) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("supp_code", String.valueOf(supp_code));
                paramV.put("date", formatDateForSQL(date));
                String data = new Gson().toJson(serial_numbers);
                paramV.put("serials", data);
                paramV.put("prod_code", String.valueOf(prod_code));
                paramV.put("warranty", warrantyString);
                String data2 = new Gson().toJson(serial_numbers_return);
                paramV.put("serial_return", data2);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

//-------------------------------------------------------------------

    //charge methods
    public void chargeAdd(ArrayList<String> serial_numbers, String date, int emp_code, Context context, MyCallback callback) throws ParseException {
        String income_date_format = formatDateForSQL(date);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(Objects.requireNonNull(sdf.parse(income_date_format)));
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/charges/chargeAddNew.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    callback.onSuccess(message);
                } else callback.onError(message);
            } catch (JSONException e) {
                callback.onError(e.toString());
                Log.e("log",e.toString());
            }
        }, error -> callback.onError("error")) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("emp_code", String.valueOf(emp_code));
                paramV.put("date", formatDateForSQL(date));
                String data = new Gson().toJson(serial_numbers);
                paramV.put("serials", data);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }
    //-------------------------------------------------------------------
    public static String formatDateForSQL(String inDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inSDF = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-MM-dd");
        String outDate = "";
        if (inDate != null) {
            try {
                Date date = inSDF.parse(inDate);
                assert date != null;
                outDate = outSDF.format(date);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return outDate;
    }

    public static String formatDateForAndroid(String inDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inSDF = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outSDF = new SimpleDateFormat("dd/MM/yyyy");
        String outDate = "";
        if (inDate != null) {
            try {
                Date date = inSDF.parse(inDate);
                assert date != null;
                outDate = outSDF.format(date);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return outDate;
    }

    public void returnFromEmpAdd(int emp_code, String date, ArrayList<String> serial_numbers, String msg, Context context, MyCallback callback) throws ParseException {
        String income_date_format = formatDateForSQL(date);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(Objects.requireNonNull(sdf.parse(income_date_format)));
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/returns/returnFromEmpAdd.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    callback.onSuccess(message);
                } else callback.onError(message);
            } catch (JSONException e) {
                callback.onError(e.toString());
                Log.e("log",e.toString());
            }
        }, error -> callback.onError("error")) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("emp_code", String.valueOf(emp_code));
                paramV.put("date", formatDateForSQL(date));
                String data = new Gson().toJson(serial_numbers);
                paramV.put("serials", data);
                paramV.put("msg", msg);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public void returnToSupAdd(int sup_code, String date, ArrayList<String> serial_numbers, String msg, Context context, MyCallback callback) throws ParseException {
        String income_date_format = formatDateForSQL(date);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(Objects.requireNonNull(sdf.parse(income_date_format)));
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/returns/returnToSupAdd.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    callback.onSuccess(message);
                } else callback.onError(message);
            } catch (JSONException e) {
                callback.onError(e.toString());
                Log.e("log",e.toString());
            }
        }, error -> callback.onError("error")) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("sup_code", String.valueOf(sup_code));
                paramV.put("date", formatDateForSQL(date));
                String data = new Gson().toJson(serial_numbers);
                paramV.put("serials", data);
                paramV.put("msg", msg);
                return paramV;
            }
        };
        queue.add(stringRequest);

    }

    public String getSettingsIP() {
        String ip = "";
        String sql = "select ip from " + SETTINGS + " WHERE code = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) ip = cursor.getString(0);
        cursor.close();
        db.close();
        return ip;
    }


    public boolean settingsWrite(String ip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ip", ip);
        long update = db.update(SETTINGS, cv, "code= ?", new String[]{String.valueOf(1)});
        db.close();
        return update != -1;
    }
}
