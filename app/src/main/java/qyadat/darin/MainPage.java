package qyadat.darin;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mohamed Elshafey on 2016-08-24.
 */
public class MainPage extends Fragment {

    private static final int SELECT_PICTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    Uri imageUri;
    Bitmap thumbnail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_page, container, false);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == SELECT_PICTURE && resultCode == getActivity().RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            Log.d("BITMAAAAP", data.getData() + "");
            Log.d("BITMAAAAP", cursor.getColumnIndex(filePath[0]) + "");
            Log.d("BITMAAAAP", imagePath + "");

            Game game = new Game();
            Bundle bundle = new Bundle();
            bundle.putString("ImagePath",imagePath);
            game.setArguments(bundle);
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, game);
            ft.addToBackStack(null);
            ft.commit();

            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();

        }
        else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK ) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(
                        getActivity().getContentResolver(), imageUri);
                String imageurl = getRealPathFromURI(imageUri);
            Game game = new Game();
            Bundle bundle = new Bundle();
            bundle.putString("ImagePath",imageurl);
            game.setArguments(bundle);
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
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
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
