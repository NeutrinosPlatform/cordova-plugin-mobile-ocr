package com.neutrinos.ocrplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Objects;


public class Textocr extends CordovaPlugin {

    //private static final int REQUEST_CODE = 99;

    private static final int NORMFILEURI = 0; // Make bitmap without compression using uri from picture library (NORMFILEURI & NORMNATIVEURI have same functionality in android)
    private static final int NORMNATIVEURI = 1; // Make compressed bitmap using uri from picture library for faster ocr but might reduce accuracy (NORMFILEURI & NORMNATIVEURI have same functionality in android)
    private static final int FASTFILEURI = 2; // Make uncompressed bitmap using uri from picture library (FASTFILEURI & FASTFILEURI have same functionality in android)
    private static final int FASTNATIVEURI = 3; // Make compressed bitmap using uri from picture library for faster ocr but might reduce accuracy (FASTFILEURI & FASTFILEURI have same functionality in android)
    private static final int BASE64 = 4;  // send base64 image instead of uri
    private TextRecognizer detector;

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("recText")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                            int argstype = NORMFILEURI;
                            String argimagestr = "";
                        try
                        {
                            argstype = args.getInt(0);
                            argimagestr = args.getString(1);
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
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> textBlocks = detector.detect(frame);

                            JSONObject resultobj = new JSONObject();

                            JSONObject blockobj = new JSONObject();
                            JSONObject lineobj = new JSONObject();
                            JSONObject wordobj = new JSONObject();

                            JSONArray blocktext = new JSONArray();
                            JSONArray blocklanguages = new JSONArray();
                            JSONArray blockpoints = new JSONArray();
                            JSONArray blockframe = new JSONArray();

                            JSONArray linetext = new JSONArray();
                            JSONArray linelanguages = new JSONArray();
                            JSONArray linepoints = new JSONArray();
                            JSONArray lineframe = new JSONArray();

                            JSONArray wordtext = new JSONArray();
                            JSONArray wordlanguages = new JSONArray();
                            JSONArray wordpoints = new JSONArray();
                            JSONArray wordframe = new JSONArray();

                            for (int index = 0; index < textBlocks.size(); index++) {

                                TextBlock tBlock = textBlocks.valueAt(index);

                                blocktext.put(tBlock.getValue());
                                blocklanguages.put(tBlock.getLanguage());

                                JSONObject blockcorners = new JSONObject();
                                if (tBlock.getCornerPoints()==null){
                                    blockcorners.put("x1", "");
                                    blockcorners.put("y1", "");

                                    blockcorners.put("x2", "");
                                    blockcorners.put("y2", "");

                                    blockcorners.put("x3", "");
                                    blockcorners.put("y3", "");

                                    blockcorners.put("x4", "");
                                    blockcorners.put("y4", "");
                                }
                                else {
                                    blockcorners.put("x1", Objects.requireNonNull(tBlock.getCornerPoints())[0].x);
                                    blockcorners.put("y1", Objects.requireNonNull(tBlock.getCornerPoints())[0].y);

                                    blockcorners.put("x2", Objects.requireNonNull(tBlock.getCornerPoints())[1].x);
                                    blockcorners.put("y2", Objects.requireNonNull(tBlock.getCornerPoints())[1].y);

                                    blockcorners.put("x3", Objects.requireNonNull(tBlock.getCornerPoints())[2].x);
                                    blockcorners.put("y3", Objects.requireNonNull(tBlock.getCornerPoints())[2].y);

                                    blockcorners.put("x4", Objects.requireNonNull(tBlock.getCornerPoints())[3].x);
                                    blockcorners.put("y4", Objects.requireNonNull(tBlock.getCornerPoints())[3].y);
                                }
                                blockpoints.put(blockcorners);

                                JSONObject blockframeobj = new JSONObject();
                                if (tBlock.getBoundingBox()==null)
                                {
                                    blockframeobj.put("x", "");
                                    blockframeobj.put("y", "");
                                    blockframeobj.put("height","");
                                    blockframeobj.put("width", "");
                                }
                                else {
                                    blockframeobj.put("x", tBlock.getBoundingBox().left);
                                    blockframeobj.put("y", tBlock.getBoundingBox().bottom);
                                    blockframeobj.put("height", tBlock.getBoundingBox().height());
                                    blockframeobj.put("width", tBlock.getBoundingBox().width());
                                }
                                blockframe.put(blockframeobj);

                                for (Text line : tBlock.getComponents())
                                {
                                    linetext.put(line.getValue());
                                    linelanguages.put(tBlock.getLanguage());

                                    JSONObject linecorners = new JSONObject();
                                    if (line.getCornerPoints()==null){
                                        linecorners.put("x1", "");
                                        linecorners.put("y1", "");

                                        linecorners.put("x2", "");
                                        linecorners.put("y2", "");

                                        linecorners.put("x3", "");
                                        linecorners.put("y3", "");

                                        linecorners.put("x4", "");
                                        linecorners.put("y4", "");
                                    }
                                    else {
                                        linecorners.put("x1", line.getCornerPoints()[0].x);
                                        linecorners.put("y1", line.getCornerPoints()[0].y);

                                        linecorners.put("x2", line.getCornerPoints()[1].x);
                                        linecorners.put("y2", line.getCornerPoints()[1].y);

                                        linecorners.put("x3", line.getCornerPoints()[2].x);
                                        linecorners.put("y3", line.getCornerPoints()[2].y);

                                        linecorners.put("x4", line.getCornerPoints()[3].x);
                                        linecorners.put("y4", line.getCornerPoints()[3].y);
                                    }
                                    linepoints.put(linecorners);

                                    JSONObject lineframeobj = new JSONObject();
                                    if (line.getBoundingBox()==null)
                                    {
                                        lineframeobj.put("x", "");
                                        lineframeobj.put("y", "");
                                        lineframeobj.put("height","");
                                        lineframeobj.put("width", "");
                                    }
                                    else
                                    {
                                        lineframeobj.put("x", line.getBoundingBox().left);
                                        lineframeobj.put("y", line.getBoundingBox().bottom);
                                        lineframeobj.put("height", line.getBoundingBox().height());
                                        lineframeobj.put("width", line.getBoundingBox().width());
                                    }
                                    lineframe.put(lineframeobj);

                                    for (Text element : line.getComponents())
                                    {
                                        wordtext.put(element.getValue());
                                        wordlanguages.put(tBlock.getLanguage());

                                        JSONObject wordcorners = new JSONObject();
                                        if (element.getCornerPoints()==null)
                                        {
                                            wordcorners.put("x1", "");
                                            wordcorners.put("y1", "");

                                            wordcorners.put("x2", "");
                                            wordcorners.put("y2", "");

                                            wordcorners.put("x3", "");
                                            wordcorners.put("y3", "");

                                            wordcorners.put("x4", "");
                                            wordcorners.put("y4", "");
                                        }
                                        else
                                        {
                                            wordcorners.put("x1", element.getCornerPoints()[0].x);
                                            wordcorners.put("y1", element.getCornerPoints()[0].y);

                                            wordcorners.put("x2", element.getCornerPoints()[1].x);
                                            wordcorners.put("y2", element.getCornerPoints()[1].y);

                                            wordcorners.put("x3", element.getCornerPoints()[2].x);
                                            wordcorners.put("y3", element.getCornerPoints()[2].y);

                                            wordcorners.put("x4", element.getCornerPoints()[3].x);
                                            wordcorners.put("y4", element.getCornerPoints()[3].y);
                                        }
                                        wordpoints.put(wordcorners);

                                        JSONObject wordframeobj = new JSONObject();
                                        if (element.getBoundingBox()==null)
                                        {
                                            wordframeobj.put("x", "");
                                            wordframeobj.put("y", "");
                                            wordframeobj.put("height","");
                                            wordframeobj.put("width", "");
                                        }
                                        else
                                        {
                                            wordframeobj.put("x", element.getBoundingBox().left);
                                            wordframeobj.put("y", element.getBoundingBox().bottom);
                                            wordframeobj.put("height", element.getBoundingBox().height());
                                            wordframeobj.put("width", element.getBoundingBox().width());
                                        }
                                        wordframe.put(wordframeobj);
                                    }
                                }
                            }
                            if (textBlocks.size() == 0)
                            {
                                // Used to return error when no text found
                                // callbackContext.error("Scan Failed: Found no text to scan");
                                // PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                                // callbackContext.sendPluginResult(r);

                                // Now return foundText false if text not found and return success
                                resultobj.put("foundText", false);

                                callbackContext.success(resultobj);
                                PluginResult r = new PluginResult(PluginResult.Status.OK);
                                callbackContext.sendPluginResult(r);
                            }
                            else
                            {
                                blockobj.put("blocktext", blocktext);
                                blockobj.put("blocklanguages", blocklanguages);
                                blockobj.put("blockpoints", blockpoints);
                                blockobj.put("blockframe", blockframe);

                                lineobj.put("linetext", linetext);
                                lineobj.put("linelanguages", linelanguages);
                                lineobj.put("linepoints", linepoints);
                                lineobj.put("lineframe", lineframe);

                                wordobj.put("wordtext", wordtext);
                                wordobj.put("wordlanguages", wordlanguages);
                                wordobj.put("wordpoints", wordpoints);
                                wordobj.put("wordframe", wordframe);

                                resultobj.put("blocks", blockobj);
                                resultobj.put("lines", lineobj);
                                resultobj.put("words", wordobj);

                                resultobj.put("foundText", true);

                                callbackContext.success(resultobj);
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
            return false;
    }


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
