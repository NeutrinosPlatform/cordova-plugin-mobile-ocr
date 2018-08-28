package com.neutrinos.ocrplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

import android.Manifest;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;


public class Textocr extends CordovaPlugin {

    //private static final int REQUEST_CODE = 99;

    private static final int NORMFILEURI = 0; // Make bitmap without compression using uri from picture library (NORMFILEURI & NORMNATIVEURI have same functionality in android)
    private static final int NORMNATIVEURI = 1; // Make compressed bitmap using uri from picture library for faster ocr but might reduce accuracy (NORMFILEURI & NORMNATIVEURI have same functionality in android)
    private static final int FASTFILEURI = 2; // Make uncompressed bitmap using uri from picture library (FASTFILEURI & FASTFILEURI have same functionality in android)
    private static final int FASTNATIVEURI = 3; // Make compressed bitmap using uri from picture library for faster ocr but might reduce accuracy (FASTFILEURI & FASTFILEURI have same functionality in android)
    private static final int BASE64 = 4;  // send base64 image instead of uri
    private TextRecognizer detector;
    private static final int BLOCKS = 0; // return blocks with 2 new lines in between
    private static final int LINES = 1; // return lines with new line in between
    private static final int WORDS = 2; // return words with comma in between
    private static final int ALL = 3; // return all with new line in between
    // protected final static String[] permissions = { Manifest.permission.CAMERA,
    //         Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        //callbackContext = callbackContext;
        if (action.equals("recText")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                            int argstype = NORMFILEURI;
                            int argrtype = ALL;
                            String argimagestr = "";
                        try
                        {
                            argstype = args.getInt(0);
                            argrtype = args.getInt(1);
                            argimagestr = args.getString(2);
                        }
                        catch(Exception e)
                        {
                        callbackContext.error("Argument error");
                        PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                        callbackContext.sendPluginResult(r);
                        }
                        Bitmap bitmap= null;
                        Uri uri = null;
                        if(argstype==NORMFILEURI || argstype==NORMNATIVEURI||argstype==FASTFILEURI || argstype==FASTNATIVEURI)
                        {
                            try
                            {
                                if(!argimagestr.trim().equals(""))
                                {
                                        String imagestr = argimagestr;

                                        // code block that allows this plugin to directly work with document scanner plugin and camera plugin
                                        if(imagestr.substring(0,6).equals("file://"))
                                        {
                                            imagestr = argimagestr.replaceFirst("file://","");
                                        }
                                        //

                                        uri = Uri.parse(imagestr);

                                        if((argstype==NORMFILEURI || argstype==NORMNATIVEURI)&& uri != null) // normal ocr
                                        {
                                            bitmap = MediaStore.Images.Media.getBitmap(cordova.getActivity().getBaseContext().getContentResolver(), uri);
                                        }
                                        else if((argstype==FASTFILEURI || argstype==FASTNATIVEURI) && uri != null) //fast ocr (might be less accurate)
                                        {
                                            bitmap = decodeBitmapUri(cordova.getActivity().getBaseContext(), uri);
                                        }

                                }
                                else
                                {
                                    callbackContext.error("Image Uri or Base64 string is empty");
                                    PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                                    callbackContext.sendPluginResult(r);
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                callbackContext.error("Exception");
                                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                                callbackContext.sendPluginResult(r);
                            }
                        }
                        else if (argstype==BASE64)
                        {
                            if(!argimagestr.trim().equals(""))
                            {
                                byte[] decodedString = Base64.decode(argimagestr, Base64.DEFAULT);
                                bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            }
                            else
                            {
                                callbackContext.error("Image Uri or Base64 string is empty");
                                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                                callbackContext.sendPluginResult(r);
                            }
                        }
                        else
                        {
                            callbackContext.error("Non existent argument. Use 0, 1, 2 , 3 or 4");
                            PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                            callbackContext.sendPluginResult(r);
                        }

                        detector = new TextRecognizer.Builder(cordova.getActivity().getBaseContext()).build();

                        if (detector.isOperational() && bitmap != null)
                        {
                            //Log.w("det", "Detector dependencies are not yet available.");
                            // Check for low storage.  If there is low storage, the native library will not be
                            // downloaded, so detection will not become operational.
                            // TODO: 05-Jul-18 low storage check
                            //                    IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                            //                    boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;
                            //
                            //                    if (hasLowStorage) {
                            //                        Toast.makeText(cordova.getContext(), "low storage", Toast.LENGTH_LONG).show();
                            //                        Log.w("det","low storage");
                            //                    }
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> textBlocks = detector.detect(frame);
                            String blocks = "";
                            String lines = "";
                            String words = "";
                            for (int index = 0; index < textBlocks.size(); index++) {
                                //extract scanned text blocks here
                                TextBlock tBlock = textBlocks.valueAt(index);
                                blocks = blocks + tBlock.getValue() + "\n" + "\n";
                                for (Text line : tBlock.getComponents()) {
                                    //extract scanned text lines here
                                    lines = lines + line.getValue() + "\n";
                                    for (Text element : line.getComponents()) {
                                        //extract scanned text words here
                                        words = words + element.getValue() + ", ";
                                    }
                                }
                            }
                            if (textBlocks.size() == 0)
                            {
                                callbackContext.error("Scan Failed: Found no text to scan");
                                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                                callbackContext.sendPluginResult(r);
                            }
                            else
                            {
                                String result="";
                                if(argrtype==BLOCKS)
                                {
                                    result= blocks;
                                }
                                else if(argrtype==LINES)
                                {
                                    result= lines;
                                }
                                else if(argrtype==WORDS)
                                {
                                    result= words;
                                }
                                else if(argrtype==ALL)
                                {
                                    result= blocks + "\n" + lines + "\n" + words;
                                }
                                else
                                {
                                    callbackContext.error("Non existent returnType argument. Use 0, 1, 2 or 3");
                                    PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                                    callbackContext.sendPluginResult(r);
                                }


                                //String result= blocks + "" + lines + "" + words;
                                Log.v("cor", "Result: " + result);
                                callbackContext.success(result);
                                PluginResult r = new PluginResult(PluginResult.Status.OK);
                                callbackContext.sendPluginResult(r);
                            }
                        }
                        else
                        {
                            if(bitmap == null)
                            {
                                callbackContext.error("Problem with uri or base64 data!");
                                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                                callbackContext.sendPluginResult(r);
                            }
                            else
                            {
                                callbackContext.error("Could not set up the detector! Try Again!");
                                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                                callbackContext.sendPluginResult(r);
                            }

                        }
                    } catch (Exception e) {
                        callbackContext.error("Main loop Exception");
                        PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                        callbackContext.sendPluginResult(r);
                    }
                    //callbackContext.success(); // Thread-safe.
                }
            });

            return true;

        }
        //else
        //{
            return false;
        //}
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE && resultCode == cordova.getActivity().RESULT_OK) {
//           // Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
//            if (uri != null) {
//                String fileLocation = FileHelper.getRealPath(uri, this.cordova);
//                this.callbackContext.success("file://" + fileLocation);
//            } else {
//                this.callbackContext.error("null data from scan libary");
//            }
//        }
//    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException
    {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }
}
