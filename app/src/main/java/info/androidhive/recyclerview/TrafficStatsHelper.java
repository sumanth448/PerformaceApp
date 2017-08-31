package info.androidhive.recyclerview;

import android.net.TrafficStats;

/**
 * Created by sumanth.reddy on 02/03/17.
 */
public class TrafficStatsHelper {

    public static long getAllRxBytes() {
        return TrafficStats.getTotalRxBytes();
    }

    public static long getAllTxBytes() {
        return TrafficStats.getTotalTxBytes();
    }

    public static long getAllRxBytesMobile() {
        return TrafficStats.getMobileRxBytes();
    }

    public static long getAllTxBytesMobile() {
        return TrafficStats.getMobileTxBytes();
    }

    public static long getAllRxBytesWifi() {
        return TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes();
    }

    public static long getAllTxBytesWifi() {
        return TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes();
    }

    public static long getPackageRxBytes(int uid) {
        return TrafficStats.getUidRxBytes(uid);
    }

    public static long getPackageTxBytes(int uid) {
        return TrafficStats.getUidTxBytes(uid);
    }
}
