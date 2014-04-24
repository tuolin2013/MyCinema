package dev.tuo.mycinema.adapter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import dev.tuo.mycinema.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ServerAdapter extends ArrayAdapter<String> {

	private final Activity context;
	private final List<String> names;

	static class ViewHolder {
		public TextView ip_text;
		public TextView media_text;
		public ImageView image;
		public ImageView play_image;
	}

	public ServerAdapter(Activity context, List<String> names) {
		super(context, R.layout.server_list_item, names);
		this.context = context;
		this.names = names;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// reuse views
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.server_list_item, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.ip_text = (TextView) rowView.findViewById(R.id.tv_server_ip);
			viewHolder.image = (ImageView) rowView.findViewById(R.id.imageView1);
			viewHolder.play_image = (ImageView) rowView.findViewById(R.id.iv_play);
			final String ip = names.get(position);
			viewHolder.play_image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {

						InetAddress serverAddr = InetAddress.getByName(ip);
						@SuppressWarnings("resource")
						Socket socket = new Socket(serverAddr, 8848);
						if (socket.isConnected()) {
							PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
							out.println("play");
							out.flush();
							// 把用户输入的内容发送给server
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
			});
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		String s = names.get(position);
		holder.ip_text.setText(s);
		holder.image.setImageResource(R.drawable.wifi);

		return rowView;
	}

}
