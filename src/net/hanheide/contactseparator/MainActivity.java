//Google Key 0gphtpAWzrMtB92ZJTy-wBhNq2lBWVrfgm8IzyQ
package net.hanheide.contactseparator;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.AggregationExceptions;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements LocationListener {
	protected static final String LOGTAG = "ContactSeparator";
	Date startTime, endTime;
	Button start;
	TextView status, addr;
	String provider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Assign interface components
		start = (Button) findViewById(R.id.startJob);
		status = (TextView) findViewById(R.id.status);
		addr = (TextView) findViewById(R.id.location);

		// Start Job
		start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				separate_merged_contacts();
			}
		});

	}

	private void separate_merged_contacts(){
		MainActivity obj = this;
		status.setText("separating contacts...");
		Cursor cur1 = obj.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,new String[]{"_id"} , null, null,null);
		Cursor cur_raw;
		ArrayList<String> raw_contact_id = new ArrayList<String>();
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		while (cur1.moveToNext()) {
			raw_contact_id.clear();
			ops.clear();
			for (int c = 0; c < cur1.getColumnCount(); c++) {
				cur_raw = obj.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, new String[]{ContactsContract.RawContacts._ID}, ContactsContract.RawContacts.CONTACT_ID+"=?",new String[]{cur1.getString(cur1.getColumnIndex(ContactsContract.Contacts._ID))} , null);
				while(cur_raw.moveToNext()){
					for (int j = 0; j < cur_raw.getColumnCount(); j++) {
						raw_contact_id.add(cur_raw.getString(cur_raw.getColumnIndexOrThrow(ContactsContract.RawContacts._ID)));
					}
				}
				for(int i=0 ; i<raw_contact_id.size();i++){
					for(int j=0;j<raw_contact_id.size();j++)
						ops.add(ContentProviderOperation.newUpdate(ContactsContract.AggregationExceptions.CONTENT_URI)
								.withValue(AggregationExceptions.TYPE,AggregationExceptions.TYPE_KEEP_SEPARATE)
								.withValue(AggregationExceptions.RAW_CONTACT_ID1,Integer.parseInt(raw_contact_id.get(i)))
								.withValue(AggregationExceptions.RAW_CONTACT_ID2,Integer.parseInt(raw_contact_id.get(j))).build());
					try {
						obj.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		status.setText("finished");
	}

	/*
	 * @Override protected void onResume() { super.onResume();
	 * loc.requestLocationUpdates(provider, 500, 1, this); }
	 * 
	 * @Override protected void onPause() { super.onPause();
	 * loc.removeUpdates(this); }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
