package jp.egaonohon.camerapet;

import android.content.res.Resources;
import android.view.View;

/**
 * ペットがゲーム中につぶやくセリフを生成するクラス。
 * @author 1107AND
 *
 */
public class FukidasiTxt {

	/**
	 * ペットがゲーム中につぶやくセリフを生成するメソッド。
	 * @param view
	 * @param eventCode
	 * @return
	 */
	public static String make(View view, int eventCode) {

		Resources res = view.getResources();

		switch (eventCode) {

		/** その日最初のアプリ起動時 */
		case 1:
			return res.getString(R.string.pet_message_welcome);

			/** お腹いっぱいになったとき */
		case 2:
			return res.getString(R.string.pet_message_satiety);

			/** 降ってくるエサの残数が0になったとき */
		case 3:
			return res.getString(R.string.pet_message_esa_zero);

			/** レベルアップした時 */
		case 4:
			return res.getString(R.string.pet_message_levelup);

			/** SNS投稿で成長日記を投稿してくれた時 */
		case 5:
			return res.getString(R.string.pet_message_thanksSNS);

		default:
			/** いずれにも当てはまらない時 */
			return res.getString(R.string.pet_message_generic);
		}

	}

}
