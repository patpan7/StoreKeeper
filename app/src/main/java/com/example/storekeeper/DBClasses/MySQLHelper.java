package com.example.storekeeper.DBClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLHelper {

    private Connection mConnection;
    private String mUrl;
    private String mUsername;
    private String mPassword;

    public MySQLHelper(Context context) {
        // Φόρτωση παραμέτρων σύνδεσης από το αρχείο ρυθμίσεων
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mUrl = preferences.getString("mysql_url", "");
        mUsername = preferences.getString("mysql_username", "");
        mPassword = preferences.getString("mysql_password", "");
    }

    public boolean connect() {
        try {
            // Δημιουργία σύνδεσης με τη βάση δεδομένων MySQL
            Class.forName("com.mysql.jdbc.Driver");
            mConnection = DriverManager.getConnection(mUrl, mUsername, mPassword);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {
        try {
            if (mConnection != null) {
                mConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        try {
            return mConnection != null && !mConnection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            Statement statement = mConnection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int executeUpdate(String query) {
        try {
            Statement statement = mConnection.createStatement();
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}

