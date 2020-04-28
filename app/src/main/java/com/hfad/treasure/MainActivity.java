package com.hfad.treasure;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static String TAG = "MainActivity";
    private TextView locationUpdDbTV;
    private TextView srvLocationResponseTV;
    private TextView srvContentResponseTV;
    private TextView fileSavedTV;
    private TextView contentStatusUpdTV;
    private TextView contentSavedDbTV;
    private CheckedTextView ctvLatitude;
    private CheckedTextView ctvLongitude;
    private EditText userName;

    private Button locationBT;
    private Button sendLocationBT;
    private Button getContentBT;
    private Button clearAll;

    private DatabaseHelper localDB;
    private AnyLocation anyLocation;
    private Coordinates coordinates;
    private Content content;
    private WSSender wsSender;
    private TreasureHttpClientHelper2 treasureHttpClientHelper2;

    private GoogleMap map;

    private Double lat = 0.0;
    private Double lon = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        },0);

        locationUpdDbTV = findViewById(R.id.locationUpdDbTV);
        srvLocationResponseTV = findViewById(R.id.srvLocationResponseTV);
        srvContentResponseTV = findViewById(R.id.srvContentResponseTV);
        fileSavedTV = findViewById(R.id.fileSavedTV);
        contentStatusUpdTV = findViewById(R.id.contentStatusUpdTV);
        contentSavedDbTV = findViewById(R.id.contentSavedDbTV);
        ctvLatitude = findViewById(R.id.ctvLatitude);
        ctvLatitude.bringToFront();
        ctvLongitude = findViewById(R.id.ctvLongitude);
        ctvLongitude.bringToFront();
        userName = findViewById(R.id.username);
        userName.bringToFront();

        locationBT = findViewById(R.id.locationBT);
        sendLocationBT = findViewById(R.id.sendLocationBT);
        getContentBT = findViewById(R.id.getContentBT);
        clearAll = findViewById(R.id.clearAll);

        localDB = new DatabaseHelper(this);
        anyLocation = new AnyLocation(this);

        wsSender = new WSSender();
        treasureHttpClientHelper2 = new TreasureHttpClientHelper2();


        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctvLongitude.setChecked(false);
                ctvLongitude.setText("Longitude: ");
                ctvLatitude.setChecked(false);
                ctvLatitude.setText("Latitude: ");
                map.clear();
            }
        });

        locationBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
* ==========================================================================================
* (1) Получаем latitude и longitude
* ==========================================================================================
*/
                anyLocation.GetCurrentLocation(MainActivity.this);
                lon = anyLocation.getLongitude();
                lat = anyLocation.getLatitude();

                //Stage1 testdata
                //lat = 55.9214905;
                //lon = 37.541913;
                //Stage2 testdata
                lat = 55.9222685;
                lon = 37.5386495;

                GoogleMapPosition(MainActivity.this, map);

                Log.d("locationBT ", "userName: " + userName.getText().toString());
                ctvLatitude.setText("Latitude: " + lat.toString());
                ctvLongitude.setText("Longitude: " + lon.toString());
/*
* ===========================================================================================
* (2) Записываем latitude и longitude в БД
* ===========================================================================================
*/
                AddLocationDataDB(lat.toString(), lon.toString(), "Accaunt1", userName.getText().toString(), null);
            }
        });

        sendLocationBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
* ========================================================================================================
* (3) Получаем последнюю не отправленную на вебсервис запись из базы данных
* ========================================================================================================
*/
                coordinates = new Coordinates();
                //GetLastLocationDB(coordinates, false, true, null); - так должно быть!
                GetLastLocationDB(coordinates, false, true, null);
                //Log.d(TAG + "locationBT: ", new StringBuilder().append(" lat: ").append(coordinates.getLatitude().get(0)).append(" lon: ").append(coordinates.getLongitude().get(0)).append(" status: ").append(coordinates.getIsSent().get(0)).append(" ID: ").append(coordinates.getId().get(0)).append(" Has saved DB").toString());
/*
* =========================================================================================================
* (4) Отправляем запись на вебсервис http://192.168.1.72:9179
* =========================================================================================================
*/
                try {
                    wsSender.setDatabaseHelper(localDB);
                    wsSender.setCoordEntity(Coordinates.ToJSONString(coordinates));
                    wsSender.SendCoordinates("http://82.148.16.121:9179");
                    //srvLocationResponseTV.setText("Location has sent to server. ID: " + coordinates.getId().get(0));
                    ctvLatitude.setChecked(true);
                    ctvLongitude.setChecked(true);

                    content = new Content();
                    GetLastContentDataDB(content, false, true, null);
                    if(content.getId().get(0) > 0){
                        contentSavedDbTV.setText("list of content saved with ID: " + content.getId().get(0));
                    } else {
                        contentSavedDbTV.setText("list of content saved with ID: " + null);
                    }
/*
* ===============================================================================================================
* (5) Помечем в БД отправленную локацию как "отправленная"
* ===============================================================================================================
*/
                        UpdLocationDataDB(coordinates.getId().get(0), true, null);
                        locationUpdDbTV.setText("location ID: " + coordinates.getId().get(0) + " is updated");
                        treasureHttpClientHelper2.resetJSONResponse();
                } catch (Exception e) {
                    e.printStackTrace();
                    srvLocationResponseTV.setText("There is no new Coordinates for sending" );
                }
            }
        });

        getContentBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
* ===============================================================================================================
* (6)
*     1. Получаем из БД список последнего нескачанного контента
*     2. Отправляем запрос на скачивание на вебсервис SendListOfContent
*     3. Сохраняем полученный в ответ файл в локальное хранилище (папка Downloads)
*     4. Отмечаем в БД список контента, как "скачанный"
* Здесь точно нужен РЕФАКТОРИНГ
* GetLastContentDataDB(content,false,false, null) - сейчас использован только лишь для получения данных для отображения в srvContentResponseTV
* Логика получения списка нескачанного контента сейчас зашита в wsSender.PrepareContentEntity
*  Логика:
*  - отправки запроса
*  - записи файла в хранилище
*  - Установка пометки в БД о том, что контент скачан
* размещена в wsSender.SendListOfContent("http://192.168.1.72:9180", localDB);
* ===============================================================================================================
 */
                try {
                    content = new Content();
                    GetLastContentDataDB(content,false,false, null);

                    wsSender.setContEntity(Content.ToJSONString(content));
                    wsSender.setLastContentId(content.getLastContentId());
                    wsSender.setLastFileName(content.getLastFileName());
                    wsSender.SendListOfContent("http://82.148.16.121:9180", localDB);

                    srvContentResponseTV.setText("Content ID: " + content.getId().get(0) + " has requested from server. Result: " + content.getContentType().get(0));
/*
*==============================================================================================================
* (7)
*  Отображаем информацию о том, какой контент скачан в fileSavedTV
* ==============================================================================================================
*/
                    GetLastContentDataDB(content, true, false, null);
                    fileSavedTV.setText("File: " + content.getItemName().get(0) + " saved to filesystem");
                } catch (Exception e) {
                    e.printStackTrace();
                    srvContentResponseTV.setText("All Content has been sent to the server.");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:
                //Add some code
               return true;
            case R.id.settings:
                anyLocation.OnGPS(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void AddLocationDataDB(String lat, String lon, String accauntName, String userName, String error){
        ContentValues contentValues = new ContentValues();
        contentValues.put("accauntName", accauntName);
        contentValues.put("userName", userName);
        contentValues.put("latitude", lat);
        contentValues.put("longitude", lon);
        contentValues.put("dt", "123456789");
        contentValues.put("isSent", 0);

        try{
            localDB.addData("current_location", contentValues, "ll_id");
        } catch (SQLiteException e){
            Log.d(TAG, "AddLocationDataDB: " + e.getMessage());
        }
    }

    private void GetLocationDataDB(Coordinates coordinates, int id, String error){

        Cursor data = localDB.getData("current_location", "ll_id", id);
        if(data == null){
            error = "There is no data";
        } else {
            while (data.moveToNext()) {
                coordinates.setId(data.getInt(0));
                coordinates.setAccauntName(data.getString(1));
                coordinates.setUserName(data.getString(2));
                coordinates.setLatitude(data.getString(3));
                coordinates.setLongitude(data.getString(4));
                coordinates.setIsSent(data.getString(5));
            }
        }
    }

    private void GetLastLocationDB(Coordinates coordinates, boolean isSent, boolean sortDesc, String error){

        String whereValue = isSent ? "1": "0";
        Cursor data = localDB.getLastRecord("current_location", "isSent", whereValue, "ll_id", sortDesc);
        if(data == null){
            error = "There is no data";
        } else {
            while (data.moveToNext()) {
                coordinates.setId(data.getInt(0));
                coordinates.setAccauntName(data.getString(1));
                coordinates.setUserName(data.getString(2));
                coordinates.setLatitude(data.getString(3));
                coordinates.setLongitude(data.getString(4));
                coordinates.setIsSent(data.getString(5));
            }
        }
    }

    private void UpdLocationDataDB(int id, boolean isSent, String error){

        String isSentValue = isSent ? "1": "0";
        ContentValues contentValues = new ContentValues();
        contentValues.put("isSent", isSentValue);

        boolean result = localDB.updData("current_location", contentValues, "ll_id", id);

        if(result){
            error = "Update is OK";
        }else{
            error = "Update is not OK";
        }
    }

    private void AddContentDataDB(String name, String content, String error){
        ContentValues contentValues = new ContentValues();
        contentValues.put("itemName", name);
        contentValues.put("contentType", content);
        contentValues.put("dt", "123456789");
        contentValues.put("isDownloaded", 0);

        try {
            localDB.addData("available_content", contentValues, "ac_id");
        } catch (SQLiteException e){
            Log.d(TAG, "AddContentDataDB: " + e.getMessage());
        }
    }

    private void UpdContentDataDB(int id, boolean isDownloaded, String error){
        String isDownloadedValue = isDownloaded ? "1": "0";
        ContentValues contentValues = new ContentValues();
        contentValues.put("isDownloaded", isDownloadedValue);

        boolean result = localDB.updData("available_content", contentValues, "ac_id", id);

        if(result){
            error = "Update is OK";
        }else{
            error = "Update is not OK";
        }
    }

    private Boolean GetContentDataDB(Content content, int id, String error){

        Cursor data = localDB.getData("available_content", "ac_id", id);
        if(data.moveToFirst() && data.getCount() > 0){
            try {
                while (data.moveToNext()) {
                    content.setItemName(data.getString(1));
                    content.setContentType(data.getString(2));
                    content.setIsDownloaded(data.getString(4));
                    content.setId(data.getInt(0));
                }
            } finally {
                data.close();
            }
            return true;
        } else {
            return false;
        }
    }

    private Boolean GetLastContentDataDB(Content content, boolean isDownloaded, boolean sortDesc, String error){
        String whereValue = isDownloaded ? "1": "0";
        Cursor data = localDB.getLastRecord("available_content", "isDownloaded", whereValue, "ac_id", sortDesc);
        //if(data.moveToFirst() && data.getCount() > 0){
        if(data == null){
            return false;
        } else {
            try {
                while (data.moveToNext()) {
                    content.setId(data.getInt(0));
                    content.setAccauntName(data.getString(1));
                    content.setUserName(data.getString(2));
                    content.setContentType(data.getString(3));
                    content.setItemName(data.getString(4));
                    content.setIsDownloaded(data.getString(6));
                }
            } catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            } finally {
                data.close();
            }
            return true;
        }
    }
    public void GoogleMapPosition(Context context, GoogleMap googleMap){
        map = googleMap;
        anyLocation.GetCurrentLocation(context);
        if(lat == 0.0 && lon == 0.0){
            lat = 37.421998333333335;
            lon = -122.08400000000002;
        }

        LatLng CurrentLocation = new LatLng(lat, lon);

        map.addMarker(new MarkerOptions().position(CurrentLocation).title("Current Location"));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        map.moveCamera(CameraUpdateFactory.newLatLng(CurrentLocation));
        map.animateCamera(zoom);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMapPosition(this, googleMap);
    }
}