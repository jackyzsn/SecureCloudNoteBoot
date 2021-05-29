package com.jszsoft.securecloudnote.local.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jszsoft.securecloudnote.helper.dto.LocalNote;

import java.util.ArrayList;
import java.util.List;

public class SecureNoteDao {

    private static final String DATABASE_TABLE = "notes";
    private static final String KEY_ID = "id";
    private static final String KEY_NOTEID = "noteId";
    private static final String KEY_NOTEUSERID = "noteUserId";
    private static final String KEY_NOTETIMETAG = "noteTimeTag";
    private static final String KEY_NOTECONTENT = "noteContent";
    private static final String KEY_DELETEKEY = "deleteKey";
    private LocalSQLLiteDBHelper dbHelper;
    private final Context context;
    private SQLiteDatabase db;

    public SecureNoteDao(Context context) {
        this.context = context;
    }

    public SecureNoteDao open() {
        dbHelper = new LocalSQLLiteDBHelper(this.context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long createNote(String id, String noteId, String noteUserId, String noteTimeTag, String noteContent, String deleteKey) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, id);
        cv.put(KEY_NOTEID, noteId);
        cv.put(KEY_NOTEUSERID, noteUserId);
        cv.put(KEY_NOTETIMETAG, noteTimeTag);
        cv.put(KEY_NOTECONTENT, noteContent);
        cv.put(KEY_DELETEKEY, deleteKey);
        return db.insert(DATABASE_TABLE, null, cv);
    }

    public boolean deleteNote(String id) {
        return db.delete(DATABASE_TABLE, KEY_ID + "='" + id + "'", null) > 0;
    }

    public void deleteNotes(String deleteKey, List<String> ids) {
        for (String id : ids) {
            LocalNote note = getNoteById(id);
            if (note.getDeleteKey().equals(deleteKey)) {
                deleteNote(id);
            }
        }
    }

    public LocalNote getNoteById(String id)
    {
        String[] columns = { KEY_ID, KEY_NOTEID, KEY_NOTEUSERID, KEY_NOTETIMETAG, KEY_NOTECONTENT, KEY_DELETEKEY};
        Cursor cursor = db.query(DATABASE_TABLE, columns, KEY_ID + "='" + id + "'", null, null, null, null);

        int idInx = cursor.getColumnIndex(KEY_ID);
        int noteIdInx = cursor.getColumnIndex(KEY_NOTEID);
        int noteUserIdInx = cursor.getColumnIndex(KEY_NOTEUSERID);
        int noteTimeTagInx = cursor.getColumnIndex(KEY_NOTETIMETAG);
        int noteContentInx = cursor.getColumnIndex(KEY_NOTECONTENT);
        int deleteKeyInx = cursor.getColumnIndex(KEY_DELETEKEY);

        LocalNote localNote = null;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            localNote = new LocalNote();
            localNote.setId(cursor.getString(idInx));
            localNote.setNoteId(cursor.getString(noteIdInx));
            localNote.setNoteUserId(cursor.getString(noteUserIdInx));
            localNote.setNoteTimeTag(cursor.getString(noteTimeTagInx));
            localNote.setNoteContent(cursor.getString(noteContentInx));
            localNote.setDeleteKey(cursor.getString(deleteKeyInx));

            break;
        }
        return localNote;
    }

    public List<LocalNote> getNotesByNotUserId(String noteUserid)
    {
        String[] columns = { KEY_ID, KEY_NOTEID, KEY_NOTEUSERID, KEY_NOTETIMETAG, KEY_NOTECONTENT, KEY_DELETEKEY};
        Cursor cursor = db.query(DATABASE_TABLE, columns, KEY_NOTEUSERID + "= '" + noteUserid + "'", null, null, null, null);

        int idInx = cursor.getColumnIndex(KEY_ID);
        int noteIdInx = cursor.getColumnIndex(KEY_NOTEID);
        int noteUserIdInx = cursor.getColumnIndex(KEY_NOTEUSERID);
        int noteTimeTagInx = cursor.getColumnIndex(KEY_NOTETIMETAG);
        int noteContentInx = cursor.getColumnIndex(KEY_NOTECONTENT);
        int deleteKeyInx = cursor.getColumnIndex(KEY_DELETEKEY);

        List<LocalNote> notes = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            LocalNote localNote = new LocalNote();
            localNote.setId(cursor.getString(idInx));
            localNote.setNoteId(cursor.getString(noteIdInx));
            localNote.setNoteUserId(cursor.getString(noteUserIdInx));
            localNote.setNoteTimeTag(cursor.getString(noteTimeTagInx));
            localNote.setNoteContent(cursor.getString(noteContentInx));
            localNote.setDeleteKey(cursor.getString(deleteKeyInx));

            notes.add(localNote);
        }
        return notes;
    }
}
