package com.example.reto8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String DATABASE_NAME = "DirEmpresas.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "empresas";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "nombre_empresa";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_TEL = "telefono";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PROD = "producto";
    private static final String COLUMN_CLAS = "clasificacion";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
        this.context =context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_URL + " TEXT, " +
                        COLUMN_TEL + " TEXT, " +
                        COLUMN_EMAIL + " TEXT, " +
                        COLUMN_PROD + " TEXT, " +
                        COLUMN_CLAS + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addEmpresa(String name, String url, String telefono, String email, String producto, String clasificacion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_URL, url);
        cv.put(COLUMN_TEL, telefono);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PROD, producto);
        cv.put(COLUMN_CLAS, clasificacion);

        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateData(String row_id, String name, String url, String telefono, String email, String producto, String clasificacion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_URL, url);
        cv.put(COLUMN_TEL, telefono);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PROD, producto);
        cv.put(COLUMN_CLAS, clasificacion);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }

    }

    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public Cursor searchData(String name, String clasificacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " LIKE ? AND " + COLUMN_CLAS + " LIKE ?";
        String nameFilter = "%" + name + "%";
        String clasificacionFilter = "%" + clasificacion + "%";
        return db.rawQuery(query, new String[]{nameFilter, clasificacionFilter});
    }

}