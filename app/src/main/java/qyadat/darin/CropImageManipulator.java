package qyadat.darin;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by Mohamed Elshafey on 2016-08-23.
 */
public class CropImageManipulator {
    Drawable image;
    public ArrayList<Bitmap> chunkedImages;
    int chunkNumbers;

    public ArrayList<Bitmap> splitImage(Drawable image, int chunkNumbers , int bestFit) {
        ArrayList<Bitmap> Chunks = new ArrayList<>();

        //For the number of rows and columns of the grid to be displayed
        int rows,cols;

        //For height and width of the small image chunks
        int chunkHeight,chunkWidth;

        //To store all the small image chunks in bitmap format in this list
        chunkedImages = new ArrayList<Bitmap>(chunkNumbers);

        //Getting the scaled bitmap of the source image
        BitmapDrawable drawable = (BitmapDrawable) image;
        Bitmap resized = Bitmap.createScaledBitmap(drawable.getBitmap(), bestFit, bestFit, true);

        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = resized.getHeight()/rows;
        chunkWidth = resized.getWidth()/cols;

        //xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for(int x=0; x<rows; x++){
            int xCoord = 0;
            for(int y=0; y<cols; y++){
                chunkedImages.add(Bitmap.createBitmap(resized, xCoord, yCoord, chunkWidth, chunkHeight));
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }

        return chunkedImages;
    }

}
