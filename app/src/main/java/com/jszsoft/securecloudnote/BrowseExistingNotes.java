package com.jszsoft.securecloudnote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.jszsoft.securecloudnote.common.util.CommonConstants;
import com.jszsoft.securecloudnote.common.util.CommonUtils;
import com.jszsoft.securecloudnote.hclient.dto.msgs.DeleteNoteRequest;
import com.jszsoft.securecloudnote.hclient.dto.msgs.DeleteNoteResponse;
import com.jszsoft.securecloudnote.hclient.dto.msgs.StoreNoteResponse;
import com.jszsoft.securecloudnote.helper.dto.NoteTransport;
import com.jszsoft.securecloudnote.helper.dto.RestParms;
import com.jszsoft.securecloudnote.local.dao.SecureNoteDao;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

public class BrowseExistingNotes extends AppCompatActivity {

    private SecureNoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_existing_notes);

        Bundle b = this.getIntent().getExtras();
        ArrayList<NoteTransport> notes = b.getParcelableArrayList("noteList");

        TableLayout tbl = findViewById(R.id.browseTbl);

        tbl.removeAllViews();

        TableRow headerRow = new TableRow(this);
        TextView headerTv = new TextView(this);

        headerTv.setText("Sel");
        //headerTv.setGravity(Gravity.CENTER);
        headerTv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        headerTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        headerTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        headerRow.addView(headerTv);

        headerTv = new TextView(this);
        headerTv.setText("Created Time");
        headerTv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 5f));
        headerTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        headerTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        headerRow.addView(headerTv);

        headerTv = new TextView(this);
        headerTv.setText("Action");
        headerTv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        headerTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        headerTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        headerRow.addView(headerTv);

        tbl.addView(headerRow);

        if (notes.isEmpty()) {
            TableRow row = new TableRow(this);
            TextView tv = new TextView(this);

            tv.setText("No records found...");
            tv.setLayoutParams(new TableRow.LayoutParams(1));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);

            row.addView(tv);
            tbl.addView(row);
        } else {
            for (NoteTransport note : notes) {
                TableRow row = new TableRow(this);

                final CheckBox ch = new CheckBox(this);
                final String id = note.getId();
                ch.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2f));
                ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SharedPreferences sharedpreferences = getSharedPreferences(CommonConstants.MyPREFERENCES, Context.MODE_PRIVATE);
                            Set selectedIds = sharedpreferences.getStringSet("toBeDeleteIds",null);
                            if (ch.isChecked()) {
                                if (!selectedIds.contains(id)) {
                                    selectedIds.add(id);
                                }
                            } else {
                                if (selectedIds.contains(id)) {
                                    selectedIds.remove(id);
                                }
                        }
                    }
                });
                row.addView(ch);

                TextView tv = new TextView(this);
                tv.setText(note.getNoteTimeTag());
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 5f));
                row.addView(tv);

                final TextView tv1 = new TextView(this);
                final String clickId = note.getId();
                final String noteContent = note.getNoteContent();
                tv1.setText("Show Content");
                tv1.setTextColor(Color.BLUE);
                tv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2f));
                tv1.setClickable(true);
                tv1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        showDetail(clickId, noteContent);
                    }
                });
                row.addView(tv1);

                tbl.addView(row);
            }
        }
    }

    private void showDetail(String id, String content) {

        Intent intent = new Intent(BrowseExistingNotes.this, NoteDetailActivity.class);
        intent.putExtra("detailId", id);
        intent.putExtra("content", content);
        startActivity(intent);
    }

    public void backToMain(View view)
    {
        Intent intent = new Intent(BrowseExistingNotes.this, MainScreenActivity.class);
        startActivity(intent);
    }

    public void deleteNotes(View view) {
        SharedPreferences sharedpreferences = getSharedPreferences(CommonConstants.MyPREFERENCES, Context.MODE_PRIVATE);
        Set selectedIds = sharedpreferences.getStringSet("toBeDeleteIds",null);
        String connectMode = sharedpreferences.getString("connectMode","");
        boolean isOnline = connectMode.equals("ONLINE") ? true : false;

        if (selectedIds.isEmpty()) {
            Toast.makeText(getBaseContext(), "Nothing to be selected...", Toast.LENGTH_LONG).show();
        } else {
            try {
                // Call REST service to save the note
                String RESTURL_DELETE = CommonConstants.HAMSTERURL + "/note/delete";
                String userId = sharedpreferences.getString("uid", "");
                String encryptKey = sharedpreferences.getString("encryptKey", "");

                DeleteNoteRequest request = new DeleteNoteRequest();
                request.setRequestHeader(CommonUtils.createHeader(userId));
                request.setDeleteKey(CommonUtils.hashText(encryptKey));
                request.setIds(new ArrayList(selectedIds));

                // Create request JSON
                String deleteRequest = CommonUtils.toJsonString(request);

                RestParms restParm = new RestParms();
                restParm.setRequest(deleteRequest);
                restParm.setRestURL(RESTURL_DELETE);
                restParm.setRequestMethod("DELETE");

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
                            DeleteNoteResponse response = CommonUtils.toObject(result, DeleteNoteResponse.class);
                            if (response.getResponseHeader().getStatusCode().equals("0")) {
                                isSuccess = true;
                            }
                        }

                        if (isSuccess) {
                            Toast.makeText(getBaseContext(), "Note successfully deleted...", Toast.LENGTH_LONG).show();
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
                    noteDao.deleteNotes(request.getDeleteKey(),request.getIds());
                    noteDao.close();
                    Toast.makeText(getBaseContext(), "Note successfully deleted...", Toast.LENGTH_LONG).show();
                    returnToMain();
                }
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Something went wrong..." + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    private void returnToMain()
    {
        Intent intent = new Intent(BrowseExistingNotes.this, MainScreenActivity.class);
        startActivity(intent);
    }
}
