package dk.ruc.madssk.barcodedetectiontest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Computer on 13-04-2016.
 */
public class AllergyScannerDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "AllergyScanner.db";

    public AllergyScannerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AllergyScannerDbContract.CreateTable.SQL_CREATE_PRODUCTS_TABLE);
        db.execSQL(AllergyScannerDbContract.CreateTable.SQL_CREATE_INGREDIENTS_TABLE);
        db.execSQL(AllergyScannerDbContract.CreateTable.SQL_CREATE_PRODUCTSINGREDIENTS_TABLE);
        db.execSQL(AllergyScannerDbContract.CreateTable.SQL_CREATE_USER_TABLE);
        db.execSQL(AllergyScannerDbContract.CreateTable.SQL_CREATE_USERALLERGIES_TABLE);
        db.execSQL(AllergyScannerDbContract.CreateTable.SQL_CREATE_MAYCONTAIN_TABLE);
        db.execSQL(AllergyScannerDbContract.CreateTable.SQL_CREATE_HASADDED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: create migration code from old to new database
        db.execSQL(AllergyScannerDbContract.DeleteTable.SQL_DELETE_PRODUCTS_TABLE);
        db.execSQL(AllergyScannerDbContract.DeleteTable.SQL_DELETE_INGREDIENTS_TABLE);
        db.execSQL(AllergyScannerDbContract.DeleteTable.SQL_DELETE_PRODUCTSINGREDIENTS_TABLE);
        db.execSQL(AllergyScannerDbContract.DeleteTable.SQL_DELETE_USER_TABLE);
        db.execSQL(AllergyScannerDbContract.DeleteTable.SQL_DELETE_USERALLERGIES_TABLE);
        db.execSQL(AllergyScannerDbContract.DeleteTable.SQL_DELETE_MAYCONTAIN_TABLE);
        db.execSQL(AllergyScannerDbContract.DeleteTable.SQL_DELETE_HASADDED_TABLE);
        onCreate(db);
    }

    public User getUserByString(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                AllergyScannerDbContract.User.TABLE_NAME,                   //Table name
                new String[] {                                              //Array of attributes
                        AllergyScannerDbContract.User.COLUMN_NAME_USER_ID,
                        AllergyScannerDbContract.User.COLUMN_NAME_NAME},
                AllergyScannerDbContract.User.COLUMN_NAME_NAME + "=?",      //The attribute to compare to
                new String[] {String.valueOf(username)},                    //The value to compare to
                null,                                                       //Group by
                null,                                                       //Having
                null,                                                       //Order by
                null                                                        //Limit
        );

        if (cursor.getCount() < 1){
            db.close();
            return null;}

        cursor.moveToFirst();

        User user = new User(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1)
        );

        db.close();
        return user;
    }

    public User getUserById(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                AllergyScannerDbContract.User.TABLE_NAME,                   //Table name
                new String[] {                                              //Array of attributes
                        AllergyScannerDbContract.User.COLUMN_NAME_USER_ID,
                        AllergyScannerDbContract.User.COLUMN_NAME_NAME},
                AllergyScannerDbContract.User.COLUMN_NAME_USER_ID + "=?",      //The attribute to compare to
                new String[] {String.valueOf(id)},                    //The value to compare to
                null,                                                       //Group by
                null,                                                       //Having
                null,                                                       //Order by
                null                                                        //Limit
        );

        if (cursor.getCount() < 1){
            db.close();
            return null;}

        cursor.moveToFirst();

        User user = new User(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1)
        );

        db.close();
        return user;
    }

    public long addUser(String username){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AllergyScannerDbContract.User.COLUMN_NAME_NAME, username);

        long id = db.insert(AllergyScannerDbContract.User.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Product getProduct(String EAN){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                AllergyScannerDbContract.Products.TABLE_NAME,                   //Table name
                new String[] {                                                  //Array of attributes requested
                        AllergyScannerDbContract.Products.COLUMN_NAME_PRODUCT_ID,
                        AllergyScannerDbContract.Products.COLUMN_NAME_PRODUCT_NAME,
                        AllergyScannerDbContract.Products.COLUMN_NAME_PRODUCER,
                        AllergyScannerDbContract.Products.COLUMN_NAME_EAN,
                        AllergyScannerDbContract.Products.COLUMN_NAME_CREATION_DATE,
                        AllergyScannerDbContract.Products.COLUMN_NAME_IMAGE},
                AllergyScannerDbContract.Products.COLUMN_NAME_EAN + "=?",       //The attribute to compare to
                new String[] {String.valueOf(EAN)},                             //The value to compare to
                null,                                                           //Group by
                null,                                                           //Having
                null,                                                           //Order by
                null                                                            //Limit
        );

        if (cursor.getCount() < 1){
            db.close();
            return null;}

        cursor.moveToFirst();



        Product product = new Product(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4)
        );
        try{
            product.setImage(BitmapFactory.decodeByteArray(cursor.getBlob(5), 0, cursor.getBlob(5).length));
        }catch(Exception e){
            db.close();
            Log.d("Get Product Image", "" + e);
        }

        db.close();
        return product;
    }

    public long addProduct(Product product, User user){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues productValues = new ContentValues();
        if(product.getName()!=null){
            productValues.put(AllergyScannerDbContract.Products.COLUMN_NAME_PRODUCT_NAME, product.getName());
        }
        if(product.getProducer()!=null) {
            productValues.put(AllergyScannerDbContract.Products.COLUMN_NAME_PRODUCER, product.getProducer());
        }
        if(product.getEAN()!=null){
            productValues.put(AllergyScannerDbContract.Products.COLUMN_NAME_EAN, product.getEAN());
        }
        if(product.getImage()!=null){
            //Compresses and converts the bitmap image to a byte array
            Bitmap image = product.getImage();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bArray = bos.toByteArray();

            productValues.put(AllergyScannerDbContract.Products.COLUMN_NAME_IMAGE, bArray);
        }
        if(product.getCreationDate()!=null){
            productValues.put(AllergyScannerDbContract.Products.COLUMN_NAME_CREATION_DATE, product.getCreationDate());
        }

        long id = db.insert(AllergyScannerDbContract.Products.TABLE_NAME, null, productValues);

        ContentValues hasAddedValues = new ContentValues();
        hasAddedValues.put(AllergyScannerDbContract.HasAdded.COLUMN_NAME_USER_ID, user.getId());
        hasAddedValues.put(AllergyScannerDbContract.HasAdded.COLUMN_NAME_PRODUCT_ID, id);

        db.insert(AllergyScannerDbContract.HasAdded.TABLE_NAME, null, hasAddedValues);

        db.close();
        return id;
    }

    public Ingredient getIngredient(String ingredints){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                AllergyScannerDbContract.Ingredients.TABLE_NAME,                   //Table name
                new String[]{                                              //Array of attributes
                        AllergyScannerDbContract.Ingredients.COLUMN_NAME_INGREDIENT_ID,
                        AllergyScannerDbContract.Ingredients.COLUMN_NAME_INGREDIENT},
                AllergyScannerDbContract.Ingredients.COLUMN_NAME_INGREDIENT + "=?",      //The attribute to compare to
                new String[]{String.valueOf(ingredints)},                    //The value to compare to
                null,                                                       //Group by
                null,                                                       //Having
                null,                                                       //Order by
                null                                                        //Limit
        );

        if (cursor.getCount() < 1){
            db.close();
            return null;}

        cursor.moveToFirst();

        Ingredient ingredient = new Ingredient(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1)
        );

        db.close();
        return ingredient;
    }

    public Hashtable getIngredients(Product product){
        Hashtable Ingredients = new Hashtable();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " +
                            AllergyScannerDbContract.Ingredients.TABLE_NAME + " JOIN " +
                            AllergyScannerDbContract.ProductsIngredients.TABLE_NAME +
                            " ON " +
                            AllergyScannerDbContract.Ingredients.COLUMN_NAME_INGREDIENT_ID + " + " +
                            AllergyScannerDbContract.ProductsIngredients.COLUMN_NAME_INGREDIENT_ID +
                            " WHERE " +
                            AllergyScannerDbContract.ProductsIngredients.COLUMN_NAME_PRODUCT_ID +
                            " =? " +
                            " ORDER BY " +
                            AllergyScannerDbContract.Ingredients.COLUMN_NAME_INGREDIENT +
                            " ASC",
                    new String[]{                                              //Array of return attributes
                            String.valueOf(product.getId())});

            if (!cursor.moveToFirst()){
                db.close();
                return Ingredients;}

            do {
                Ingredient ingredient = new Ingredient();
                ingredient.id = Integer.parseInt(cursor.getString(0));
                ingredient.ingredent = cursor.getString(1);
                Ingredients.put(ingredient.id, ingredient);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }catch(Exception e){
            db.close();
            Log.d("getIng. failed", "" + e);}
        db.close();
        return Ingredients;
    }

    public ArrayList<Ingredient> getAllIngredients() {
        ArrayList<Ingredient> Ingredients = new ArrayList<Ingredient>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " +
                            AllergyScannerDbContract.Ingredients.TABLE_NAME +
                            " ORDER BY " +
                            AllergyScannerDbContract.Ingredients.COLUMN_NAME_INGREDIENT,
                    null);

            if (!cursor.moveToFirst()){
                db.close();
                return Ingredients;}

            do {
                Ingredient ingredient = new Ingredient();
                ingredient.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(
                        AllergyScannerDbContract.Ingredients.COLUMN_NAME_INGREDIENT_ID)));
                ingredient.ingredent = cursor.getString(cursor.getColumnIndex(
                        AllergyScannerDbContract.Ingredients.COLUMN_NAME_INGREDIENT));
                Ingredients.add(ingredient);
            } while (cursor.moveToNext());
            cursor.close();
        }catch(Exception e){
            db.close();
            Log.d("getAllIng. failed", "" + e);}
        db.close();
        return Ingredients;
    }

    public long addIngredient(String ingredient, User user){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AllergyScannerDbContract.Ingredients.COLUMN_NAME_INGREDIENT, ingredient);

        long id = db.insert(AllergyScannerDbContract.Ingredients.TABLE_NAME, null, values);

        ContentValues hasAddedValues = new ContentValues();
        hasAddedValues.put(AllergyScannerDbContract.HasAdded.COLUMN_NAME_USER_ID, User.getId());
        hasAddedValues.put(AllergyScannerDbContract.HasAdded.COLUMN_NAME_INGREDIENT_ID, id);

        db.insert(AllergyScannerDbContract.HasAdded.TABLE_NAME, null, hasAddedValues);

        db.close();
        return id;
    }

    public long addIngredient(String ingredient, User user, Product product){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put(AllergyScannerDbContract.Ingredients.COLUMN_NAME_INGREDIENT, ingredient);

        long id = db.insert(AllergyScannerDbContract.Ingredients.TABLE_NAME, null, ingredientValues);

        ContentValues prodIngValues = new ContentValues();
        prodIngValues .put(AllergyScannerDbContract.ProductsIngredients.COLUMN_NAME_PRODUCT_ID, product.getId());
        prodIngValues .put(AllergyScannerDbContract.ProductsIngredients.COLUMN_NAME_INGREDIENT_ID, id);

        db.insert(AllergyScannerDbContract.ProductsIngredients.TABLE_NAME, null, prodIngValues);

        ContentValues hasAddedValues = new ContentValues();
        hasAddedValues.put(AllergyScannerDbContract.HasAdded.COLUMN_NAME_USER_ID, user.getId());
        hasAddedValues.put(AllergyScannerDbContract.HasAdded.COLUMN_NAME_INGREDIENT_ID, id);

        db.insert(AllergyScannerDbContract.HasAdded.TABLE_NAME, null, hasAddedValues);

        db.close();
        return id;
    }

    public long updateProduct(Product product, User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues productValues = new ContentValues();
        if(product.getName()!=null){
            productValues.put(AllergyScannerDbContract.Products.COLUMN_NAME_PRODUCT_NAME, product.getName());
        }
        if(product.getProducer()!=null) {
            productValues.put(AllergyScannerDbContract.Products.COLUMN_NAME_PRODUCER, product.getProducer());
        }
        if(product.getEAN()!=null){
            productValues.put(AllergyScannerDbContract.Products.COLUMN_NAME_EAN, product.getEAN());
        }
        if(product.getImage()!=null){
            //Compresses and converts the bitmap image to a byte array
            Bitmap image = product.getImage();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bArray = bos.toByteArray();

            productValues.put(AllergyScannerDbContract.Products.COLUMN_NAME_IMAGE, bArray);
        }
        if(product.getCreationDate()!=null){
            productValues.put(AllergyScannerDbContract.Products.COLUMN_NAME_CREATION_DATE, product.getCreationDate());
        }

        long id = db.update(
                AllergyScannerDbContract.Products.TABLE_NAME,
                productValues,
                "Id =" + product.getId(),
                null);

        ContentValues hasAddedValues = new ContentValues();
        hasAddedValues.put(AllergyScannerDbContract.HasAdded.COLUMN_NAME_USER_ID, user.getId());
        hasAddedValues.put(AllergyScannerDbContract.HasAdded.COLUMN_NAME_PRODUCT_ID, id);

        db.insert(AllergyScannerDbContract.HasAdded.TABLE_NAME, null, hasAddedValues);

        db.close();
        return id;
    }
}
