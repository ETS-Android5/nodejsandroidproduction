package android.gr.katastima;

import android.accounts.Account;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.google.android.gms.common.Scopes;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Functions {

    // Calculate the list for timokatalogos
    public static ArrayList<Product> calcProductlist(Context context) {
        ArrayList<Product> list = new ArrayList<Product>();
        Resources res = context.getResources();

        String images = res.getString(R.string.productImages);
        String titles = res.getString(R.string.productTitles);
        String details = res.getString(R.string.productDetails);
        String prices = res.getString(R.string.productPrices);

        String delims = "\\/";
        String[] images1 = images.split(delims);
        String[] titles1 = titles.split(delims);
        String[] details1 = details.split(delims);
        String[] prices1 = prices.split(delims);


        for(int i = 0; i < images1.length; i++) {
            Product p = new Product();
            p.image = images1[i];
            p.title = titles1[i];
            p.details = details1[i];
            p.price = prices1[i];
            list.add(p);
        }

        return list;
    }

    // Calculate the list for gifts list
    public static ArrayList<Gift> calcGiftlist(Context context) {
        ArrayList<Gift> list = new ArrayList<Gift>();
        Resources res = context.getResources();

        String images = res.getString(R.string.giftImages);
        String titles = res.getString(R.string.giftTitles);
        String details = res.getString(R.string.giftDetails);
        String points = res.getString(R.string.giftPoints);

        String delims = "\\/";
        String[] images1 = images.split(delims);
        String[] titles1 = titles.split(delims);
        String[] details1 = details.split(delims);
        String[] points1 = points.split(delims);

        for(int i = 0; i < images1.length; i++) {
            Gift g = new Gift();
            g.image = images1[i];
            g.title = titles1[i];
            g.details = details1[i];
            g.points = points1[i];
            list.add(g);
        }

        return list;
    }

    // Map
    public static String[] getMapDetails(Context context) {

        Resources res = context.getResources();

        return res.getString(R.string.street).split("/");

    }

    // Gps
    public static LocationFinder LOCATION_FINDER;

    // Calculate distance between 2 locations
    public static float getDistance(double lat1, double lng1, double lat2, double lng2) {

        float[] results = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, results);

        return results[0];
    }

    // Image resize
    public static Bitmap getScaledBitmap(Resources res, int resId, int reqWidth, int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        int scale = 1;
        while(options.outWidth / scale / 2 >= reqWidth &&
                options.outHeight / scale / 2 >= reqHeight) {
            scale *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    // Convert dp to px
    public static int convertDpToPx(int dp) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        float px = dp * (dm.densityDpi / 160f);
        return Math.round(px);
    }

    // Get listItemPrefHeight
    public static float getListItemHeight(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.listPreferredItemHeight, value, true);
        TypedValue.coerceToString(value.type, value.data);
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return value.getDimension(dm);
    }

    public static String CURRENT_CODE;

    // Create a code based on current time and appId
    public static String constructCode(Context context) {
        char[] part1 = (System.currentTimeMillis() + "").toCharArray();
        char[] part2 = context.getString(R.string.appId).toCharArray();

        int length = (part1.length >= part2.length) ? part1.length : part2.length;

        char[] codeChars = new char[length];

        int index1 = 0;
        int index2 =0;

        for(int i = 0; i < length; i++) {
            if(index1 >= part1.length) index1 = 0;
            if(index2 >= part2.length) index2 = 0;

            int charWithInt = (int)(part1[index1]) + (int)(part2[index2]);
            charWithInt = (charWithInt < 33) ? charWithInt + 33 : charWithInt;

            codeChars[i] = (char)charWithInt;

            index1++;
            index2++;
        }

        CURRENT_CODE = codeChars.toString();

        return CURRENT_CODE;
    }

}
