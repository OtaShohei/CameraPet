package jp.egaonohon.camerapet;

import java.util.HashMap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

/**
 * Google Analyticsを管理するクラス。詳しくは、
 * http://qiita.com/chonbo2525/items/bbc55d728f8e1b8dca39
 * https://teratail.com/questions/6619
 * 
 * @author OtaShohei
 *
 */
public class App extends Application {

	private static final String PROPERTY_ID = "UA-61693929-1";

	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg:
						// roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a
							// company.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
					.newTracker(PROPERTY_ID)
					: (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
							.newTracker(R.xml.global_tracker) : analytics
							.newTracker(R.xml.ecommerce_tracker);
			t.enableAdvertisingIdCollection(true);
			mTrackers.put(trackerId, t);

		}
		return mTrackers.get(trackerId);
	}
}