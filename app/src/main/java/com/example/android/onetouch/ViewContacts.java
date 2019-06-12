package com.example.android.onetouch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ViewContacts extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextName;
    private EditText editTextContacts;
    private EditText editTextId;
    private Button btnPrev;
    private Button btnNext;
    private Button btnSave;
    private Button btnDelete;

    private static final String SELECT_SQL = "SELECT * FROM persons";

    private SQLiteDatabase db;

    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        openDatabase();

        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextContacts = (EditText) findViewById(R.id.editTextContacts);

        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        c = db.rawQuery(SELECT_SQL, null);
        c.moveToFirst();
        showRecords();
    }

    protected void openDatabase() {
        db = openOrCreateDatabase("PersonDB", Context.MODE_PRIVATE, null);
    }

    protected void showRecords() {
        String id = c.getString(0);
        String name = c.getString(1);
        String contact_num = c.getString(2);
        editTextId.setText(id);
        editTextName.setText(name);
        editTextContacts.setText(contact_num);

    }

    protected void moveNext() {
        if (!c.isLast())
            c.moveToNext();
        else if (c.isLast())
            Toast.makeText(getApplicationContext(), "No Further contacts ", Toast.LENGTH_LONG).show();


        showRecords();
    }

    protected void movePrev() {
        if (!c.isFirst())
            c.moveToPrevious();
        else if(c.isFirst())
            Toast.makeText(getApplicationContext(), "No Previous contacts ", Toast.LENGTH_LONG).show();

        showRecords();

    }



    protected void saveRecord() {
        String id = editTextId.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String con = editTextContacts.getText().toString().trim();

        if (name.equals("") || (con.equals(""))) {
            Toast.makeText(getApplicationContext(), "Provide contact name along with contact Mobile Number", Toast.LENGTH_LONG).show();
            return;
        }

        String sql = "UPDATE persons SET name='" + name + "', contact='" + con + "' WHERE id=" + id + ";";

        db.execSQL(sql);

        Toast.makeText(getApplicationContext(), "Records Saved Successfully", Toast.LENGTH_LONG).show();
        c = db.rawQuery(SELECT_SQL, null);
        c.moveToPosition(Integer.parseInt(id));
    }

    private void deleteRecord() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want delete this Contact?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String id = editTextId.getText().toString().trim();
                        String name = editTextName.getText().toString().trim();
                        String con = editTextContacts.getText().toString().trim();
                        String delSQL = "DELETE FROM persons WHERE id=" + id + ";";


                          if(name.equals("") && con.equals("")){
                            Toast.makeText(getApplicationContext(), "No Contacts to be deleted", Toast.LENGTH_LONG).show();
                        }
                        else if(c.isLast()&&c.isFirst()) {
                            editTextId.setText(" ");
                            editTextName.setText(" ");
                            editTextContacts.setText(" ");
                           //Toast.makeText(getApplicationContext(), "Record Deleted", Toast.LENGTH_LONG).show();
                           //String query = "INSERT OR REPLACE INTO persons(name,contact,email) VALUES('"+" "+"','"+" "+"','"+" "+"');";

                            //String query = "REPLACE INTO persons(name,contact,email) VALUES('" +" "+ "','" +" "+"','" +" "+ "');";

                           // String query = "UPDATE persons SET name='" +" "+ "', contact='" +" "+ "' WHERE id=" + id + ";";
                            db.execSQL(delSQL);
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            Toast.makeText(getApplicationContext(), "No more Contacts", Toast.LENGTH_LONG).show();
                                finish();

                        }
                        else {
                            db.execSQL(delSQL);
                            Toast.makeText(getApplicationContext(), "Record Deleted", Toast.LENGTH_LONG).show();

                             c = db.rawQuery(SELECT_SQL,null);
                              if (!c.isLast())
                                  c.moveToNext();
                              else if (!c.isFirst())
                                  c.moveToPrevious();
                              else
                                  c.moveToFirst();// Toast.makeText(getApplicationContext(), "No Further contacts ", Toast.LENGTH_LONG).show();
                              showRecords();
                          }


                       // editTextName.setText(" ",null);
                       // editTextContacts.setText(" ",null);

                      /*  if(c.isLast()&&c.isFirst()) {
                            editTextId.setText(" ");
                            editTextName.setText(" ");
                            editTextContacts.setText(" ");
                           Toast.makeText(getApplicationContext(), "Record Deleted", Toast.LENGTH_LONG).show();
                        }
                    */
                    }
                });


        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void ContactsDatabase(){
        Intent intent = new Intent(getApplicationContext(),ContactsDatabase.class);
        startActivity(intent);
        finish();
    }



    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            moveNext();
        }

        if (v == btnPrev) {
            movePrev();
        }

        if (v == btnSave) {
            saveRecord();
        }

        if (v == btnDelete) {
            deleteRecord();
        }
    }

}