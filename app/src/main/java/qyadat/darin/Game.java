package qyadat.darin;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mohamed Elshafey on 2016-08-24.
 */
public class Game extends Fragment
{
    GridView gridView;
    LinearLayout linearLayout;
    private Boolean bad_move=false;
    public GridViewAdapter gridAdapter;
    ArrayList<Bitmap> cropped;
    ArrayList<Bitmap> cropped_Shuffle = new ArrayList<>();
    int empty_pos = 0;
    TextView IndicatorTextView;
    int splitters_number = 9;
    ArrayList<Integer> Sorting = new ArrayList<>();
    ArrayList<Integer> Shuffle = new ArrayList<>();
    int bestFit;
    Drawable imageFromPath;
    int moves;
    ProgressBar progressBar;
    TextView correctMovesTextView;
    SquareImageView originalImageView;
    int imageName;
    String imagePath;
    boolean ImagePath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_layout,container,false);
        Bundle bundle = this.getArguments();

        gridView = (GridView) view.findViewById(R.id.gridView);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        correctMovesTextView = (TextView)view.findViewById(R.id.correctMovesTextView);
        originalImageView = (SquareImageView) view.findViewById(R.id.originalImageView);
        CropImageManipulator cr = new CropImageManipulator();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(),R.drawable.darin1, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        if(imageHeight>imageWidth)
            bestFit =imageHeight;
        else
            bestFit =imageWidth;



        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(9);



        if(bundle.containsKey("Image")) {
            imageName = bundle.getInt("Image");
            cropped = cr.splitImage(getResources().getDrawable(imageName), splitters_number, bestFit);
            cropped.remove(0);
            originalImageView.setImageDrawable(getResources().getDrawable(imageName));
            ImagePath=false;
        }else if(bundle.containsKey("ImagePath")) {
            imagePath = bundle.getString("ImagePath");
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(Uri.parse("file://" +imagePath));
                imageFromPath = Drawable.createFromStream(inputStream, "file://" +imagePath );
                cropped = cr.splitImage(imageFromPath, splitters_number, bestFit);
                cropped.remove(0);
                originalImageView.setImageDrawable(imageFromPath);
                Log.d("IMAGE_PATH",imageFromPath+"");
            } catch (FileNotFoundException e) {
                Log.d("IMAGE_PATH",e.toString());
            }

            ImagePath=true;
        }


        Drawable myDrawable = getResources().getDrawable(R.drawable.white);
        Bitmap whiteImage      = ((BitmapDrawable) myDrawable).getBitmap();

        cropped.add(0,whiteImage);
        for (int i = 0 ; i<splitters_number ; i++){
            Sorting.add(i);
        }
        List<Integer> indexArray = Arrays.asList(0,1, 2,3,4,5,6,7,8);
        Collections.shuffle(indexArray);
        for (int i = 0 ; i<indexArray.size() ; i++) {
            cropped_Shuffle.add(cropped.get(indexArray.get(i)));
            Shuffle.add(indexArray.get(i));
            if(indexArray.get(i)==0)
                empty_pos=Sorting.get(i);
        }

        //TEST to be Winner
//        cropped_Shuffle=cropped;
//        empty_pos = 0 ;


        IndicatorTextView = (TextView)view.findViewById(R.id.IndicatorTextView);
        gridAdapter = new GridViewAdapter(getActivity(), cropped_Shuffle);
        gridView.setAdapter(gridAdapter);
        calculateCorrect();




        //Swipe OnTouch


        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){


                int action = MotionEventCompat.getActionMasked(motionEvent);

                switch(action) {
                    case (MotionEvent.ACTION_DOWN):
                        Log.d("DEBUGTAG", "Action was DOWN");
                        return true;
//                    case (MotionEvent.ACTION_MOVE):
//                        Log.d("DEBUGTAG", "Action was MOVE");
//                        return true;
                    case (MotionEvent.ACTION_UP):
                        Log.d("DEBUGTAG", "Action was UP");
                        return true;
                    case (MotionEvent.ACTION_CANCEL):
                        Log.d("DEBUGTAG", "Action was CANCEL");
                        return true;
                    case (MotionEvent.ACTION_OUTSIDE):
                        Log.d("DEBUGTAG", "Movement occurred outside bounds " +
                                "of current screen element");
                        return true;
                    default:
                        return true;
                }

            }
        });


        return view;
    }
    public class GridViewAdapter extends ArrayAdapter {
        Context context;
        ArrayList<Bitmap> croppedImage;

        public GridViewAdapter(Context c, ArrayList<Bitmap> croppedImage) {
            super(c, R.layout.activity_main, croppedImage);
            this.context = c;
            this.croppedImage = croppedImage;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;
            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.image_view_inflate, parent, false);
                holder = new ViewHolder();
                holder.thumb = (SquareImageView) row.findViewById(R.id.picture);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

//            if(position!=empty_pos) {
            holder.thumb.setImageBitmap(croppedImage.get(position));
            holder.thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeMove((SquareImageView) view, position);
                }
            });

            return row;
        }

        public class ViewHolder {
            SquareImageView thumb;
        }
    }


    public void makeMove(final SquareImageView b,int position) {
        bad_move=true;
        int clicked_pos = position;
        switch(empty_pos)
        {
            case(0):
                if(clicked_pos==1||clicked_pos==3)
                    bad_move=false;
                break;
            case(1):
                if(clicked_pos==0||clicked_pos==2||clicked_pos==4)
                    bad_move=false;
                break;
            case(2):
                if(clicked_pos==1||clicked_pos==5)
                    bad_move=false;
                break;
            case(3):
                if(clicked_pos==0||clicked_pos==4||clicked_pos==6)
                    bad_move=false;
                break;
            case(4):
                if(clicked_pos==1||clicked_pos==3||clicked_pos==5||clicked_pos==7)
                    bad_move=false;
                break;
            case(5):
                if(clicked_pos==2||clicked_pos==4||clicked_pos==8)
                    bad_move=false;
                break;
            case(6):
                if(clicked_pos==3||clicked_pos==7)
                    bad_move=false;
                break;
            case(7):
                if(clicked_pos==4||clicked_pos==6||clicked_pos==8)
                    bad_move=false;
                break;
            case(8):
                if(clicked_pos==5||clicked_pos==7)
                    bad_move=false;
                break;
        }
        if(bad_move==true)
        {
            IndicatorTextView.setText("NOT ALLOWED");
            return;
        }else {
            moves++;
            IndicatorTextView.setText("Moves : "+moves);
            Bitmap CurrentImage = cropped_Shuffle.get(clicked_pos);
            Bitmap EmptyImage = (Bitmap) cropped_Shuffle.get(empty_pos);

            cropped_Shuffle.remove(empty_pos);
            cropped_Shuffle.add(empty_pos, CurrentImage);
            cropped_Shuffle.remove(clicked_pos);
            cropped_Shuffle.add(clicked_pos, EmptyImage);

            Log.d("TRYYY_CLICK",clicked_pos+"");
            Log.d("TRYYY_EMPTY",empty_pos+"");
            Log.d("TRYYY_CROP",cropped.size()+"");

            gridAdapter.notifyDataSetChanged();

            empty_pos = clicked_pos;
        }

        calculateCorrect();
    }

    private void calculateCorrect() {
        int Correct = 0;
        for (int i=0 ; i < splitters_number ; i ++){
            if(cropped.get(i)==cropped_Shuffle.get(i)){
                Correct +=1;
            }
            progressBar.setProgress(Correct);
            correctMovesTextView.setText(Correct+"/9");

        }
        if(Correct == splitters_number){

            new AlertDialog.Builder(getActivity())
                    .setTitle("WINNER !!")
                    .setMessage("We have a winner :D  \nNumber of Moves : "+moves)
                    .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            Game game = new Game();
                            Bundle bundle = new Bundle();
                            if(ImagePath){
                                bundle.putString("ImagePath",imagePath);
                            }else {
                                bundle.putInt("Image",imageName);
                            }
                            game.setArguments(bundle);
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.fragment_container, game);
                            ft.commit();
                        }
                    })
                    .setNegativeButton("Choose From Menu", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing

                            getFragmentManager().beginTransaction().replace(R.id.fragment_container,new MainPage()).commit();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

//            IndicatorTextView.setText("We have a winner :D  \nNumber of Moves : "+moves);
            progressBar.setProgress(9);
            correctMovesTextView.setText("9/9");
        }
    }
}
