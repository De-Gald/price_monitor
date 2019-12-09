package com.google.price.utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public final class PriceJsonUtils {

    public static final String PRICE = "price";
    public static final String TITLE = "title";
    public static final String LINK_TO_PAGE = "link_to_page";
    public static final String LINK_TO_ICON = "link_to_icon";

    public static Map<String, String> getPriceStringsFromJson(String priceJsonStr)
            throws JSONException {

        JSONObject priceJson = new JSONObject(priceJsonStr);

        String price = priceJson.getString("price");
        String title = priceJson.getString("title").split("Details about")[1];
        String link_to_page = priceJson.getString("link");
        String link_to_icon = priceJson.getString("imageLink");

        Map<String, String> record = new HashMap<String, String>();
        record.put(PRICE, price);
        record.put(TITLE, title);
        record.put(LINK_TO_PAGE, link_to_page);
        record.put(LINK_TO_ICON, link_to_icon);

        return record;
    }

}