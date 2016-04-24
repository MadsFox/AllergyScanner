package dk.ruc.madssk.barcodedetectiontest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Enumeration;
import java.util.Hashtable;

public class EditProductActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    public static final String BARCODE = "dk.ruc.madssk.barcodedetectiontest.BARCODE";
    public static final String USERID = "dk.ruc.madssk.barcodedetectiontest.USERID";
    public static final String PRODUCTID = "dk.ruc.madssk.barcodedetectiontest.PRODUCTID";

    Intent EANSearchActivity;
    AllergyScannerDbHelper mDbHelper = new AllergyScannerDbHelper(this);
    Button view_product;
    User user;
    Product product;
    String ean;
    long productId;
    boolean newProduct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        view_product = (Button) findViewById(R.id.addProduct);

        getAndUseExtraFromIntent();

        Button change_image = (Button) findViewById(R.id.changeImage);
        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNameProducerToProduct();
                long productId = mDbHelper.updateProduct(product, user);
                Log.d("Product id updated", "" + productId);
                dispatchTakePictureIntent();

            }
        });

        Button add_ingredients = (Button) findViewById(R.id.addIngredients);
        add_ingredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNameProducerToProduct();
                addIngredients();
            }
        });

        view_product.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                viewNewProductValues();
            }
        });
    }

    @Override
    public void onRestart(){
        super.onRestart();
        getAndUseExtraFromIntent();

        Button change_image = (Button) findViewById(R.id.changeImage);
        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNameProducerToProduct();
                mDbHelper.getWritableDatabase();
                long productId = mDbHelper.updateProduct(product, user);
                Log.d("Product id updated", "" + productId);
                dispatchTakePictureIntent();

            }
        });

        Button add_ingredients = (Button) findViewById(R.id.addIngredients);
        add_ingredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredients();
            }
        });

        view_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewNewProductValues();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == RESULT_OK){
                if (data != null){
                    Bundle captureImageExtras = data.getExtras();
                    Bitmap captureImageBitmap = (Bitmap) captureImageExtras.get("data");
                    product.setImage(captureImageBitmap);

                    mDbHelper.updateProduct(product, user);
                    ImageView productImage = (ImageView) findViewById(R.id.product_edit_image);
                    productImage.setImageBitmap(captureImageBitmap);
                }else {Log.d("Camera intent ", "no barcode captured, intent data is null");}
            }else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "No image detected", Toast.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
            Toast.makeText(this, "Image detected failed", Toast.LENGTH_LONG).show();
            return;
        }
        updateViews(product);
    }

    private void getAndUseExtraFromIntent() {
        EANSearchActivity = getIntent();
        mDbHelper.getReadableDatabase();

        TextView EAN = (TextView) findViewById(R.id.product_edit_barcode);

        if(EANSearchActivity.getStringExtra(BARCODE)!=null) {
            ean = EANSearchActivity.getStringExtra(BARCODE);
            EAN.setText(ean);
        }else{Log.d("Edit Product", "no BARCODE received");}

        //Checks if there already is a product in the database
        if (mDbHelper.getProduct(ean)!=null){
            product = mDbHelper.getProduct(ean);
            view_product.setText("Save Changes");
            newProduct = false;
        }else{
            product = new Product(ean);
            mDbHelper.getWritableDatabase();
            productId = mDbHelper.addProduct(product, user);
            product.setEAN(ean);
            view_product.setText("Add Product");
            newProduct = true;
        }

        TextView usernameView = (TextView) findViewById(R.id.product_edit_username);

        if (EANSearchActivity.getStringExtra(USERID)!=null) {
            user = mDbHelper.getUserById(Long.parseLong(EANSearchActivity.getStringExtra(USERID)));
            usernameView.setText(user.getName());
        }else{Log.d("Edit Product", "no USERID received");}

        updateViews(product);

    }

    private void addIngredients() {
        Intent addIngredients = new Intent(this, EditIngredientsActivity.class);
        addIngredients.putExtra(USERID, "" + user.getId());
        addIngredients.putExtra(BARCODE, product.getEAN());
        startActivity(addIngredients);
    }

    private void viewNewProductValues() {
        addNameProducerToProduct();

        long productId = mDbHelper.updateProduct(product, user);
        Log.d("Product id updated", "" + productId);

        Intent productView = new Intent(this, ProductViewActivity.class);
        productView.putExtra(USERID, "" + user.getId());
        Log.d("addProductValues userID", "" + user.getId());
        productView.putExtra(BARCODE, product.getEAN());
        startActivity(productView);
    }

    private void addNameProducerToProduct() {
        EditText productName = (EditText) findViewById(R.id.product_edit_name);
        EditText producer = (EditText) findViewById(R.id.product_edit_producer);
        product.setName(productName.getText().toString());
        product.setProducer(producer.getText().toString());
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void updateViews(Product product){
        if (product.getName()!=null) {
            TextView ProductName = (TextView) findViewById(R.id.product_edit_name);
            ProductName.setText(product.getName());
        }

        if (product.getImage()!=null) {
            ImageView Image = (ImageView) findViewById(R.id.product_edit_image);
            Image.setImageBitmap(product.getImage());
        }

        if (product.getProducer()!=null) {
            TextView Producer = (TextView) findViewById(R.id.product_edit_producer);
            Producer.setText(product.getProducer());
        }

        Hashtable productIngredients =  mDbHelper.getIngredients(product);

        LinearLayout ingredientsView = (LinearLayout) findViewById(R.id.product_edit_ingredients);

        for (Enumeration<Ingredient> prodIng = productIngredients.elements() ; prodIng.hasMoreElements();){
            Ingredient ingredient = prodIng.nextElement();
            TextView ingView = new TextView(getApplicationContext());
            ingView.setText(ingredient.ingredent);
            ingredientsView.addView(ingView);
        }
    }
}
