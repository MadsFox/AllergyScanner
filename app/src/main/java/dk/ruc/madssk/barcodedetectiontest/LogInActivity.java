package dk.ruc.madssk.barcodedetectiontest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {

    private static final String USERID = "dk.ruc.madssk.barcodedetectiontest.USERID";

    AllergyScannerDbHelper mDbHelper = new AllergyScannerDbHelper(this);

    EditText usernameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameField = (EditText) findViewById(R.id.username_field);


        Button logInButton = (Button) findViewById(R.id.log_in_button);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn(usernameField.getText().toString());
            }

        });

        Button newUserButton = (Button) findViewById(R.id.new_user_button);
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                newUser(usernameField.getText().toString());
            }

        });

        usernameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO){
                    logIn(usernameField.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });

    }

    public void logIn(String username){
        try {
            Intent EANSearch = new Intent(this, EANSearchActivity.class);

            if (mDbHelper.getUserByString(username) == null) {

                Toast toast = Toast.makeText(this, "Username dose not exist", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                Log.v("Log In: ", "Username dose not exist");
                return;
            }

            long userId = mDbHelper.getUserByString(username).getId();
            Log.v("Log In: ", username +" logged in");
            EANSearch.putExtra(USERID, "" + userId);
            startActivity(EANSearch);
        }catch(Exception e){
            Toast.makeText(this, "Username missing", Toast.LENGTH_LONG).show();
            Log.w("Log In: ", "Missing username value");
            Log.w("Log In - Exception: ", e);
        }
    }

    public void newUser(String username){
        int userId;
        try {
            Intent EANSearch = new Intent(this, EANSearchActivity.class);

            if(mDbHelper.getUserByString(username) != null){
                Toast toast = Toast.makeText(this, "Username exists", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return;
            }else{
                userId = (int)mDbHelper.addUser(username);
                Toast toast = Toast.makeText(this, "Username added, id: " + userId, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                Log.v("Log In: ", "New user added, id: " + userId);
            }

            EANSearch.putExtra(USERID, "" + userId);
            startActivity(EANSearch);
        }catch(Exception e){
            Toast toast = Toast.makeText(this, "Username missing", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            Log.w("Log In: ", "Missing username");
            Log.w("Log In - Exception: ", e);
        }
    }
}
