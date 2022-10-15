package com.example.storekeeper.DBClasses;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.storekeeper.Models.productModel;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, "storekeeper.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table if not exists products(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, barcode TEXT unique, warranty int)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public boolean addProduct(productModel productModel) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", productModel.getProductName());
        cv.put("barcode", productModel.getProductBarcode());
        cv.put("warranty", productModel.getProductWarranty());

        long insert = db.insert("products", null, cv);
        return insert != -1;
    }

    public ArrayList<productModel> getAllProducts (){

        ArrayList<productModel> returnArray = new ArrayList<>();

        String sql = "select * from products";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql,null);

        if (cursor.moveToFirst()){
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String barcode = cursor.getString(2);
                int warranty = cursor.getInt(3);

                productModel newProduct = new productModel(id,name,barcode,warranty);
                returnArray.add(newProduct);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return returnArray;
    }

    public  boolean productUpdate(productModel productModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", productModel.getProductName());
        cv.put("barcode", productModel.getProductBarcode());
        long update = db.update("products", cv,"id= ?",new String[]{String.valueOf(productModel.getProductId())});
        db.close();
        return update != -1;
    }

    public int productNextID(){
        int nextID = 0;
        String sql = "select seq from sqlite_sequence WHERE name = 'products'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToFirst();
        nextID = cursor.getInt(0);
        cursor.close();
        db.close();
        return nextID+1;
    }
}
