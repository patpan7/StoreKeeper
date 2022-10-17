package com.example.storekeeper.DBClasses;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.Models.supplierModel;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String PRODUCTS = "products";
    public static final String EMPLOYEES = "employees";
    public static final String SUPPLIERS = "suppliers";

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
        if (cursor.moveToFirst())
            nextID = cursor.getInt(0);
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
        if (cursor.moveToFirst())
            nextID = cursor.getInt(0);
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
        if (cursor.moveToFirst())
            nextID = cursor.getInt(0);
        cursor.close();
        db.close();
        return nextID + 1;
    }
    //-------------------------------------------------------------------

}
