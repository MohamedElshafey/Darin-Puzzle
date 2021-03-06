package qyadat.darin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by Mohamed Elshafey on 2016-08-24.
 */
public class ExistingFragment extends Fragment {
    ArrayList<Bitmap> appBitmaps = new ArrayList<>();
    ArrayList<Integer> appBitmapsInt = new ArrayList<>();
    int bestFit;
    BitmapFactory.Options options;
    GridView gridView;
    ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.existing_fragment,container,false);
        gridView = (GridView)view.findViewById(R.id.gridView);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);

        appBitmapsInt.clear();
        appBitmaps.clear();

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        appBitmapsInt.add(R.drawable.darin1);
        appBitmapsInt.add(R.drawable.darin2);
        appBitmapsInt.add(R.drawable.darin3);
        appBitmapsInt.add(R.drawable.darin4);
        appBitmapsInt.add(R.drawable.darin5);
        appBitmapsInt.add(R.drawable.darin6);
        appBitmapsInt.add(R.drawable.darin7);
        appBitmapsInt.add(R.drawable.darin8);

        new GetResults().execute();
        return view;
    }

    public class GetResults extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            gridView.setAdapter(new GridViewAdapter(getActivity(),appBitmaps));
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            appBitmaps.add(getBitmapResize(R.drawable.darin1));
            appBitmaps.add(getBitmapResize(R.drawable.darin2));
            appBitmaps.add(getBitmapResize(R.drawable.darin3));
            appBitmaps.add(getBitmapResize(R.drawable.darin4));
            appBitmaps.add(getBitmapResize(R.drawable.darin5));
            appBitmaps.add(getBitmapResize(R.drawable.darin6));
            appBitmaps.add(getBitmapResize(R.drawable.darin7));
            appBitmaps.add(getBitmapResize(R.drawable.darin8));
            return null;
        }
    }

    private Bitmap getBitmapResize(int image) {
        BitmapFactory.decodeResource(getResources(),image, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        Bitmap resized = Bitmap.createScaledBitmap(((BitmapDrawable)getResources().getDrawable(image)).getBitmap(),imageWidth,imageHeight,true);
        return resized;
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

            holder.thumb.setImageBitmap(croppedImage.get(position));
            holder.thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Game game = new Game();
                    Bundle bundle = new Bundle();
                    bundle.putInt("Image",appBitmapsInt.get(position));
                    game.setArguments(bundle);
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, game);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            return row;
        }

        public class ViewHolder {
            SquareImageView thumb;
        }
    }
}
