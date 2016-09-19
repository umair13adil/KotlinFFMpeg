package com.editor.com.images2video.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.editor.com.images2video.R;

import java.io.File;
import java.text.DecimalFormat;


public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static Typeface setTypeface(Context context) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        return font;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    public static boolean isInternetAvailable(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    public static void setHideSoftKeyboard(EditText editText, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static String arrageDate(String date) {
        String[] months = {"January", "February", "March", "April", "May",
                "June", "July", "August", "September", "October", "November",
                "December"};
        String strDate = "";
        String[] arrDate = date.split("-");
        strDate = months[Integer.parseInt(arrDate[1])] + " " + arrDate[2]
                + ", " + arrDate[0];
        return strDate;
    }

    public static Spanned getColoredSpanned(String text, int color) {
        String input = "<font color='" + color + "'>" + text + "</font>";
        Spanned spannedStrinf = Html.fromHtml(input);
        return spannedStrinf;
    }



    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {

        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


    public static boolean isPackageExisted(Context context, String targetPackage) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage,
                    PackageManager.GET_META_DATA);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }



    public static Boolean createNewDirectory(String directory_name) {
        Boolean if_created = false;


        Log.i(TAG, "Created folder in External storage..");
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + directory_name);
        if (!directory.exists()) {
            if_created = true;
            directory.mkdirs();
        }

        return if_created;
    }

    public static String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;


        if (size < sizeMo)
            return df.format(size / sizeKb) + " KB";
        else if (size < sizeGo)
            return df.format(size / sizeMo) + " MB";
        else if (size < sizeTerra)
            return df.format(size / sizeGo) + " GB";

        return "";
    }

    public static boolean IsExternalStorageAvailable() {
        return
                Environment.MEDIA_MOUNTED
                        .equals(Environment.getExternalStorageState());
    }

    public static String getPackageInfo(Context context) {
        PackageInfo pi = null;
        String pacname = "";
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pacname = pi.versionName + "";
    }

    public static Boolean isValidApk(Context context, String path) {
        Boolean isValid = false;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, 0);

        try {
            Log.i(TAG, "Verify isValidApk: " + info.versionName + " " + info.versionCode);
            if (info.versionName != null) {
                isValid = true;
            }
        } catch (NullPointerException e) {
            isValid = false;
        }

        return isValid;
    }

    public static Boolean isPathValid(String path) {
        Boolean isValid = false;

        if (path.equals("") || path.isEmpty() || path.matches("0") || path.matches("null")) {
            isValid = false;
        } else {
            isValid = true;
        }
        return isValid;
    }

    public static void shareContent(Context context, String msg_share, String share_url) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            String sAux = share_url;
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            context.startActivity(Intent.createChooser(i, "Share"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean isSupportedFormat(File f) {
        String ext = getFileExtension(f);
        if (ext == null) return false;
        try {
            if (SupportedFileFormat.valueOf(ext.toUpperCase()) != null) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            //Not known enum value
            return false;
        }
        return false;
    }

    public static String getFileExtension(File f) {
        int i = f.getName().lastIndexOf('.');
        if (i > 0) {
            return f.getName().substring(i + 1);
        } else
            return null;
    }

    public enum SupportedFileFormat {
        JPG("jpg"),
        JPEG("jpeg"),
        PNG("png");

        private String filesuffix;

        SupportedFileFormat(String filesuffix) {
            this.filesuffix = filesuffix;
        }

        public String getFilesuffix() {
            return filesuffix;
        }
    }
}
