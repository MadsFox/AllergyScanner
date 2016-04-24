package dk.ruc.madssk.barcodedetectiontest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Enumeration;
import java.util.Hashtable;

public class ProductViewActivity extends AppCompatActivity {

    public static final String BARCODE = "dk.ruc.madssk.barcodedetectiontest.BARCODE";
    public static final String USERID = "dk.ruc.madssk.barcodedetectiontest.USERID";

    Intent editProductActivity;

    Product product;
    User user;

    AllergyScannerDbHelper mDbHelper = new AllergyScannerDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editProductActivity = new Intent(this, EditProductActivity.class);

        Intent EANSearchActivity = getIntent();
        String barcode = EANSearchActivity.getStringExtra(BARCODE);
        String userId = EANSearchActivity.getStringExtra(USERID);
        Log.d("Product view userID", "" + userId);

        product = mDbHelper.getProduct(barcode);
        user = mDbHelper.getUserById(Long.parseLong(userId));

        updateViews(product, user);

        Button editProduct = (Button) findViewById(R.id.edit_product);
        editProduct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editProductActivity.putExtra(BARCODE, product.getEAN());
                editProductActivity.putExtra(USERID, "" + user.getId());
                startActivity(editProductActivity);
            }
        });
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Intent EANSearchActivity = getIntent();
        String barcode = EANSearchActivity.getStringExtra(BARCODE);
        String userId = EANSearchActivity.getStringExtra(USERID);
        Log.d("Product view userID", "" + userId);

        product = mDbHelper.getProduct(barcode);
        user = mDbHelper.getUserById(Long.parseLong(userId));

        updateViews(product, user);
    }

    public void updateViews(Product product, User user){
        TextView Username = (TextView) findViewById(R.id.product_view_username);
        Username.setText(user.getName());

        TextView ProductName = (TextView) findViewById(R.id.product_view_name);
        ProductName.setText(product.getName());

        ImageView Image = (ImageView) findViewById(R.id.product_view_image);
        Image.setImageBitmap(product.getImage());

        TextView Barcode = (TextView) findViewById(R.id.product_view_barcode);
        Barcode.setText(product.getEAN());

        TextView Producer = (TextView) findViewById(R.id.product_view_producer);
        Producer.setText(product.getProducer());

        Hashtable productIngredients =  mDbHelper.getIngredients(product);

        LinearLayout ingredientsView = (LinearLayout) findViewById(R.id.product_view_ingredients);

        for (Enumeration<Ingredient> prodIng = productIngredients.elements() ; prodIng.hasMoreElements();){
            Ingredient ingredient = prodIng.nextElement();
            TextView ingView = new TextView(getApplicationContext());
            ingView.setText(ingredient.ingredent);
            ingredientsView.addView(ingView);
        }
    }
}
