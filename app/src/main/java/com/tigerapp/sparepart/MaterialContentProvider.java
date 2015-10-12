package com.tigerapp.sparepart;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Little on 7/7/2015.
 */
public class MaterialContentProvider extends ContentProvider {
    public static final String tag = "MaterialContentProvider";
    public static final String AUTHORITY = "com.tigerapp.sparepart.MaterialContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/materials");

    // DataAccess Layer
    MaterialDB materialDB = null;

    // List of contentPrivider mode
    public static final int SUGGESTIONS_MATERIAL = 1;
    public static final int SEARCH_MATERIAL = 2;
    public static final int GET_MATERIAL = 3;

    UriMatcher uriMatcher = buildUriMatcher();

    private UriMatcher buildUriMatcher() {
        UriMatcher uriMatcherA = new UriMatcher(UriMatcher.NO_MATCH);

        // Suggestion items of SearchDialog is privided by this uri
        uriMatcherA.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SUGGESTIONS_MATERIAL);

        // When press "go" on keyboard of SearchDialog, ListView item of SearchableActivity is privided by this uri
        uriMatcherA.addURI(AUTHORITY, "materials", SEARCH_MATERIAL);

        // this uri invoked when user select a suggestion from search dialog or an item from the SearchableActivity
        // result to open MaterialActivity to show detail
        uriMatcherA.addURI(AUTHORITY, "material/#", GET_MATERIAL);

        return uriMatcherA;
    }


    @Override
    public boolean onCreate() {
        materialDB = new MaterialDB(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(tag, "query() called. selectionArgs=" + selectionArgs+", uri= "+uri.getLastPathSegment());

        Cursor c = null;
        switch (uriMatcher.match(uri)) {

            case SUGGESTIONS_MATERIAL:
                Log.d(tag, "SUGGESTIONS_MATERIAL case");
                c = materialDB.findAllByIdLikeOrDescrLike(selectionArgs);
                break;
            case SEARCH_MATERIAL:
                Log.d(tag, "SEARCH_MATERIAL case");
                c = materialDB.findAllByIdLikeOrDescrLike(selectionArgs);
                break;
            case GET_MATERIAL:
                Log.d(tag, "GET_MATERIAL case");
                String id = uri.getLastPathSegment();
                c = materialDB.getMaterial(id);
        }
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
