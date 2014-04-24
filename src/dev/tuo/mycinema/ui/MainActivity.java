package dev.tuo.mycinema.ui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Shader.TileMode;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import dev.tuo.mycinema.R;
import dev.tuo.mycinema.adapter.ServerAdapter;

public class MainActivity extends Activity {
	EditText serverIpText;
	ListView serverListView;
	Button scanButton;
	ServerAdapter adapter;
	List<String> host = new ArrayList<String>();
	ExecutorService executor = Executors.newCachedThreadPool();
	Activity ctx = MainActivity.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}

	private Handler mHandler = new Handler() {
	};
	private Handler scanHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			String s = (String) msg.obj;
			host.add(s);
			adapter.notifyDataSetChanged();
			scanButton.setEnabled(true);
			setProgressBarIndeterminateVisibility(false);
		}

	};

	void initView() {
		serverIpText = (EditText) findViewById(R.id.etv_server_ip);
		serverListView = (ListView) findViewById(R.id.lv_server);
		adapter = new ServerAdapter(ctx, host);
		serverListView.setAdapter(adapter);

		findViewById(R.id.btn_connect).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final String ip = serverIpText.getText().toString();

				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						try {
							InetAddress serverAddr = InetAddress.getByName(ip);
							@SuppressWarnings("resource")
							Socket socket = new Socket(serverAddr, 8848);
							if (socket.isConnected()) {
								scanHandler.obtainMessage(0, -1, -1, ip).sendToTarget();
							}
							socket.close();
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, 1000);

			}
		});
		scanButton = (Button) findViewById(R.id.btn_scan);
		scanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setEnabled(false);
				setProgressBarIndeterminateVisibility(true);
				host.clear();
				adapter.notifyDataSetChanged();
				scanHandler.postDelayed(new ScanRunnable(), 1000);
			}
		});

	}

	class ScanRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String ip;
			try {
				ip = getWiFiIpAddress();
				String ipHead = ip.substring(0, ip.lastIndexOf('.') + 1);
				for (int tail = 60; tail < 65; tail++) {
					String remote = ipHead + tail + "";
					if (!remote.equals(ip)) {
						executor.execute(new PingRunnable(remote));
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	class PingRunnable implements Runnable {
		private Handler pingHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					String ip = (String) msg.obj;
					InetAddress serverAddr = InetAddress.getByName(ip);
					@SuppressWarnings("resource")
					Socket socket = new Socket(serverAddr, 8848);
					if (socket.isConnected()) {
						scanHandler.obtainMessage(0, -1, -1, ip).sendToTarget();
					}
					socket.close();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		String ip;

		public PingRunnable(String ip) {
			this.ip = ip;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String ping = "ping -c 1 -w 0.5 ";
			Process proc = null;
			Runtime run = Runtime.getRuntime();
			String p = ping + ip;
			try {
				proc = run.exec(p);
				int result = proc.waitFor();
				if (result == 0) {
					pingHandler.obtainMessage(0, -1, -1, ip).sendToTarget();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public String getWiFiIpAddress() {
		WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		String ipAddress = Formatter.formatIpAddress(ip);
		return ipAddress;
	}
}
