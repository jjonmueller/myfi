
package com.vision.works;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class MyFi extends Activity {


	WifiManager mainWifi;

	ListView mainListView;
	mItems[] itemss;
	ArrayAdapter<mItems> listAdapter;
	ArrayList<String> checked = new ArrayList<String>();
	StringBuilder sb = new StringBuilder();
	ArrayList<mItems> wifiList = new ArrayList<mItems>();
	Context mCtx = this;

	WifiConfiguration wc = new WifiConfiguration();
	private final Handler handler = new Handler();


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Find the ListView resource.
		mainListView = (ListView) findViewById(R.id.mainListView);


		mainListView
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item,
					int position, long id) {
				Log.d("MYFI", " " +  position + " " + id);
				mItems wifi = listAdapter.getItem(position);
				wifi.toggleChecked();
				SelectViewHolder viewHolder = (SelectViewHolder) item
						.getTag();
				viewHolder.getCheckBox().setChecked(wifi.isChecked());

			}
		});


		// Create and populate wifi list.
		itemss = (mItems[]) getLastNonConfigurationInstance();	


		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if(mainWifi.isWifiEnabled()==false) {
			mainWifi.setWifiEnabled(true);
		}


		sb = new StringBuilder();
		List<ScanResult> wifiListScan = mainWifi.getScanResults();


		List<WifiConfiguration> configs = mainWifi.getConfiguredNetworks();
		for (WifiConfiguration config : configs) {

			wifiList.add(new mItems(config.SSID));
			mainWifi.removeNetwork(config.networkId);
			///Make sure to do this otherwise we remember networks 
			mainWifi.saveConfiguration();
			//Log.i("MYFI", "Removed Network: SSID=[" + config.SSID + "] and ID=[" + config.networkId + "]");

		}

		listAdapter = new SelectArralAdapter(mCtx, wifiList);
		mainListView.setBackgroundColor(Color.GRAY);
		mainListView.setAdapter(listAdapter);


	}


	@Override
	protected void onPause()
	{	
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, Menu.NONE, "Clear Networks");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Log.d("MYFI", " " +  item.toString());
		return super.onOptionsItemSelected(item);
	}

	/** Hold wifi data. */
	private static class mItems {
		private String name = "";
		private boolean checked = true;

		public mItems() {
		}

		public mItems(String name) {
			this.name = name;
		}

		public mItems(String name, boolean checked) {
			this.name = name;
			this.checked = checked;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public String toString() {
			return name;
		}

		public void toggleChecked() {
			checked = !checked;
		}
	}

	/** Holds child views for one row. */
	private static class SelectViewHolder {
		private CheckBox checkBox;
		private TextView textView;

		public SelectViewHolder() {
		}

		public SelectViewHolder(TextView textView, CheckBox checkBox) {
			this.checkBox = checkBox;
			this.textView = textView;
		}

		public CheckBox getCheckBox() {
			return checkBox;
		}

		public void setCheckBox(CheckBox checkBox) {
			this.checkBox = checkBox;
		}

		public TextView getTextView() {
			return textView;
		}

		public void setTextView(TextView textView) {
			this.textView = textView;
		}
	}

	/** Custom adapter for displaying an array of wifi objects. */
	private static class SelectArralAdapter extends ArrayAdapter<mItems> {
		private LayoutInflater inflater;

		public SelectArralAdapter(Context context, List<mItems> wifiList) {
			super(context, R.layout.simplerow, R.id.rowTextView, wifiList);
			// Cache the LayoutInflate to avoid asking for a new one each time.
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// wifi to display
			mItems wifi = (mItems) this.getItem(position);

			// The child views in each row.
			CheckBox checkBox;
			TextView textView;

			// Create a new row view
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.simplerow, null);

				// Find the child views.
				textView = (TextView) convertView
						.findViewById(R.id.rowTextView);
				checkBox = (CheckBox) convertView.findViewById(R.id.CheckBox01);
				// Optimization: Tag the row with it's child views, so we don't
				// have to
				// call findViewById() later when we reuse the row.
				convertView.setTag(new SelectViewHolder(textView, checkBox));
				// If CheckBox is toggled, update the wifi it is tagged with.
				checkBox.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						mItems wifi = (mItems) cb.getTag();
						wifi.setChecked(cb.isChecked());
					}
				});
			}
			// Reuse existing row view
			else {
				// Because we use a ViewHolder, we avoid having to call
				// findViewById().
				SelectViewHolder viewHolder = (SelectViewHolder) convertView
						.getTag();
				checkBox = viewHolder.getCheckBox();
				textView = viewHolder.getTextView();
			}

			// Tag the CheckBox with the wifi it is displaying, so that we can
			// access the wifi in onClick() when the CheckBox is toggled.
			checkBox.setTag(wifi);
			// Display wifi data
			checkBox.setChecked(wifi.isChecked());
			textView.setText(wifi.getName());
			return convertView;
		}
	}

	public Object onRetainNonConfigurationInstance() {
		return itemss;
	}
}