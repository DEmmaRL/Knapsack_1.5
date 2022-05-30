package com.example.knapsack.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DbContactos extends DbHelper{
    Context context;
    public DbContactos(@Nullable Context context) {
        super(context);
        this.context=context;
    }
    public long insertarDescripcion( String path, String descripcion)
    {
        long id=0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("path", path);
            values.put("descripcion", descripcion);
            id = db.insert(TABLE_DESCRIPCIONES, null, values);

        }
        catch (Exception e)
        {
            Toast.makeText(context, "ERROR DE INSERCIÓN" + e, Toast.LENGTH_SHORT).show();
        }
        return id;
    }
    public String getDescription(String path)
    {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String description=null;
        String path_2=null;
        int id=0;
        Cursor cursorDescription=null;
        cursorDescription=db.rawQuery("SELECT * FROM "+ TABLE_DESCRIPCIONES + " WHERE path ='" + path+"'", null);

        if(cursorDescription.moveToFirst())
        {
            do {
                id=cursorDescription.getInt(0);
                path_2=cursorDescription.getString(1);
                description=cursorDescription.getString(2);
            }while(cursorDescription.moveToNext());
        }
        return description;
    }
}
