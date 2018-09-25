package com.example.devicesignaturegenerator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import com.example.checker.MyLocationChecker;
import com.example.checker.ScreenDisplay;
import android.support.v7.app.ActionBarActivity;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Size;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {
	LocationManager locationManager;
	WifiManager wifiManager;
	WifiInfo wifiInfo;
	TelephonyManager telephonyManager;
	CellInfoGsm cellinfogsm;
	CellInfo cellInfo;
	CellSignalStrengthGsm cellSignalStrengthGsm;
	CameraManager cameraManager;
	DhcpInfo dhcpInfo;
	deviceSignature mySignature;
	LocationManager mLocationManager;
	Location myLocationl;
	Geocoder geocoder;
	List<Address> addresses;
	String cityName;
	EditText mEdtTxtSgn;
	EditText mEdtTxtLctn;
	Button btnSndDta;
	String data;
	float megaPixel = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		mySignature = new deviceSignature();
		mySignature.screenDisplay = new ScreenDisplay();
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		wifiInfo = wifiManager.getConnectionInfo();
		cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
		dhcpInfo = wifiManager.getDhcpInfo();
		getDeviceLocation();
		getIPAddress();
		getRSSI_WiFi();
		getMacAddress();
		getBSSID();
		getWiFiLinkSpeed();
		getNetworkID();
		getSSID();
		getIMEI();
		getWiFiFrequency(); // in MHz
		getdevicetoAPRTT();
		getTDLSValue();
		getGroupCipherValue();
		getCameraInfo();
		getGatewayAddress();
		getOperatingSystem();
		getAllCellInfo();
		getWiFiSignalLevel();
		getDeviceID();
		getScreenDisplay();
		writeToFile();
		mEdtTxtSgn = (EditText) findViewById(R.id.edtTxtDvcSgntr);
		mEdtTxtSgn.setText(data);
		mEdtTxtLctn = (EditText) findViewById(R.id.edtTxtLocation);
		mEdtTxtLctn.setText(cityName);
		btnSndDta = (Button) findViewById(R.id.sndData);
		btnSndDta.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				emailIntent.setType("text/plain");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { "umesh554@gmail.com" });
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"My Device Signature");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, data);
				emailIntent.setType("message/rfc822");
				try {
					startActivity(Intent.createChooser(emailIntent,
							"Send email using..."));
				} catch (android.content.ActivityNotFoundException ex) {

				}
			}
		});
	}

	private void getScreenDisplay() {
		// TODO Auto-generated method stub
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		mySignature.screenDisplay.setData(size.x, size.y);
	}

	private void getDeviceID() {
		mySignature.deviceID = Secure.getString(getApplicationContext()
				.getContentResolver(), Secure.ANDROID_ID);
	}

	private void getWiFiSignalLevel() {
		// TODO Auto-generated method stub
		int x = (int) mySignature.rssi_WiFi;
		// Integer.valueOf(mySignature.rssi_WiFi);
		mySignature.wifiSignalLevel = WifiManager.calculateSignalLevel(x, 5);
	}

	private void getAllCellInfo() {
		// TODO Auto-generated method stub
		List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
		if (cellInfos != null) {
			for (int i = 0; i < cellInfos.size(); i++) {
				if (cellInfos.get(i).isRegistered()) {
					if (cellInfos.get(i) instanceof CellInfoWcdma) {
						CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) telephonyManager
								.getAllCellInfo().get(0);
						CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma
								.getCellSignalStrength();
						mySignature.rssi_Cell = String
								.valueOf(cellSignalStrengthWcdma.getDbm());
					} else if (cellInfos.get(i) instanceof CellInfoGsm) {
						CellInfoGsm cellInfogsm = (CellInfoGsm) telephonyManager
								.getAllCellInfo().get(0);
						CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm
								.getCellSignalStrength();
						mySignature.rssi_Cell = String
								.valueOf(cellSignalStrengthGsm.getDbm());
					} else if (cellInfos.get(i) instanceof CellInfoLte) {
						CellInfoLte cellInfoLte = (CellInfoLte) telephonyManager
								.getAllCellInfo().get(0);
						CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte
								.getCellSignalStrength();
						mySignature.rssi_Cell = String
								.valueOf(cellSignalStrengthLte.getDbm());
					}
				}
			}
		}
	}

	private void writeToFile() {
		// TODO Auto-generated method stub

		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					openFileOutput("config.txt", Context.MODE_APPEND));
			data = "Latitude=" + Double.toString(mySignature.latitude)
					+ " Longitude=" + Double.toString(mySignature.longitude)
					+ " IP Address=" + mySignature.ipAddress + " Mac add="
					+ mySignature.macAddress + " BSSID=" + mySignature.BSSID
					+ "Link speed wifi= "
					+ Integer.toString(mySignature.linkSpeed_WiFi) + "RSSI="
					+ Double.toString(mySignature.rssi_WiFi)
					+ mySignature.rssi_Cell + "Network ID="
					+ Integer.toString(mySignature.networkID_WiFi)
					+ "SSID WiFi=" + mySignature.SSID_WiFi + " deviceID="
					+ mySignature.deviceID + "WiFi Frequency="
					+ Integer.toString(mySignature.wifiFrequency)
					+ " Center Frequency0"
					+ Integer.toString(mySignature.centerFrequency0)
					+ " Center Frequency1="
					+ Integer.toString(mySignature.centerFrequency1)
					+ " Capabilities=" + mySignature.capabilities
					+ "Venue Name=" + mySignature.venueName + " devicetoAPRTT="
					+ Boolean.toString(mySignature.devicetoAPRTT)
					+ " tdlsSupported="
					+ Boolean.toString(mySignature.tdlsSupported)
					+ " ccmpSupport="
					+ Integer.toString(mySignature.ccmpSupport)
					+ "tkipSupport="
					+ Integer.toString(mySignature.tkipSupport)
					+ "camera characteristics= " + mySignature.maxResolution
					+ " gateWayAddress=" + mySignature.gatewayAddress
					+ "operatingsystemname=" + mySignature.operatingSystemName
					+ "operatingSystemVersion= "
					+ Integer.toString(mySignature.operatingSystemVersion)
					+ " WiFisigLevel=" + mySignature.wifiSignalLevel
					+ "deviceID=" + mySignature.deviceID + " imei="
					+ mySignature.imei + "screen resol=" + ""
					+ mySignature.screenDisplay.getData();
			Log.i("mySign", data);
			Log.d("mySign", data);
			Log.i("mySign", data);
			// outputStreamWriter.write(data);
			outputStreamWriter.close();

		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	private void getOperatingSystem() {
		// TODO Auto-generated method stub
		mySignature.operatingSystemName = android.os.Build.VERSION_CODES.class
				.getFields()[android.os.Build.VERSION.SDK_INT].getName();
		mySignature.operatingSystemVersion = android.os.Build.VERSION.SDK_INT;

	}

	private void getGatewayAddress() {
		// TODO Auto-generated method stub
		mySignature.gatewayAddress = mySignature.intToIp(dhcpInfo.gateway);
	}

	private void getGroupCipherValue() {
		// TODO Auto-generated method stub
		mySignature.ccmpSupport = WifiConfiguration.GroupCipher.CCMP;
		mySignature.tkipSupport = WifiConfiguration.GroupCipher.TKIP;
	}

	private void getTDLSValue() {
		// TODO Auto-generated method stub
		mySignature.tdlsSupported = wifiManager.isTdlsSupported();
	}

	private void getdevicetoAPRTT() {
		// TODO Auto-generated method stub
		mySignature.devicetoAPRTT = wifiManager.isDeviceToApRttSupported();
	}

	private void getWiFiFrequency() {
		// TODO Auto-generated method stub

		mySignature.wifiFrequency = wifiInfo.getFrequency();

		int channel_number = 0;
		if (mySignature.wifiFrequency == 2484) {
			channel_number = 14;
			Log.i("channel_Number=", "" + channel_number);
			return;
		}
		if (mySignature.wifiFrequency < 2484) {
			channel_number = (mySignature.wifiFrequency - 2407) / 5;
			Log.i("channel_Number=", "" + channel_number);
			return;
		}
		channel_number = mySignature.wifiFrequency / 5 - 1000;
		Log.i("channel_Number=", "" + channel_number);

	}

	private void getIMEI() {
		// TODO Auto-generated method stub
		// telephonyManager.getDeviceId();
		mySignature.imei = telephonyManager.getDeviceId(0);
		// String imeiSIM1 = telephonyInfo.getImsiSIM1();

	}

	private void getSSID() {
		// TODO Auto-generated method stub
		mySignature.SSID_WiFi = wifiInfo.getSSID();
	}

	private void getNetworkID() {
		// TODO Auto-generated method stub
		mySignature.networkID_WiFi = wifiInfo.getNetworkId();
	}

	private void getWiFiLinkSpeed() {
		// TODO Auto-generated method stub
		mySignature.linkSpeed_WiFi = wifiInfo.getLinkSpeed();
	}

	private void getBSSID() {
		// TODO Auto-generated method stub
		mySignature.BSSID = wifiInfo.getBSSID();
	}

	private void getMacAddress() {
		// TODO Auto-generated method stub
		mySignature.macAddress = wifiInfo.getMacAddress();

		try {
			List<NetworkInterface> all = Collections.list(NetworkInterface
					.getNetworkInterfaces());
			for (NetworkInterface nif : all) {
				if (!nif.getName().equalsIgnoreCase("wlan0"))
					continue;

				byte[] macBytes = nif.getHardwareAddress();
				if (macBytes == null) {
					// return "";
				}

				StringBuilder res1 = new StringBuilder();
				for (byte b : macBytes) {
					res1.append(String.format("%02X:", b));
				}

				if (res1.length() > 0) {
					res1.deleteCharAt(res1.length() - 1);
				}
				mySignature.macAddress = res1.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.i("Exception in mac address ", "");
			mySignature.macAddress = "02:00:00:00:00:00";
		}
	}

	private void getRSSI_WiFi() {
		// TODO Auto-generated method stub
		mySignature.rssi_WiFi = wifiManager.getConnectionInfo().getRssi();

	}

	private void getIPAddress() {
		// TODO Auto-generated method stub
		int ipAddress = wifiInfo.getIpAddress();
		String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),
				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));
		mySignature.ipAddress = ip;
	}

	static class CompareSizesByArea implements Comparator<Size> {

		@Override
		public int compare(Size lhs, Size rhs) {
			// We cast here to ensure the multiplications won't overflow
			return Long.signum((long) lhs.getWidth() * lhs.getHeight()
					- (long) rhs.getWidth() * rhs.getHeight());
		}

	}

	private void getCameraInfo() {

		CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		long pixelCount = -1;
		long tempPixelCount = -1;
		Size largest = new Size(0, 0);

		// String cID;
		// try {
		// cID = cameraManager.getCameraIdList()[1];
		// CameraCharacteristics cameraCharacteristics = cameraManager
		// .getCameraCharacteristics(cID);
		/*
		 * android.util.Size[] s = cameraCharacteristics
		 * .get(CameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES); for (int
		 * i = 0; i < s.length; i++) { long pixelCountTemp = s[i].getHeight() *
		 * s[i].getWidth(); if (pixelCountTemp > pixelCount) { pixelCount =
		 * pixelCountTemp; } }
		 */

		try {
			for (String cameraId : cameraManager.getCameraIdList()) {
				CameraCharacteristics chars = cameraManager
						.getCameraCharacteristics(cameraId);
				StreamConfigurationMap map = chars
						.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
				if (map == null) {
					continue;
				}

				// For still image captures, we use the largest available size.
				largest = Collections.max(
						Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
						new CompareSizesByArea());
				tempPixelCount = largest.getHeight() * largest.getWidth();
				if (tempPixelCount > pixelCount) {
					pixelCount = tempPixelCount;
				}
			}
			
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}

		Log.e("pixelCount=", Long.toString(pixelCount));
		Log.e("pixelCount=", Long.toString(pixelCount));
		Log.e("pixelCount=", Long.toString(pixelCount));
		Log.e("pixelCount=Largest", Long.toString(pixelCount));
		megaPixel = (float) pixelCount / (1024000.0f);
		mySignature.maxResolution = Float.toString(megaPixel);
		Log.e("megaPixel=", Float.toString(megaPixel));
	}

	private void getDeviceLocation() {
		// TODO Auto-generated method stub

		mLocationManager = (LocationManager) getApplicationContext()
				.getSystemService(LOCATION_SERVICE);
		List<String> providers = mLocationManager.getProviders(true);
		Location bestLocation = null;
		for (String provider : providers) {
			Location l = mLocationManager.getLastKnownLocation(provider);
			if (l == null) {
				continue;
			}
			if (bestLocation == null
					|| l.getAccuracy() < bestLocation.getAccuracy()) {
				// Found best last known location: %s", l);
				bestLocation = l;
			}
		}

		mySignature.latitude = bestLocation.getLatitude();
		mySignature.longitude = bestLocation.getLongitude();
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

		try {
			addresses = geocoder.getFromLocation(mySignature.latitude,
					mySignature.longitude, 1);
			if (!addresses.isEmpty()) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");

				for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress
							.append(returnedAddress.getAddressLine(i)).append(
									" ");
				}
				Log.e("MyCurrentLoctionAddress",
						"" + strReturnedAddress.toString());
				cityName = addresses.get(0).getLocality();
				Log.e("City Name=", "" + strReturnedAddress.toString());
			} else {

				Log.e("MyCurrentLoctionAddress", "No Address returned!");

			}
			MyLocationChecker myLocationChecker = new MyLocationChecker();
			mySignature.isLocationInRadius = myLocationChecker
					.checkLocationWithinRadius(bestLocation, bestLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
