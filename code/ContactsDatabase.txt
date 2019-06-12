package com.example.android.onetouch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.android.onetouch.R.id.addButton;


public class ContactsDatabase extends AppCompatActivity implements View.OnClickListener  {
    private EditText name,contact;
    private Button addContact,viewContact;

    private SQLiteDatabase db;

    private static final int RESULT_PICK_CONTACT = 85;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_database);

        createDatabase();

        name = (EditText) findViewById(R.id.Contact_Name);
        contact = (EditText) findViewById(R.id.Contact_Number);

        addContact = (Button) findViewById(addButton);
        viewContact = (Button) findViewById(R.id.viewButton);

        addContact.setOnClickListener(this);
        viewContact.setOnClickListener(this);
    }

    public void pickContact(View v)
    {

        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }
    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String contactName = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            contactName = cursor.getString(nameIndex);
            // Set the value to the textviews
            name.setText(contactName);
            contact.setText(phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    protected void createDatabase() {
        db = openOrCreateDatabase("PersonDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS persons(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR," +
                "contact VARCHAR);");
    }
    protected void insertIntoDB() {
        String nam = name.getText().toString().trim();
        String con = contact.getText().toString().trim();
        if (nam.equals("") || con.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Provide Contact Person Name along with Contact Mobile Number", Toast.LENGTH_LONG).show();
            return;
        }

       String query = "INSERT OR REPLACE INTO persons(name,contact) VALUES('"+nam+"','"+con+"');";
        db.execSQL(query);
        Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
    }

    public void viewContacts(){
        Intent intent = new Intent(getApplicationContext(),ViewContacts.class);
        startActivity(intent);
        finish();
    }

/*
    public void Moble_Contacts(View v){
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();

        }
        phones.close();

    }
*/
    @Override
    public void onClick(View v) {

        if (v == addContact) {
            insertIntoDB();
        }
        if (v == viewContact) {
            boolean empty = true;
            Cursor cur = db.rawQuery("SELECT COUNT(*) FROM persons", null);
            if (cur != null && cur.moveToFirst()) {
                empty = (cur.getInt(0) == 0);
            }
            //cur.close();

            if (!empty) {
                viewContacts();

            } else {
                Toast Empty = Toast.makeText(this,"NO SAVED CONTACTS", Toast.LENGTH_SHORT);
                Empty.show();
            }
        }


    }





}

