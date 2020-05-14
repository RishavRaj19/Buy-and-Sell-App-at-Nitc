package olditemse_marketplace.com;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Objects;

public class SellActivity extends AppCompatActivity {
    private static final String TAG = "SellActivity";
    private ImageView productImageSell;
    private EditText productNameSell;
    private EditText productPriceSell;
    private EditText productContactNoSell;
    private RadioGroup mRadioGroup;
    private EditText productDescSell;
    private Button uploadSell;
    private String productName, productPrice, productDesc, productContactNo;
    private String defContactNumber;

    private ProgressDialog mProgressDialog;
    private int Image_Request_Code = 7;
    private Uri FilePathUri;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        setupVariables();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                defContactNumber = (String)dataSnapshot.child("ContactNo").getValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError);
                Toast.makeText(SellActivity.this, databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        mProgressDialog = new ProgressDialog(this);
        Toolbar toolbar = findViewById(R.id.toolbarSellActivity);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //onBackPressed();
            }
        });

        try {
            productImageSell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: browseButton clicked");
                    //selecting the image from phone
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Image_Request_Code);
                }
            });
            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch(checkedId){
                        case R.id.rbCustomSell:
                            productContactNoSell.setText("");
                            break;
                        case R.id.rbDefaultSell:
                            productContactNoSell.setText(defContactNumber);
                            break;
                    }
                }
            });
            uploadSell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isInternetConn() && isAllDataGiven()) {
                        BackgroundClassUpload backgroundClassUpload = new BackgroundClassUpload();
                        backgroundClassUpload.execute();
                    }else{
                        Toast.makeText(SellActivity.this,"Fill all details please!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }catch(Exception e){
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }

        Log.d(TAG, "onCreate: ended");
    }

    boolean isInternetConn(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null)
            return networkInfo.isConnectedOrConnecting();
        else {
            Toast.makeText(this, "Turn ON the INTERNET and then UPLOAD", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    private class BackgroundClassUpload extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: start");
            mProgressDialog.setMessage("Uploading. Please wait!");
            mProgressDialog.show();
            Log.d(TAG, "onPreExecute: end");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: start");

            try {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference storageReference2 = storageReference.child(productName + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
                storageReference2.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onSuccess(Uri uri) {
                                String urlString = uri.toString();
                                UserProduct userProduct = new UserProduct(productName, productPrice, productContactNo, productDesc, urlString);

                                databaseReference = FirebaseDatabase.getInstance().getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("UserProducts");
                                databaseReference.push().setValue(userProduct);
                                databaseReference = FirebaseDatabase.getInstance().getReference("Products");
                                databaseReference.push().setValue(userProduct);

                                mProgressDialog.dismiss();
                                Toast.makeText(SellActivity.this, "Upload Successful !", Toast.LENGTH_LONG).show();

                                finish();
                            }
                        });
                    }
                });
                storageReference2.putFile(FilePathUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        Toast.makeText(SellActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }catch(Exception e){
                Toast.makeText(SellActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "doInBackground: " + e.getMessage());
            }
            Log.d(TAG, "doInBackground: end");
            return null;
        }
    }

    private boolean isAllDataGiven(){
        Log.d(TAG, "isAllDataGiven: called");
        productName = productNameSell.getText().toString().trim();
        productPrice = productPriceSell.getText().toString().trim();
        productContactNo = productContactNoSell.getText().toString().trim();
        productDesc = productDescSell.getText().toString().trim();

        return !productName.isEmpty() && !productPrice.isEmpty() && !productDesc.isEmpty() && !productContactNo.isEmpty() && FilePathUri != null;
    }

    //verifying and collecting the data returned from the activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.d(TAG, "onActivityResult: " + data);
            FilePathUri = data.getData();
            Log.d(TAG, "onActivityResult: " + FilePathUri);
            try {
                //bitmap is used to get the image from the url
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                Log.d(TAG, "onActivityResult: " + bitmap);
                productImageSell.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupVariables(){
        Log.d(TAG, "setupVariables: started");
        productImageSell = findViewById(R.id.ivProductImageSell);
        productNameSell = findViewById(R.id.etProductNameSell);
        productPriceSell = findViewById(R.id.etProductPriceSell);
        productContactNoSell = findViewById(R.id.etProductContactNoSell);
        mRadioGroup = findViewById(R.id.rgSell);
        productDescSell = findViewById(R.id.etProductDescSell);
        uploadSell = findViewById(R.id.btnUploadSell);
        Log.d(TAG, "setupVariables: ended");
    }
}
