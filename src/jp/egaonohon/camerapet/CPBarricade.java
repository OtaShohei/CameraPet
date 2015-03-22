package jp.egaonohon.camerapet;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

/**
 * ペットと餌の衝突判定用。2015-03-21現在未完成。
 * 
 * @author OtaShohei
 *
 */
public class CPBarricade extends CPTask {

	public enum eType { // 障害物のタイプ
		OUT, // あたるとアウトなタイプ
		GOAL // あたるとゴールなタイプ
	}

	protected PointF _center = new PointF(0, 0); // 図形の中心点
	protected PointF _pt[]; // 図形の頂点
	protected Paint _paint = new Paint(); // ペイント
	protected eType _type; // タイプ(当たるとアウトな壁、ゴールの壁、等)
	protected float _rotaSpeed = 0; // 回転スピード

	/**
	 * コンストラクタ。 障害物には「当たるとアウトなタイプ」「当たるとゴールなタイプ」の２種類存在します。 その識別を変数 _type
	 * に持たせます。ポイントは _pt[] です。コンストラクタで、何角形であるかという数値が渡されてきます。
	 * 渡されてきた頂点の数だけ頂点を用意します。そうすれば後は線分が全て分かりますし、配列要素分計算をループすればいいので、何角形にでも対応可能です。
	 * また、図形は回転するとか、移動するとか、色んな設定したい情報を持つことがあるでしょう。 そのような設定情報はコンストラクタの引数である BConf
	 * に設定します。CPBConfについては↓で後述。
	 * 
	 * type=タイプ、 n=頂点の数、 conf=設定情報
	 * 
	 * @param n
	 * @param conf
	 */
	public CPBarricade(int n, CPBconf conf) {
		if (conf != null) {
			_rotaSpeed = conf.speed; // 回転スピード
			_type = conf.type; // 物体のタイプ
		}
		switch (_type) {
		case OUT: // 接触してアウトな物
			_paint.setColor(Color.RED); // 赤に
			break;
		case GOAL: // 接触してゴールな物
			_paint.setColor(Color.GREEN); // 緑に
			break;
		}
		_pt = new PointF[n]; // 頂点配列を作る
		for (int i = 0; i < n; i++) {
			_pt[i] = new PointF(); // 頂点を作る
		}
	}

	// 更新する
	@Override
	public boolean onUpdate() {
		if (_rotaSpeed != 0) { // 回転するなら
			CPDiagramCalcr.RotateDiagram(_pt, _center, _rotaSpeed); // 頂点リスト(_pt)を_centerを中心に回転する
		}
		return true;
	}

	/** 接触しているかを問う。円cirが接触していれば接触した線分をvecに格納し、物体のタイプを返す。接触していなければNOを返す */
	public CPDef.eHitCode isHit(final Circle cir, CPVec vec) {
		if (CPDiagramCalcr.Collision(_pt, cir, vec) == true) {// 頂点群_ptが示す各辺と円cirが接触していればベクトルをvecに入れてtrueを返す
			switch (_type) {
			case OUT:// アウトな線なら
				return CPDef.eHitCode.OUT;
			case GOAL:// ゴールな線なら
				return CPDef.eHitCode.GOAL;
			}
		}
		return CPDef.eHitCode.NO; // 何も接触していない
	}

	// 描画する
	@Override
	public void onDraw(Canvas c) {
		if (_pt.length < 1) { // 頂点が1未満なんて図形はありえない
			return;
		}
		Path path = new Path();
		path.moveTo(_pt[0].x, _pt[0].y); // パスの初期位置をセット
		for (int i = 0; i < _pt.length; i++) {
			path.lineTo(_pt[i].x, _pt[i].y); // 頂点の位置へラインを引いていく
		}
		c.drawPath(path, _paint); // 引いたラインを描画する
	}
}
