package com.surefiz.utils;

import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hari on 3/28/2016.
 */
public class MediaUtils {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    //private Context context;
    
    /*public MediaUtils(Context context) {
        this.context = context;
    }*/

    //public static final String STORAGE_DIRECTORY_NAME = "";

    /**
     * This checks the media mount and then create a directory for the application.
     * If media mount is failed or directory is not created then returns null.
     *
     * @param context
     * @param type
     * @return File (null if directory is invalid.)
     */
    public static File getOutputMediaFile(Context context, int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File mediaStorageDir = getStorageDirectory(context, type);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("STORAGE_DIRECTORY_NAME", "failed to create directory: " + getStorageDirectory(context, type));
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * This checks the media mount and then create a directory for the application.
     * If media mount is failed or directory is not created then returns null.
     * @param context
     * @param type
     * @return File (null if directory is invalid.)
     */
    /**
     * @param context
     * @param type
     * @param folderName
     * @param fileName
     * @return
     */
    public static File getOutputMediaFile(Context context, int type, String folderName, String fileName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File mediaStorageDir;
        if (folderName != null)
            mediaStorageDir = getStorageDirectoty(context, type, folderName);
        else
            mediaStorageDir = getStorageDirectory(context, type);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("STORAGE_DIRECTORY_NAME", "failed to create directory: " + getStorageDirectory(context, type));
                return null;
            }
        }

        // Create a media file name
        File mediaFile = null;
        if (fileName != null) {
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        fileName + ".jpg");
            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        fileName + ".mp4");
            } else {
                return null;
            }
        } else {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_" + timeStamp + ".jpg");
            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_" + timeStamp + ".mp4");
            } else {
                return null;
            }
        }

        return mediaFile;
    }

    public static File getStorageDirectory(Context context, int type) {
        if (type == MEDIA_TYPE_IMAGE) {
            return new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getApplicationContext().getPackageName() +
                    "/" + /*context.getResources().getString(R.string.app_name) +*/ "/image");
        } else if (type == MEDIA_TYPE_VIDEO) {
            return new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getApplicationContext().getPackageName() +
                    "/" + /*context.getResources().getString(R.string.app_name) +*/ "/video");
        } else
            return null;
    }

    public static File getStorageDirectoty(Context context, int type, String folderName) {
        if (type == MEDIA_TYPE_IMAGE) {
            /*return new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getApplicationContext().getPackageName() +
                    "/" + *//*context.getResources().getString(R.string.app_name) +*//* "/image/" + folderName);*/
            return new File(Environment.getExternalStorageDirectory(), "DCIM/" + folderName);
        } else if (type == MEDIA_TYPE_VIDEO) {
            return new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getApplicationContext().getPackageName() +
                    "/" + /*context.getResources().getString(R.string.app_name) +*/ "/video/" + folderName);
        } else
            return null;
    }

    /**
     * @param activity
     * @param videoResourceId
     * @return file display duration in long
     */
    public static long getMediaFileDuration(Activity activity, int videoResourceId) {
        long timeDuration = 0;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            //use one of overloaded setDataSource() functions to set your data source
            //retriever.setDataSource(KaraokeVideoRecordActivity.this, Uri.fromFile(videoFile));
            retriever.setDataSource(activity, Uri.parse("android.resource://" + activity.getPackageName() + "/" + videoResourceId));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeDuration = Long.parseLong(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("file", "Video file duration: " + timeDuration);
        return timeDuration;
    }

    /**
     * @param activity
     * @param file
     * @return file display duration in long
     */
    public static long getMediaFileDuration(Activity activity, File file) {

        long timeDuration = 0;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            //use one of overloaded setDataSource() functions to set your data source
            //retriever.setDataSource(KaraokeVideoRecordActivity.this, Uri.fromFile(videoFile));
            retriever.setDataSource(activity, Uri.fromFile(file));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeDuration = Long.parseLong(time);

            Log.d("file", "Video file duration: " + timeDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeDuration;
    }

    /**
     * Delete all the file from a directory and then the directory itself.
     *
     * @param fileOrDirectory
     */
    public static void deleteRecursive(File fileOrDirectory) {
        try {

            if (fileOrDirectory != null && fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            fileOrDirectory.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
