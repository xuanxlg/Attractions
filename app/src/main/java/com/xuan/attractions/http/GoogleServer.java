package com.xuan.attractions.http;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.xuan.attractions.object.GoogleMapsNearSearch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class GoogleServer implements HttpParameters {

	public ArrayList<GoogleMapsNearSearch> get(LatLng latLng) throws UnsupportedEncodingException{

		ArrayList<GoogleMapsNearSearch> locationArray = new ArrayList<GoogleMapsNearSearch>();

		StringBuffer path = new StringBuffer();

		path.append(GOOGLE_MAPS_API);
		path.append(GOOGLE_MAPS_PARAMETER_NEAR);
		path.append("?");
		path.append(GOOGLE_MAPS_PARAMETER_LOCATION + "=" + latLng.latitude + "," + latLng.longitude);
		path.append("&");
		path.append(GOOGLE_MAPS_PARAMETER_RADIUS);
		path.append("&");
		path.append(GOOGLE_MAPS_PARAMETER_TYPES);
		path.append("&");
		path.append(GOOGLE_MAPS_PARAMETER_LANGUARE);
		path.append("&");
		path.append(GOOGLE_MAPS_PARAMETER_API_KEY + "=" + GOOGLE_MAPS_API_KEY);

		String response = new ServerGet().executeHttpGet(path.toString());
		try {
			JSONObject jsonObject = new JSONObject(response.trim());

			if (jsonObject.has(RESULTS)) {
				JSONArray jsonArray = jsonObject.getJSONArray(RESULTS);

				if (jsonArray.length() > 0) {
					for (int index = 0; index < jsonArray.length(); index++) {
						JSONObject object = jsonArray.getJSONObject(index);

						String name = "";
						double latitude = 0;
						double longitude = 0;

						if (object.has(NAME)) {
							name = object.get(NAME).toString();
						}

						if (object.has(GEOMETRY)) {
							JSONObject geometryObject = object.getJSONObject(GEOMETRY);
							if (geometryObject.has(LOCATION)) {
								JSONObject locationObject = geometryObject.getJSONObject(LOCATION);

								if (locationObject.has(LAT)) {
									latitude = Double.parseDouble(locationObject.get(LAT).toString());
								}
								if (locationObject.has(LNG)) {
									longitude = Double.parseDouble(locationObject.get(LNG).toString());
								}
							}
						}

						if (!TextUtils.isEmpty(name)) {

							GoogleMapsNearSearch googleMapsNearSearch = new GoogleMapsNearSearch();

							googleMapsNearSearch.setName(name);
							googleMapsNearSearch.setLatitude(latitude);
							googleMapsNearSearch.setLongitude(longitude);

							locationArray.add(googleMapsNearSearch);
						}

					}
				}

			}
		} catch (Exception e) {
			System.out.println("e: "+e.toString());
		}

		return locationArray;

	}

}
