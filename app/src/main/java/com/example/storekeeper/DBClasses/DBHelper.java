package com.example.storekeeper.DBClasses;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.storekeeper.Models.chargeModel;
import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.Models.fromEmpReturnModel;
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
    public static final String CHARGE = "charges";
    private static final String EMPRETURNS = "emp_returns";

    public DBHelper(@Nullable Context context) {
        super(context, "storekeeper.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table if not exists " + PRODUCTS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, barcode TEXT unique, warranty int)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + EMPLOYEES + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, mobile TEXT, mail TEXT, work TEXT, id TEXT unique)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + SUPPLIERS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, mobile TEXT, mail TEXT, afm TEXT unique)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + INCOMES + "(code INTEGER PRIMARY KEY AUTOINCREMENT, supplier_name TEXT, income_date DATE)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + CHARGE + "(code INTEGER PRIMARY KEY AUTOINCREMENT, employee_name TEXT, charge_date DATE)";
        db.execSQL(createTable);
        createTable = "create table if not exists " + EMPRETURNS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, employee_name TEXT, return_date DATE, msg TEXT)";
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
                String phone = cursor.getString(2);
                String mobile = cursor.getString(3);
                String mail = cursor.getString(4);
                String work = cursor.getString(5);
                String id = cursor.getString(6);

                employeesModel newEmployee = new employeesModel(code, name, phone, mobile, mail, work, id);

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

    public void incomeAdd(String supplier, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("supplier_name", supplier);
        cv.put("income_date", formatDateForSQL(date));
        String createTable = "create table if not exists " + INCOMES + "(code INTEGER PRIMARY KEY AUTOINCREMENT, supplier_name TEXT, income_date DATE)";
        db.execSQL(createTable);
        long insert = db.insert(INCOMES, null, cv);
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
        cv.put("charge_date", 0);
        cv.put("available", 1);
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
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
        String createTable = "create table if not exists " + SERIALS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, serialnumber TEXT unique, prod_code int, income_date DATE, warranty_date DATE, supplier_code INTEGER, employee_code INTEGER, charge_date DATE, available INTEGER)";
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

    public ArrayList<String> productsGetAllNamesIncome(int supplierCode, String date) {
        ArrayList<String> returnList = new ArrayList<>();
        String income_date = formatDateForSQL(date);
        String sql = "select " + PRODUCTS + ".name from " + PRODUCTS + "," + SERIALS + " where " + PRODUCTS + ".code=" + SERIALS + ".prod_code AND income_date = '" + income_date + "' and supplier_code=" + supplierCode + " GROUP BY prod_code";
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

    public ArrayList<String> serialGetAllIncome(int prod_code, String date) {
        ArrayList<String> returnList = new ArrayList<>();
        String income_date = formatDateForSQL(date);
        String sql = "select serialnumber from " + SERIALS + " where prod_code = " + prod_code + " AND income_date = '" + income_date + "'";
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
        if (employeeName.equals("0"))
            employeeName = "";
        return employeeName;
    }

    public String chargeDateGet(String sn) {
        String charge_date = "";
        String sql = "select charge_date from " + SERIALS + " WHERE serialnumber = '" + sn + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst())
            if (!cursor.getString(0).equals("0"))
                charge_date = formatDateForAndroid(cursor.getString(0));
        cursor.close();
        db.close();
        return charge_date;
    }
    //-------------------------------------------------------------------

    //charge methods
    public ArrayList<chargeModel> chargeGetAll(String start, String end) {
        ArrayList<chargeModel> returnArray = new ArrayList<>();

        String sql = "select * from " + CHARGE + " where charge_date BETWEEN '" + formatDateForSQL(start) + "' AND '" + formatDateForSQL(end) + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + CHARGE + "(code INTEGER PRIMARY KEY AUTOINCREMENT, employee_name TEXT, charge_date DATE)";
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
        if (cursor.moveToFirst())
            if (cursor.getInt(0) == 1)
                available = true;

        cursor.close();
        db.close();
        return available;
    }

    public boolean serialUpdateEmployee(String sn, String date, int emp_code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("charge_date", formatDateForSQL(date));
        cv.put("employee_code", emp_code);
        cv.put("available", 0);
        long update = db.update(SERIALS, cv, "serialnumber= ?", new String[]{sn});
        db.close();
        return update != -1;
    }

    public void chargeAdd(String emp, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("employee_name", emp);
        cv.put("charge_date", formatDateForSQL(date));
        String createTable = "create table if not exists " + CHARGE + "(code INTEGER PRIMARY KEY AUTOINCREMENT, employee_name TEXT, charge_date DATE)";
        db.execSQL(createTable);
        long insert = db.insert(CHARGE, null, cv);
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

    public ArrayList<String> productsGetAllNamesCharge(int employeeCode, String date) {
        ArrayList<String> returnList = new ArrayList<>();
        String charge_date = formatDateForSQL(date);
        String sql = "select " + PRODUCTS + ".name from " + PRODUCTS + "," + SERIALS + " where " + PRODUCTS + ".code=" + SERIALS + ".prod_code AND charge_date = '" + charge_date + "' and employee_code=" + employeeCode + " GROUP BY prod_code";
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

    public ArrayList<String> serialGetAllCharge(int prod_code, String date) {
        ArrayList<String> returnList = new ArrayList<>();
        String charge_date = formatDateForSQL(date);
        String sql = "select serialnumber from " + SERIALS + " where prod_code = " + prod_code + " AND charge_date = '" + charge_date + "'";
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

    public ArrayList<fromEmpReturnModel> returnsFromEmpGetAll(String start, String end) {
        ArrayList<fromEmpReturnModel> returnArray = new ArrayList<>();

        String sql = "select * from " + EMPRETURNS + " where return_date BETWEEN '" + formatDateForSQL(start) + "' AND '" + formatDateForSQL(end) + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        String createTable = "create table if not exists " + EMPRETURNS + "(code INTEGER PRIMARY KEY AUTOINCREMENT, employee_name TEXT, return_date DATE, msg TEXT)";
        db.execSQL(createTable);
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String employeeName = cursor.getString(1);
                String date = cursor.getString(2);
                String msg = cursor.getString(3);
                fromEmpReturnModel newEmpReturn = new fromEmpReturnModel(employeeName,formatDateForAndroid(date), msg);

                returnArray.add(newEmpReturn);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;

    }
}
