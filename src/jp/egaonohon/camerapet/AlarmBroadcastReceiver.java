package jp.egaonohon.camerapet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

/**
 * AlarmManagerとNotificationManagerを用いてペットから定期的におなかがすいた旨ユーザーに通知を出すクラス。
 *
 * 参考 http://blog.asial.co.jp/1275
 * http://droibit.blogspot.jp/2012/06/notification.html
 *
 * @author OtaShohei
 *
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

	private Context alarmReceiverContext;
	private int notificationProvisionalId;
	/** Logのタグを定数で確保 */
	private static final String TAG = "PetAlarmBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent receivedIntent) {
		CameLog.setLog(TAG, "onReceiveやってきました!");
		alarmReceiverContext = context;

		notificationProvisionalId = receivedIntent.getIntExtra(
				"notificationId", 0);
		NotificationManager myNotification = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = prepareNotification();
		myNotification.notify(notificationProvisionalId, notification);
	}

	private Notification prepareNotification() {
		CameLog.setLog(TAG, "prepareNotification、動作!");
		/** タイトル用のひな形の文字列を取得する */
		Resources res = alarmReceiverContext.getResources();
		String hinagataTxt = res.getString(R.string.alarm_sign);
		/** 本文用の文字列を取得する */
		String bodyMsg01 = res.getString(R.string.alarm_body_txt01);
		String bodyMsg01b = res.getString(R.string.alarm_body_txt01b);
		String bodyMsg02 = res.getString(R.string.alarm_body_txt02);
		String bodyMsg03 = res.getString(R.string.alarm_body_txt03);
		String fromCamerapet = res.getString(R.string.from_camerapet);
		/** 最後に起動していた時のペット種別名を取得する */
		String SpeciesName = CamPePref.loadPetSpeciesName(alarmReceiverContext);
		CameLog.setLog(TAG, "取得したペット名は" + SpeciesName);

		Intent bootIntent = new Intent(alarmReceiverContext, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				alarmReceiverContext, 0, bootIntent, 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				alarmReceiverContext);
		/** タイトル表示時の画像をセット。 */
		builder.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(bodyMsg01 + bodyMsg02 + SpeciesName + bodyMsg03)
				.setContentTitle(SpeciesName + hinagataTxt)
				/** 通知のタイトル下の文字列 */
				.setContentText(bodyMsg01)
				.setWhen(System.currentTimeMillis()).setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_SOUND)
				.setContentIntent(contentIntent);

		/**
		 * InboxStyle。
		 * 詳しくは
		 * http://dev.classmethod.jp/smartphone/android/android-tips-23-android4-1-notification-style/
		 */
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(builder);
		inboxStyle.setBigContentTitle(SpeciesName + hinagataTxt);
		inboxStyle.addLine(" ");
		inboxStyle.addLine(bodyMsg01);
		inboxStyle.addLine(bodyMsg01b);
		inboxStyle.addLine("/// " + bodyMsg02);
		inboxStyle.addLine("/// " + SpeciesName + bodyMsg03);
		inboxStyle.addLine(" ");
		inboxStyle.setSummaryText(fromCamerapet);
		return inboxStyle.build();
	}
}
