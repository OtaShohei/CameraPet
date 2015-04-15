package jp.egaonohon.camerapet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.basicinc.gamefeat.android.sdk.controller.GameFeatAppController;
import jp.egaonohon.camerapet.encyc.Encyc01Activity;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CamPeGameFeat {

	/**
	 * コンストラクター
	 */
	public CamPeGameFeat() {
		super();
	}

	/** GameFeat用のインスタンス */
	private GameFeatAppController gfAppController;
	protected GameFeatGetImageData getImageData;

	public void workAtOnCreate(Activity activity) {
		// GFコントローラ
		gfAppController = new GameFeatAppController();
		gfAppController.init(activity);

		// LinearLayout incLayout = null;
		RelativeLayout incLayout = null;
		LinearLayout mainLayout = (LinearLayout) activity
				.findViewById(R.id.adMobSpace);

		// カスタム広告のデータを取得
		ArrayList<HashMap<String, String>> customArrayList = gfAppController
				.getCustomAds();

		for (final HashMap<String, String> map : customArrayList) {
			// LayoutInflaterの準備
			LayoutInflater inflater = (LayoutInflater) activity
					.getApplicationContext().getSystemService(
							Context.LAYOUT_INFLATER_SERVICE);
			incLayout = (RelativeLayout) inflater.inflate(R.layout.custom_row,
					null);

			// アイコン画像の読み込み
			ImageView appIcon = (ImageView) incLayout
					.findViewById(R.id.app_icon);
			getImageData = new GameFeatGetImageData(map.get("appIconUrl"), appIcon);
			getImageData.execute();

			// タイトルの設定
			TextView title = (TextView) incLayout.findViewById(R.id.title);
			title.setText(map.get("appTitle"));

			// タイトルの設定
			TextView description = (TextView) incLayout
					.findViewById(R.id.description);
			description.setText(map.get("appDescription"));

			// レビューボタンの設定
			Button btnReview = (Button) incLayout.findViewById(R.id.btn_review);
			if (map.get("hasReview") == "0") {
				btnReview.setVisibility(View.GONE);
			}
			btnReview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// レビューへ
					gfAppController.onAdReviewClick(map);
				}
			});

			// DLボタンの設定
			Button btnStore = (Button) incLayout.findViewById(R.id.btn_store);
			btnStore.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// AppStoreへ
					gfAppController.onAdStoreClick(map);
				}
			});

			mainLayout.addView(incLayout);
		}
	}

	/**
	 * 画像の非同期読み込み
	 *
	 */
	class GameFeatGetImageData extends AsyncTask<String, Integer, Bitmap> {

		private ImageView imageView;
		private String imageUrl;

		public GameFeatGetImageData(String imageUrl, ImageView imageView) {
			super();
			this.imageView = imageView;
			this.imageUrl = imageUrl;
		}

		@Override
		protected Bitmap doInBackground(String... param) {
			Bitmap bitmap;

			try {
				URL url = new URL(this.imageUrl);
				InputStream inputStream = url.openStream();
				bitmap = BitmapFactory.decodeStream(inputStream);
				return bitmap;
			} catch (IOException ex) {
				Logger.getLogger(Encyc01Activity.class.getName()).log(
						Level.SEVERE, null, ex);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
		}
	}
}
