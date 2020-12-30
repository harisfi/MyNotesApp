package com.hryzx.mynotesapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hryzx.mynotesapp.db.NoteHelper;

import static com.hryzx.mynotesapp.db.DatabaseContract.AUTHORITY;
import static com.hryzx.mynotesapp.db.DatabaseContract.NoteColumns.CONTENT_URI;
import static com.hryzx.mynotesapp.db.DatabaseContract.NoteColumns.TABLE_NAME;

public class NoteProvider extends ContentProvider {
    private static final int NOTE = 1;
    private static final int NOTE_ID = 2;
    private NoteHelper noteHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // content://com.hryzx.mynotesapp/note
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME, NOTE);
        // content://com.hryzx.mynotesapp/note/id
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/#", NOTE_ID);
    }

    @Override
    public boolean onCreate() {
        noteHelper = NoteHelper.getInstance(getContext());
        noteHelper.open();
        return true;
    }
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case NOTE:
                cursor = noteHelper.queryAll();
                break;
            case NOTE_ID:
                cursor = noteHelper.queryById(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long added;
        if (sUriMatcher.match(uri) == NOTE) {
            added = noteHelper.insert(contentValues);
        } else {
            added = 0;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return Uri.parse(CONTENT_URI + "/" + added);
    }
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int updated;
        if (sUriMatcher.match(uri) == NOTE_ID) {
            updated = noteHelper.update(uri.getLastPathSegment(), contentValues);
        } else {
            updated = 0;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return updated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int deleted;
        if (sUriMatcher.match(uri) == NOTE_ID) {
            deleted = noteHelper.deleteById(uri.getLastPathSegment());
        } else {
            deleted = 0;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return deleted;
    }
}