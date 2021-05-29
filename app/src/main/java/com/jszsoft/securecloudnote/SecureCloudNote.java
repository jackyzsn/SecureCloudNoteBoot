package com.jszsoft.securecloudnote;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.jszsoft.securecloudnote.common.util.CommonConstants;


public class SecureCloudNote extends AppCompatActivity {

    private PopupWindow mPopupWindow;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_cloud_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Context thisContext = this;

        sharedpreferences = getSharedPreferences(CommonConstants.MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.getString("connectMode","").isEmpty()) {
            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString("connectMode", "LOCAL");
            editor.commit();

            RadioButton rd = findViewById(R.id.localMode);
            rd.setChecked(true);
        } else {
            String mode = sharedpreferences.getString("connectMode","");

            if (mode.equals("LOCAL")) {
                RadioButton rd = findViewById(R.id.localMode);
                rd.setChecked(true);
            } else {
                RadioButton rd = findViewById(R.id.onlineMode);
                rd.setChecked(true);
            }
        }

        Button button = findViewById(R.id.nextButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();

                TextInputEditText uidText = findViewById(R.id.uidText);
                String uid = uidText.getText().toString();
                editor.putString("uid", uid);
                editor.commit();

                TextInputEditText encryptKeyText = findViewById(R.id.encryptKeyText);
                String encryptKey = encryptKeyText.getText().toString();
                editor.putString("encryptKey", encryptKey);
                editor.commit();

                Intent myIntent = new Intent(SecureCloudNote.this,
                        MainScreenActivity.class);
                startActivity(myIntent);
            }
        });

        RadioGroup rg = findViewById(R.id.modeRadioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.onlineMode) {

                    if(!isNetworkAvailable(thisContext)) {
                        RadioButton rd = findViewById(R.id.localMode);
                        rd.setChecked(true);
                        Toast.makeText(thisContext,"No Internet connection, switch back to local mode..",Toast.LENGTH_LONG).show();
                    }
                }

                String mode = (checkedId == R.id.onlineMode ? "ONLINE" : "LOCAL");

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("connectMode", mode);
                editor.commit();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_secure_cloud_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_information) {
            informationPopup();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method check mobile is connected to network.
     * @param context
     * @return true if connected otherwise false.
     */
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }

    private void informationPopup(){

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View inforView = inflater.inflate(R.layout.information_popup,null);

        Button closePopupBtn = (Button) inforView.findViewById(R.id.closePopupBtn);

        mPopupWindow = new PopupWindow(
                inforView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );

        closePopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        ConstraintLayout backLayout = findViewById(R.id.init_layout);
        mPopupWindow.showAtLocation(backLayout, Gravity.CENTER, 0, 0);
    }
}
