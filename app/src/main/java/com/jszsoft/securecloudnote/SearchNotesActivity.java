package com.jszsoft.securecloudnote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jszsoft.securecloudnote.common.util.CommonConstants;
import com.jszsoft.securecloudnote.common.util.CommonUtils;
import com.jszsoft.securecloudnote.hclient.dto.msgs.Note;
import com.jszsoft.securecloudnote.hclient.dto.msgs.RequestHeader;
import com.jszsoft.securecloudnote.hclient.dto.msgs.RetrieveNoteRequest;
import com.jszsoft.securecloudnote.hclient.dto.msgs.RetrieveNoteResponse;
import com.jszsoft.securecloudnote.helper.dto.LocalNote;
import com.jszsoft.securecloudnote.helper.dto.NoteTransport;
import com.jszsoft.securecloudnote.helper.dto.RestParms;
import com.jszsoft.securecloudnote.local.dao.SecureNoteDao;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SearchNotesActivity extends AppCompatActivity {

    private SecureNoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void searchText(View view){
        EditText searchText = findViewById(R.id.searchTextEdit);
        final String searchWords = searchText.getText().toString();

        SharedPreferences sharedpreferences = getSharedPreferences(CommonConstants.MyPREFERENCES, Context.MODE_PRIVATE);

        String userId = sharedpreferences.getString("uid", "");
        final String encryptKey = sharedpreferences.getString("encryptKey", "");

        String connectMode = sharedpreferences.getString("connectMode","");
        boolean isOnline = connectMode.equals("ONLINE") ? true : false;

        String RESTURL_BROWSE = CommonConstants.HAMSTERURL + "/note/browse";


        // Retrieve data from cloud thru RESTful service call
        RetrieveNoteRequest request = new RetrieveNoteRequest();
        RequestHeader header = CommonUtils.createHeader(userId);

        request.setRequestHeader(header);
        request.setNoteUserId(userId);
        request.setNoteId(CommonConstants.NOTE_ID);

        // Create request JSON
        String browseRequest = CommonUtils.toJsonString(request);

        RestParms restParm = new RestParms();
        restParm.setRequest(browseRequest);
        restParm.setRestURL(RESTURL_BROWSE);
        restParm.setRequestMethod("POST");

        AsyncTask<RestParms, Void, String> task = new AsyncTask<RestParms, Void, String>() {

            @Override
            protected String doInBackground(RestParms... params) {
                try {
                    URL url = new URL(params[0].getRestURL());
                    String requestMethod = params[0].getRequestMethod();
                    String request = params[0].getRequest();

                    // create HttpURLConnection
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    if (requestMethod.equalsIgnoreCase("POST")) {
                        conn.setRequestMethod("POST");
                    } else if (requestMethod.equalsIgnoreCase("PUT")) {
                        conn.setRequestMethod("PUT");
                    } else if (requestMethod.equalsIgnoreCase("DELETE")) {
                        conn.setRequestMethod("DELETE");
                    } else if (requestMethod.equalsIgnoreCase("GET")) {
                        conn.setRequestMethod("GET");
                    }

                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    // make POST request to the given URL
                    conn.connect();

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(request);

                    os.flush();
                    os.close();

                    int status = conn.getResponseCode();

                    String returnMessage = "";
                    if (status == 200) {
                        returnMessage = CommonUtils.convertInputStreamToString(conn.getInputStream());
                    }

                    conn.disconnect();

                    // return response message
                    return returnMessage;
                } catch (Exception ex) {
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String result)
            {
                List<Note> noteList = new ArrayList<>();
                if (result.isEmpty()) {

                } else {
                    RetrieveNoteResponse response = CommonUtils.toObject(result, RetrieveNoteResponse.class);
                    noteList = response.getNoteList();
                }

                ArrayList<NoteTransport> notes = new ArrayList<>();

                StringTokenizer tokens = new StringTokenizer(searchWords);
                List<String> keywordList = new ArrayList<>();

                while(tokens.hasMoreTokens()){
                    keywordList.add(tokens.nextToken().toLowerCase());
                }

                for (Note note : noteList) {
                    try {
                        boolean foundText = false;
                        String decryptText = CommonUtils.decryptText(encryptKey, note.getNoteContent());

                        for (String token : keywordList) {
                            if (decryptText.toLowerCase().contains(token)) {
                                foundText = true;
                                break;
                            }
                        }
                        if (foundText){
                            NoteTransport noteTrans = new NoteTransport(note.getId(), note.getNoteId(), note.getNoteUserId(),
                                    note.getNoteTimeTag(), note.getNoteContent());
                            notes.add(noteTrans);
                        }

                    } catch(Exception e) {
                        // not able to decrypt, do nothing..
                    }
                }

                createBrowseActivity(notes);
            }
        };

        if (isOnline) {
            task.execute(restParm);
        } else {
            // Get all local notes by userId
            List<LocalNote> localNotes = new ArrayList<>();
            try {
                noteDao = new SecureNoteDao(this);
                noteDao.open();
                localNotes = noteDao.getNotesByNotUserId(userId);
                noteDao.close();
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Local database not yet created, add a new note first...", Toast.LENGTH_LONG).show();
            }

            ArrayList<NoteTransport> notes = new ArrayList<>();

            StringTokenizer tokens = new StringTokenizer(searchWords);
            List<String> keywordList = new ArrayList<>();

            while(tokens.hasMoreTokens()){
                keywordList.add(tokens.nextToken().toLowerCase());
            }

            for (LocalNote note : localNotes) {
                try {
                    // Try decrypt and see if notes contains keyword
                    boolean foundText = false;
                    String decryptText = CommonUtils.decryptText(encryptKey, note.getNoteContent());

                    for (String token : keywordList) {
                        if (decryptText.toLowerCase().contains(token)) {
                            foundText = true;
                            break;
                        }
                    }
                    if (foundText){
                        NoteTransport noteTrans = new NoteTransport(note.getId(), note.getNoteId(), note.getNoteUserId(),
                                note.getNoteTimeTag(), note.getNoteContent());
                        notes.add(noteTrans);
                    }

                } catch(Exception e) {
                    // not able to decrypt, do nothing..
                }
            }

            createBrowseActivity(notes);
        }
    }

    private void createBrowseActivity(ArrayList<NoteTransport> noteList) {
        // Display note list..
        Intent myIntent = new Intent(SearchNotesActivity.this,
                BrowseExistingNotes.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("noteList", noteList);
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void backToMain(View view)
    {
        Intent intent = new Intent(SearchNotesActivity.this, MainScreenActivity.class);
        startActivity(intent);
    }

    public void clearSearchText(View view){
        EditText searchText = findViewById(R.id.searchTextEdit);
        searchText.setText("", TextView.BufferType.NORMAL);
    }
}
