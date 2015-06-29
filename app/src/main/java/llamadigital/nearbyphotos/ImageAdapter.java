package llamadigital.nearbyphotos;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
    private ViewGroup layout;
    private Context context;
    private ArrayList<Bitmap> bitmapList;
    private List<String> titleList;

    public ImageAdapter(Context context, ArrayList<Bitmap> bitmapList, List<String> titleList) {
        super();
        this.context = context;
        this.bitmapList = bitmapList;
        this.titleList = titleList;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.thumb_container, parent, false);
        layout = (ViewGroup) view.findViewById(R.id.thumbContainer);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView textView = (TextView) view.findViewById(R.id.textView);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int newWidth = (int) (dm.widthPixels - 25 * dm.density) / 2;

        layout.setLayoutParams(new GridView.LayoutParams(newWidth, newWidth + 25));
        imageView.setId(position);
        textView.setId(position);

        Bitmap bm = bitmapList.get(position);
        String title = titleList.get(position);

        Bitmap resizeBitmap = Bitmap.createScaledBitmap(bm, newWidth - 50, newWidth - 50, true);

        imageView.setImageBitmap(resizeBitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        textView.setText(title);

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return bitmapList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return bitmapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}