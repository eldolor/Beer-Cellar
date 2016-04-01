/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cm.android.beercellar.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.StrictMode;

import com.cm.android.common.logger.Log;
import com.cm.android.beercellar.ui.ImageDetailActivity;
import com.cm.beer.activity.lite.AnalyticsTrackers;
import com.cm.beer.activity.lite.ImageGridActivity;
import com.google.android.gms.analytics.Tracker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class containing some static utility methods.
 */
public class Utils {

    private static String sTag = Utils.class.getName();


    private Utils() {
    }

    ;
    private static final String TAG = Utils.class.getName();
    public static final int IO_BUFFER_SIZE = 8 * 1024;
    public static final String SHARED_PREF_NAME = "com.cm.beer.activity.lite";
    public static final int THUMBNAIL_SIZE = 320;
    public static final String PICTURES_EXTENSION = ".jpg";


    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode() {
        if (Utils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                vmPolicyBuilder
                        .setClassInstanceLimit(ImageGridActivity.class, 1)
                        .setClassInstanceLimit(ImageDetailActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }

    /**
     * Recursively extracts all file names from a given path
     *
     * @param path
     * @return list of file names
     * @author anshu
     */
    public static List<String> listFiles(String path) {

        File root = new File(path);
        File[] list = root.listFiles();
        ArrayList<String> fileNames = new ArrayList<String>();

        if (list == null)
            return null;

        for (File f : list) {
            if (f.isDirectory()) {
                Utils.listFiles(f.getAbsolutePath());
            } else {
                fileNames.add(f.getName());
            }
        }
        return fileNames;
    }

    /**
     * Recursively extracts all file names with their absolute path from a given path
     *
     * @param path
     * @return list of file names with absolute path
     * @author anshu
     */
    public static List<String> listFileAbsolutePaths(String path) {

        File root = new File(path);
        File[] list = root.listFiles();
        ArrayList<String> fileNames = new ArrayList<String>();

        if (list == null)
            return null;

        for (File f : list) {
            if (f.isDirectory()) {
                Utils.listFiles(f.getAbsolutePath());
            } else {
                fileNames.add(f.getAbsolutePath());
            }
        }
        return fileNames;
    }

    /**
     * Recursively deletes all files
     *
     * @param f
     * @throws java.io.IOException
     */
    public static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            Log.d(TAG, "Failed to delete file: " + f);
    }


    /**
     * Get the external storage directory.
     *
     * @param context The context to use
     * @return The external storage dir
     */
    @TargetApi(VERSION_CODES.FROYO)
    public static File getExternalImageStorageDir(Context context) {
        File storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/images");
        if (!storageDir.exists())
            storageDir.mkdirs();
        return storageDir;
    }

    /**
     * Get the external storage directory.
     *
     * @param context The context to use
     * @return The external storage dir
     */
    @TargetApi(VERSION_CODES.FROYO)
    public static File getExternalThumbnailStorageDir(Context context) {
        File storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/thumbs");
        if (!storageDir.exists())
            storageDir.mkdirs();
        return storageDir;
    }

    /**
     * @return
     * @throws IOException
     * @author anshu
     */
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";

//        File storageDir = Utils.getExternalImageStorageDir(context);

//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );


        String pathname = Utils.getExternalImageStorageDir(context).getAbsolutePath() + File.separator + (Math.abs(new Random(System
                .currentTimeMillis()).nextLong())) + PICTURES_EXTENSION;
        File image = new File(pathname);

        return image;
    }

    /**
     *
     * @param urlString
     * @param outputStream
     * @return
     */
    public static boolean write(String urlString, OutputStream outputStream) {

        File image = new File(urlString);
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            //final URL url = new URL(urlString);
            in = new BufferedInputStream(new FileInputStream(image), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            Log.e(TAG, "Error in downloadBitmap - " + e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
            }
        }
        return false;
    }


    /**
     * Create a thumnail image, and store it
     *
     * @param context
     * @param imageAbsolutePath
     * @param thumbnailAbsolutePath
     * @param thumbnailWidth
     * @param thumbnailHeight
     * @return the bitmap
     */
    public static Bitmap createThumbnail(Context context, String imageAbsolutePath, String thumbnailAbsolutePath, int thumbnailWidth, int thumbnailHeight) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {

            //bis = new BufferedInputStream(new FileInputStream(imageAbsolutePath), Utils.IO_BUFFER_SIZE);
            bos = new BufferedOutputStream(new FileOutputStream(thumbnailAbsolutePath), Utils.IO_BUFFER_SIZE);


//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//
//            options.inSampleSize = ImageResizer.calculateInSampleSize(options, thumbnailWidth, thumbnailHeight);
//
//            Bitmap decodedBitmap = BitmapFactory.decodeStream(bis, null, options);
//            if(decodedBitmap == null ){
//                Log.e("createThumbnail", "image data could not be decoded.");
//            }
//
//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(decodedBitmap, thumbnailWidth, thumbnailHeight, false);
//            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            Bitmap bMap = ThumbnailUtils.extractThumbnail(imageBitmap, thumbnailWidth, thumbnailHeight);
//            bMap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageAbsolutePath), thumbnailWidth, thumbnailHeight);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            return thumbnail;
        } catch (Exception e) {
            Log.e("createThumbnail", e.toString());
            return null;
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();
                    //imageBitmap.recycle();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (final Exception e) {
            }
        }


    }

    /**
     * Return GA Tracker
     *
     * @param context
     * @param target
     * @return
     */
    public static Tracker getAnalyticsTracker(Context context, AnalyticsTrackers.Target target){
        try{
            AnalyticsTrackers.initialize(context);
        }catch (Exception e) {}

        return AnalyticsTrackers.getInstance().get(target);

    }

    /**
     * Returns null for any exception thrown
     *
     * @param filename
     * @return
     */
    public static Long extractRowIdFromFileName(String filename) {
        try {
            String[] split = filename.split(".jpg");
            int lastIndexOf = split[0].lastIndexOf(File.separator);
            return Long.valueOf(split[0].substring(lastIndexOf + 1));
        } catch (Throwable e) {
            android.util.Log.e(Utils.sTag, "error: "
                    + ((e.getMessage() != null) ? e.getMessage().replace(" ",
                    "_") : ""), e);
        }
        return null;

    }

    public static int retryIfNetworkDisconnected(ConnectivityManager connectivityManager) {
        long NETWORK_DISCONNECTED_BACKOFF = Configuration.HTTP_BACKOFF_MS
                + new Random().nextInt(Configuration.HTTP_BACKOFF_MS);
        int MAX_ATTEMPTS = Configuration.HTTP_MAX_ATTEMPTS;
        try {
            android.util.Log.d(Utils.sTag,"Entering");
            for (int j = 1; j <= MAX_ATTEMPTS; j++) {

                if (isNetworkConnected(connectivityManager)) {
                    // success
                    j = MAX_ATTEMPTS;
                    return StatusCode.SUCCESS;
                } else {
                    // Sleep till the network state is connected, and then
                    // restart
                    // the download process
                    try {
                        android.util.Log.d(Utils.sTag,"Network disconnected. Sleeping for "
                                    + NETWORK_DISCONNECTED_BACKOFF
                                    + " ms before retry");
                        Thread.sleep(NETWORK_DISCONNECTED_BACKOFF);
                    } catch (InterruptedException e1) {
                        // Activity
                        // finished
                        // before we
                        // complete -
                        // exit.
                        android.util.Log.d(Utils.sTag,"Thread interrupted: abort remaining retries!");
                        Thread.currentThread().interrupt();
                    }
                    // increase backoff
                    // exponentially
                    NETWORK_DISCONNECTED_BACKOFF *= 2;

                }
            }
            return StatusCode.NETWORK_DISCONNECTED;
        } finally {
            android.util.Log.d(Utils.sTag,"Exiting");

        }

    }

    private static boolean isNetworkConnected(ConnectivityManager connectivityManager) {
        return (connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                .getActiveNetworkInfo().isConnected());
    }

    private static boolean isConnectedToWifi(ConnectivityManager connectivityManager) {
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnected())
            return true;
        return false;
    }

    public static final String[] BEERS = new String[] { "Aged Beer",
            "American Belgo Style Ale", "American Belgo Style Dark Ale",
            "American Belgo Style Pale Ale", "American Rye Ale",
            "American Style Amber Lager", "American Style Amber/Red Ale",
            "American Style Barley Wine Ale", "American Style Brown Ale",
            "American Style Cream Ale or Lager", "American Style Dark Lager",
            "American Style Ice Lager", "American Style Imperial Stout",
            "American Style India Pale Ale", "American Style Lager",
            "American Style Light Low Calorie Lager",
            "American Style Low Carbohydrate Light Lager",
            "American Style Malt Liquor", "American Style M‰rzen/Oktoberfest",
            "American Style Pale Ale", "American Style Pilsener",
            "American Style Premium Lager", "American Style Sour Ale",
            "American Style Specialty Lager", "American Style Stout",
            "American Style Strong Pale Ale", "American Style Wheat Beer",
            "American Style Wheat Wine Ale", "Australasian Pale Ale",
            "Baltic Style Porter", "Bamberg Style Rauchbier", "Banana beer",
            "Bappir", "Barley Wine Style Ale", "Belgian Style Abbey Ale",
            "Belgian Style Blonde Ale", "Belgian Style Dark Strong Ale",
            "Belgian Style Dubbel",
            "Belgian Style Flanders/Oud Bruin or Oud Red Ale",
            "Belgian Style Fruit Lambic", "Belgian Style Gueuze Lambic",
            "Belgian Style Lambic", "Belgian Style Pale Ale",
            "Belgian Style Pale Strong Ale", "Belgian Style Sour Ale",
            "Belgian Style Strong Specialty Ale", "Belgian Style Table Beer",
            "Belgian Style Tripel", "Belgian Style Witbier",
            "Berliner Style Weisse", "Berliner Weisse",
            "Bohemian Style Pilsener", "Boza", "Brem",
            "British Style Imperial Stout", "Brown Porter", "Brunswick Mum",
            "California Common Beer", "Cauim", "Chhaang", "Chicha",
            "Chocolate/Cocoa Flavored Beer", "Choujiu", "Chuak", "Cider",
            "Classic Irish Style Dry Stout", "Coffee Flavored Beer", "Cuirm",
            "Dark American Wheat Ale", "Dark American Wheat Ale",
            "Dortmunder/European Style Export", "Draught Beer", "Dry Lager",
            "English Style Barley Wine Ale", "English Style Brown Ale",
            "English Style Dark Mild Ale", "English Style India Pale Ale",
            "English Style Mild Ale", "English Style Pale Ale",
            "English Style Pale Mild Ale", "English Style Summer Ale",
            "European Style Dark/M¸nchner Dunkel",
            "European Style Low Alcohol Lager/German Style Leicht Bier",
            "Experimental Beer", "Extra Special Bitter or Strong Bitter",
            "Field Beer", "Foreign Style Stout",
            "French & Belgian Style Saison", "French Style BiËre de Garde",
            "Fresh Hop Ale", "Fruit Beer", "Fruit Wheat Beer",
            "Fruited American Style Sour Ale",
            "Fruited Wood and Barrel Aged Sour Beer",
            "German Style Brown Ale/D¸sseldorf Style Altbier",
            "German Style Dark Wheat Ale", "German Style Doppelbock",
            "German Style Eisbock", "German Style Heller Bock/Maibock",
            "German Style Kˆlsch/Kˆln Style Kˆlsch",
            "German Style Leichtes Weizen/Weissbier", "German Style M‰rzen",
            "German Style Oktoberfest/Wiesen Meadow",
            "German Style Pale Wheat Ale", "German Style Pilsener",
            "German Style Rye Ale", "German Style Schwarzbier",
            "German Style Sour Ale", "Gluten Free Beer",
            "Golden or Blonde Ale", "Gose", "Gruit", "Gueuze", "Happoshu",
            "Heller Bock", "Herb and Spice Beer", "High Gravity Beer",
            "Huangjiu", "Ibwatu", "Ice Beer", "Ice Cider",
            "Imperial India Pale Ale", "Imperial Red Ale",
            "International India Pale Ale", "International Pale Ale",
            "International Strong Pale Ale", "International Style Lager",
            "International Style Pilsener", "Irish Style Red Ale",
            "Kellerbier", "Kentucky Common Beer", "Leipzig Style Gose",
            "Light American Wheat Ale", "Low Alcohol beer", "Malt beer",
            "Malzbier", "Mbege", "Millet Beer", "M¸nchner Style Helles",
            "Oatmeal Stout", "Old Ale", "Ordinary Bitter", "Oshikundu",
            "Peated Scotch Ale", "Perry", "Pito", "Pumpkin Beer", "Radler",
            "Robust Porter", "Roggenbier", "Sahti", "Sake", "Sake Daiginjo",
            "Sake Futsu shu", "Sake Genshu", "Sake Ginjo", "Sake Honjozo",
            "Sake Infused", "Sake Junmai", "Sake Koshu", "Sake Namasake",
            "Sake Nigori", "Sake Taru", "Sake Tokubetsu", "Sato",
            "Scottish Style Ale", "Scottish Style Export Ale",
            "Scottish Style Heavy Ale", "Scottish Style Light Ale",
            "Session Beer", "Shandy", "Smoked Beer", "Smoked Porter",
            "South German Style Bernsteinfarbenes Weizen/Weissbier",
            "South German Style Dunkel Weizen/Dunkel Weissbier",
            "South German Style Hefeweizen/Hefeweissbier",
            "South German Style Kristal Weizen/Kristal Weissbier",
            "South German Style Weizenbock/Weissbock",
            "Special Bitter or Best Bitter", "Specialty Beer",
            "Specialty Honey Beer", "Steam Beer", "Steinbier", "Strong Ale",
            "Strong Ale or Lager", "Strong Scotch Ale", "Sweet Stout", "Tella",
            "Tiswin", "Tongba", "Traditional German Style Bock",
            "Traditionally Brewed Beer", "Unfiltered German Style Ale",
            "Unfiltered German Style Lager", "Vienna Style Lager",
            "Wood and Barrel Aged Beer", "Wood and Barrel Aged Dark Beer",
            "Wood and Barrel Aged Pale to Amber Beer",
            "Wood and Barrel Aged Sour Beer",
            "Wood and Barrel Aged Strong Beer", "Zozu", "Zutho", "Zwickelbier"};
}
