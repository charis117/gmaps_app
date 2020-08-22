package gr.chris.transportation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "routes";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_EMAIL = "label";
    public static final String CONTACTS_COLUMN_STREET = "title";
    public static final String CONTACTS_COLUMN_CITY = "direction";
    public static final String CONTACTS_COLUMN_PHONE = "color";
    private HashMap hp;

    public DBHelper(Context context,String fileName) {
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
        static class Station{
        float lat;
        float lon;
        String name;
        int id;
        String sym;

        public Station(float lat, float lon, String name, int id, String sym) {
            this.lat = lat;
            this.lon = lon;
            this.name = name;
            this.id = id;
            this.sym = sym;
        }
    }
}