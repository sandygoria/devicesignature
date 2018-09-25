package com.example.devicesignaturegenerator;

import com.example.checker.ScreenDisplay;

public class deviceSignature {
	double latitude;
	double longitude;
	String ipAddress;
	String macAddress;
	String BSSID;
	int linkSpeed_WiFi;
	double rssi_WiFi; // in dBm radio signal strength of WiFi
	String rssi_Cell; // in dBm radio signal strength of Cell
	int networkID_WiFi;
	String SSID_WiFi;
	String deviceID;
	int wifiFrequency;
	int wifiSignalLevel;
	int centerFrequency0;
	int centerFrequency1;
	String capabilities; //
	String venueName;
	boolean devicetoAPRTT = false;
	boolean tdlsSupported = false;
	int ccmpSupport;
	int tkipSupport;
	String maxResolution;
	String gatewayAddress;
	String operatingSystemName;
	int operatingSystemVersion;
	String imei;
	boolean isLocationInRadius;
	ScreenDisplay screenDisplay;

	public String intToIp(int i) {

		return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
				+ ((i >> 8) & 0xFF) + "." + (i & 0xFF);
	}
}
