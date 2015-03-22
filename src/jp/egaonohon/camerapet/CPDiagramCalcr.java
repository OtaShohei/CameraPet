package jp.egaonohon.camerapet;

import android.graphics.PointF;

/**
 * 回転させる系メソッドとあたり判定を計算する系メソッド群を持つクラス。 いわゆるstatic utilsクラス。
 * 
 * @author OtaShohei
 *
 */
public class CPDiagramCalcr {

	/** centerを中心に角度ang、頂点群ptを回転する */
	public static void RotateDiagram(PointF pt[], final PointF center,
			final float ang) {
	}

	/** rotaPtを中心に角度ang、origPtを回転する */
	public static void RotatePt(PointF rotaPt, final PointF origPt,
			final float ang) {
	}

	/** 頂点群ptの線分とcirが接触していたらその接触しているベクトルをverに格納して返す */
	public static boolean Collision(PointF pt[], final Circle cir, CPVec vec) {
		return false;
	}

	/** 線分EsaとPet（円cir）が当たっていればtrueを返す */
	public static boolean CollisionLC(CPEsa esa, Circle cir) {
		return false;
	}

//	public static boolean isHit(PointF[] _pt, Circle cir, CPVec vec) {
//		// TODO 自動生成されたメソッド・スタブ
//		return false;
//	}
}