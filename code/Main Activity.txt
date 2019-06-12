package com.example.android.onetouch;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.makeText;


public class MainActivity extends AppCompatActivity {
    // Content ctx;
    ImageButton ambulance, fire, crash, police;
    Button button1, button2, button3, button4;
    private SQLiteDatabase c_db, u_db;
    private Cursor c_c, u_c;

    private static final String SELECT_SQL_FROM_C = "SELECT * FROM Persons";
    private static final String SELECT_SQL_FROM_U = "SELECT * FROM user";

    private TextView textLocation, locationCoordinates;

    private LocationManager locationManager;
    private LocationListener listener;

    String locationURL = " ";


    //GeoLocation API
    Geocoder geocoder;
    List<Address> addresses;

    String c_contact_num = " ";

    String msg = " ", toast_msg = " ", properAddress = " ", s = " ";
    String address, locality, subLocality, city, state, country;
    String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        openDatabasePersondb();
        openDatabasePersondb_User();
        c_c = c_db.rawQuery(SELECT_SQL_FROM_C, null);
        u_c = u_db.rawQuery(SELECT_SQL_FROM_U, null);

        c_c.moveToFirst();

        u_c.moveToFirst();


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationCoordinates = (TextView) findViewById(R.id.locationCoordinates);
        textLocation = (TextView) findViewById(R.id.textViewLoc);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationURL = "LocationURL: \n http://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();

                locationCoordinates.setText("Location Coordinates:" +" "+ location.getLongitude() +" "+ location.getLatitude());
                try {
                    textLocation.setText(location_text(location.getLatitude(),location.getLongitude()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

    }

    protected void openDatabasePersondb() {
        c_db = openOrCreateDatabase("PersonDB", Context.MODE_PRIVATE, null);
        c_db.execSQL("CREATE TABLE IF NOT EXISTS persons (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR,contact VARCHAR);");
    }

    protected void openDatabasePersondb_User() {
        u_db = openOrCreateDatabase("PersonDB", Context.MODE_PRIVATE, null);
        u_db.execSQL("CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR,contact_number1 VARCHAR, contact_number2 VARCHAR,contact_number3 VARCHAR,contact_number4 VARCHAR,contact_number5 VARCHAR);");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 1000, 1, listener);

    }

    boolean checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }

    String location_text(double latitude, double longitude) throws IOException {

        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

         address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
         locality = addresses.get(0).getLocality();
         city = addresses.get(0).getLocality();
         state = addresses.get(0).getAdminArea();
         country = addresses.get(0).getCountryName();
         subLocality = addresses.get(0).getSubLocality();




        if (subLocality != null) {
            String sl =  address + "\n" + "SubLocality:" + subLocality;
            properAddress = sl;
        }

        if (city != null) {
            properAddress += "\nCity:" + city;
        }

        if (locality != null) {
            properAddress += "\nLocality:" + locality;
        }

        if (state != null) {
            properAddress += "\nState:" + state;
        }

        if(country != null) {
            properAddress += "\nCountry:" + country;
        }
/*
        String featureName = addresses.get(0).getFeatureName();
        if(featureName != null) {
            properAddress += "\nFEATURE ADDRESS" + featureName;
        }
*/
        Bundle getExtras = addresses.get(0).getExtras();
        if(getExtras != null) {
            properAddress += "\n Extra info:" + getExtras;
        }

        String phone = addresses.get(0).getPhone();
        if(phone != null) {
            properAddress += "\n Nearest contact" + phone;
        }

        String premises = addresses.get(0).getPremises();
        if(premises != null) {
            properAddress += "\n PREMISES:" + premises;
        }

        if(properAddress.length()<159){
            while(properAddress.length()!=159)
                properAddress += " ";
        }
       // pA = properAddress.substring(0,159);
        return properAddress;
    }





    public void ambulance(View v) {
            msg = "MEDICAL EMERGENCY";
        popupWindow(msg);
        }

    public void fire(View v){
            msg = "FIRE";
        popupWindow(msg);
        }

    public void police(View v){
            msg = "NEED OF POLICE";
        popupWindow(msg);
        }

    public void crash(View v){
            msg = "CAR CRASH";
        popupWindow(msg);

        }

    public void Call_Ambulance(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:7203924085"));

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    public void Call_Fire(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:7203924085"));

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    public void Call_Police(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:7203924085"));

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    public void Call_Help(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:7203924085"));

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }


    int popupWindow(final String s){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        boolean u_db_empty = true;
        Cursor u_cur = u_db.rawQuery("SELECT COUNT(*) FROM persons", null);
        if (u_cur != null && u_cur.moveToFirst()) {
            u_db_empty = (u_cur.getInt(0) == 0);
        }

        boolean c_db_empty = true;
        Cursor c_cur = c_db.rawQuery("SELECT COUNT(*) FROM persons", null);
        if (c_cur != null && c_cur.moveToFirst()) {
            c_db_empty = (c_cur.getInt(0) == 0);
        }

        if (c_db_empty && u_db_empty) {
            Toast Empty = makeText(this,"NO SAVED CONTACTS OR USER INFORMATION", Toast.LENGTH_SHORT);
            Empty.show();
            return 1;
        }

        alert.setTitle("EXTRA INFO");
        alert.setMessage("Provide extra Information if any, like extra available contact numbers or new recipient number if any & then click the SEND button ");

        Context context = alert.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //an EditText view to get user input
        final EditText extraInfo = new EditText(this);
        extraInfo.setHint("EXTRA INFO");
        layout.addView(extraInfo);

        final EditText otherContact = new EditText(this);
        otherContact.setHint("Other recipient's number ");
        layout.addView(otherContact);

        alert.setView(layout);

        alert.setPositiveButton("SEND", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                help_needed( s, "\n" + extraInfo.getText().toString(),otherContact.getText().toString());
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
        return 1;
    }


    void help_needed(final String msg,String extraInfo, String otherContact) {


        u_c = u_db.rawQuery(SELECT_SQL_FROM_U, null);
        c_c = c_db.rawQuery(SELECT_SQL_FROM_C, null);
        c_c.moveToFirst();
        u_c.moveToFirst();

        String user_name = u_c.getString(1);
        String contact_name =  c_c.getString(1);
        String user_contact1=  u_c.getString(2) + "\n";
        String user_contact2=  u_c.getString(3) + "\n";
        String user_contact3=  u_c.getString(4) + "\n";
        String user_contact4=  u_c.getString(5) + "\n";
        String user_contact5=  u_c.getString(6) + "\n";

        final String help_msg;
        boolean isInternetAvailable = checkConnectivity();
        if(isInternetAvailable) {
            help_msg = " your friend " + user_name + " needs your help. \n Its " + msg + "\n"
                    + "Contact Numbers" + "\n" +  user_contact1 + user_contact2 + user_contact3 + user_contact4 + user_contact5 + "\n"
                    + "Extra Information: " + extraInfo + "\n" + locationURL + "\n Address:\n" + textLocation.getText().toString();
            SMS(help_msg);
            //WhatsApp(help_msg);
            //SystemClock.sleep(20000);
            //Email(help_msg);
            //SystemClock.sleep(20000);
        }
        else{
            help_msg = " your friend " + user_name + " needs your help. \n Its " + msg + "\n"
                    + "Contact Numbers" + "\n" +  user_contact1 + user_contact2 + user_contact3 + user_contact4 + user_contact5 + "\n"
                    + "Extra Information: " + extraInfo + "\n" + locationURL ;
            SMS(help_msg);
        }
        if(otherContact!=null){
            sendSMS(otherContact,"Dear" + help_msg);
        }

        toast_msg ="Your contacts have been notified about your Emergency condition along with your location";
        Toast toast = makeText(getApplicationContext(),toast_msg,Toast.LENGTH_LONG);
        toast.show();

    }

    void SMS(String help_msg) {
        c_c = c_db.rawQuery(SELECT_SQL_FROM_C, null);
        c_c.moveToFirst();
        do {
            c_contact_num = c_c.getString(2);
            sendSMS(c_contact_num, "Dear " + c_c.getString(1) + help_msg);
            c_c.moveToNext();
        } while (!c_c.isAfterLast());
    }

    private void sendSMS(String contact_num, String msg) {

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(msg);
        sms.sendMultipartTextMessage(contact_num, null, parts, null, null);

    }


    void WhatsApp(final String help_msg) {
        new Thread(new Runnable() {
            public void run() {
                sendWhatsappmsg("Dear" + help_msg);
            }
        }).start();
    }

    private void sendWhatsappmsg(String msg){
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        //sendIntent.putExtra("jid", c_contact_num + "@s.whatsapp.net"); //jid - Java Image Downloader for Android
        sendIntent.putExtra(Intent.EXTRA_TEXT,  msg);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    void Email(final String help_msg) {
        new Thread(new Runnable() {
            public void run() {
                sendEmail(("Dear" + help_msg));
            }
        }).start();
    }

    private void sendEmail(String help_msg){
        Intent it = new Intent(Intent.ACTION_SEND_MULTIPLE);
    //    it.putExtra(Intent.EXTRA_EMAIL, "kumawat_ritesh@hotmail.com");
        it.putExtra(Intent.EXTRA_SUBJECT, msg);
        it.putExtra(Intent.EXTRA_TEXT, (help_msg));
        it.setType("message/rfc822");
        startActivity(it);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_Info:
                intent = new Intent(getApplicationContext(),UserData.class);
                startActivity(intent);
                return true;
            case R.id.menu_add:
                intent = new Intent(getApplicationContext(),ContactsDatabase.class);
                startActivity(intent);
                return true;


            case R.id.menu_view: {
                boolean empty = true;
                Cursor cur = c_db.rawQuery("SELECT COUNT(*) FROM persons", null);
                cur.moveToFirst();
                if (cur != null && cur.moveToFirst()) {
                    empty = (cur.getInt(0) == 0);
                }



                if (!empty) {
                    intent = new Intent(getApplicationContext(), ViewContacts.class);
                    startActivity(intent);
                } else {
                    Toast Empty = makeText(this,"NO SAVED CONTACTS", Toast.LENGTH_SHORT);
                    Empty.show();
                }
                return true;
            }

            case R.id.App_Desc:
                toast_msg ="Prepared by:\nRitesh Kumawat";
                Toast toast = Toast.makeText(getApplicationContext(),toast_msg,Toast.LENGTH_LONG);
                toast.show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
