package com.xuan.attractions.http;

public interface HttpParameters {

    // Google maps

    final String GOOGLE_MAPS_API_KEY = "AIzaSyCfKUH-DtNzs6tlynrCLPXu6qa9Oy81Y5g";

    final String TEST_ATTRACTION = "市府轉運站";

    final String GOOGLE_MAPS_API = "https://maps.googleapis.com/maps/api";
    final String GOOGLE_MAPS_PARAMETER_NEAR = "/place/nearbysearch/json";
    final String GOOGLE_MAPS_PARAMETER_LOCATION = "location";
    final String GOOGLE_MAPS_PARAMETER_RADIUS = "radius=500";
    final String GOOGLE_MAPS_PARAMETER_TYPES = "types=tourist_attraction";
    final String GOOGLE_MAPS_PARAMETER_LANGUARE = "language=zh-TW";
    final String GOOGLE_MAPS_PARAMETER_API_KEY = "key";

    final String RESULTS = "results";
    final String GEOMETRY = "geometry";
    final String LOCATION = "location";
    final String LAT = "lat";
    final String LNG = "lng";
    final String NAME = "name";



    // Wiki

    final String WIKI_API = "https://zh.wikipedia.org/w/api.php";
    final String WIKI_PARAMETER_ACTION = "?action=query";

    final String WIKI_PARAMETER_LIST = "&list=search";
    final String WIKI_PARAMETER_SRSEARCH = "&srsearch=";
    final String WIKI_PARAMETER_PROP = "&prop=wikitext";
    final String WIKI_PARAMETER_FORMAT = "&format=json";

    final String WIKI_PARAMETER_EXTRACT = "&prop=extracts&exintro&explaintext&redirects=1&format=json&utf8&titles=";

    final String QUERY = "query";
    final String PAGES = "pages";
    final String TITLE = "title";
    final String EXTRACT = "extract";
    final String SEARCH = "search";
}
