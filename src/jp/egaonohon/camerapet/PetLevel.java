package jp.egaonohon.camerapet;

import jp.egaonohon.camerapet.pet.AbstractPet;
import jp.egaonohon.camerapet.pet.Pet001A;
import jp.egaonohon.camerapet.pet.Pet002A;
import jp.egaonohon.camerapet.pet.Pet003A;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

/**
 * 　アプリ起動時にどのペットをインスタンス化すべきかを判定してペットのインスタンス化を実行するメソッド。
 * 
 * @author OtaShohei
 */
public class PetLevel {

	/** 判定後に生成されたペットのインスタンスを格納する箱 */
	private static AbstractPet decisionedPet;
	/** ペット画像右向き */
	private Bitmap petPhR;
	/** ペット画像右向き */
	private Bitmap petPhL;
	/** ペット002Aに必要な経験値 */
	private static int pet002ARequiredEXP = 20;
	/** ペット002Aに必要な経験値 */
	private static int pet03ARequiredEXP = 25;

	/** Logのタグを定数で確保 */
	private static final String TAG = "PetLevel";

	/**
	 * GameSurfaceViewで呼び出されることで経験値などからペットのレベル判定を行い、
	 * そうして適切なペットをインスタンス化し戻り値としてペットを戻すメソッド。
	 * 
	 * @param context
	 * @param itemWidth
	 *            ペットの幅
	 * @param itemHeight
	 *            ペットの高さ
	 * @param defaultX
	 *            初期位置x
	 * @param defaultY
	 *            初期位置y
	 * @param viewWidth
	 *            Viewの幅
	 * @param viewHeight
	 *            Viewの高さ
	 * @return ペットのインスタンス
	 */
	public static AbstractPet up(Context context, View view, int itemWidth,
			int itemHeight, int defaultX, int defaultY, int viewWidth,
			int viewHeight) {

		/** 累計経験値を取得 */
		int totalEXP = CamPePref.loadTotalExp(context);

		/** ペット画像の入れ物を準備 */
		Bitmap petPhR;
		Bitmap petPhL;

		/** 経験値がPet002Aに必要な経験値に満たない場合はPet001Aを生成して返却する。 */
		if (totalEXP < pet002ARequiredEXP) {

			CameLog.setLog(TAG, "経験値がPet002Aに必要な経験値に満たないと判定し、Pet001Aを生成して返却");

			/** ペット写真取得 */
			petPhR = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet001a_r);
			petPhL = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet001a_l);
			decisionedPet = new Pet001A(view, petPhR, petPhL, itemWidth, itemHeight,
					defaultX, defaultY, viewWidth, viewHeight);
			/** 経験値がPet002Aに必要な経験値を満たす場合はPet002Aを生成して返却する。 */
		} else if (totalEXP >= pet002ARequiredEXP
				&& totalEXP < pet03ARequiredEXP) {

			CameLog.setLog(TAG, "経験値がPet002Aに必要な経験値を満たすと判定し、Pet002Aを生成して返却");

			petPhR = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet002a_r);
			petPhL = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet002a_l);
			decisionedPet = new Pet002A(view, petPhR, petPhL, itemWidth, itemHeight,
					defaultX, defaultY, viewWidth, viewHeight);
			/** 経験値がPet003Aに必要な経験値を満たす場合はPet003Aを生成して返却する。 */
		} else if (totalEXP >= pet03ARequiredEXP) {

			CameLog.setLog(TAG, "経験値がPet003Aに必要な経験値を満たすと判定し、Pet003Aを生成して返却");

			petPhR = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet003a_r);
			petPhL = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet003a_l);
			decisionedPet = new Pet003A(view, petPhR, petPhL, itemWidth, itemHeight,
					defaultX, defaultY, viewWidth, viewHeight);
		}
		return decisionedPet;
	}

	/** 現在のステータスでレベルアップが可能か否かを判定するメソッド。 */
	public static boolean judge(Context context, AbstractPet nowPet) {

		/** 累計経験値を取得 */
		int totalEXP = CamPePref.loadTotalExp(context);

		/** 経験値がPet002Aに必要な経験値を満たし、かつ、現在Pet002Aではない場合、Pet002Aにレベルアップが可能と判定 */
		if (totalEXP >= pet002ARequiredEXP && totalEXP < pet03ARequiredEXP
				&& !(nowPet instanceof Pet002A)) {
			return true;
			/** 経験値がPet003Aに必要な経験値を満たし、かつ、現在Pet003Aではない場合、Pet003Aにレベルアップが可能と判定 */
		} else if (totalEXP >= pet03ARequiredEXP
				&& !(nowPet instanceof Pet003A)) {
			return true;
		}
		return false;
	}

}
