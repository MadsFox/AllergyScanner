package dk.ruc.madssk.barcodedetectiontest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

public class EditIngredientsActivity extends AppCompatActivity {

    public static final String BARCODE = "dk.ruc.madssk.barcodedetectiontest.BARCODE";
    public static final String USERID = "dk.ruc.madssk.barcodedetectiontest.USERID";
    Intent EditProductActivity;
    LinearLayout ingredientLayout;
    Hashtable productIngredients;
    ArrayList<Ingredient> ingredients;
    AllergyScannerDbHelper mDbHelper = new AllergyScannerDbHelper(this);
    User user;
    Product product;
    TextView usernameView;
    TextView productView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ingredients);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getAndUseExtraFromIntent();

        final EditText editSearch = (EditText) findViewById(R.id.addIngredient);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if(str.length() > 0 && str.contains(" "))
                {
                    Toast toast = Toast.makeText(EditIngredientsActivity.this, "No space allowed", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0,0);
                    toast.show();
                    str.replace(" ", "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0 && editSearch.getText() != null) {
                    searchIngredients(ingredients, productIngredients, editSearch.getText());
                }else{updateIngredientsView(ingredients, productIngredients);}
            }
        });

        Button addNew = (Button) findViewById(R.id.add_new_button);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient(product);
                updateProductIngredients(product);
                updateIngredientsView(ingredients, productIngredients);
                editSearch.clearComposingText();
            }
        });

        final Button addToProduct = (Button) findViewById(R.id.add_to_product_button);
        addToProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredientsToProduct(ingredients, productIngredients, product);
                goBackToEditProduct();
            }
        });
    }

    @Override
    public void onRestart(){
        super.onRestart();
        getAndUseExtraFromIntent();
    }

    public void getAndUseExtraFromIntent(){
        EditProductActivity = getIntent();

        String ean = EditProductActivity.getStringExtra(BARCODE);
        String userId = EditProductActivity.getStringExtra(USERID);

        user = mDbHelper.getUserById(Long.parseLong(userId));
        product = mDbHelper.getProduct(ean);

        ingredients =  mDbHelper.getAllIngredients();
        productIngredients =  mDbHelper.getIngredients(product);

        productView = (TextView) findViewById(R.id.edit_ingredients_product);
        productView.setText(product.getName());
        productView.setTextColor(Color.GRAY);


        usernameView = (TextView) findViewById(R.id.edit_ingredients_username);
        usernameView.setText(user.getName());

        updateIngredientsView(ingredients, productIngredients);
    }

    private void goBackToEditProduct() {
        Intent editProduct = new Intent(this, EditProductActivity.class);
        editProduct.putExtra(BARCODE, product.getEAN());
        editProduct.putExtra(USERID, user.getId());
        startActivity(editProduct);
    }

    private void addIngredientsToProduct(ArrayList<Ingredient> ingredients, Hashtable productIngredients, Product product) {
        for (int i=0 ; i<ingredientLayout.getChildCount() ; i++){
            CheckBox ingredientCb = (CheckBox) ingredientLayout.getChildAt(i);
            if (ingredientCb.isChecked() && productIngredients.get(ingredientCb.getId())==null){
                addIngredient(product);
            }
        }
    }

    private void searchIngredients(ArrayList<Ingredient> ingredients, Hashtable productIngredients, Editable searchText) {
        ingredientLayout.removeAllViews();

        for (Ingredient ingredient : ingredients) {
            if (ingredient.ingredent.contains(searchText.toString())) {
                CheckBox ingredientCb = new CheckBox(getApplicationContext());
                ingredient.ingredent.replace(searchText.toString(), searchText.toString().toUpperCase());
                ingredientCb.setText(ingredient.ingredent);
                ingredientCb.setTextColor(Color.BLACK);
                ingredientCb.setId(ingredient.id);
                if (ingredient.equals(productIngredients.get(ingredient.id))) {
                    ingredientCb.setChecked(true);
                }
                ingredientLayout.addView(ingredientCb);
            }
        }
    }

    private void updateIngredientsView(ArrayList<Ingredient> ingredients, Hashtable productIngredients){
        ingredientLayout.removeAllViews();

        for (Ingredient ingredient : ingredients) {
            CheckBox ingredientCb = new CheckBox(getApplicationContext());
            ingredientCb.setText(ingredient.ingredent);
            ingredientCb.setTextColor(Color.BLACK);
            ingredientCb.setId(ingredient.id);
            if (ingredient.equals(productIngredients.get(ingredient.id))){
                ingredientCb.setChecked(true);
            }
            ingredientLayout.addView(ingredientCb);
        }
    }

    private void updateProductIngredients(Product product){
        ingredients = mDbHelper.getAllIngredients();
        productIngredients =  mDbHelper.getIngredients(product);
    }

    private long addIngredient(Product product) {
        EditText addIngredient = (EditText) findViewById(R.id.addIngredient);
        long id = mDbHelper.addIngredient(addIngredient.getText().toString(), user, product);
        Log.d("Edit ing. addIng() id", addIngredient.getText() + " " + id);
        return id;
    }

}
