package dk.ruc.madssk.barcodedetectiontest;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class EANSearchActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int PICK_IMAGE = 200;
    public static final String BARCODE = "dk.ruc.madssk.barcodedetectiontest.BARCODE";
    public static final String USERID = "dk.ruc.madssk.barcodedetectiontest.USERID";

    Intent LogInActivity;
    EditText searchField;

    AllergyScannerDbHelper mDbHelper = new AllergyScannerDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ean_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LogInActivity = getIntent();

        TextView usernameView = (TextView) findViewById(R.id.ean_search_username);

        if (LogInActivity.getStringExtra(USERID)!=null) {
            Log.d("EAN search got USERID", LogInActivity.getStringExtra(USERID));
            mDbHelper.getReadableDatabase();
            User user = mDbHelper.getUserById(Integer.parseInt(LogInActivity.getStringExtra(USERID)));
            usernameView.setText(user.getName());
        }else{Log.d("EAN Search", "no USERID received");}

        searchField = (EditText) findViewById(R.id.searchField);


        //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        //From: http://developer.android.com/guide/topics/media/camera.html
        //At: "Image capture intent"
        //handles the camera_button click
        Button scan_btn = (Button) findViewById(R.id.scan_button);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        Button load_btn = (Button) findViewById(R.id.load_button);
        load_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loadPicture = new Intent();
                loadPicture.setType("image/*");
                loadPicture.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(loadPicture, "Select Picture"), PICK_IMAGE);

            }
        });

        //handles the search_button click
        Button search_btn = (Button) findViewById(R.id.search_button);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBarcode(LogInActivity.getStringExtra(USERID), searchField.getText().toString());
            }
        });

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchBarcode(LogInActivity.getStringExtra(USERID), searchField.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });

    }

    //From http://developer.android.com/guide/topics/media/camera.html
    //At: "Receiving camera intent result"

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("RequestCode", requestCode + "");
        Log.d("Result_Code", resultCode + "   Result_OK = " + RESULT_OK);

        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(getApplicationContext()).build();

        EditText searchField = (EditText) findViewById(R.id.searchField);

        if (!barcodeDetector.isOperational()) {
            Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_LONG).show();
            Log.v("Barcode Detector: ", "Could not set up the detector!");
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "Has to low storage for library", Toast.LENGTH_LONG).show();
                Log.w("Barcode Detecion: ", "Failed do to low storage");
                searchField.setHint("To low storage");
                return;

            } else {
                Toast.makeText(this, "Has enough storage space", Toast.LENGTH_LONG).show();
                Log.w("Barcode Detecion: ", "Has enough storage space");
            }
            searchField.setHint("Barcode detector not working");
            return;
        } else {
            Log.d("Operational", "" + barcodeDetector.isOperational());
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Log.d("STATUS ", "CAPTURE_IMAGE");
                    Log.d("INTENT ", "" + data);
                    Bundle captureImageExtras = data.getExtras();
                    Bitmap captureImageBitmap = (Bitmap) captureImageExtras.get("data");

                    ImageView picturePreview = (ImageView) findViewById(R.id.picture_preview);
                    picturePreview.setImageBitmap(captureImageBitmap);

                    Frame frame = new Frame.Builder().setBitmap(captureImageBitmap).build();
                    Log.d("FRAME ", "" + frame);
                    SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
                    Log.d("BARCODE ARRAY", "" + barcodes);

                    Log.d("ARRAY SIZE", "" + barcodes.size());

                    if (barcodes.size()>0) {
                        if (barcodes.valueAt(0) == null) {
                            Log.d("Barcodes detected", "" + barcodes.size());
                            Log.d("Barcode0 value", "" + barcodes.valueAt(0));
                            searchField.setHint("No barcode detected, insert manually");
                        }else if (barcodes.valueAt(0) != null){
                            Log.d("Barcodes detected", "" + barcodes.size());

                            Barcode barcode = barcodes.valueAt(0);
                            searchField.setText(barcode.rawValue);

                            Toast.makeText(this, "Barcode detected", Toast.LENGTH_LONG).show();
                        }
                    } else {searchField.setHint("No barcode detected, insert manually");}
                }else {
                    Log.d("Camera intent ", "no barcode captured, intent data is null");
                }
            }else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No image detected", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Log.d("STATUS ", "LOAD_IMAGE");
                    Log.d("Bundle extras", data.getExtras().toString());
                    Bundle loadImageExtras = data.getExtras();
                    Bitmap loadImageBitmap = (Bitmap) loadImageExtras.get("data");


                    ImageView img = (ImageView) findViewById(R.id.picture_preview);
                    img.setImageBitmap(loadImageBitmap);

                    Frame frame = new Frame.Builder().setBitmap(loadImageBitmap).build();
                    Log.d("FRAME ", "" + frame);
                    SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
                    Log.d("BARCODE ARRAY", "" + barcodes);

                    Log.d("ARRAY SIZE", "" + barcodes.size());

                    if (barcodes.size()>0) {
                        if (barcodes.valueAt(0) == null) {
                            Log.d("Barcodes detected", "" + barcodes.size());
                            Log.d("Barcode0 value", "" + barcodes.valueAt(0));
                            searchField.setHint("No barcode detected, insert manually");
                        }else if (barcodes.valueAt(0) != null){
                            Log.d("Barcodes detected", "" + barcodes.size());

                            Barcode barcode = barcodes.valueAt(0);
                            searchField.setText(barcode.rawValue);

                            Toast.makeText(this, "Barcode detected", Toast.LENGTH_LONG).show();
                        }
                    } else {searchField.setHint("No barcode detected, insert manually");}
                }
            }else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No image detected", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            Toast.makeText(this, "Image detected failed", Toast.LENGTH_LONG).show();
        }
    }

    private void searchBarcode(String userId, String barcode) {

        if(mDbHelper.getProduct(barcode) == null){
            Intent createProduct = new Intent(this, EditProductActivity.class);

            createProduct.putExtra(USERID, userId);
            createProduct.putExtra(BARCODE, barcode);
            startActivity(createProduct);
        } else {
            Intent productView = new Intent(this, ProductViewActivity.class);

            productView.putExtra(USERID, userId);
            productView.putExtra(BARCODE, barcode);
            startActivity(productView);
        }
    }
}
