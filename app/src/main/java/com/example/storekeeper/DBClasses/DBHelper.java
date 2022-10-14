package com.example.storekeeper.DBClasses;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.storekeeper.Models.productModel;

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

    public boolean addOne(productModel productModel) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", productModel.getProductName());
        cv.put("barcode", productModel.getProductBarcode());
        cv.put("warranty", productModel.getProductWarranty());

        long insert = db.insert("products", null, cv);
        return insert != -1;
    }
}
