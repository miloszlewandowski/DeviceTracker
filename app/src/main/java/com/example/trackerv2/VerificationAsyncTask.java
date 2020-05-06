package com.example.trackerv2;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;



public class VerificationAsyncTask extends AsyncTask {

	private Context context;
	private ProgressDialog dialogPrompt;
	private LocationManager locationManager;
	private ConnectivityManager connectivityManager;
	private Runnable runnable;


	//comment

	public VerificationAsyncTask(Context context, LocationManager locationManager, ConnectivityManager connectivityManager, Runnable runnable) {

		this.context = context;
		dialogPrompt = new ProgressDialog(context);
		dialogPrompt.setMessage("Please enable GPS and WiFi/Mobile Data network");
		dialogPrompt.setIndeterminate(false);
		dialogPrompt.setCancelable(false);
		this.locationManager = locationManager;
		this.runnable = runnable;
		this.connectivityManager = connectivityManager;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialogPrompt.show();
	}

	@Override
	protected Object doInBackground(Object[] objects) {
		while (true) {

			boolean isLocationProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			boolean isNetworkEnabled = false;

			try {
				if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE || activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					isNetworkEnabled = true;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			if (isLocationProviderEnabled && isNetworkEnabled) { return null; }
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);
		this.dialogPrompt.cancel();
		this.runnable.run();
	}



}
