package jp.egaonohon.camerapet;

/**
 * 図形に回転や移動などを設定。そうした設定情報はこのクラスを用いる。
 * CPBarricadeのコンストラクタ引数として利用。
 * @author OtaShohei
 *
 */
public class CPBconf {
	public float speed = (float) Math.PI / 180;
	public CPBarricade.eType type = CPBarricade.eType.OUT;

	/**
	 * 物体の種類を引数とするコンストラクタ。
	 * @param atype
	 */
	public CPBconf(CPBarricade.eType atype) {
		type = atype;
	}

	/**
	 * 物体の回転スピードを引数とするコンストラクタ。
	 * @param aspeed
	 */
	public CPBconf(float aspeed) {
		speed = aspeed;
	}
}
