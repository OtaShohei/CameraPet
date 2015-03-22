package jp.egaonohon.camerapet;

/**
 * アプリでの物体移動をベクトルで表現し計算。ベクトルを計算するクラス。
 * @author OtaShohei
 *
 */
public class CPVec {

	public float _x, _y;

	/**
	 * コンストラクタ
	 */
	CPVec() {
		_x = _y = 0;
	}

	CPVec(float x, float y) {
		_x = x;
		_y = y;
	}

	/**
	 *  角度を取得する
	 * @return
	 */
	float getAngle() {
		return (float) Math.atan2(_y, _x);
	}

	/**
	 *  大きさを取得する
	 * @return
	 */
	float getLength() {
		return (float) Math.sqrt(_x * _x + _y * _y);
	}
}