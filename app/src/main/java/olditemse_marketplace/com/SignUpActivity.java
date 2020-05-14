package olditemse_marketplace.com;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity" ;

    private Toolbar toolbar;
    private EditText userName ;
    private EditText contactNo ;
    private EditText userEmail ;
    private EditText hostelName ;
    private EditText roomNo ;
    private EditText userPassword ;
    private ImageView userImage;
    String name, contact, email, hostel, room;

    private Button regBtn ;
    private TextView alreadyHaveAnAcc ;
    private FirebaseAuth firebaseAuth ;
    private FirebaseUser firebaseUser;
    private ProgressDialog mProgressDialog ;

    private int Image_Request_Code = 7;
    private Uri FilePathUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setUpVariables() ;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance() ;
        mProgressDialog = new ProgressDialog(this) ;

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Image_Request_Code);
            }
        });

        try {
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: regBtn clicked");
                if(isInternetConn()) {
                    if (validateIfEmpty()) {

                        String email = userEmail.getText().toString().trim();
                        String password = userPassword.getText().toString().trim();

                        BackgroundClass backgroundClass = new BackgroundClass();
                        backgroundClass.execute(email, password);
                    }
                }else{
                    Toast.makeText(SignUpActivity.this, "Turn ON the INTERNET", Toast.LENGTH_SHORT).show();
                }
            }
        }) ; } catch(Exception e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }

        alreadyHaveAnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: already have an acc btn clicked");
                finish();
            }
        });
        Log.d(TAG, "onCreate: ended");
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
    //verifying and collecting the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: called");
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == Image_Request_Code) && (resultCode == RESULT_OK) && (data != null) && (data.getData() != null)){
            FilePathUri = data.getData();
            Picasso.get().load(FilePathUri).placeholder(R.drawable.profile_picture).error(R.drawable.error).into(userImage);
        }
    }

    private class BackgroundClass extends AsyncTask<String, Void, Void>{
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: called");
            mProgressDialog.setMessage("Registering! Please Wait!");
            mProgressDialog.show();
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ended");
        }

        @Override
        protected Void doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: start sign up");
            firebaseAuth.createUserWithEmailAndPassword(strings[0], strings[1]).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mProgressDialog.dismiss();
                        sendEmailVerification();
                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, task.getException().toString().split(":")[1], Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onComplete: " + task.getException() );
                    }
                }
            });

            Log.d(TAG, "doInBackground: end sign up");
            return null;
        }

        private void sendEmailVerification() {
            Log.d(TAG, "sendEmailVerification: start");
            firebaseUser = firebaseAuth.getCurrentUser();

            assert firebaseUser != null;
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendDataToDatabase();
                    } else {
                        Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onComplete: " + task.getException());
                    }
                }
            });
            Log.d(TAG, "sendEmailVerification: end");
        }

        private void sendDataToDatabase(){
            Log.d(TAG, "sendDataToDatabase: start");
            try{
                if(FilePathUri == null){
                    UserProfile userProfile = new UserProfile(contact, email, hostel, name, room, null);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());
                    databaseReference.setValue(userProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: uploaded to database");
                            Toast.makeText(SignUpActivity.this, "Registration Successful.Verify pls.", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(name + System.currentTimeMillis() + ".jpg");
                    storageReference.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onSuccess: Profile pic uploaded to storage");

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "onSuccess: DownloadUrl");
                                    UserProfile userProfile = new UserProfile(contact, email, hostel, name, room, uri.toString());

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());
                                    databaseReference.setValue(userProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: uploaded to database");
                                            Toast.makeText(SignUpActivity.this, "Registration Successful.Verify pls.", Toast.LENGTH_SHORT).show();
                                            firebaseAuth.signOut();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.getMessage());
                                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.getMessage());
                                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }catch (Exception e){
                Log.e(TAG, "sendDataToDatabase: " + e.getMessage());
            }
            Log.d(TAG, "sendDataToDatabase: end");
        }
    }

    private Boolean validateIfEmpty() {
        Log.d(TAG, "validateIfEmpty: start");
        name = userName.getText().toString().trim();
        contact = contactNo.getText().toString().trim();
        email = userEmail.getText().toString().trim() ;
        hostel = hostelName.getText().toString().trim();
        room = roomNo.getText().toString().trim();
        String password = userPassword.getText().toString().trim() ;

        if(name.isEmpty() || contact.isEmpty() || email.isEmpty() || hostel.isEmpty() || room.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Fill all the details", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "validateIfEmpty: end");
            return false ;
        }else {
            Log.d(TAG, "validateIfEmpty: end");
            return true ;
        }
    }

    private void setUpVariables() {
        Log.d(TAG, "setUpVariables: start");

        toolbar = findViewById(R.id.toolbarSignUpActivity);

        userName = findViewById(R.id.etRegName);
        contactNo = findViewById(R.id.etRegContactNo);
        userEmail = findViewById(R.id.etRegEmail) ;
        hostelName = findViewById(R.id.etRegHostelName);
        roomNo = findViewById(R.id.etRegRoomNo);
        userPassword = findViewById(R.id.etRegPass);
        userImage = findViewById(R.id.ivRegProfilePic);

        regBtn = findViewById(R.id.btnRegister);
        alreadyHaveAnAcc = findViewById(R.id.tvAlreadyHaveAnAccount);
        Log.d(TAG, "setUpVariables: end");
    }
}
