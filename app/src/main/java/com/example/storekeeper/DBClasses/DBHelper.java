package com.example.storekeeper.DBClasses;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.Models.incomeModel;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.Models.supplierModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    public static final String PRODUCTS = "products";
    public static final String EMPLOYEES = "employees";
    public static final String SUPPLIERS = "suppliers";
    public static final String SERIALS = "serials";
    public static final String INCOMES = "incomes";

    public DBHelper(@Nullable Context context) {
        super(context, "storekeeper.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table if not exists " + PRODUCTS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, barcode TEXT unique, warranty int)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + EMPLOYEES + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, surname TEXT, phone TEXT, mobile TEXT, mail TEXT, work TEXT, id TEXT unique)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + SUPPLIERS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, mobile TEXT, mail TEXT, afm TEXT unique)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date TEXT, supplier_code INTEGER, employee_code INTEGER, available INTEGER)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + INCOMES + "(code INTEGER PRIMARY KEY AUTOINCREMENT, supplier_name TEXT, income_date DATE)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    //products methods
    public boolean productAdd(productModel productModel) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", productModel.getName());
        cv.put("barcode", productModel.getBarcode());
        cv.put("warranty", productModel.getWarranty());

        long insert = db.insert(PRODUCTS, null, cv);
        return insert != -1;
    }

    public ArrayList<productModel> productsGetAll() {

        ArrayList<productModel> returnArray = new ArrayList<>();

        String sql = "select * from " + PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + PRODUCTS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, barcode TEXT unique, warranty int)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                int code = cursor.getInt(0);
                String name = cursor.getString(1);
                String barcode = cursor.getString(2);
                int warranty = cursor.getInt(3);

                productModel newProduct = new productModel(code, name, barcode, warranty);
                returnArray.add(newProduct);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;
    }

    public boolean productUpdate(productModel productModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", productModel.getName());
        cv.put("barcode", productModel.getBarcode());
        long update = db.update(PRODUCTS, cv, "code= ?", new String[]{String.valueOf(productModel.getCode())});
        db.close();
        return update != -1;
    }

    public int productNextID() {
        int nextID = 0;
        String sql = "select seq from sqlite_sequence WHERE name = '" + PRODUCTS + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) nextID = cursor.getInt(0);
        cursor.close();
        db.close();
        return nextID + 1;
    }
    //-------------------------------------------------------------------

    //employees methods
    public boolean employeeAdd(employeesModel employeesModel) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", employeesModel.getName());
        cv.put("surname", employeesModel.getSurname());
        cv.put("phone", employeesModel.getPhone());
        cv.put("mobile", employeesModel.getMobile());
        cv.put("mail", employeesModel.getMail());
        cv.put("work", employeesModel.getWork());
        cv.put("id", employeesModel.getId());

        long insert = db.insert(EMPLOYEES, null, cv);
        return insert != -1;
    }

    public ArrayList<employeesModel> employeesGetAll() {

        ArrayList<employeesModel> returnArray = new ArrayList<>();

        String sql = "select * from " + EMPLOYEES;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                int code = cursor.getInt(0);
                String name = cursor.getString(1);
                String surname = cursor.getString(2);
                String phone = cursor.getString(3);
                String mobile = cursor.getString(4);
                String mail = cursor.getString(5);
                String work = cursor.getString(6);
                String id = cursor.getString(7);

                employeesModel newEmployee = new employeesModel(code, name, surname, phone, mobile, mail, work, id);

                returnArray.add(newEmployee);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;
    }

    public boolean employeeUpdate(employeesModel employeesModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", employeesModel.getName());
        cv.put("surname", employeesModel.getSurname());
        cv.put("phone", employeesModel.getPhone());
        cv.put("mobile", employeesModel.getMobile());
        cv.put("mail", employeesModel.getMail());
        cv.put("work", employeesModel.getWork());
        cv.put("id", employeesModel.getId());
        long update = db.update(EMPLOYEES, cv, "code= ?", new String[]{String.valueOf(employeesModel.getCode())});
        db.close();
        return update != -1;
    }

    public int employeeNextID() {
        int nextID = 0;
        String sql = "select seq from sqlite_sequence WHERE name = '" + EMPLOYEES + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) nextID = cursor.getInt(0);
        cursor.close();
        db.close();
        return nextID + 1;
    }
    //-------------------------------------------------------------------

    //suppliers methods
    public boolean supplierAdd(supplierModel supplierModel) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", supplierModel.getName());
        cv.put("phone", supplierModel.getPhone());
        cv.put("mobile", supplierModel.getMobile());
        cv.put("mail", supplierModel.getMail());
        cv.put("afm", supplierModel.getAfm());
        String createTable = "create table if not exists " + SUPPLIERS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, mobile TEXT, mail TEXT, afm TEXT unique)";
        db.execSQL(createTable);
        long insert = db.insert(SUPPLIERS, null, cv);
        return insert != -1;
    }

    public ArrayList<supplierModel> suppliersGetAll() {

        ArrayList<supplierModel> returnArray = new ArrayList<>();

        String sql = "select * from " + SUPPLIERS;
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + SUPPLIERS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, mobile TEXT, mail TEXT, afm TEXT unique)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                int code = cursor.getInt(0);
                String name = cursor.getString(1);
                String phone = cursor.getString(2);
                String mobile = cursor.getString(3);
                String mail = cursor.getString(4);
                String afm = cursor.getString(5);

                supplierModel newSupplier = new supplierModel(code, name, phone, mobile, mail, afm);

                returnArray.add(newSupplier);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;
    }

    public boolean supplierUpdate(supplierModel supplierModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", supplierModel.getName());
        cv.put("phone", supplierModel.getPhone());
        cv.put("mobile", supplierModel.getMobile());
        cv.put("mail", supplierModel.getMail());
        cv.put("afm", supplierModel.getAfm());

        String createTable = "create table if not exists " + SUPPLIERS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, mobile TEXT, mail TEXT, afm TEXT unique)";
        db.execSQL(createTable);
        long update = db.update(SUPPLIERS, cv, "code= ?", new String[]{String.valueOf(supplierModel.getCode())});
        db.close();
        return update != -1;
    }

    public int supplierNextID() {
        int nextID = 0;
        String sql = "select seq from sqlite_sequence WHERE name = '" + SUPPLIERS + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) nextID = cursor.getInt(0);
        cursor.close();
        db.close();
        return nextID + 1;
    }
    //-------------------------------------------------------------------

    //income methods
    public ArrayList<incomeModel> incomeGetAll(String start, String end) throws ParseException {
        ArrayList<incomeModel> returnArray = new ArrayList<>();

        String sql = "select * from " + INCOMES + " where income_date BETWEEN '" + formatDateForSQL(start) + "' AND '" + formatDateForSQL(end) + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + INCOMES + "(code INTEGER PRIMARY KEY AUTOINCREMENT, supplier_name TEXT, income_date DATE)";
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

        String sql = "select name from " + PRODUCTS;
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

    public boolean incomeAdd(String supplier, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("supplier_name", supplier);
        cv.put("income_date", formatDateForSQL(date));
        String createTable = "create table if not exists " + INCOMES + "(code INTEGER PRIMARY KEY AUTOINCREMENT, supplier_name TEXT, income_date DATE)";
        db.execSQL(createTable);
        long insert = db.insert(INCOMES, null, cv);
        return insert != -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean serialAdd(String serialnumber, int prod_code, String income_date, int supplier_code, int warranty) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        String income_date_format = formatDateForSQL(income_date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(income_date_format));
        c.add(Calendar.MONTH, warranty);
        String warrantyString = sdf.format(c.getTime());

        cv.put("serialnumber", serialnumber);
        cv.put("prod_code", prod_code);
        cv.put("income_date", income_date_format);
        cv.put("warranty_date", warrantyString);
        cv.put("supplier_code", supplier_code);
        cv.put("employee_code", 0);
        cv.put("available", 1);
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date TEXT, supplier_code INTEGER, employee_code INTEGER, available INTEGER)";
        db.execSQL(createTable);
        long insert = db.insert(SERIALS, null, cv);
        return insert != -1;
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
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, available INTEGER)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst())
            isOld = true;

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
    //-------------------------------------------------------------------

    //warranty methods
    public String productGetNameFromSerial(String sn) {
        String productName = null;
        String sql = "select " + PRODUCTS + ".name from " + SERIALS + "," + PRODUCTS + " WHERE serialnumber = '" + sn + "' AND " + PRODUCTS + ".code = " + SERIALS + ".prod_code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) productName = cursor.getString(0);
        cursor.close();
        db.close();
        return productName;
    }

    public String warrantyGetIncomeDate(String sn) {
        String incomeDate = null;
        String sql = "select income_date from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) incomeDate = formatDateForAndroid(cursor.getString(0));
        cursor.close();
        db.close();
        return incomeDate;
    }

    public String supplierGetName(String sn) {
        String supplierName = null;
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
        String warrantyDate = null;
        String sql = "select warranty_date from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) warrantyDate = formatDateForAndroid(cursor.getString(0));
        cursor.close();
        db.close();
        return warrantyDate;
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
}
