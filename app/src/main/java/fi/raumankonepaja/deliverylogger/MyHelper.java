package fi.raumankonepaja.deliverylogger;

import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Created by Sami on 15.10.2017.
 */

public class MyHelper {


    private static String TAG = "MyHelper Class: ";

    // static method that can be run from not instantied MyHelper
    public static ImageView matchViewToPhotoOrientation(ImageView imageView, String photoFilePath) {

        // *** match orientation of photo and imageview


        int rotation = photoOrientation(photoFilePath);

        imageView.setRotation(rotation);

        return imageView;
    }

    public static int photoOrientation(String photoFilePath) {

        int rotation = 0;

        try {
            // print to console
            Log.i(TAG, "matching imageview to photo (ExifInterface");

            ExifInterface exif = new ExifInterface(photoFilePath);

            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);


            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                rotation = 90;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                rotation = 180;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                rotation = 270;
            }

            return rotation;

        } catch (Exception e) {
            Log.i(TAG, "Orientation solve failed");
            return rotation;
        }
    }


     // this is not working because thumbnail -file is missing meta information
    // todo 77 same issue here
    public static ImageView matcViewToPhotoOrientation(ImageView imageView, byte[] imageData) {
        Log.i(TAG, "matching imageview to imagedata...");
        try {
            // Extract metadata.
            Metadata metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(new ByteArrayInputStream(imageData)), imageData.length);

            Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);



            if(directory == null){
                Log.i(TAG, "didnt get directory");
            }

            // Log each directory.
            for(Directory dir : metadata.getDirectories())
            {
                Log.i("LOG DIRECTORIES", "Directory: " + dir.getName());

                // Log all errors.
                for(String error : dir.getErrors())
                {
                    Log.i("LOG DIRECTORIES", "> error: " + error);
                }

                // Log all tags.
                for(Tag tag : dir.getTags())
                {
                    Log.i("LOG DIRECTORIES", "> tag: " + tag.getTagName() + " = " + tag.getDescription());
                }
            }





            int orientation = 1;
            try {
                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            } catch (MetadataException me) {
                Log.i(TAG, "Could not get orientation");
            }

            imageView.setRotation(orientation);

            return imageView;


        } catch (Exception e){
            Log.i(TAG, "error on matching..." + e.getMessage().toString());
            return imageView;
        }
    }


    // http://www.rgagnon.com/javadetails/java-0483.html

    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }




}

