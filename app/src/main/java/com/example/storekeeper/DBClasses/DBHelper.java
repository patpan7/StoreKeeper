package com.example.storekeeper.DBClasses;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.storekeeper.Models.chargeModel;
import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.Models.fromEmpReturnModel;
import com.example.storekeeper.Models.incomeModel;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.Models.supplierModel;
import com.example.storekeeper.Models.toSupReturnModel;
import com.example.storekeeper.alertDialogs;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final int STATUS_UNSYNC = 0;
    private static final int STATUS_SYNC = 1;
    private static final int STATUS_UPDATE = 2;

    public static final String PRODUCTS = "products";
    public static final String EMPLOYEES = "employees";
    public static final String SUPPLIERS = "suppliers";
    public static final String SERIALS = "serials";
    public static final String INCOMES = "incomes";
    public static final String CHARGE = "charges";
    private static final String EMPRETURNS = "emp_returns";
    private static final String SUPRETURNS = "sup_returns";
    private static final String SETTINGS = "settings";
    alertDialogs dialog;


    public DBHelper(@Nullable Context context) {
        super(context, "storekeeper.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table if not exists " + PRODUCTS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, barcode TEXT unique, warranty int, sync_status int)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + EMPLOYEES + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, mobile TEXT, mail TEXT, work TEXT, id TEXT unique, sync_status int)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + SUPPLIERS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, mobile TEXT, mail TEXT, afm TEXT unique,sync_status int)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER, sync_status int)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + INCOMES + "(code INTEGER PRIMARY KEY AUTOINCREMENT, supplier_name TEXT, income_date DATE, serial_number TEXT, sync_status int)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + CHARGE + "(code INTEGER PRIMARY KEY AUTOINCREMENT, employee_name TEXT, charge_date DATE, serial_number TEXT, sync_status int)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + EMPRETURNS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, employee_name TEXT, return_date DATE, serial_number TEXT, msg, sync_status int)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + SUPRETURNS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, supplier_name TEXT, return_date DATE, serial_number TEXT, msg TEXT, sync_status int)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + SETTINGS + "(code INTEGER PRIMARY KEY, ip TEXT, port TEXT, standalone int)";
        db.execSQL(createTable);
        ContentValues cv = new ContentValues();
        cv.put("code", 1);
        cv.put("ip", "192.168.1.10");
        cv.put("port", "1433");
        cv.put("standalone", 1);
        db.insert(SETTINGS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Loop through each version when an upgrade occurs.
        for (int version = oldVersion + 1; version <= newVersion; version++) {
            switch (version) {

                case 2:
                    // Apply changes made in version 2
                    break;
                case 3:
                    // Apply changes made in version 3
                    break;
                case 4:
                    // Apply changes made in version 4
                    break;
            }
        }
    }


    public interface MyCallback {
        void onSuccess(String response);

        void onError(String error);
    }

    //products methods
    public void productAdd(productModel productModel, Context context, MyCallback callback) throws JSONException {
//        SQLiteDatabase db = this.getReadableDatabase();
//        ContentValues cv = new ContentValues();
//
//        cv.put("name", productModel.getName());
//        cv.put("barcode", productModel.getBarcode());
//        cv.put("warranty", productModel.getWarranty());
//        cv.put("sync_status", STATUS_UNSYNC);
//        long insert = db.insert(PRODUCTS, null, cv);
//        return insert != -1;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productAdd.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("error");
            }
        }) {
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

    public void productUpdate(productModel productModel, Context context, MyCallback callback) throws JSONException {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put("name", productModel.getName());
//        cv.put("barcode", productModel.getBarcode());
//        cv.put("sync_status", STATUS_UPDATE);
//        long update = db.update(PRODUCTS, cv, "code= ?", new String[]{String.valueOf(productModel.getCode())});
//        db.close();
//        return update != -1;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productUpdate.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("error");
            }
        }) {
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
//        String sql = "select seq from sqlite_sequence WHERE name = '" + PRODUCTS + "'";
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) nextID = cursor.getInt(0);
//        cursor.close();
//        db.close();
//        return nextID + 1;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productNextID.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    public void productsGetIncomeSum(Context context, int code, EditText incomeSum) {
//        int sum = 0;
//        String sql = "select count(serialnumber) from " + SERIALS + " WHERE prod_code = " + code + " and available != -1";
//        SQLiteDatabase db = this.getReadableDatabase();
//        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
//        db.execSQL(createTable);
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) sum = cursor.getInt(0);
//        cursor.close();
//        db.close();
//        return sum;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productsGetIncomeSum.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //callback.onError("error");
            }
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
//        int sum = 0;
//        String sql = "select count(serialnumber) from " + SERIALS + " WHERE prod_code = " + code + " and available = 1";
//        SQLiteDatabase db = this.getReadableDatabase();
//        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
//        db.execSQL(createTable);
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) sum = cursor.getInt(0);
//        cursor.close();
//        db.close();
//        return sum;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productsGetAvailable.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //callback.onError("error");
            }
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
//        int sum = 0;
//        String sql = "select count(serialnumber) from " + SERIALS + " WHERE prod_code = " + code + " and available = 0";
//        SQLiteDatabase db = this.getReadableDatabase();
//        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
//        db.execSQL(createTable);
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) sum = cursor.getInt(0);
//        cursor.close();
//        db.close();
//        return sum;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/products/productsGetCharged.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //callback.onError("error");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("code", code + "");
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public ArrayList<String> productGetAllAvailableSN(int code) {
        ArrayList<String> returnList = new ArrayList<>();
        String sql = "select serialnumber from " + SERIALS + " WHERE prod_code = " + code + " and available = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();

        return returnList;
    }
    //-------------------------------------------------------------------

    //employees methods
    public void employeeAdd(employeesModel employeesModel, Context context, MyCallback callback) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        ContentValues cv = new ContentValues();
//
//        cv.put("name", employeesModel.getName());
//        cv.put("phone", employeesModel.getPhone());
//        cv.put("mobile", employeesModel.getMobile());
//        cv.put("mail", employeesModel.getMail());
//        cv.put("work", employeesModel.getWork());
//        cv.put("id", employeesModel.getId());
//        cv.put("sync_status", STATUS_UNSYNC);
//        long insert = db.insert(EMPLOYEES, null, cv);
//        return insert != -1;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/employees/employeeAdd.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("error");
            }
        }) {
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

    public void employeeUpdate(employeesModel employeesModel, Context context, MyCallback callback) throws JSONException {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put("name", employeesModel.getName());
//        cv.put("phone", employeesModel.getPhone());
//        cv.put("mobile", employeesModel.getMobile());
//        cv.put("mail", employeesModel.getMail());
//        cv.put("work", employeesModel.getWork());
//        cv.put("id", employeesModel.getId());
//        cv.put("sync_status", STATUS_UPDATE);
//        long update = db.update(EMPLOYEES, cv, "code= ?", new String[]{String.valueOf(employeesModel.getCode())});
//        db.close();
//        return update != -1;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/employees/employeeUpdate.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("error");
            }
        }) {
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
//        int nextID = 0;
//        String sql = "select seq from sqlite_sequence WHERE name = '" + EMPLOYEES + "'";
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) nextID = cursor.getInt(0);
//        cursor.close();
//        db.close();
//        return nextID + 1;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/employees/employeeNextID.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    public void employeeGetChargedProd(Context context, int code, EditText charged) {
//        int sum = 0;
//        String sql = "select count(serialnumber) from " + SERIALS + " WHERE employee_code = " + code + " and available = 0";
//        SQLiteDatabase db = this.getReadableDatabase();
//        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
//        db.execSQL(createTable);
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) sum = cursor.getInt(0);
//        cursor.close();
//        db.close();
//        return sum;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/employees/employeeGetChargedProd.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //callback.onError("error");
            }
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
//        SQLiteDatabase db = this.getReadableDatabase();
//        ContentValues cv = new ContentValues();
//
//        cv.put("name", supplierModel.getName());
//        cv.put("phone", supplierModel.getPhone());
//        cv.put("mobile", supplierModel.getMobile());
//        cv.put("mail", supplierModel.getMail());
//        cv.put("afm", supplierModel.getAfm());
//        cv.put("sync_status", STATUS_UNSYNC);
//        String createTable = "create table if not exists " + SUPPLIERS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, mobile TEXT, mail TEXT, afm TEXT unique)";
//        db.execSQL(createTable);
//        long insert = db.insert(SUPPLIERS, null, cv);
//        return insert != -1;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/suppliers/supplierAdd.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        }) {
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

    public void supplierUpdate(supplierModel supplierModel,Context context, MyCallback callback) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put("name", supplierModel.getName());
//        cv.put("phone", supplierModel.getPhone());
//        cv.put("mobile", supplierModel.getMobile());
//        cv.put("mail", supplierModel.getMail());
//        cv.put("afm", supplierModel.getAfm());
//        cv.put("sync_status", STATUS_UPDATE);
//        String createTable = "create table if not exists " + SUPPLIERS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, mobile TEXT, mail TEXT, afm TEXT unique)";
//        db.execSQL(createTable);
//        long update = db.update(SUPPLIERS, cv, "code= ?", new String[]{String.valueOf(supplierModel.getCode())});
//        db.close();
//        return update != -1;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/suppliers/supplierUpdate.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("error");
            }
        }) {
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
//        int nextID = 0;
//        String sql = "select seq from sqlite_sequence WHERE name = '" + SUPPLIERS + "'";
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) nextID = cursor.getInt(0);
//        cursor.close();
//        db.close();
//        return nextID + 1;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/suppliers/supplierNextID.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    public void supplierGetAllIncomeProd(Context context, int code, EditText income) {
//        int sum = 0;
//        String sql = "select count(serialnumber) from " + SERIALS + " WHERE supplier_code = " + code + " and (available = 0 or available = 1)";
//        SQLiteDatabase db = this.getReadableDatabase();
//        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
//        db.execSQL(createTable);
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) sum = cursor.getInt(0);
//        cursor.close();
//        db.close();
//        return sum;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/suppliers/supplierGetAllIncomeProd.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //callback.onError("error");
            }
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
    public ArrayList<incomeModel> incomeGetAll(String start, String end) throws ParseException {
        ArrayList<incomeModel> returnArray = new ArrayList<>();

        String sql = "select * from " + INCOMES + " where income_date BETWEEN '" + formatDateForSQL(start) + "' AND '" + formatDateForSQL(end) + "' group by income_date,supplier_name";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + INCOMES + "(code INTEGER PRIMARY KEY AUTOINCREMENT, supplier_name TEXT, income_date DATE, serial_number TEXT)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String suppliername = cursor.getString(1);
                String date = cursor.getString(2);

                incomeModel newIncome = new incomeModel(formatDateForAndroid(date), suppliername);

                returnArray.add(newIncome);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;
    }

    public ArrayList<String> suppliersGetAllNames() {
        ArrayList<String> returnArray = new ArrayList<>();

        String sql = "select name from " + SUPPLIERS;
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SUPPLIERS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, mobile TEXT, mail TEXT, afm TEXT unique)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                returnArray.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;

    }

    public ArrayList<String> productsGetAllNames() {
        ArrayList<String> returnArray = new ArrayList<>();

        String sql = "select name from " + PRODUCTS + " where code != -1";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + PRODUCTS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, barcode TEXT unique, warranty int)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                returnArray.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;
    }

    public String productGetName(String barcode) {
        String name = null;
        String sql = "select name from " + PRODUCTS + " WHERE barcode = '" + barcode + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) name = cursor.getString(0);
        cursor.close();
        db.close();
        return name;
    }

    public void incomeAdd(int supplier_code, String date, String sn, Context context, MyCallback callback) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put("supplier_name", supplier);
//        cv.put("income_date", formatDateForSQL(date));
//        cv.put("serial_number", sn);
//        cv.put("sync_status", STATUS_UNSYNC);
//        String createTable = "create table if not exists " + INCOMES + "(code INTEGER PRIMARY KEY AUTOINCREMENT, supplier_name TEXT, income_date DATE, serial_number TEXT)";
//        db.execSQL(createTable);
//        long insert = db.insert(INCOMES, null, cv);
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/incomes/incomeAdd.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("supplier_code", String.valueOf(supplier_code));
                paramV.put("income_date", formatDateForSQL(date));
                paramV.put("serial_number", sn);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void serialAdd(String serialnumber, int prod_code, String income_date, int supplier_code, int warranty,Context context, MyCallback callback) throws ParseException, AuthFailureError {
//        SQLiteDatabase db = this.getReadableDatabase();
//        ContentValues cv = new ContentValues();
//        String income_date_format = formatDateForSQL(income_date);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar c = Calendar.getInstance();
//        c.setTime(sdf.parse(income_date_format));
//        c.add(Calendar.MONTH, warranty);
//        String warrantyString = sdf.format(c.getTime());
//
//        cv.put("serialnumber", serialnumber);
//        cv.put("prod_code", prod_code);
//        cv.put("income_date", income_date_format);
//        cv.put("warranty_date", warrantyString);
//        cv.put("supplier_code", supplier_code);
//        cv.put("employee_code", 0);
//        cv.put("charge_date", 0);
//        cv.put("available", 1);
//        cv.put("sync_status", STATUS_UNSYNC);
//        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
//        db.execSQL(createTable);
//        long insert = db.insert(SERIALS, null, cv);
//        return insert != -1;
        String income_date_format = formatDateForSQL(income_date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(income_date_format));
        c.add(Calendar.MONTH, warranty);
        String warrantyString = sdf.format(c.getTime());

        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/incomes/serialAdd.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("serialnumber", serialnumber);
                paramV.put("prod_code", String.valueOf(prod_code));
                paramV.put("income_date", income_date_format);
                paramV.put("warranty_date", warrantyString);
                paramV.put("supplier_code", String.valueOf(supplier_code));
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public int productGetCode(String name) {
        int code = 0;
        String sql = "select code from " + PRODUCTS + " WHERE name = '" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) code = cursor.getInt(0);
        cursor.close();
        db.close();
        return code;
    }

    public int supplierGetCode(String name) {
        int code = 0;
        String sql = "select code from " + SUPPLIERS + " WHERE name = '" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) code = cursor.getInt(0);
        cursor.close();
        db.close();
        return code;
    }

    public boolean checkSerialNumber(String sn) {
        boolean isOld = false;
        String sql = "select serialnumber from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) isOld = true;

        cursor.close();
        db.close();
        return isOld;
    }

    public int productGetWarranty(int prod_code) {
        int warranty = 0;
        String sql = "select warranty from " + PRODUCTS + " WHERE code = " + prod_code + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) warranty = cursor.getInt(0);
        cursor.close();
        db.close();
        return warranty;

    }

    public ArrayList<String> productsGetAllNamesIncome(String supplierName, String date) {
        ArrayList<String> returnList = new ArrayList<>();
        String income_date = formatDateForSQL(date);
        String sql = "select " + PRODUCTS + ".name from " + PRODUCTS + ", " + SERIALS + ", " + INCOMES + " where " + PRODUCTS + ".code=" + SERIALS + ".prod_code AND " + INCOMES + ".income_date = '" + income_date + "' and " + INCOMES + ".supplier_name='" + supplierName + "' AND serial_number = serialnumber GROUP BY prod_code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;
    }

    public ArrayList<String> serialGetAllIncome(String name, String date, int prod_code) {
        ArrayList<String> returnList = new ArrayList<>();
        String income_date = formatDateForSQL(date);
        String sql = "select " + INCOMES + ".serial_number from " + INCOMES + ", " + SERIALS + " where " + INCOMES + ".income_date = '" + income_date + "' AND " + SERIALS + ".prod_code = " + prod_code + " and serialnumber=serial_number and supplier_name = '" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;
    }
//-------------------------------------------------------------------

    //warranty methods
    public String productGetNameFromSerial(String sn) {
        String productName = "";
        String sql = "select " + PRODUCTS + ".name from " + SERIALS + "," + PRODUCTS + " WHERE serialnumber = '" + sn + "' AND " + PRODUCTS + ".code = " + SERIALS + ".prod_code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) productName = cursor.getString(0);
        cursor.close();
        db.close();
        return productName;
    }

    public String warrantyGetIncomeDate(String sn) {
        String incomeDate = "";
        String sql = "select income_date from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) incomeDate = formatDateForAndroid(cursor.getString(0));
        cursor.close();
        db.close();
        return incomeDate;
    }

    public String supplierGetName(String sn) {
        String supplierName = "";
        String sql = "select " + SUPPLIERS + ".name from " + SERIALS + "," + SUPPLIERS + " WHERE serialnumber = '" + sn + "' AND " + SUPPLIERS + ".code = " + SERIALS + ".supplier_code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) supplierName = cursor.getString(0);
        cursor.close();
        db.close();
        return supplierName;
    }

    public int warrantyGetMonths(String sn) {
        int warrantyMonths = 0;
        String sql = "select " + PRODUCTS + ".warranty from " + SERIALS + "," + PRODUCTS + " WHERE serialnumber = '" + sn + "' AND " + PRODUCTS + ".code = " + SERIALS + ".prod_code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) warrantyMonths = cursor.getInt(0);
        cursor.close();
        db.close();
        return warrantyMonths;
    }

    public String warrantyGetEndDate(String sn) {
        String warrantyDate = "";
        String sql = "select warranty_date from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) warrantyDate = formatDateForAndroid(cursor.getString(0));
        cursor.close();
        db.close();
        return warrantyDate;
    }

    public String employeeGetName(String sn) {
        String employeeName = "";
        String sql = "select " + EMPLOYEES + ".name from " + SERIALS + "," + EMPLOYEES + " WHERE serialnumber = '" + sn + "' AND " + EMPLOYEES + ".code = " + SERIALS + ".employee_code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) employeeName = cursor.getString(0);
        cursor.close();
        db.close();
        if (employeeName.equals("0")) employeeName = "";
        return employeeName;
    }

    public String chargeDateGet(String sn) {
        String charge_date = "";
        String sql = "select charge_date from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) if (!cursor.getString(0).equals("0"))
            charge_date = formatDateForAndroid(cursor.getString(0));
        cursor.close();
        db.close();
        return charge_date;
    }
//-------------------------------------------------------------------

    //charge methods
    public ArrayList<chargeModel> chargeGetAll(String start, String end) {
        ArrayList<chargeModel> returnArray = new ArrayList<>();

        String sql = "select * from " + CHARGE + " where charge_date BETWEEN '" + formatDateForSQL(start) + "' AND '" + formatDateForSQL(end) + "' group by charge_date, employee_name";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + CHARGE + "(code INTEGER PRIMARY KEY AUTOINCREMENT, employee_name TEXT, charge_date DATE, serial_number TEXT)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String employeeName = cursor.getString(1);
                String date = cursor.getString(2);

                chargeModel newCharge = new chargeModel(formatDateForAndroid(date), employeeName);

                returnArray.add(newCharge);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;
    }

    public ArrayList<String> employeesGetAllNames() {
        ArrayList<String> returnArray = new ArrayList<>();

        String sql = "select name from " + EMPLOYEES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                returnArray.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;
    }

    public int employeeGetCode(String name) {
        int code = 0;
        String sql = "select code from " + EMPLOYEES + " WHERE name = '" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) code = cursor.getInt(0);
        cursor.close();
        db.close();
        return code;
    }

    public boolean checkSerialNumberAvailable(String sn) {
        boolean available = false;
        String sql = "select available from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) if (cursor.getInt(0) == 1) available = true;

        cursor.close();
        db.close();
        return available;
    }

    public boolean serialUpdateEmployee(String sn, String date, int emp_code, int available) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("charge_date", formatDateForSQL(date));
        cv.put("employee_code", emp_code);
        cv.put("available", available);
        long update = db.update(SERIALS, cv, "serialnumber= ?", new String[]{sn});
        db.close();
        return update != -1;
    }

    public void chargeAdd(String emp, String date, String serial) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("employee_name", emp);
        cv.put("charge_date", formatDateForSQL(date));
        cv.put("serial_number", serial);
        cv.put("sync_status", STATUS_UNSYNC);
        String createTable = "create table if not exists " + CHARGE + "(code INTEGER PRIMARY KEY AUTOINCREMENT, employee_name TEXT, charge_date DATE, serial_number TEXT)";
        db.execSQL(createTable);
        long insert = db.insert(CHARGE, null, cv);
    }

    public ArrayList<String> productsGetAllNamesCharge(String employeeName, String date) {
        ArrayList<String> returnList = new ArrayList<>();
        String charge_date = formatDateForSQL(date);
        String sql = "select " + PRODUCTS + ".name from " + PRODUCTS + ", " + SERIALS + ", " + CHARGE + " where " + PRODUCTS + ".code=" + SERIALS + ".prod_code AND " + CHARGE + ".charge_date = '" + charge_date + "' and " + CHARGE + ".employee_name='" + employeeName + "' AND serial_number = serialnumber GROUP BY prod_code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;
    }

    public ArrayList<String> serialGetAllCharge(String name, String date, int prod_code) {
        ArrayList<String> returnList = new ArrayList<>();
        String charge_date = formatDateForSQL(date);
        String sql = "select " + CHARGE + ".serial_number from " + CHARGE + ", " + SERIALS + " where " + CHARGE + ".charge_date = '" + charge_date + "' AND " + SERIALS + ".prod_code = " + prod_code + " and serialnumber=serial_number and employee_name = '" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;
    }

    //-------------------------------------------------------------------
    public static String formatDateForSQL(String inDate) {
        SimpleDateFormat inSDF = new SimpleDateFormat("dd/mm/yyyy");
        SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-mm-dd");
        String outDate = "";
        if (inDate != null) {
            try {
                Date date = inSDF.parse(inDate);
                outDate = outSDF.format(date);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return outDate;
    }

    public static String formatDateForAndroid(String inDate) {
        SimpleDateFormat inSDF = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat outSDF = new SimpleDateFormat("dd/mm/yyyy");
        String outDate = "";
        if (inDate != null) {
            try {
                Date date = inSDF.parse(inDate);
                outDate = outSDF.format(date);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return outDate;
    }

    public ArrayList<fromEmpReturnModel> returnsFromEmpGetAll(String start, String end) {
        ArrayList<fromEmpReturnModel> returnArray = new ArrayList<>();

        String sql = "select * from " + EMPRETURNS + " where return_date BETWEEN '" + formatDateForSQL(start) + "' AND '" + formatDateForSQL(end) + "' group by employee_name,return_date";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + EMPRETURNS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, employee_name TEXT, return_date DATE, serial_number TEXT, msg TEXT)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String employeeName = cursor.getString(1);
                String date = cursor.getString(2);
                String msg = cursor.getString(4);
                fromEmpReturnModel newEmpReturn = new fromEmpReturnModel(employeeName, formatDateForAndroid(date), msg);

                returnArray.add(newEmpReturn);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;
    }


    public ArrayList<toSupReturnModel> returnsToSupGetAll(String start, String end) {
        ArrayList<toSupReturnModel> returnArray = new ArrayList<>();

        String sql = "select * from " + SUPRETURNS + " where return_date BETWEEN '" + formatDateForSQL(start) + "' AND '" + formatDateForSQL(end) + "' group by supplier_name,return_date";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SUPRETURNS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, supplier_name TEXT, return_date DATE, serial_number TEXT, msg TEXT)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String supplierName = cursor.getString(1);
                String date = cursor.getString(2);
                String serial = cursor.getString(3);
                String msg = cursor.getString(4);
                toSupReturnModel newSupReturn = new toSupReturnModel(supplierName, formatDateForAndroid(date), serial, msg);

                returnArray.add(newSupReturn);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;
    }

    public boolean checkSerialNumberisCharged(String sn, int emp_code) {
        boolean isCharged = false;
        String sql = "select employee_code from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) if (cursor.getInt(0) == emp_code) isCharged = true;

        cursor.close();
        db.close();
        return isCharged;

    }

    public void returnFromEmpAdd(String emp_name, String date, String sn, String msg) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("employee_name", emp_name);
        cv.put("return_date", formatDateForSQL(date));
        cv.put("serial_number", sn);
        cv.put("msg", msg);
        cv.put("sync_status", STATUS_UNSYNC);
        String createTable = "create table if not exists " + EMPRETURNS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, employee_name TEXT, return_date DATE, serial_number TEXT, msg TEXT)";
        db.execSQL(createTable);
        long insert = db.insert(EMPRETURNS, null, cv);
    }

    public boolean checkSerialNumberisCharged(String sn) {
        boolean isCharged = false;
        String sql = "select employee_code from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) if (cursor.getInt(0) != 0) isCharged = true;

        cursor.close();
        db.close();
        return isCharged;

    }

    public Boolean checkSerialNumberisfromSup(int sup_code, String sn) {
        boolean isFromSup = false;
        String sql = "select supplier_code from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) if (cursor.getInt(0) == sup_code) isFromSup = true;

        cursor.close();
        db.close();
        return isFromSup;

    }

    public boolean returnToSupAdd(String sup_name, String date, String sn, String msg) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("supplier_name", sup_name);
        cv.put("return_date", formatDateForSQL(date));
        cv.put("serial_number", sn);
        cv.put("msg", msg);
        cv.put("sync_status", STATUS_UNSYNC);
        String createTable = "create table if not exists " + SUPRETURNS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, supplier_name TEXT, return_date DATE, serial_number TEXT, msg TEXT)";
        db.execSQL(createTable);
        long insert = db.insert(SUPRETURNS, null, cv);
        return insert != -1;
    }

    public void serialUpdateAvailable(String sn, int available, Context context, MyCallback callback) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put("available", available);
//        cv.put("sync_status", STATUS_UPDATE);
//        long update = db.update(SERIALS, cv, "serialnumber= ?", new String[]{sn});
//        db.close();
//        return update != -1;
        String ip = getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ip + "/storekeeper/incomes/serialUpdateAvailable.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("serialnumber",sn);
                paramV.put("available", String.valueOf(available));
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public ArrayList<String> productsGetAllNamesRerunEmp(String employeename, String date) {
        ArrayList<String> returnList = new ArrayList<>();
        String return_date = formatDateForSQL(date);
        String sql = "select " + PRODUCTS + ".name from " + PRODUCTS + ", " + SERIALS + ", " + EMPRETURNS + " where " + PRODUCTS + ".code=" + SERIALS + ".prod_code AND " + EMPRETURNS + ".return_date = '" + return_date + "' and " + EMPRETURNS + ".employee_name='" + employeename + "' AND serial_number = serialnumber GROUP BY prod_code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;

    }

    public ArrayList<String> serialGetAllReturnEmp(String name, String date, int prod_code) {
        ArrayList<String> returnList = new ArrayList<>();
        String return_date = formatDateForSQL(date);
        String sql = "select " + EMPRETURNS + ".serial_number from " + EMPRETURNS + ", " + SERIALS + " where return_date = '" + return_date + "' AND " + SERIALS + ".prod_code = " + prod_code + " and serialnumber=serial_number and employee_name = '" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;
    }

    public ArrayList<String> productsGetAllNamesRerunSup(String suppliername, String date) {
        ArrayList<String> returnList = new ArrayList<>();
        String return_date = formatDateForSQL(date);
        String sql = "select " + PRODUCTS + ".name from " + PRODUCTS + ", " + SERIALS + ", " + SUPRETURNS + " where " + PRODUCTS + ".code=" + SERIALS + ".prod_code AND " + SUPRETURNS + ".return_date = '" + return_date + "' and " + SUPRETURNS + ".supplier_name='" + suppliername + "' AND serial_number = serialnumber GROUP BY prod_code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;

    }

    public ArrayList<String> serialGetAllReturnSup(String name, String date, int prod_code) {
        ArrayList<String> returnList = new ArrayList<>();
        String return_date = formatDateForSQL(date);
        String sql = "select " + SUPRETURNS + ".serial_number from " + SUPRETURNS + ", " + SERIALS + " where return_date = '" + return_date + "' AND " + SERIALS + ".prod_code = " + prod_code + " and serialnumber=serial_number and supplier_name = '" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;
    }

    public boolean checkSerialNumberStock(String sn) {
        boolean isOld = false;
        String sql = "select serialnumber,available from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) if (cursor.getInt(1) >= 0) isOld = true;

        cursor.close();
        db.close();
        return isOld;
    }

    public boolean checkSerialNumberOnReturn(String sn) {
        boolean isReturn = false;
        String sql = "select serialnumber, available from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) if (cursor.getInt(1) == -1) isReturn = true;

        cursor.close();
        db.close();
        return isReturn;
    }

    public boolean checkSerialNumberProd(String sn, int prod_code) {
        boolean isThisProd = false;
        String sql = "select prod_code from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) if (cursor.getInt(0) == prod_code) isThisProd = true;

        cursor.close();
        db.close();
        return isThisProd;
    }

    public ArrayList<String> employeesGetAllNamesWithSN(int code) {
        ArrayList<String> returnList = new ArrayList<>();
        String sql = "select " + EMPLOYEES + ".name from " + SERIALS + "," + EMPLOYEES + " WHERE prod_code = " + code + " and  " + SERIALS + ".employee_code = " + EMPLOYEES + ".code group by name";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();

        return returnList;
    }

    public ArrayList<String> serialsGetFromEmpProd(int prod_code, int emp_code) {
        ArrayList<String> returnList = new ArrayList<>();
        String sql = "select serialnumber from " + SERIALS + " WHERE prod_code = " + prod_code + " and  employee_code = " + emp_code;
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();

        return returnList;
    }

    public ArrayList<String> productsGetAllNamesCharge(int code) {
        ArrayList<String> returnList = new ArrayList<>();
        String sql = "select " + PRODUCTS + ".name from " + PRODUCTS + ", " + SERIALS + " where " + PRODUCTS + ".code=" + SERIALS + ".prod_code  and employee_code=" + code + "  GROUP BY prod_code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;
    }

    public ArrayList<String> serialGetAllCharge(int prod_code, int code) {
        ArrayList<String> returnList = new ArrayList<>();
        String sql = "select serialnumber from " + SERIALS + " where prod_code = " + prod_code + "  and employee_code=" + code;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;

    }

    public ArrayList<String> productsGetAllNamesIncome(int code) {
        ArrayList<String> returnList = new ArrayList<>();
        String sql = "select " + PRODUCTS + ".name from " + PRODUCTS + ", " + SERIALS + " where " + PRODUCTS + ".code=" + SERIALS + ".prod_code  and supplier_code = " + code + "  and (available = 0 or available = 1) GROUP BY prod_code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;

    }

    public ArrayList<String> serialGetAllIncome(int prod_code, int code) {
        ArrayList<String> returnList = new ArrayList<>();
        String sql = "select serialnumber from " + SERIALS + " where prod_code = " + prod_code + "  and supplier_code = " + code + " and (available = 0 or available = 1)";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;

    }

    public ArrayList<String> serialGetAllFromEmp(int emp_code) {
        ArrayList<String> returnList = new ArrayList<>();
        String sql = "select serialnumber from " + SERIALS + " where employee_code = " + emp_code + " and available = 0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;

    }

    public ArrayList<String> serialGetAllFromSup(int sup_code) {
        ArrayList<String> returnList = new ArrayList<>();
        String sql = "select serialnumber from " + SERIALS + " where supplier_code = " + sup_code + " and available = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;
    }

    public ArrayList<String> getAllTables() {
        ArrayList<String> returnList = new ArrayList<>();
        String sql = "select name from sqlite_sequence";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnList;

    }

    public void clean() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SERIALS, null, null);
        db.delete(INCOMES, null, null);
        db.delete(CHARGE, null, null);
        db.delete(EMPRETURNS, null, null);
        db.delete(SUPRETURNS, null, null);

        //db.execSQL("delete from "+ SERIALS);
        //db.execSQL("TRUNCATE table" + TABLE_NAME);
        db.close();
    }

    public boolean hasChanges() {
        return false;
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
