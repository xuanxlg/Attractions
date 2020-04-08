package com.xuan.attractions.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerGet implements HttpParameters {

	private String DEFAULT_ENCODING = "UTF-8";
	private int TIME_OUT = 30 * 1000;

	protected String executeHttpGet(String targetURL){

		HttpURLConnection httpConn = null;
		BufferedReader reader = null;
		String result = "";

		try {
			URL url = new URL(targetURL);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setReadTimeout(TIME_OUT);

			httpConn.connect();

			int responseCode = httpConn.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK){
				reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(),DEFAULT_ENCODING));
			}
			else {
				reader = new BufferedReader(new InputStreamReader(httpConn.getErrorStream(),DEFAULT_ENCODING));
			}

			StringBuffer sb = new StringBuffer("");
			String line = "";
			while((line = reader.readLine()) != null){
				sb.append(line);
			}
			reader.close();
			result = sb.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(httpConn != null)
				httpConn.disconnect();
		}

		return result;
	}
}
