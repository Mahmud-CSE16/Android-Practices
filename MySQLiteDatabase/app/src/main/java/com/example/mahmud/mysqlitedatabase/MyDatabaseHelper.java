package com.example.mahmud.mysqlitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

     private Context context;
     private static final String DatabaseName = "student.db";
     private static final String TableName = "student_details";
     private static final String ID = "_id";
     private static final String S_Name = "S_Name";
     private static final String Age = "Age";
     private static final String Gender = "Gender";
     private static final int version = 2;
     private static final String CreatTable = "CREATE TABLE "+TableName+"("+ID+" Integer primary key autoincrement,"+S_Name+" VARCHAR(255),"+Age+" VARCHAR(5) ,"+Gender+" VERCHAR(6) );";

    public MyDatabaseHelper(Context context) {
        super(context, DatabaseName, null, version);
        this.context= context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Toast.makeText(context,"Table is created ",Toast.LENGTH_SHORT).show();
            db.execSQL(CreatTable);
        }catch (Exception e){
            Toast.makeText(context,"Exeption: "+e,Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            Toast.makeText(context,"OnUpdate is called",Toast.LENGTH_SHORT).show();
            db.execSQL("DROP TABLE IF EXISTS "+TableName);
            onCreate(db);

        }catch (Exception e){
            Toast.makeText(context,"Exeption : "+e,Toast.LENGTH_SHORT).show();
        }
    }

    public long insertData(String name,String age,String gender){
        try {

            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues =new ContentValues();
            contentValues.put(S_Name,name);
            contentValues.put(Age,age);
            contentValues.put(Gender,gender);
            return sqLiteDatabase.insert(TableName,null,contentValues);

        }catch (Exception e){
            Toast.makeText(context,"Exeption: "+e,Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    public Cursor showAllData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return  sqLiteDatabase.rawQuery("SELECT * FROM "+TableName,null);
    }

    public void updateData(String id,String name,String age,String gender){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(ID,id);
        contentValues.put(S_Name,name);
        contentValues.put(Age,age);
        contentValues.put(Gender,gender);

        sqLiteDatabase.update(TableName,contentValues,ID+" = ?",new String[]{id});
    }
    public int deleteData(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
       return sqLiteDatabase.delete(TableName,ID+" = ?",new String[]{id});
    }
}
