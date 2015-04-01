package jp.egaonohon.camerapet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 　アプリ起動時にどのペットをインスタンス化すべきかを判定してペットのインスタンス化を実行するメソッド。
 *
 * @author OtaShohei
 *
 */
public class PetLevel {

	/** 判定後に生成されたペットのインスタンスを格納する箱 */
	private static AbstractPet decisionedPet;
	/** ペット画像右向き */
	private Bitmap petPhR;
	/** ペット画像右向き */
	private Bitmap petPhL;
	/** ペット002Aに必要な経験値 */
	private static int pet002ARequiredEXP = 620;

	/**
	 * GameSurfaceViewで呼び出されることで経験値などからペットのレベル判定を行い、
	 * そうして適切なペットをインスタンス化し戻り値としてペットを戻すメソッド。
	 * */
	public static AbstractPet up(Context context, int itemWidth,
			int itemHeight, int defaultX, int defaultY, int viewWidth,
			int viewHeight) {

		/** 累計経験値を取得 */
		int totalEXP = CamPePref.loadTotalExp(context);

		/** ペット画像の入れ物を準備 */
		Bitmap petPhR;
		Bitmap petPhL;

		/** 経験値がPet002Aに必要な経験値に満たない場合はPet001Aを生成して返却する。 */
		if (totalEXP < pet002ARequiredEXP) {

			/** ペット写真取得 */
			petPhR = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet001a_r);
			petPhL = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet001a_l);
			decisionedPet = new Pet001A(petPhR, petPhL, itemWidth, itemHeight,
					defaultX, defaultY, viewWidth, viewHeight);
			/** 経験値がPet002Aに必要な経験値を満たす場合はPet002Aを生成して返却する。 */
		} else if (totalEXP >= pet002ARequiredEXP) {
			petPhR = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet002a_r);
			petPhL = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet002a_l);
			decisionedPet = new Pet002A(petPhR, petPhL, itemWidth, itemHeight,
					defaultX, defaultY, viewWidth, viewHeight);
		}
		return decisionedPet;
	}

	/** 現在のステータスでレベルアップが可能か否かを判定するメソッド。 */
	public static boolean judge(Context context, AbstractPet nowPet) {

		/** 累計経験値を取得 */
		int totalEXP = CamPePref.loadTotalExp(context);

		/** 累計経験値を取得 */
		if (totalEXP > pet002ARequiredEXP && !(nowPet instanceof Pet002A)) {
			return true;
		}
		return false;
	}

}
