package olditemse_marketplace.com;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity  {
    private static final String TAG = "HomeActivity";
    private TextView buy, sell;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

        }catch (Exception e){
            Log.e(TAG, "onCreate: " + e.getMessage());
            finish();
        }

        setUpVariables();
        Toolbar toolbar = findViewById(R.id.toolbarHomeActivity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.userProfileMenu: {
                        startActivity(new Intent(HomeActivity.this, UserProfileActivity.class));
                        break ;
                    }case R.id.removeProductsMenu: {
                        startActivity(new Intent(HomeActivity.this, RemoveProductsActivity.class));
                        break ;
                    } case R.id.feedbackMenu: {
                        startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));
                        break;
                    }case R.id.logoutMenu : {
                        Logout() ;
                        break;
                    }
                }
                return false;
            }
        });
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: buy clicked");
                startActivity(new Intent(HomeActivity.this, BuyActivity.class));
            }
        });
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: sell clicked");
                startActivity(new Intent(HomeActivity.this, SellActivity.class));
            }
        });
        Log.d(TAG, "onCreate: ended");
    }

    private void setUpVariables(){
        Log.d(TAG, "setUpVariables: start");
        buy = findViewById(R.id.tvBuy);
        sell = findViewById(R.id.tvSell);
        Log.d(TAG, "setUpVariables: end");
    }

    private void Logout() {
        Log.d(TAG, "Logout: called");
        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //instance of current user
            firebaseAuth.signOut();

            finish();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }catch(Exception e){
            Log.e(TAG, "Logout: " + e.getMessage() );
        }
        Log.d(TAG, "Logout: ended");
    }
}
