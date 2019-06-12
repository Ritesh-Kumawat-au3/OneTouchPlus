package com.example.android.onetouch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserData extends AppCompatActivity implements View.OnClickListener {

    protected String User_contacts;

    private EditText name, contact_number1, contact_number2, contact_number3, contact_number4, contact_number5, contacts;

    private Button save;

    private SQLiteDatabase db;


    String user_name, num1, num2, num3, num4, num5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        createUserDatabase();

        name = (EditText) findViewById(R.id.user_name);
        contact_number1 = (EditText) findViewById(R.id.EditText_contact_number1);
        contact_number2 = (EditText) findViewById(R.id.EditText_contact_number2);
        contact_number3 = (EditText) findViewById(R.id.EditText_contact_number3);
        contact_number4 = (EditText) findViewById(R.id.EditText_contact_number4);
        contact_number5 = (EditText) findViewById(R.id.EditText_contact_number5);
        contacts = (EditText) findViewById(R.id.EditText_contacts);
        save = (Button) findViewById(R.id.Save);

      /*  boolean c_db_empty = true;
        Cursor c_cur = c_db.rawQuery("SELECT COUNT(*) FROM persons", null);
        if (c_cur != null && c_cur.moveToFirst()) {
            c_db_empty = (c_cur.getInt(0) == 0);
        }
        String query = "INSERT OR REPLACE INTO user(name, contact_number1, contact_number2, contact_number3, contact_number4, " +
              "contact_number5) VALUES('"+user_name+"','"+num1+"','"+num2+"','"+num3+"','"+num4+"','"+num5+"');";
*/

        save.setOnClickListener(this);
        User_contacts = num1 + num2 + num3 + num4 + num5;


    }


    protected void createUserDatabase() {
        db = openOrCreateDatabase("PersonDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS user (id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + "name VARCHAR," +
                "contact_number1 VARCHAR, contact_number2 VARCHAR,contact_number3 VARCHAR,contact_number4 VARCHAR,contact_number5 VARCHAR);");
    }

    public void onClick(View v) {
        if (v == save) {
            insertIntoUserDB();
        }
    }

    protected void insertIntoUserDB() {
        user_name = name.getText().toString().trim();
        num1 = contact_number1.getText().toString().trim();
        num2 = contact_number2.getText().toString().trim();
        num3 = contact_number3.getText().toString().trim();
        num4 = contact_number4.getText().toString().trim();
        num5 = contact_number5.getText().toString().trim();

        if (user_name.equals("") || ((num1.equals("") && num2.equals("") && num3.equals("") && num4.equals("") && num5.equals("")))) {
            Toast.makeText(getApplicationContext(), "Please provide your name along with at least one contact number", Toast.LENGTH_LONG).show();
            return;
        }

  /*      String query = "UPDATE user SET name = '"+user_name+"', contact_number1 = '"+num1+"', contact_number2 = '"+num2+"'," +
                " contact_number3 = '"+num3+"', contact_number4 = '"+num4+"', " + "contact_number5 = '"+num5+"' WHERE id = " + id + ";";
*/


        String query = "INSERT OR REPLACE INTO user(name, contact_number1, contact_number2, contact_number3, contact_number4, " +
               "contact_number5) VALUES('"+user_name+"','"+num1+"','"+num2+"','"+num3+"','"+num4+"','"+num5+"');";

            db.execSQL(query);
            Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
     //   contacts.setText(num1 + "\n " + num2+ "\n " + num3+ "\n " + num4+ "\n " + num5);
    }

}