package qyadat.darin;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static String FACEBOOK_URL = "https://www.facebook.com/darin00013";
    public static String FACEBOOK_PAGE_ID = "darin00013";

    private static final int SELECT_PICTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    Uri imageUri;
    Bitmap thumbnail;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // Create a new Fragment to be placed in the activity layout
        MainPage firstFragment = new MainPage();
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, firstFragment)
                .commit();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_existing) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new ExistingFragment());
            ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_gallery) {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
            startActivityForResult(chooserIntent, SELECT_PICTURE);
        } else if(id == R.id.nav_camera) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = this.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }else if (id == R.id.nav_share_insta) {
            shareInsta();
        }else if(id == R.id.nav_share_facebook){
            shareFacebook();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == SELECT_PICTURE && resultCode == this.RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getApplicationContext().getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            Log.d("BITMAAAAP", data.getData() + "");
            Log.d("BITMAAAAP", cursor.getColumnIndex(filePath[0]) + "");
            Log.d("BITMAAAAP", imagePath + "");

            Game game = new Game();
            Bundle bundle = new Bundle();
            bundle.putString("ImagePath",imagePath);
            game.setArguments(bundle);
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, game);
            ft.addToBackStack(null);
            ft.commit();

            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();

        }
        else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == this.RESULT_OK ) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), imageUri);
                String imageurl = getRealPathFromURI(imageUri);
                Game game = new Game();
                Bundle bundle = new Bundle();
                bundle.putString("ImagePath",imageurl);
                game.setArguments(bundle);
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, game);
                ft.addToBackStack(null);
                ft.commit();

                Log.d("IAM_INN",imageurl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void shareInsta(){
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://instagram.com/_u/darin00013/"));
            intent.setPackage("com.instagram.android");
            startActivity(intent);
        }
        catch(Exception e)
        { //e.toString();
            Toast.makeText(MainActivity.this,"Cannot open",Toast.LENGTH_LONG).show();
        }
    }

    public void shareFacebook(){
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = getFacebookPageURL(this);
        facebookIntent.setData(Uri.parse(facebookUrl));
        startActivity(facebookIntent);
    }


    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }
}
