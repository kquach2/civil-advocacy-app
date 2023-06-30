package com.example.civiladvocacy;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class CivicInfoDownloader {
    private static final String TAG = "CivicInfoDownloader";
    private static MainActivity mainActivity;
    private static RequestQueue queue;
    private static ArrayList<Official> officialList = new ArrayList<>();
    private static final String BASE_URL = "https://www.googleapis.com/civicinfo/v2/representatives";
    private static final String yourAPIKey = "AIzaSyBpJEtoVbhuQDUn762GD4V6HvcrVOl0vw8";

    public static void downloadCivicInfo(MainActivity mainActivityIn,
                                         String location){
        mainActivity = mainActivityIn;

        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(BASE_URL).buildUpon();
        buildURL.appendQueryParameter("key", yourAPIKey);
        buildURL.appendQueryParameter("address", location);
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener = response -> {
            officialList.clear();
            handleResults(mainActivity, response);
        };
        Response.ErrorListener error = error1 -> handleResults(mainActivity, null);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void handleResults(MainActivity mainActivity, JSONObject s) {
        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.downloadFailed();
            return;
        } else parseJSON(s);
    }

    private static void parseJSON(JSONObject s) {
        try {
            JSONObject normalizedInput = s.getJSONObject("normalizedInput");
            String line1 = normalizedInput.getString("line1");
            String city = normalizedInput.getString("city");
            String state = normalizedInput.getString("state");
            String zip = normalizedInput.getString("zip");
            String currentLoc = (Objects.equals(line1, "") ? "" : line1+", ") + city + ", " + state + (zip.equals("") ? "" : " " + zip);

            JSONArray offices = s.getJSONArray("offices");
            JSONArray officials = s.getJSONArray("officials");

            for (int i = 0; i < offices.length(); i++) {
                String officeName = offices.getJSONObject(i).getString("name");
                JSONArray officialIndices = offices.getJSONObject(i).getJSONArray("officialIndices");
                for (int j = 0; j < officialIndices.length(); j++) {
                    JSONObject official = officials.getJSONObject(officialIndices.getInt(j));
                    String officialName = official.getString("name");
                    String address = "";

                    if (official.has("address")) {
                        JSONObject addressParts = official.getJSONArray("address").getJSONObject(0);
                        address = addressParts.getString("line1") + "\n" + addressParts.getString("city")
                                + ", " + addressParts.getString("state") + " " + addressParts.getString("zip");
                    }
                    String party = official.has("party") ? official.getString("party") : "Unknown";
                    String phoneNumber = official.has("phones") ? official.getJSONArray("phones").getString(0) : "";
                    String website = official.has("urls") ? official.getJSONArray("urls").getString(0) : "";
                    String email = official.has("emails") ? official.getJSONArray("emails").getString(0) : "";
                    String photoURL = official.has("photoUrl") ? official.getString("photoUrl") : "";
                    String facebookID = "";
                    String twitterID = "";
                    String youtubeID = "";
                    if (official.has("channels")) {
                        JSONArray channels = official.getJSONArray("channels");

                        for (int k = 0; k < channels.length(); k++) {
                            JSONObject channel = channels.getJSONObject(k);
                            if (Objects.equals(channel.get("type"), "Facebook")) facebookID = channel.getString("id");
                            else if (Objects.equals(channel.get("type"), "Twitter")) twitterID = channel.getString("id");
                            else if (Objects.equals(channel.get("type"), "YouTube")) twitterID = channel.getString("id");
                            else Log.d(TAG, "parseJSON: Unknown channel type");
                        }
                    }
                    officialList.add(new Official(officeName, officialName, address, party, phoneNumber, website, email, photoURL, facebookID, twitterID, youtubeID));
                }
            }
            mainActivity.updateCurrentLocation(currentLoc);
            mainActivity.updateOfficialList(officialList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
