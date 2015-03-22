package jp.egaonohon.camerapet;

import android.graphics.Canvas;

/**
 * 「更新しないといけないもの」という抽象的な形で各オブジェクトを持たせておくための抽象クラス。 
 * 端からリストに追加して、ループで回して一気に更新させる。
 * 
 * @author OtaShohei
 *
 */
public abstract class CPTask {

	public boolean onUpdate() {
		return true;
	}

	public void onDraw(Canvas c) {
	}
}
