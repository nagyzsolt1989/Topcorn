package com.nagy.zsolt.topcorn.api;

import org.json.JSONObject;

/**
 * Created by Zsolti on 2018.03.10..
 */

public interface FetchDataListener {
    void onFetchComplete(JSONObject data);

    void onFetchFailure(String msg);

    void onFetchStart();
}
