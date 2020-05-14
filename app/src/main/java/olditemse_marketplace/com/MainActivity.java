package olditemse_marketplace.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText userEmail ;
    private EditText userPassword ;
    private Button loginBtn ;
    private TextView signUpLink ;
    private FirebaseAuth mFirebaseAuth ;
    private ProgressDialog mProgressDialog ;
    private TextView forgotPasswordLink ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            setUpVariables();
            mFirebaseAuth = FirebaseAuth.getInstance();
            mProgressDialog = new ProgressDialog(MainActivity.this);

            FirebaseUser user = mFirebaseAuth.getCurrentUser();
            if (user != null) {
                finish();
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }
        }catch(Exception e){
            Log.e(TAG, "onCreate: settingVariables" + e.getMessage());
        }

        //user is null right now
        try {
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isInternetConn()) {
                        if (validateIfEmpty()) {
                            Log.d(TAG, "onClick: loginBtn: Login Button Clicked");
                            String email = userEmail.getText().toString().trim();
                            String password = userPassword.getText().toString().trim();

                            BackgroundClass backgroundClass = new BackgroundClass();
                            backgroundClass.execute(email, password);
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Turn ON the INTERNET", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch(Exception e){
            Log.e(TAG, "onCreate: loginBtn: " + e.getMessage());
        }

        try {
            forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: forgotPassword: clicked");
                    startActivity(new Intent(MainActivity.this, PasswordResetActivity.class));
                }
            });
        }catch(Exception e){
            Log.e(TAG, "onCreate: Forgot password: " + e.getMessage());
        }

        try {
            signUpLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: signUp button clicked");
                    startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                }
            });
        }catch (Exception e){
            Log.e(TAG, "onCreate: signUp: " + e.getMessage());
        }

        Log.d(TAG, "onCreate: End");
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
    private class BackgroundClass extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            try {
                Log.d(TAG, "onPreExecute: start");
                mProgressDialog.setMessage("Validating User. Kindly Wait!");
                mProgressDialog.show();

                super.onPreExecute();
                Log.d(TAG, "onPreExecute: End");
            }catch(Exception e){
                Log.e(TAG, "onPreExecute: " + e.getMessage() );
            }
        }
        @Override
        protected Void doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: start");
            try {
                //user will no more be null after this statement
                mFirebaseAuth.signInWithEmailAndPassword(strings[0], strings[1]).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: user exists");
                            mProgressDialog.dismiss();
                            checkIfEmailVerified();
                        } else {
                            mProgressDialog.dismiss();
                            Log.d(TAG, "onComplete: user doesn't exist");
                            Toast.makeText(MainActivity.this, "Invalid email and/or password." + "\n" + "Or Check Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }catch(Exception e){
                Log.e(TAG, "doInBackground: " + e.getMessage());
            }
            Log.d(TAG, "doInBackground: end");
            return null;
        }

        private void checkIfEmailVerified() {
            Log.d(TAG, "checkIfEmailVerified: start");
            try {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user.isEmailVerified()) {
                    Log.d(TAG, "checkIfEmailVerified: email is verified");
                    finish();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                } else {
                    Log.d(TAG, "checkIfEmailVerified: not verified");
                    Toast.makeText(MainActivity.this, "Verify ur email id.", Toast.LENGTH_LONG).show();
                    mFirebaseAuth.signOut();
                }
            } catch (Exception e) {
                Log.e(TAG, "checkIfEmailVerified: " + e.getMessage() );
            }
            Log.d(TAG, "checkIfEmailVerified: end");
        }
    }

    private Boolean validateIfEmpty() {
        Log.d(TAG, "validateIfEmpty: start");
        try {
            String email = userEmail.getText().toString();
            String password = userPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Fill all the login details", Toast.LENGTH_SHORT).show();
                return false;
            } else
                return true;
        }catch(Exception e){
            Log.e(TAG, "validateIfEmpty: " + e.getMessage() );
        }
        Log.d(TAG, "validateIfEmpty: end");
        return false;
    }

    private void setUpVariables() {
        Log.d(TAG, "setUpVariables: start");
        try {
            Log.d(TAG, "setUpVariables");
            userEmail = findViewById(R.id.etUserEmail);
            userPassword = findViewById(R.id.etUserPassword);
            loginBtn = findViewById(R.id.btnLogin);
            signUpLink = findViewById(R.id.tvSignUpLink);
            forgotPasswordLink = findViewById(R.id.tvForgotPassword);
        }catch(Exception e){
            Log.e(TAG, "setUpVariables: " + e.getMessage());
        }
        Log.d(TAG, "setUpVariables: end");
    }

}
