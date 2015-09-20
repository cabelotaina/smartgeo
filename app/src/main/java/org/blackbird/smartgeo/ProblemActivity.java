package org.blackbird.smartgeo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationManager;
import android.location.Location;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.io.IOException;


public class ProblemActivity extends Activity {

    public final static String EXTRA_TITLE = "org.blackbird.smartgeo.TITLE";
    public final static String EXTRA_DESCRIPTION = "org.blackbird.smartgeo.DESCRIPTION";

    private final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;

    private static final String TAG = "SMARTGEO";

    private TextView address_aux;
    private ImageView picture_one = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_problem);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Double longitude = location.getLongitude();
        Double latitude = location.getLatitude();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses == null) {
                Log.w("SMARTGEO", "GPS is false because address is null");
            } else {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                address_aux = (TextView) findViewById(R.id.address);
                address_aux.setText(address + ", " + city);
            }
        } catch (Exception e) {
            Log.e("SMARTGEO", "exception: " + e.getMessage());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editTitle = (EditText) findViewById(R.id.edit_title);
        String title = editTitle.getText().toString();
        EditText editDescription = (EditText) findViewById(R.id.edit_description);
        String description = editDescription.getText().toString();
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        startActivity(intent);
    }

    public void addImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("return-data", true); //added snippet
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    public void loadCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File image;
            Uri image_path = null;
            try {
                image = getOutputPhotoFile();
                image_path = Uri.fromFile(image);
            } catch (Exception ex) {
                Log.e("SMARTGEO", "exception: " + ex.getMessage());
                Log.e("SMARTGEO", "exception: " + ex.toString());
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            Toast.makeText(this, "Image saved to:\n" +
                    image_path, Toast.LENGTH_LONG).show();
            if (image_path != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, image_path);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File getOutputPhotoFile() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getPackageName());
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAG, "Failed to create storage directory.");
                Toast.makeText(this, "Failed to create storage directory." +
                        null, Toast.LENGTH_LONG).show();
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.UK).format(new Date());
        return new File(directory.getPath() + File.separator + "IMG_"
                + timeStamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "resultCode: " + resultCode, Toast.LENGTH_LONG).show();
        //if (resultCode == RESULT_OK){
        picture_one = (ImageView) findViewById(R.id.picture_one);

        if (requestCode == REQUEST_TAKE_PHOTO && data != null && data.getData() != null) {
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            picture_one.setImageBitmap(bp);
        }

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                picture_one.setImageBitmap(bitmap);
            } catch (IOException ex) {
                Log.e("SMARTGEO", "exception: " + ex.getMessage());
                Log.e("SMARTGEO", "exception: " + ex.toString());
                ex.printStackTrace();
            }
        }
        //}


    }


}
