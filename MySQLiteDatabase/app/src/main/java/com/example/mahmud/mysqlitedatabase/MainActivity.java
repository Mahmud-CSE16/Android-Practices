package com.example.mahmud.mysqlitedatabase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MyDatabaseHelper myDatabaseHelper;
    private EditText nameEditText,ageEditText,genderEditText,idEditText;
    private Button insertButton,showButton,updateButton,deleteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDatabaseHelper = new MyDatabaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase= myDatabaseHelper.getWritableDatabase();

        findId();

        insertButton.setOnClickListener(this);
        showButton.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String name = nameEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String gender = genderEditText.getText().toString();
        String id = idEditText.getText().toString();

        if(v.getId()==R.id.insertButtonId) {
            long rowId = myDatabaseHelper.insertData(name,age,gender);
            if(rowId != -1){
                Toast.makeText(getApplicationContext(),"Row "+rowId+" is Successfully Inserted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"Unsuccessfully Inserted",Toast.LENGTH_SHORT).show();
            }
        }else if(v.getId()==R.id.ShowButtonId){
            Cursor cursor= myDatabaseHelper.showAllData();
            if(cursor.getCount()==0){
                showData("Error","No Data found");
                return;
            }

            StringBuffer stringBuffer = new StringBuffer();
            while(cursor.moveToNext()){
                stringBuffer.append("Id: "+cursor.getString(0)+"\n");
                stringBuffer.append("Name: "+cursor.getString(1)+"\n");
                stringBuffer.append("Age: "+cursor.getString(2)+"\n");
                stringBuffer.append("Gender: "+cursor.getString(3)+"\n\n\n");
            }
            showData("Result Set",stringBuffer.toString());
        }else if(v.getId()==R.id.UpdateButtonId){
            myDatabaseHelper.updateData(id,name,age,gender);
            Toast.makeText(getApplicationContext(),"Data is updated",Toast.LENGTH_SHORT).show();
        }else if(v.getId()==R.id.DeleteButtonId){
           int isDelete= myDatabaseHelper.deleteData(id);

           if(isDelete==0){
               Toast.makeText(getApplicationContext(),"Data is Not Deleted",Toast.LENGTH_SHORT).show();
           }else{
               Toast.makeText(getApplicationContext(),"Row "+isDelete+"is deleted",Toast.LENGTH_SHORT).show();
           }
        }

    }

    public void showData(String title,String data){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(data);
        builder.setCancelable(true);
        builder.show();

    }
    void findId(){

        nameEditText = findViewById(R.id.NameEditTextId);
        ageEditText = findViewById(R.id.AgeEditTextId);
        genderEditText = findViewById(R.id.GenderEditTextId);
        idEditText = findViewById(R.id.IdEditTextId);
        insertButton = findViewById(R.id.insertButtonId);
        showButton = findViewById(R.id.ShowButtonId);
        updateButton = findViewById(R.id.UpdateButtonId);
        deleteButton = findViewById(R.id.DeleteButtonId);
    }
}
