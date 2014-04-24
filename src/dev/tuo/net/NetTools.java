package dev.tuo.net;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetTools {
	public static List<String> getIp(Context c) {
		final List<String> ipAddressList = new ArrayList<String>();
		final WifiManager mWifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		final WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		mWifiManager.getConfiguredNetworks();
		List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration wifiConfiguration : list) {
			String ssid = wifiConfiguration.SSID;
			String mSsid = ssid.substring(1, ssid.length() - 1);
			Log.d("Tag", mSsid + "=" + wifiInfo.getSSID());
			String result = wifiConfiguration.toString();
			if (mSsid.equalsIgnoreCase(wifiInfo.getSSID())) {
				Log.d("Tag", wifiConfiguration.toString());
				try {
					int i = result.indexOf("LinkAddresses");
					int i1 = result.indexOf("Routes");
					String ipAddress = result.substring(i, i1).trim().substring(16, result.substring(i, i1).length() - 6);
					ipAddressList.add(ipAddress);
					Log.d("Tag", ipAddress);
					int i2 = result.indexOf("DnsAddresses");
					String mWifiIntAddress = result.substring(i2).trim().substring(15, result.substring(i2).length() - 4);
					Log.d("Tag", "WifiInetAddresses   " + mWifiIntAddress);
					ipAddressList.add(mWifiIntAddress);

				} catch (Exception e) {
					Log.e("Tag", "erro" + e);
				}
			}
		}

		return ipAddressList;

	}
}
