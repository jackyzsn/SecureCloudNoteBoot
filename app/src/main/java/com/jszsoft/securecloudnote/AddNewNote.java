package com.jszsoft.securecloudnote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jszsoft.securecloudnote.common.util.CommonConstants;
import com.jszsoft.securecloudnote.common.util.CommonUtils;
import com.jszsoft.securecloudnote.hclient.dto.msgs.Note;
import com.jszsoft.securecloudnote.hclient.dto.msgs.RequestHeader;
import com.jszsoft.securecloudnote.hclient.dto.msgs.RetrieveNoteResponse;
import com.jszsoft.securecloudnote.hclient.dto.msgs.StoreNoteRequest;
import com.jszsoft.securecloudnote.hclient.dto.msgs.StoreNoteResponse;
import com.jszsoft.securecloudnote.helper.dto.NoteTransport;
import com.jszsoft.securecloudnote.helper.dto.RestParms;
import com.jszsoft.securecloudnote.local.dao.SecureNoteDao;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddNewNote extends AppCompatActivity {

    private SecureNoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void backToMain(View view)
    {
        Intent intent = new Intent(AddNewNote.this, MainScreenActivity.class);
        startActivity(intent);
    }

    public void saveNewNote(View view) {
        SharedPreferences sharedpreferences = getSharedPreferences(CommonConstants.MyPREFERENCES, Context.MODE_PRIVATE);

        String userId = sharedpreferences.getString("uid", "");
        String encryptKey = sharedpreferences.getString("encryptKey", "");
        String connectMode = sharedpreferences.getString("connectMode","");
        boolean isOnline = connectMode.equals("ONLINE") ? true : false;

        EditText editText = findViewById(R.id.new_note_editText);
        String content = editText.getText().toString();

        try {
            // Encrypt text
            String encryptText = CommonUtils.encryptText(encryptKey, content);

            // Call REST service to save the note
            String RESTURL_SAVE = CommonConstants.HAMSTERURL + "/note/add";

            StoreNoteRequest request = new StoreNoteRequest();
            RequestHeader header = CommonUtils.createHeader(userId);

            request.setRequestHeader(header);
            request.setNoteUserId(userId);
            request.setNoteId(CommonConstants.NOTE_ID);
            request.setDeleteKey(CommonUtils.hashText(encryptKey));
            request.setNoteContent(encryptText);
            request.setNoteTimeTag(CommonUtils.formatDate(new Date()));

            // Create request JSON
            String saveRequest = CommonUtils.toJsonString(request);

            RestParms restParm = new RestParms();
            restParm.setRequest(saveRequest);
            restParm.setRestURL(RESTURL_SAVE);
            restParm.setRequestMethod("PUT");

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
                    boolean isSuccess = false;
                    if (result.isEmpty()) {

                    } else {
                        StoreNoteResponse response = CommonUtils.toObject(result, StoreNoteResponse.class);
                        if (response.getResponseHeader().getStatusCode().equals("0")) {
                            isSuccess = true;
                        }
                    }

                    if (isSuccess) {
                        Toast.makeText(getBaseContext(), "Note successfully added...", Toast.LENGTH_LONG).show();
                        returnToMain();
                    } else {
                        Toast.makeText(getBaseContext(), "Something went wrong...", Toast.LENGTH_LONG).show();
                    }
                }
            };

            if (isOnline) {
                task.execute(restParm);
            } else {
                noteDao = new SecureNoteDao(this);
                noteDao.open();
                noteDao.createNote(UUID.randomUUID().toString(), request.getNoteId(), request.getNoteUserId(),
                        request.getNoteTimeTag(), request.getNoteContent(), request.getDeleteKey());
                noteDao.close();
                Toast.makeText(getBaseContext(), "Note successfully added...", Toast.LENGTH_LONG).show();
                returnToMain();
            }


        } catch (Exception ex){
            Toast.makeText(getBaseContext(), "Something went wrong..." + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void returnToMain()
    {
        Intent intent = new Intent(AddNewNote.this, MainScreenActivity.class);
        startActivity(intent);
    }
}
