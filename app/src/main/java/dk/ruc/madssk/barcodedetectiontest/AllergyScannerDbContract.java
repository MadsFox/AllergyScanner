package dk.ruc.madssk.barcodedetectiontest;

import android.provider.BaseColumns;

/**
 * Created by Mads Saustrup Kristensen on 13-04-2016.
 */
public final class AllergyScannerDbContract {

    public AllergyScannerDbContract() {
    }

    public static class Products implements BaseColumns {
        public static final String TABLE_NAME = "Products";
        public static final String COLUMN_NAME_PRODUCT_ID = "Id";
        public static final String COLUMN_NAME_PRODUCT_NAME = "Name";
        public static final String COLUMN_NAME_PRODUCER = "Producer";
        public static final String COLUMN_NAME_EAN = "EAN";
        public static final String COLUMN_NAME_CREATION_DATE = "CreationDate";
        public static final String COLUMN_NAME_IMAGE = "Image";

    }

    public static class Ingredients implements BaseColumns {
        public static final String TABLE_NAME = "Ingredients";
        public static final String COLUMN_NAME_INGREDIENT_ID = "Id";
        public static final String COLUMN_NAME_INGREDIENT = "Ingredient";

    }

    public static class User implements BaseColumns {
        public static final String TABLE_NAME = "User";
        public static final String COLUMN_NAME_USER_ID = "Id";
        public static final String COLUMN_NAME_NAME = "Name";
    }

    public static class UserAllergies implements BaseColumns {
        public static final String TABLE_NAME = "Allergies";
        public static final String COLUMN_NAME_USER_ID = "UserId";
        public static final String COLUMN_NAME_INGREDIENT_ID = "IngredientId";
    }

    public static class ProductsIngredients implements BaseColumns {
        public static final String TABLE_NAME = "ProductsIngredients";
        public static final String COLUMN_NAME_PRODUCT_ID = "ProductId";
        public static final String COLUMN_NAME_INGREDIENT_ID = "IngredientId";
    }

    public static class MayContain implements BaseColumns {
        public static final String TABLE_NAME = "MayContain";
        public static final String COLUMN_NAME_PRODUCT_ID = "ProductId";
        public static final String COLUMN_NAME_INGREDIENT_ID = "IngredientId";
    }

    public static class HasAdded implements BaseColumns {
        public static final String TABLE_NAME = "HasAdded";
        public static final String COLUMN_NAME_USER_ID = "UserId";
        public static final String COLUMN_NAME_PRODUCT_ID = "ProductId";
        public static final String COLUMN_NAME_INGREDIENT_ID = "IngredientId";
    }

    public static class CreateTable {
        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String REAL_TYPE = " REAL";
        private static final String BOLB_TYPE = " BOLB";
        private static final String COMMA_SEP = ",";

        public static final String SQL_CREATE_PRODUCTS_TABLE =
                "CREATE TABLE " + Products.TABLE_NAME + " (" +
                        Products.COLUMN_NAME_PRODUCT_ID + " INTEGER PRIMARY KEY," +
                        Products.COLUMN_NAME_PRODUCT_NAME + TEXT_TYPE + COMMA_SEP +
                        Products.COLUMN_NAME_CREATION_DATE + TEXT_TYPE + COMMA_SEP +
                        Products.COLUMN_NAME_EAN + INTEGER_TYPE + COMMA_SEP +
                        Products.COLUMN_NAME_PRODUCER + TEXT_TYPE + COMMA_SEP +
                        Products.COLUMN_NAME_IMAGE + BOLB_TYPE +
                        " )";

        public static final String SQL_CREATE_INGREDIENTS_TABLE =
                "CREATE TABLE " + Ingredients.TABLE_NAME + " (" +
                        Ingredients.COLUMN_NAME_INGREDIENT_ID + " INTEGER PRIMARY KEY," +
                        Ingredients.COLUMN_NAME_INGREDIENT + TEXT_TYPE +
                        " )";

        public static final String SQL_CREATE_USER_TABLE =
                "CREATE TABLE " + User.TABLE_NAME + " (" +
                        User.COLUMN_NAME_USER_ID + " INTEGER PRIMARY KEY," +
                        User.COLUMN_NAME_NAME + TEXT_TYPE +
                        " )";

        public static final String SQL_CREATE_USERALLERGIES_TABLE =
                "CREATE TABLE " + UserAllergies.TABLE_NAME + " (" +
                        UserAllergies.COLUMN_NAME_USER_ID + INTEGER_TYPE + COMMA_SEP +
                        UserAllergies.COLUMN_NAME_INGREDIENT_ID + INTEGER_TYPE + COMMA_SEP +
                        "FOREIGN KEY(" + UserAllergies.COLUMN_NAME_USER_ID +
                        ") REFERENCES " + User.TABLE_NAME + "(" + User.COLUMN_NAME_USER_ID + ")" + COMMA_SEP +
                        "FOREIGN KEY(" + UserAllergies.COLUMN_NAME_INGREDIENT_ID +
                        ") REFERENCES " + Ingredients.TABLE_NAME + "(" + Ingredients.COLUMN_NAME_INGREDIENT_ID + ")" +
                        ")";

        public static final String SQL_CREATE_PRODUCTSINGREDIENTS_TABLE =
                "CREATE TABLE " + ProductsIngredients.TABLE_NAME + " (" +
                        ProductsIngredients.COLUMN_NAME_INGREDIENT_ID + INTEGER_TYPE + COMMA_SEP +
                        ProductsIngredients.COLUMN_NAME_PRODUCT_ID + INTEGER_TYPE + COMMA_SEP +
                        "FOREIGN KEY(" + ProductsIngredients.COLUMN_NAME_INGREDIENT_ID +
                        ") REFERENCES " + Ingredients.TABLE_NAME + "(" + Ingredients.COLUMN_NAME_INGREDIENT_ID + ")" + COMMA_SEP +
                        "FOREIGN KEY(" + ProductsIngredients.COLUMN_NAME_PRODUCT_ID +
                        ") REFERENCES " + Products.TABLE_NAME + "(" + Products.COLUMN_NAME_PRODUCT_ID + ")" +
                        ")";

        public static final String SQL_CREATE_MAYCONTAIN_TABLE =
                "CREATE TABLE " + MayContain.TABLE_NAME + " (" +
                        MayContain.COLUMN_NAME_INGREDIENT_ID + INTEGER_TYPE + COMMA_SEP +
                        MayContain.COLUMN_NAME_PRODUCT_ID + INTEGER_TYPE + COMMA_SEP +
                        "FOREIGN KEY(" + MayContain.COLUMN_NAME_INGREDIENT_ID +
                        ") REFERENCES " + Ingredients.TABLE_NAME + "(" + Ingredients.COLUMN_NAME_INGREDIENT_ID + ")" + COMMA_SEP +
                        "FOREIGN KEY(" + MayContain.COLUMN_NAME_PRODUCT_ID +
                        ") REFERENCES " + Products.TABLE_NAME + "(" + Products.COLUMN_NAME_PRODUCT_ID + ")" +
                        ")";

        public static final String SQL_CREATE_HASADDED_TABLE =
                "CREATE TABLE " + HasAdded.TABLE_NAME + " (" +
                        HasAdded.COLUMN_NAME_USER_ID + INTEGER_TYPE + COMMA_SEP +
                        HasAdded.COLUMN_NAME_INGREDIENT_ID + INTEGER_TYPE + COMMA_SEP +
                        HasAdded.COLUMN_NAME_PRODUCT_ID + INTEGER_TYPE + COMMA_SEP +
                        "FOREIGN KEY(" + HasAdded.COLUMN_NAME_USER_ID +
                        ") REFERENCES " + User.TABLE_NAME + "(" + Ingredients.COLUMN_NAME_INGREDIENT_ID + ")" + COMMA_SEP +
                        "FOREIGN KEY(" + HasAdded.COLUMN_NAME_INGREDIENT_ID +
                        ") REFERENCES " + Ingredients.TABLE_NAME + "(" + Ingredients.COLUMN_NAME_INGREDIENT_ID + ")" + COMMA_SEP +
                        "FOREIGN KEY(" + HasAdded.COLUMN_NAME_PRODUCT_ID +
                        ") REFERENCES " + Products.TABLE_NAME + "(" + Products.COLUMN_NAME_PRODUCT_ID + ")" +
                        ")";

    }
    public static class DeleteTable{
        public static final String SQL_DELETE_PRODUCTS_TABLE =
                "DROP TABLE IF EXISTS " + Products.TABLE_NAME;

        public static final String SQL_DELETE_INGREDIENTS_TABLE =
                "DROP TABLE IF EXISTS " + Ingredients.TABLE_NAME;

        public static final String SQL_DELETE_USER_TABLE =
                "DROP TABLE IF EXISTS " + User.TABLE_NAME;

        public static final String SQL_DELETE_USERALLERGIES_TABLE =
                "DROP TABLE IF EXISTS " + UserAllergies.TABLE_NAME;

        public static final String SQL_DELETE_PRODUCTSINGREDIENTS_TABLE =
                "DROP TABLE IF EXISTS " + ProductsIngredients.TABLE_NAME;

        public static final String SQL_DELETE_MAYCONTAIN_TABLE =
                "DROP TABLE IF EXISTS " + MayContain.TABLE_NAME;

        public static final String SQL_DELETE_HASADDED_TABLE =
                "DROP TABLE IF EXISTS " + HasAdded.TABLE_NAME;
    }
}
