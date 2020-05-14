package olditemse_marketplace.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {
    private static final String TAG = "PasswordResetActivity";
    private Toolbar toolbar;
    private EditText passwordResetEmail;
    private Button btnPasswordReset;
    private TextView goLoginPageLink;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        setUpVariables();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mFirebaseAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);

        btnPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnResetPass clicked");
                String userEmail = passwordResetEmail.getText().toString().trim();
                if(isInternetConn()) {
                    if (userEmail.equals("")) {
                        Toast.makeText(PasswordResetActivity.this, "Enter your registered email.", Toast.LENGTH_SHORT).show();
                    } else {
                        BackgroundClass backgroundClass = new BackgroundClass();
                        backgroundClass.execute(userEmail);
                    }
                }else{
                    Toast.makeText(PasswordResetActivity.this, "Turn ON the INTERNET", Toast.LENGTH_SHORT).show();
                }
            }
        });

        goLoginPageLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: goBackLink clicked");
                finish();
                startActivity(new Intent(PasswordResetActivity.this, MainActivity.class));
            }
        });
        Log.d(TAG, "onCreate: ended");
    }

    private class BackgroundClass extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: called");
            mProgressDialog.setMessage("Sending reset link to ur email.");
            mProgressDialog.show();
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ended");
        }

        @Override
        protected Void doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: start sending resetPass Link");
            try {
                mFirebaseAuth.sendPasswordResetEmail(strings[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(PasswordResetActivity.this, "Password Reset Link sent to your email.", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(PasswordResetActivity.this, MainActivity.class));
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(PasswordResetActivity.this, "Email your entered is not registered with us.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }catch(Exception e){
                Log.e(TAG, "doInBackground: " + e.getMessage());
            }
            Log.d(TAG, "doInBackground: end sending resetPass Link");
            return null;
        }
    }
    boolean isInternetConn(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null)
            return networkInfo.isConnectedOrConnecting();
        else
            return false;
    }
    private void setUpVariables(){
        Log.d(TAG, "setUpVariables: start");
        toolbar = findViewById(R.id.toolbarPasswordResetActivity);
        passwordResetEmail = findViewById(R.id.etPasswordResetEmail);
        btnPasswordReset = findViewById(R.id.btnPasswordReset);
        goLoginPageLink = findViewById(R.id.tvBackToLoginPage);
        Log.d(TAG, "setUpVariables: ended");
    }
}
