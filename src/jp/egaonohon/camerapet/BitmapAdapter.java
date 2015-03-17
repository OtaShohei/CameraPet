package jp.egaonohon.camerapet;

import android.widget.ArrayAdapter;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * GetPhクラスから利用するBitmapを格納するArrayAdapterを作成するクラス。参考↓
 * http://dev.classmethod.jp/smartphone/basic-android-component-04-gridview/
 * @author OtaShohei
 *
 */
public class BitmapAdapter extends ArrayAdapter<Bitmap> {

	private int resourceId;

	public BitmapAdapter(Context context, int resource, List<Bitmap> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resourceId, null);
		}

		ImageView view = (ImageView) convertView;
		view.setImageBitmap(getItem(position));

		return view;
	}

}