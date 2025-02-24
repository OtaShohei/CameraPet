package jp.egaonohon.camerapet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class PetAlarmBroadcastReceiver {

	/**
	 * Notificationを出したい間隔。デフォルトは4日後の345600000。開発中のテストでは30秒後の60000。
	 * 本番時には、正規の数字に必ず変更すること。
	 */
	private static final long NOTIFICATION_INTERVAL_TIME = 345600000;
	
	/** Logのタグを定数で確保 */
	private static final String TAG = "PetAlarmBroadcastReceiver";
	
	/**
	 * 引数一つ用のメソッドも用意
	 * @param context
	 */
	public static void set(Context context) {
	set(context, NOTIFICATION_INTERVAL_TIME);
	}

	/**
	 * アプリをしばらく起動していないと、「おなかがすいた！」とペットがユーザーに4日後に通知を出すメソッド。
	 * PetAlarmBroadcastReceiverクラスと連携する。
	 */
	public static void set(Context context, long notification_interval_time) {

		// TimePicker tPicker;
		int notificationId = 0;
		PendingIntent alarmIntent;

		/** 現在時間を取得 */
		long now = System.currentTimeMillis();
		/** テスト時は、30秒後にalert */
		long fourDayAfter = now + notification_interval_time;
		// /** 4日後を割り出す */
		// long fourDayAfter = now + NOTIFICATION_INTERVAL_TIME;

		/** 4日後の20時台を割り出すコードは一時保留 */
		// /** 4日後を持つDateインスタンス生成 */
		// Date fourDayAfterDate = new Date(fourDayAfter);
		// /** Dateクラスから日時のString型文字列生成 */
		// SimpleDateFormat format = new SimpleDateFormat("MM_dd__HH__mm");
		// String s = format.format(fourDayAfterDate);
		// String kiridasi = s.substring(6,8);
		// /** 候補時間をint型に変換 */
		// int kouhoTime = new Integer(kiridasi).intValue();
		// String henkanTimeStr = s.replaceAll("__HH__","sasuke");

		Intent bootIntent = new Intent(context,
				AlarmBroadcastReceiver.class);
		bootIntent.putExtra("notificationId", notificationId);
		alarmIntent = PendingIntent.getBroadcast(context, 0,
				bootIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		alarm.set(AlarmManager.RTC_WAKEUP, fourDayAfter, alarmIntent);
		CameLog.setLog(TAG, "1分後" + fourDayAfter + "に通知セット完了");
	}

}
