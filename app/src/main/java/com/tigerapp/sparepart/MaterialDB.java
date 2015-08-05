package com.tigerapp.sparepart;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

import com.tigerapp.sparepart.com.tigerapp.sparepart.okhttp.MaterialManager;
import com.tigerapp.sparepart.model.Material;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Little on 7/7/2015.
 */
public class MaterialDB {
    public static final String tag = "MaterialDB";
    private static final String DBNAME = "SPAREPART";
    private static final String TABLE_NAME = "material";

    /**
     * aliasMap for mapping result to cursor, that show in quick suggestion
     */
    private HashMap<String,String > cursorAliasMap;

    public MaterialDB(Context context) {
        cursorAliasMap = new HashMap<>();

        // Unique id for the each Suggestion (Mandatory)
        cursorAliasMap.put("_ID", "materialNo as _id");

        // Text for quick Suggestion
        cursorAliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, "descr as "+SearchManager.SUGGEST_COLUMN_TEXT_1);

        // This value will be append to the intent data on selecting an item from Search Result, or Suggestion
        cursorAliasMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "materialNo as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }

    /**
     * return materials by matNo Like ?
     * @param selectionArgs
     * @return
     */
    public Cursor findAllByIdLikeOrDescrLike(String[] selectionArgs) {
        Log.d(tag, "findAllByIdLikeOrDescrLike() called.");
        MatrixCursor matrixCursor = null;
        String searchText = "";

        if (selectionArgs[0] != null) {
            searchText = selectionArgs[0];
        }


        try {
            List<Material> materials = MaterialManager.findAllByIdLikeOrDescrLike(searchText);
            matrixCursor = new MatrixCursor(new String[]{
                    "_id"
                    , SearchManager.SUGGEST_COLUMN_TEXT_1
                    , SearchManager.SUGGEST_COLUMN_TEXT_2
                    , SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                    , SearchManager.SUGGEST_COLUMN_ICON_1
            });
            for (Material material : materials) {

                matrixCursor.newRow()
                        .add(material.get_id())
                        .add(material.getMaterialNo())
                        .add(material.getDescr())
                        .add(material.getMaterialNo())
                        .add("android.resource://10.0.173.20/sparepart/thumbnails/"+material.getMaterialNo()+"-tn.jpg");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrixCursor;
    }

    public Cursor getMaterial(String id) {
        Log.d(tag, "getMaterial() id = "+id);
        MatrixCursor matrixCursor = null;


        Material material = MaterialManager.getMaterial(id);
        matrixCursor = new MatrixCursor(new String[]{
                "_id"
                , SearchManager.SUGGEST_COLUMN_TEXT_1
                , SearchManager.SUGGEST_COLUMN_TEXT_2
                , SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
        });

            matrixCursor.newRow()
                    .add(material.get_id())
                    .add(material.getMaterialNo())
                    .add(material.getDescr())
                    .add(material.getMaterialNo());


        return matrixCursor;
    }

}
