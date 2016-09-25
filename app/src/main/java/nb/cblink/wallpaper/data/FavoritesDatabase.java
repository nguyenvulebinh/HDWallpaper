package nb.cblink.wallpaper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import nb.cblink.wallpaper.model.Image;

/**
 * Created by nguyenbinh on 24/09/2016.
 */

public class FavoritesDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favorites.db";

    private static final String TABLE_IMAGE = "image";
    private static final String TABLE_LINK = "link";

    public static final int TYPE_FAVORITES = 1;
    public static final int TYPE_HISTORY = 0;
    public static final int TYPE_DELETE = -1;

    //Cac truong cua bang image
    private static final String TABLE_IMAGE_FIELD_ID = "id_image";
    private static final String TABLE_IMAGE_FIELD_TYPE = "type";
    private static final String TABLE_IMAGE_FIELD_NAME = "name";
    private static final String TABLE_IMAGE_FIELD_THUMB_URL = "thumb_url";
    private static final String TABLE_IMAGE_FIELD_COUNT_VIEW = "count_view";

    //Cac truong cua bang link
    private static final String TABLE_LINK_FIELD_ID = "id";
    private static final String TABLE_LINK_FIELD_ID_IMAGE = "id_image";
    private static final String TABLE_LINK_FIELD_TYPE = "type";
    private static final String TABLE_LINK_FIELD_LINK = "link";


    private static final String[] listRes = {"320 x 480", "480 x 800", "480 x 854", "540 x 960", "600 x 800", "600 x 1024", "640 x 960", "640 x 1136", "720 x 720", "720 x 1280", "768 x 1024", "768 x 1280", "800 x 1280", "1080 x 1920", "1200 x 1920", "1536 x 2048"};

    private static final int DATABASE_VERSION = 1;

    private static FavoritesDatabase favoritesDatabase = null;

    private FavoritesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static FavoritesDatabase getInstance(Context context){
        if(favoritesDatabase == null){
            favoritesDatabase = new FavoritesDatabase(context);
        }
        return favoritesDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_IMAGE +
                "(" + TABLE_IMAGE_FIELD_ID + " integer PRIMARY KEY," +
                TABLE_IMAGE_FIELD_TYPE + " integer, " +
                TABLE_IMAGE_FIELD_NAME + " TEXT, " +
                TABLE_IMAGE_FIELD_THUMB_URL + " TEXT, " +
                TABLE_IMAGE_FIELD_COUNT_VIEW + " TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_LINK +
                "(" + TABLE_LINK_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TABLE_LINK_FIELD_ID_IMAGE + " integer, " +
                TABLE_LINK_FIELD_TYPE + " TEXT, " +
                TABLE_LINK_FIELD_LINK + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }


    public void saveImage(Image image, int type) {
        switch (type){
            case TYPE_FAVORITES:
                deleteRecord(image.getId());
                addRecord(image, type);
                break;
            case TYPE_HISTORY:
                if(!existFavorit(image.getId())){
                    addRecord(image, type);
                }
                break;
            case TYPE_DELETE:
                deleteRecord(image.getId());
                break;
        }
    }

    private void addRecord(Image image, int type) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valuesImage = new ContentValues();
        valuesImage.put(TABLE_IMAGE_FIELD_ID, image.getId());
        valuesImage.put(TABLE_IMAGE_FIELD_TYPE, type);
        valuesImage.put(TABLE_IMAGE_FIELD_NAME, image.getName());
        valuesImage.put(TABLE_IMAGE_FIELD_THUMB_URL, image.getThumbUrl());
        valuesImage.put(TABLE_IMAGE_FIELD_COUNT_VIEW, image.getCountView());

        ContentValues valuesLink = new ContentValues();
        valuesLink.put(TABLE_LINK_FIELD_ID_IMAGE, image.getId());
        for (String key : listRes) {
            String temp = image.getListUrl().get(key);
            if (temp != null) {
                valuesLink.put(TABLE_LINK_FIELD_TYPE, key);
                valuesLink.put(TABLE_LINK_FIELD_LINK, temp);
                db.insert(TABLE_LINK, null, valuesLink);
            }
        }

        db.insert(TABLE_IMAGE, null, valuesImage);
    }

    private int updateRecord(int id, int type) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_IMAGE_FIELD_ID, id);
        values.put(TABLE_IMAGE_FIELD_TYPE, type);
        return db.update(TABLE_IMAGE, values, TABLE_IMAGE_FIELD_ID + " = ?",
                new String[]{id + ""});
    }

    private void deleteRecord(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_IMAGE, TABLE_IMAGE_FIELD_ID + " = ?", new String[]{id + ""});
        db.delete(TABLE_LINK, TABLE_LINK_FIELD_ID_IMAGE + " = ?", new String[]{id + ""});
    }

    private int findImageID(int id) {
        int returnVal = -1;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + TABLE_IMAGE_FIELD_ID + " FROM " + TABLE_IMAGE +
                        " WHERE " + TABLE_IMAGE_FIELD_ID + " = ?", new String[]{id + ""});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            returnVal = cursor.getInt(0);
        }
        cursor.close();
        return returnVal;
    }


    public boolean exist(int id) {
        boolean exist = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT "+TABLE_IMAGE_FIELD_ID+" FROM " + TABLE_IMAGE +
                        " WHERE "+TABLE_IMAGE_FIELD_ID+" = ?", new String[]{id+""});
        if(cursor.getCount() == 1) exist = true;
        cursor.close();
        return exist;
    }

    public boolean existFavorit(int id) {
        boolean exist = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT "+TABLE_IMAGE_FIELD_ID+" FROM " + TABLE_IMAGE +
                        " WHERE "+TABLE_IMAGE_FIELD_ID+" = ? AND " + TABLE_IMAGE_FIELD_TYPE + " = " + TYPE_FAVORITES, new String[]{id+""});
        if(cursor.getCount() == 1) exist = true;
        cursor.close();
        return exist;
    }

    public ArrayList<Image> getList(int type) {
        ArrayList<Image> listFavorites = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT *  FROM " + TABLE_IMAGE + " WHERE "+TABLE_IMAGE_FIELD_TYPE+" = "+type;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                Image image = new Image();
                image.setName(cursor.getString(cursor.getColumnIndex(TABLE_IMAGE_FIELD_NAME)));
                image.setThumbUrl(cursor.getString(cursor.getColumnIndex(TABLE_IMAGE_FIELD_THUMB_URL)));
                image.setCountView(cursor.getString(cursor.getColumnIndex(TABLE_IMAGE_FIELD_COUNT_VIEW)));
                image.setId(cursor.getInt(cursor.getColumnIndex(TABLE_IMAGE_FIELD_ID)));
                String queryLink = "SELECT *  FROM " + TABLE_LINK + " WHERE "+TABLE_LINK_FIELD_ID_IMAGE+" = "+image.getId();
                Cursor cursorLink = db.rawQuery(queryLink, null);
                if(cursorLink.moveToFirst()){
                    do{
                        image.getListUrl().put(cursorLink.getString(cursorLink.getColumnIndex(TABLE_LINK_FIELD_TYPE)), cursorLink.getString(cursorLink.getColumnIndex(TABLE_LINK_FIELD_LINK)));
                    }while (cursorLink.moveToNext());
                }
                cursorLink.close();
                listFavorites.add(image);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return listFavorites;
    }

    public Image getSingle(int id) {
        Image image = new Image();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT *  FROM " + TABLE_IMAGE + " WHERE "+TABLE_IMAGE_FIELD_ID+" = "+id;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                image = new Image();
                image.setName(cursor.getString(cursor.getColumnIndex(TABLE_IMAGE_FIELD_NAME)));
                image.setThumbUrl(cursor.getString(cursor.getColumnIndex(TABLE_IMAGE_FIELD_THUMB_URL)));
                image.setCountView(cursor.getString(cursor.getColumnIndex(TABLE_IMAGE_FIELD_COUNT_VIEW)));
                image.setId(cursor.getInt(cursor.getColumnIndex(TABLE_IMAGE_FIELD_ID)));
                String queryLink = "SELECT *  FROM " + TABLE_LINK + " WHERE "+TABLE_LINK_FIELD_ID_IMAGE+" = "+image.getId();
                Cursor cursorLink = db.rawQuery(queryLink, null);
                if(cursorLink.moveToFirst()){
                    do{
                        image.getListUrl().put(cursorLink.getString(cursorLink.getColumnIndex(TABLE_LINK_FIELD_TYPE)), cursorLink.getString(cursorLink.getColumnIndex(TABLE_LINK_FIELD_LINK)));
                    }while (cursorLink.moveToNext());
                }
                cursorLink.close();
            }while (cursor.moveToNext());
        }
        cursor.close();

        return image;
    }

}
