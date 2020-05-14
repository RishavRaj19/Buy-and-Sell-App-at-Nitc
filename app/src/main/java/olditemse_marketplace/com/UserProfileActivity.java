package olditemse_marketplace.com;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "UserProfileActivity";
    private  TextView name, contact, email, hostel, room;
    private  ImageView profilePic;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.toolbarUserProfile);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setUpVariables();

        mProgressDialog.setMessage("Loading");
        mProgressDialog.show();

        if(!isConnected()){
            Log.d(TAG, "onCreate: Check internet connection");
            Toast.makeText(this, "Turn on the INTERNET",Toast.LENGTH_LONG).show();
            mProgressDialog.dismiss();
        }
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Log.d(TAG, "onCreate: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: inside");
                name.setText((String)dataSnapshot.child("Name").getValue());
                contact.setText((String)dataSnapshot.child("ContactNo").getValue());
                email.setText((String)dataSnapshot.child("Email").getValue());
                hostel.setText((String)dataSnapshot.child("HostelName").getValue());
                room.setText((String)dataSnapshot.child("RoomNo").getValue());

                String stringImage = (String) dataSnapshot.child("ProfileImage").getValue();
                if(stringImage == null){
                    profilePic.setImageResource(R.drawable.profile_picture);
                }else
                    Picasso.get().load(stringImage).placeholder(R.drawable.profile_picture).error(R.drawable.error).into(profilePic);

                mProgressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError);
                Toast.makeText(UserProfileActivity.this, databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void setUpVariables(){
        Log.d(TAG, "setUpVariables: called");
        name = findViewById(R.id.tvUserProfileNameValue);
        contact = findViewById(R.id.tvUserProfileContactValue);
        email = findViewById(R.id.tvUserProfileEmailValue);
        hostel = findViewById(R.id.tvUserProfileHostelValue);
        room = findViewById(R.id.tvUserProfileRoomValue);
        profilePic = findViewById(R.id.ivUserProfilePicture);

        mProgressDialog = new ProgressDialog(this);
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
