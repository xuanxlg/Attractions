package com.xuan.attractions.http;

import com.xuan.attractions.object.WikiContent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

public class WikiServer implements HttpParameters {

	public ArrayList<String> getMostSimilar(String attractions) throws UnsupportedEncodingException{
		System.out.println("WikiServer.getMostSimilar("+attractions+")");

		StringBuffer path = new StringBuffer();

		path.append(WIKI_API);
		path.append(WIKI_PARAMETER_ACTION);
		path.append(WIKI_PARAMETER_LIST);
		path.append(WIKI_PARAMETER_PROP);
		path.append(WIKI_PARAMETER_FORMAT);
		path.append(WIKI_PARAMETER_SRSEARCH);
//		path.append(URLEncoder.encode(attractions, "utf-8"));
		path.append(attractions);

		String response = new ServerGet().executeHttpGet(path.toString());
		ArrayList<String> mostSimilars = new ArrayList<String>();

		try {
			JSONObject jsonObject = new JSONObject(response.trim());

			if (jsonObject.has(QUERY)) {
				JSONObject jsonQueryObject = jsonObject.getJSONObject(QUERY);
				if (jsonQueryObject.has(SEARCH)) {
					JSONArray jsonSearchArray = jsonQueryObject.getJSONArray(SEARCH);
					for (int index = 0; index < jsonSearchArray.length(); index++) {
						JSONObject locationObject = jsonSearchArray.getJSONObject(index);
						if (locationObject.has(TITLE)) {
							if (attractions.contains(locationObject.getString(TITLE))) {
								mostSimilars.add(locationObject.getString(TITLE));
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}

		return mostSimilars;

	}

	public WikiContent getExtract(String attractions) throws UnsupportedEncodingException{
		System.out.println("WikiServer.getExtract("+attractions+")");

		StringBuffer path = new StringBuffer();

		path.append(WIKI_API);
		path.append(WIKI_PARAMETER_ACTION);
		path.append(WIKI_PARAMETER_EXTRACT);
		path.append(attractions);

		String response = new ServerGet().executeHttpGet(path.toString());
		WikiContent wikiContent = new WikiContent();

		try {
			JSONObject jsonObject = new JSONObject(response.trim());

			if (jsonObject.has(QUERY)) {
				JSONObject jsonQueryObject = jsonObject.getJSONObject(QUERY);
				if (jsonQueryObject.has(PAGES)) {
					JSONObject jsonPagesObject = jsonQueryObject.getJSONObject(PAGES);

					Iterator<String> keys = jsonPagesObject.keys();
					while(keys.hasNext()) {
						String key = keys.next();
						if (jsonPagesObject.get(key) instanceof JSONObject) {
							JSONObject jsonContentObject = jsonPagesObject.getJSONObject(key);
							if (jsonContentObject.has(TITLE) && jsonContentObject.has(EXTRACT)) {
								wikiContent.setTitle(jsonContentObject.get(TITLE).toString());
								wikiContent.setContent(jsonContentObject.get(EXTRACT).toString());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			wikiContent.setTitle("Error");
			wikiContent.setContent(e.toString());
		}


		return wikiContent;

	}

}
