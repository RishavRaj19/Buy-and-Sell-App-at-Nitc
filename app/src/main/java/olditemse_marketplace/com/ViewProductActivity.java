package olditemse_marketplace.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class ViewProductActivity extends AppCompatActivity {
    private static final String TAG = "ViewProductActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");

        //for removing the content from top of the screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        PhotoView productImage = findViewById(R.id.ivViewProduct);
        TextView productDesc = findViewById(R.id.tvViewProduct);

        String url = getIntent().getStringExtra("ImageUrl");
        String desc = getIntent().getStringExtra("ImageDesc");

        Picasso.get().load(url).placeholder(R.drawable.loading).into(productImage);
        productDesc.setText(desc);
    }
}
