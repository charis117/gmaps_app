package gr.chris.transportation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import gr.chris.transportation.legacy.Engine;

public class SQLEngine extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "routes";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_EMAIL = "label";
    public static final String CONTACTS_COLUMN_STREET = "title";
    public static final String CONTACTS_COLUMN_CITY = "direction";
    public static final String CONTACTS_COLUMN_PHONE = "color";
    private HashMap hp;

    public SQLEngine(Context context, String fileName) {
        super(context, fileName , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table contacts " +
                        "(id integer primary key, name text,phone text,email text, street text,place text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact (String name, String phone, String email, String street,String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from routes where _id="+id, null );

        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllCotacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from routes", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Station> getRoute(String name){
        ArrayList<Station> dromologio = new ArrayList<Station>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select marker from stops where route="+name, null );
        res.moveToFirst();

        ArrayList<Integer> routeIDS=new ArrayList<Integer>();
        while(res.isAfterLast() == false){
            routeIDS.add(res.getInt(res.getColumnIndex("marker")));
            res.moveToNext();
        }

        for(int routeID:routeIDS) {
            Cursor r2 = db.rawQuery("select * from markers where _id=" + routeID, null);
            r2.moveToFirst();
            dromologio.add(new Station(
                    r2.getFloat(r2.getColumnIndex("lat")),
                    r2.getFloat(r2.getColumnIndex("lon")),
                    r2.getString(r2.getColumnIndex("name")),
                    routeID,
                    r2.getString(r2.getColumnIndex("symbol"))
            ));
        }

        return dromologio;
    }




    public ArrayList<Engine.Route> getRoutes(String stationSym){
        ArrayList<Station> dromologio = new ArrayList<Station>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select _id from markers where symbol="+stationSym, null );
        res.moveToFirst();
        Log.v("INFO","REQUESTED STATION WITH SYMBOL:"+stationSym);

        if(res.getCount()==0){
            return null;
        }

        int stationID=res.getInt(res.getColumnIndex("_id"));
        Log.v("INFO","FOUND STATION ID:"+stationID);
        res=db.rawQuery("select route from stops where marker="+stationID,null);
        res.moveToFirst();
        Log.v("INFO","FOUND ROUTES COUNT:"+res.getCount());


        ArrayList<Engine.Route> avaRoutes=new ArrayList<Engine.Route>();
        int cnt=1;
        while(res.isAfterLast() == false){
            int routeID=res.getInt(res.getColumnIndex("route"));

            Cursor c2=db.rawQuery("select * from routes where _id="+routeID,null);
            c2.moveToFirst();
            avaRoutes.add(new Engine.Route(
                    c2.getString(c2.getColumnIndex("title")),
                    c2.getString(c2.getColumnIndex("label")),
                    String.valueOf(routeID),
                    String.valueOf(c2.getInt(c2.getColumnIndex("agency"))),
                    cnt
            ));
            cnt++;

            res.moveToNext();
        }

        return avaRoutes;
    }

    public Engine.Route getRouteFromID(String ID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c2=db.rawQuery("select * from routes where _id="+ID,null);
        c2.moveToFirst();
        return new Engine.Route(
                c2.getString(c2.getColumnIndex("title")),
                c2.getString(c2.getColumnIndex("label")),
                String.valueOf(ID),
                String.valueOf(c2.getInt(c2.getColumnIndex("agency"))),
                c2.getPosition());
    }

    public int getM1id(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c2=db.rawQuery("select * from routes where name=\"m1\"",null);
        c2.moveToFirst();
        return
                c2.getInt(c2.getColumnIndex("_id"));

    }

    public ArrayList<Engine.Route> getAllRoutes(){
        ArrayList<Station> dromologio = new ArrayList<Station>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from routes", null );
        res.moveToFirst();
        ArrayList<Engine.Route> avaRoutes=new ArrayList<Engine.Route>();
        int cnt=1;
        while(res.isAfterLast() == false){
            avaRoutes.add(new Engine.Route(
                    res.getString(res.getColumnIndex("title")),
                    res.getString(res.getColumnIndex("label")),
                    String.valueOf(res.getInt(res.getColumnIndex("_id"))),
                    String.valueOf(res.getInt(res.getColumnIndex("agency"))),
                    cnt
            ));
            cnt++;

            res.moveToNext();
        }

        return avaRoutes;
    }

    public ArrayList<Station> getAllStations(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from markers", null );
        res.moveToFirst();
        ArrayList<Station> stats=new ArrayList<Station>();
        int cnt=1;
        while(res.isAfterLast() == false){
            stats.add(new Station(
                    res.getFloat(res.getColumnIndex("lat")),
                    res.getFloat(res.getColumnIndex("lon")),
                    res.getString(res.getColumnIndex("name")),
                    res.getInt(res.getColumnIndex("_id")),
                    res.getString(res.getColumnIndex("symbol"))
            ));
            cnt++;

            res.moveToNext();
        }

        return stats;
    }
        static class Station{
        float lat;
        float lon;
        String name;
        int id;
        String sym;
        LatLng gmloc;

        public Station(float lat, float lon, String name, int id, String sym) {
            this.lat = lat;
            this.lon = lon;
            this.name = name;
            this.id = id;
            this.sym = sym;
            this.gmloc=new LatLng(lat,lon);
        }
    }

    //public Engine.Station getStation(String stati)
}