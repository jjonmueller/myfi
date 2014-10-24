
package com.vision.works;

import java.util.List;
import android.util.Log;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.net.wifi.WifiConfiguration;

public class MyFi extends Activity {

	mItems[] items;
	Context mCtx = this;
	WifiManager mainWifi;
	ListView mainListView;	
	ArrayAdapter<mItems> listAdapter;
	ArrayList<String> checked = new ArrayList<String>();	
	ArrayList<mItems> wifiList = new ArrayList<mItems>();
	WifiConfiguration wc = new WifiConfiguration();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setTitle(R.string.app_name);
			actionBar.setHomeButtonEnabled(true);
			actionBar.show();
		}

		Log.d("MYFI", "onCreate");
		// Find the ListView resource.
		mainListView = (ListView) findViewById(R.id.mainListView);


		mainListView
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item,
					int position, long id) {
				mItems wifi = listAdapter.getItem(position);
				wifi.toggleChecked();
				SelectViewHolder viewHolder = (SelectViewHolder) item
						.getTag();
				viewHolder.getCheckBox().setChecked(wifi.isChecked());

			}
		});


		// Create and populate wifi list.
		items = (mItems[]) getLastNonConfigurationInstance();	


		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if(mainWifi.isWifiEnabled()==false) {
			mainWifi.setWifiEnabled(true);
		}

		List<WifiConfiguration> configs = mainWifi.getConfiguredNetworks();
		for (WifiConfiguration config : configs) {
			wifiList.add(new mItems(config.SSID, config.networkId, true));
		}

		listAdapter = new SelectArrayAdapter(mCtx, wifiList);
		mainListView.setBackgroundColor(Color.BLACK);
		mainListView.setAdapter(listAdapter);
	
	}


	@Override
	protected void onPause() {	
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//menu.add(0, 1, Menu.NONE, "Remove selected networks");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.discard, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int count = listAdapter.getCount();
		Log.d("MYFI", String.valueOf(count));
		switch(item.getItemId()) {
		case R.id.myfi_discard:
            for (int i = 0; i < count; i++) {
        	    Log.d("MYFI", String.valueOf(i));
        	    mItems wifi = listAdapter.getItem(i);
			    if (wifi.isChecked()) {
				    Log.d("MYFI", wifi.getName());
				    mainWifi.removeNetwork(wifi.getNetworkId());
		            mainWifi.saveConfiguration();
			    }
            }
        
            Intent refresh = new Intent(mCtx, MyFi.class);
            Log.d("MYFI", "refresh");
            startActivity(refresh);
            this.finish();
		    break;
		}
        return true;
	}

	/** Hold wifi data. */
	private static class mItems {
		private String name = "";
		private boolean checked = true;
		private int networkId = -1;

		public mItems(String name, int networkId, boolean checked) {
			this.name = name;
			this.networkId = networkId;
			this.checked = checked;
		}
		
		public String getName() {
			return this.name;
		}

		public boolean isChecked() {
			return this.checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public String toString() {
			return name;
		}

		public void toggleChecked() {
			this.checked = !this.checked;
		}
		
		public int getNetworkId() {
			return this.networkId;
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
	private static class SelectArrayAdapter extends ArrayAdapter<mItems> {
		private LayoutInflater inflater;

		public SelectArrayAdapter(Context context, List<mItems> wifiList) {
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
			// access the Wi-Fi in onClick() when the CheckBox is toggled.
			checkBox.setTag(wifi);
			// Display Wi-Fi data
			checkBox.setChecked(wifi.isChecked());
			textView.setText(wifi.getName());
			return convertView;
		}
	}

	public Object onRetainNonConfigurationInstance() {
		return items;
	}
}