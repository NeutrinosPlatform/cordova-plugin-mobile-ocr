|[Introduction](#cordova-plugin-mobile-ocr) | [Supported_Platforms](#supported-platforms) | [Installation_Steps](#installation-steps) | [Plugin_Usage](#plugin-usage) | [Working_Examples](#working-examples) | [More_about_us!](#more-about-us)|
|:---:|:------:|:---:|:---:|:---:|:---:|

> **BREAKING CHANGES** introduced in plugin version 2.0.0 and 3.0.0. Older version will run as expected!
> In 2.x.x `returnType` input was removed!
> In 3.x.x if no text was found in the image, the success callback will be called instead of the error callback along with a new key `foundText` with boolean value. Please see example objects at the very end for more explanation.

> **DOCUMENTATION** here applies only to plugin ver 3.x.x! The respective documentation of each of the versions is available with the npm release

# cordova-plugin-mobile-ocr

> This plugin was made possible because of [google mobile vision text recognition API](https://developers.google.com/vision/android/text-overview). The supported languages are listed in the [link](https://developers.google.com/vision/android/text-overview). This plugin is absolutely free and will work offline once install is complete. All required files required for Text Recognition are downloaded during install if necessary space is available.



This plugin defines a global `textocr` object, which provides an method that accepts image uri or base64 inputs. If some text was detected in the image, this text will be returned as a string. The imageuri or base64 can be send to the plugin using any another plugin like [cordova-plugin-camera](https://github.com/apache/cordova-plugin-camera) or [cordova-plugin-document-scanner](https://github.com/NeutrinosPlatform/cordova-plugin-document-scanner). Although the object is attached to the global scoped `window`, it is not available until after the `deviceready` event.

```
document.addEventListener("deviceready", onDeviceReady, false);
function onDeviceReady() {
console.log(textocr);
}
```

# Supported Platforms

- Android
- iOS

# Installation Steps

This requires cordova 7.1.0+ , cordova android 6.4.0+ and cordova ios 4.3.0+ <br/>
npm link :- https://www.npmjs.com/package/cordova-plugin-mobile-ocr

`cordova plugin add cordova-plugin-mobile-ocr`

For **Android** that is all.

For **iOS** please also follow the steps below.
- Once the iOS platform is added in command line, change directory to where podfile is found. Example location :- (myapp/platforms/ios). 
- Make sure you have [cocoapods](https://cocoapods.org/) installed then in command line do `pod update`. 
- Now open myapp.xcworkspace which is usually found in the same directory as the podfile, then build and run. <br/> 
*Note :- if you use myapp.xcodeproj to build and run, it will not work and it will show a linker error.* <br/>
*Note :- if you manually create the podfile, please refer to issue [#2](https://github.com/NeutrinosPlatform/cordova-plugin-mobile-ocr/issues/2)*

# Plugin Usage

`textocr.recText(sourceType,  /*returnType,*/ uriOrBase, successCallback, errorCallback) //returnType no longer accepted from plugin version 2.x.x`
- **textocr.recText**
The **`textocr.recText`** function accepts image data as uri or base64 and uses google mobile vision to recognize text and return the recognized text as string on its successcallback.

- **sourceType**
The **`sourceType`** parameter can take values 0,1,2,3 or 4 each of which are explained in detail in the table below. `sourceType` is an `Int` within the native code.

| sourceType        | uriOrBase     | Accuracy      | Recommendation  | Notes       |
| :-------------:   |:-------------:|:-------------:|:-------------:  |:-------------:  |
| 0                 | NORMFILEURI   | Very High     | Recommended     | On android this is same as NORMNATIVEURI |
| 1                 | NORMNATIVEURI | Very High     | Not Recommended (See note below)     | On android this is same as NORMFILEURI |
| 2                 | FASTFILEURI   | Very Low      | Not Recommended | On android this is same as FASTNATIVEURI. Compression allows for faster processing but sacrifices a lot of accuracy. Best used if ocr images will always be extremely large with large text in them. |
| 3                 | FASTNATIVEURI | Very Low      | Not Recommended | On android this is same as FASTFILEURI. Compression allows for faster processing but sacrifices a lot of accuracy. Best used if ocr images will always be extremely large with large text in them. |
| 4                 | BASE64        | Very High     | Not Recommended | Extremely memory intensive and thus not recommended

>*Note :- NORMNATIVEURI & FASTNATIVEURI for iOS uses deprecated methods to access images. This is to support the [camera](https://github.com/apache/cordova-plugin-camera) plugin which still uses the deprecated methods to return native image URI's using [ALAssetsLibrary](https://developer.apple.com/documentation/assetslibrary/alassetslibrary). This plugin uses non deprecated [PHAsset](https://developer.apple.com/documentation/photokit/phasset?language=objc) library whose deprecated method [fetchAssets(withALAssetURLs:options:)](https://developer.apple.com/documentation/photokit/phasset/1624782-fetchassets) is used to retrieve the image data.*

- **returnType**
> Note :- This input is **no longer accepted** from plugin version 2.0.0 and above. <br>

The **`returnType`** parameter can take values 0,1,2 or 3 each of which are explained in detail in the table below. If a wrong value is passed into this parameter it will default to 3 or `ALL`. See the image below the table to get a better understanding of `BLOCKS`, `LINES` and `WORDS`. Each of these (`BLOCKS`, `LINES` or `WORDS`) will contain the entire recognized text but they can be used for better formatting and thus are separated. `ALL` will contain all blocks first, followed by a new line character `\n`, followed by all lines, followed by another new line character `\n`, followed by all words. So using `ALL` will return duplicates.  `returnType` is an `Int` within the native code.

| returnType     | returnConst   | Notes       |
| :-------------:|:-------------:|:-----------:|
| 0              | BLOCKS        | Detected blocks of text seperated by **two** new line `\n`  characters.|
| 1              | LINES         | Detected lines of text seperated by **one** new line `\n`  character.   |
| 2              | WORDS         | Detected words in the text seperated by `,` and ` `. A comma followed by a space.   |
| 3              | ALL           | `ALL` will contain all blocks first, followed by a new line character `\n`, followed by all lines, followed by another new line character `\n`, followed by all words. So using `ALL` will return duplicates.    |

![N|Solid](https://developers.google.com/vision/images/text-structure.png "Difference between blocks, lines and words")

- **uriOrBase**
The plugin accepts image uri or base64 data in **`uriOrBase`** which is obtained from another plugin like cordova-plugin-document-scanner or cordova-plugin-camera.  This `uriOrBase` is then used by the plugin and via google mobile vision, it detects the text on the image. The data required for OCR is initially downloaded when the app is first installed. 

> Example uriOrBase for NORMFILEURI or FASTFILEURI as obtained from [camera plugin](https://github.com/apache/cordova-plugin-camera) or [scanner plugin](https://github.com/NeutrinosPlatform/cordova-plugin-document-scanner) :- file:///var/mobile/Containers/Data/Application/FF505EA5-F16E-4CBA-8F8B-76A219EDA407/tmp/cdv_photo_001.jpg

> Example uriOrBase for NORMNATIVEURI or FASTNATIVEURI as obtained from [camera plugin](https://github.com/apache/cordova-plugin-camera). [scanner plugin](https://github.com/NeutrinosPlatform/cordova-plugin-document-scanner) doesn't return this :- assets-library://asset/asset.JPG?id=EFBA7BCD-3031-4646-9874-49368849749A&ext=JPG

- **successCallback**
The return value is sent to the **`successCallback`** callback function, in string format if no errors occured. 

- **errorCallback**
The **`errorCallback`** function returns `Scan Failed: Found no text to scan` if no text was detected on the image. It also return other messages based on the error conditions.

>*Note :- After install the OCR App using this plugin does not need an internet connection for Optical Character Recognition since all the required data is downloaded locally on install.*

You can do whatever you want with the string obtained from this plugin, for example:
- **Render the image** in an `<p>` tag.
- `<p id="pp">nothing yet. wait</p>` in html
- `var element = document.getElementById('pp');
element.innerHTML=recognizedText;` in js

> *Note :- This plugin doesn't handle permissions as it only requires the URIs or Base64 data of images and thus expects the other plugins that provide it the URI or Base64 data to handle permissions.*

# Working Examples
Please use `cordova plugin add cordova-plugin-camera` or `cordova plugin add cordova-plugin-document-scanner` before using the following examples.

>*Note :- The cordova-plugin-mobile-ocr plugin will not automatically download either of these plugins as dependencies (This is because this plugin can be used as standalone plugin which can accept URIs or Base64 data through any method or plugin).*

**Using [cordova-plugin-camera](https://github.com/apache/cordova-plugin-camera)** 
```js 
navigator.camera.getPicture(onSuccess, onFail, { quality: 100, correctOrientation: true });

function onSuccess(imageData) {
      textocr.recText(0, /*3,*/ imageData, onSuccess, onFail); // removed returnType (here 3) from version 2.0.0
      // for sourceType Use 0,1,2,3 or 4
      // for returnType Use 0,1,2 or 3 // 3 returns duplicates[see table]
      function onSuccess(recognizedText) {
            //var element = document.getElementById('pp');
            //element.innerHTML=recognizedText;
            //Use above two lines to show recognizedText in html
            console.log(recognizedText);
            alert(recognizedText);
      }
      function onFail(message) {
            alert('Failed because: ' + message);
      }
}
function onFail(message) {
      alert('Failed because: ' + message);
}

```

**Using [cordova-plugin-document-scanner](https://github.com/NeutrinosPlatform/cordova-plugin-document-scanner)** 
>*Note :- base64 and NATIVEURIs won't work with cordova-plugin-document-scanner plugin*
```js 
scan.scanDoc(1, onSuccess, onFail);

function onSuccess(imageURI) {
      textocr.recText(0, /*3,*/ imageURI, onSuccess, onFail); // removed returnType (here 3) from version 2.0.0
      // for sourceType Use 0,2 // 1,3,4 won't work
      // for returnType Use 0,1,2 or 3 // 3 returns duplicates[see table]
      function onSuccess(recognizedText) {
            //var element = document.getElementById('pp');
            //element.innerHTML=recognizedText;
            //Use above two lines to show recognizedText in html
            console.log(recognizedText);
            alert(recognizedText);
      }
      function onFail(message) {
            alert('Failed because: ' + message);
      }
}
function onFail(message) {
      alert('Failed because: ' + message);
}
```

# More about us
Find out more or contact us directly here :- http://www.neutrinos.co/

Facebook :- https://www.facebook.com/Neutrinos.co/ <br/>
LinkedIn :- https://www.linkedin.com/company/25057297/ <br/>
Twitter :- https://twitter.com/Neutrinosco <br/>
Instagram :- https://www.instagram.com/neutrinos.co/

[![N|Solid](https://image4.owler.com/logo/neutrinos_owler_20171023_142541_original.jpg "Neutrinos")](http://www.neutrinos.co/) 


# Example Objects
> For the cordova plugin 2.x.x the success callback will return this object

The five properties text, languages, confidence, points and frame are obtained as arrays and are associated with each other using the index of the array.

>For example :- 
The text **linetext[0]** contains the languages **linelanguages[0]** and have a confidence of **lineconfidence[0]** with **linepoints[0]** and **lineframe [0]**. 

>Refer the examples to see how the points and frame are returned. Points hold four (x,y) point values that can be used to draw a box around each text. The Frame holds the origin x,y value, the height and the width of the rectangle that can be drawn around the text. The x,y value returned from the Frame property usually correspond to x1 and y4 of the Points property. The Points and Frame values can be used to obtain the placement of the text on the image

The basic structure of the object is as follows :- 

> **foundText** was added in plugin version 3.0.0 and above. In earlier plugin versions if image did not contain text the error callback was called. From 3.0.0 onwards all success callbacks will contain the `foundText` key with a boolean value. Letting the user know if a text was present in the image. if `foundText` is false, text was not found and hence the `blocks`, `lines`, `words` keys won't be returned

 - **foundText** - **boolean** value that is true if image contains text else false
 - **blocks**
   - **blocktext** - **Array** that contains each text block
   - **blocklanguages** - **Array** of languages (Currently returns unusable values)
   - **blockconfidence** - **Array** of confidence values (Currently return nil for on-Device text recognition) 
   - **blockpoints** - **Array** of objects of four points each that represent a block drawn around the text
     - x1 - Key (Example to get x1 of the first text block :- recognizedText.blocks.blockpoints[0].x1)
     - y1 - Key
     - x2 - Key
     - y2 - Key
     - x3 - Key
     - y3 - Key
     - x4 - Key
     - y4 - Key
   - **blockframe** - **Array** of objects that contain origin point and size of the rectangle that holds text
     - x - Key (Example to get x from blockframe of the first text block :- recognizedText.blocks.blockframe[0].x)
     - y - Key
     - height - Key
     - width - Key
 - **lines**
   - **linetext** - **Array** that contains each text block
   - **linelanguages** - **Array** of languages (Currently returns unusable values)
   - **lineconfidence** - **Array** of confidence values (Currently return nil for on-Device text recognition) 
   - **linepoints** - **Array** of objects of four points each that represent a block drawn around the text
        - x1 - Key
        - y1 - Key
        - x2 - Key
        - y2 - Key
        - x3 - Key
        - y3 - Key
        - x4 - Key
        - y4 - Key
   - **lineframe** - **Array** of objects that contain origin point and size of the rectangle that holds text
     - x - Key
     - y - Key
     - height - Key
     - width - Key
 - **words**
   - **wordtext** - **Array** that contains each text block
   - **wordlanguages** - **Array** of languages (Currently returns unusable values)
   - **wordconfidence** - **Array** of confidence values (Currently return nil for on-Device text recognition) 
   - **wordpoints** - **Array** of objects of four points each that represent a block drawn around the text
        - x1 - Key
        - y1 - Key
        - x2 - Key
        - y2 - Key
        - x3 - Key
        - y3 - Key
        - x4 - Key
        - y4 - Key
   - **wordframe** - **Array** of objects that contain origin point and size of the rectangle that holds text
     - x - Key
     - y - Key
     - height - Key
     - width - Key
# Example Object when no text in image
```json
{
  "foundText" : false
}
```
# iOS Example Object
```json
{
  "foundText" : true,
  "blocks": {
    "blocklanguages": [
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c4009800>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c400a100>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c4009b00>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c4009fb0>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c4008d60>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c4009fc0>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c400a170>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c4008fa0>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c4008c40>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c4007790>\"]"
    ],
    "blockpoints": [
      {
        "x3": "2338.143066",
        "y1": "52.000000",
        "x1": "2073.000000",
        "y4": "656.654541",
        "x4": "1972.193848",
        "y2": "113.009895",
        "x2": "2438.949219",
        "y3": "717.664429"
      },
      {
        "x3": "1204.772949",
        "y1": "255.000000",
        "x1": "942.000000",
        "y4": "537.928284",
        "x4": "865.838440",
        "y2": "346.237946",
        "x2": "1280.934570",
        "y3": "629.166199"
      },
      {
        "x3": "628.515869",
        "y1": "1192.000000",
        "x1": "398.000000",
        "y4": "1452.757080",
        "x4": "386.741180",
        "y2": "1202.439209",
        "x2": "639.774719",
        "y3": "1463.196289"
      },
      {
        "x3": "1787.353516",
        "y1": "1257.000000",
        "x1": "1495.000000",
        "y4": "1482.905884",
        "x4": "1488.478027",
        "y2": "1265.628662",
        "x2": "1793.875488",
        "y3": "1491.534546"
      },
      {
        "x3": "2804.546387",
        "y1": "1267.000000",
        "x1": "2495.000000",
        "y4": "1547.713013",
        "x4": "2468.088867",
        "y2": "1299.255127",
        "x2": "2831.457520",
        "y3": "1579.968140"
      },
      {
        "x3": "939.620850",
        "y1": "2279.000000",
        "x1": "587.000000",
        "y4": "2548.592773",
        "x4": "572.175903",
        "y2": "2299.204590",
        "x2": "954.444946",
        "y3": "2568.797363"
      },
      {
        "x3": "1968.580078",
        "y1": "2307.000000",
        "x1": "1776.000000",
        "y4": "2534.936768",
        "x4": "1770.634888",
        "y2": "2311.659180",
        "x2": "1973.945190",
        "y3": "2539.595947"
      },
      {
        "x3": "2982.085693",
        "y1": "2334.000000",
        "x1": "2793.000000",
        "y4": "2544.980225",
        "x4": "2790.103760",
        "y2": "2336.635254",
        "x2": "2984.981934",
        "y3": "2547.615479"
      },
      {
        "x3": "1426.792480",
        "y1": "3287.000000",
        "x1": "1072.000000",
        "y4": "3611.215088",
        "x4": "1037.933228",
        "y2": "3327.859375",
        "x2": "1460.859253",
        "y3": "3652.074463"
      },
      {
        "x3": "2455.617920",
        "y1": "3346.000000",
        "x1": "2255.000000",
        "y4": "3559.973633",
        "x4": "2251.643066",
        "y2": "3349.199951",
        "x2": "2458.974854",
        "y3": "3563.173584"
      }
    ],
    "blocktext": [
      "# 3",
      "2",
      "Q",
      "W",
      "E",
      "A",
      "S",
      "D",
      "Z",
      "X"
    ],
    "blockconfidence": [
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    ],
    "blockframe": [
      {
        "y": "52.000000",
        "x": "1972.000000",
        "height": "666.000000",
        "width": "467.000000"
      },
      {
        "y": "255.000000",
        "x": "865.000000",
        "height": "375.000000",
        "width": "416.000000"
      },
      {
        "y": "1192.000000",
        "x": "386.000000",
        "height": "272.000000",
        "width": "254.000000"
      },
      {
        "y": "1257.000000",
        "x": "1488.000000",
        "height": "235.000000",
        "width": "306.000000"
      },
      {
        "y": "1267.000000",
        "x": "2468.000000",
        "height": "313.000000",
        "width": "364.000000"
      },
      {
        "y": "2279.000000",
        "x": "572.000000",
        "height": "290.000000",
        "width": "383.000000"
      },
      {
        "y": "2307.000000",
        "x": "1770.000000",
        "height": "233.000000",
        "width": "204.000000"
      },
      {
        "y": "2334.000000",
        "x": "2790.000000",
        "height": "214.000000",
        "width": "195.000000"
      },
      {
        "y": "3287.000000",
        "x": "1037.000000",
        "height": "366.000000",
        "width": "424.000000"
      },
      {
        "y": "3346.000000",
        "x": "2251.000000",
        "height": "218.000000",
        "width": "208.000000"
      }
    ]
  },
  "lines": {
    "lineframe": [
      {
        "y": "53.000000",
        "x": "2048.000000",
        "height": "231.000000",
        "width": "264.000000"
      },
      {
        "y": "342.000000",
        "x": "1979.000000",
        "height": "369.000000",
        "width": "405.000000"
      },
      {
        "y": "255.000000",
        "x": "865.000000",
        "height": "375.000000",
        "width": "416.000000"
      },
      {
        "y": "1192.000000",
        "x": "386.000000",
        "height": "272.000000",
        "width": "254.000000"
      },
      {
        "y": "1257.000000",
        "x": "1488.000000",
        "height": "235.000000",
        "width": "306.000000"
      },
      {
        "y": "1267.000000",
        "x": "2468.000000",
        "height": "313.000000",
        "width": "364.000000"
      },
      {
        "y": "2279.000000",
        "x": "572.000000",
        "height": "290.000000",
        "width": "383.000000"
      },
      {
        "y": "2307.000000",
        "x": "1770.000000",
        "height": "233.000000",
        "width": "204.000000"
      },
      {
        "y": "2334.000000",
        "x": "2790.000000",
        "height": "214.000000",
        "width": "195.000000"
      },
      {
        "y": "3287.000000",
        "x": "1037.000000",
        "height": "366.000000",
        "width": "424.000000"
      },
      {
        "y": "3346.000000",
        "x": "2251.000000",
        "height": "218.000000",
        "width": "208.000000"
      }
    ],
    "linelanguages": [
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c40084f0>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c4009e70>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c400a0a0>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c40076e0>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c40067e0>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c400a150>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c4008fb0>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c400a140>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c400a000>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c400a210>\"]",
      "[    \"<FIRVisionTextRecognizedLanguage: 0x1c4007c80>\"]"
    ],
    "lineconfidence": [
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    ],
    "linetext": [
      "#",
      "3",
      "2",
      "Q",
      "W",
      "E",
      "A",
      "S",
      "D",
      "Z",
      "X"
    ],
    "linepoints": [
      {
        "x3": "2279.747070",
        "y1": "53.000000",
        "x1": "2081.000000",
        "y4": "245.345245",
        "x4": "2048.932861",
        "y2": "91.480637",
        "x2": "2311.814209",
        "y3": "283.825897"
      },
      {
        "x3": "2295.458252",
        "y1": "342.000000",
        "x1": "2067.000000",
        "y4": "605.842957",
        "x4": "1979.416382",
        "y2": "446.911316",
        "x2": "2383.041992",
        "y3": "710.754272"
      },
      {
        "x3": "1204.772949",
        "y1": "255.000000",
        "x1": "942.000000",
        "y4": "537.928284",
        "x4": "865.838440",
        "y2": "346.237946",
        "x2": "1280.934570",
        "y3": "629.166199"
      },
      {
        "x3": "628.515869",
        "y1": "1192.000000",
        "x1": "398.000000",
        "y4": "1452.757080",
        "x4": "386.741180",
        "y2": "1202.439209",
        "x2": "639.774719",
        "y3": "1463.196289"
      },
      {
        "x3": "1787.353516",
        "y1": "1257.000000",
        "x1": "1495.000000",
        "y4": "1482.905884",
        "x4": "1488.478027",
        "y2": "1265.628662",
        "x2": "1793.875488",
        "y3": "1491.534546"
      },
      {
        "x3": "2804.546387",
        "y1": "1267.000000",
        "x1": "2495.000000",
        "y4": "1547.713013",
        "x4": "2468.088867",
        "y2": "1299.255127",
        "x2": "2831.457520",
        "y3": "1579.968140"
      },
      {
        "x3": "939.620850",
        "y1": "2279.000000",
        "x1": "587.000000",
        "y4": "2548.592773",
        "x4": "572.175903",
        "y2": "2299.204590",
        "x2": "954.444946",
        "y3": "2568.797363"
      },
      {
        "x3": "1968.580078",
        "y1": "2307.000000",
        "x1": "1776.000000",
        "y4": "2534.936768",
        "x4": "1770.634888",
        "y2": "2311.659180",
        "x2": "1973.945190",
        "y3": "2539.595947"
      },
      {
        "x3": "2982.085693",
        "y1": "2334.000000",
        "x1": "2793.000000",
        "y4": "2544.980225",
        "x4": "2790.103760",
        "y2": "2336.635254",
        "x2": "2984.981934",
        "y3": "2547.615479"
      },
      {
        "x3": "1426.792480",
        "y1": "3287.000000",
        "x1": "1072.000000",
        "y4": "3611.215088",
        "x4": "1037.933228",
        "y2": "3327.859375",
        "x2": "1460.859253",
        "y3": "3652.074463"
      },
      {
        "x3": "2455.617920",
        "y1": "3346.000000",
        "x1": "2255.000000",
        "y4": "3559.973633",
        "x4": "2251.643066",
        "y2": "3349.199951",
        "x2": "2458.974854",
        "y3": "3563.173584"
      }
    ]
  },
  "words": {
    "wordtext": [
      "#",
      "3",
      "2",
      "Q",
      "W",
      "E",
      "A",
      "S",
      "D",
      "Z",
      "X"
    ],
    "wordlanguages": [
      "[]",
      "[]",
      "[]",
      "[]",
      "[]",
      "[]",
      "[]",
      "[]",
      "[]",
      "[]",
      "[]"
    ],
    "wordpoints": [
      {
        "x3": "2279.747070",
        "y1": "53.000000",
        "x1": "2081.000000",
        "y4": "245.345245",
        "x4": "2048.932861",
        "y2": "91.480637",
        "x2": "2311.814209",
        "y3": "283.825897"
      },
      {
        "x3": "2295.458252",
        "y1": "342.000000",
        "x1": "2067.000000",
        "y4": "605.842957",
        "x4": "1979.416382",
        "y2": "446.911316",
        "x2": "2383.041992",
        "y3": "710.754272"
      },
      {
        "x3": "1204.772949",
        "y1": "255.000000",
        "x1": "942.000000",
        "y4": "537.928284",
        "x4": "865.838440",
        "y2": "346.237946",
        "x2": "1280.934570",
        "y3": "629.166199"
      },
      {
        "x3": "628.515869",
        "y1": "1192.000000",
        "x1": "398.000000",
        "y4": "1452.757080",
        "x4": "386.741180",
        "y2": "1202.439209",
        "x2": "639.774719",
        "y3": "1463.196289"
      },
      {
        "x3": "1787.353516",
        "y1": "1257.000000",
        "x1": "1495.000000",
        "y4": "1482.905884",
        "x4": "1488.478027",
        "y2": "1265.628662",
        "x2": "1793.875488",
        "y3": "1491.534546"
      },
      {
        "x3": "2804.546387",
        "y1": "1267.000000",
        "x1": "2495.000000",
        "y4": "1547.713013",
        "x4": "2468.088867",
        "y2": "1299.255127",
        "x2": "2831.457520",
        "y3": "1579.968140"
      },
      {
        "x3": "939.620850",
        "y1": "2279.000000",
        "x1": "587.000000",
        "y4": "2548.592773",
        "x4": "572.175903",
        "y2": "2299.204590",
        "x2": "954.444946",
        "y3": "2568.797363"
      },
      {
        "x3": "1968.580078",
        "y1": "2307.000000",
        "x1": "1776.000000",
        "y4": "2534.936768",
        "x4": "1770.634888",
        "y2": "2311.659180",
        "x2": "1973.945190",
        "y3": "2539.595947"
      },
      {
        "x3": "2982.085693",
        "y1": "2334.000000",
        "x1": "2793.000000",
        "y4": "2544.980225",
        "x4": "2790.103760",
        "y2": "2336.635254",
        "x2": "2984.981934",
        "y3": "2547.615479"
      },
      {
        "x3": "1426.792480",
        "y1": "3287.000000",
        "x1": "1072.000000",
        "y4": "3611.215088",
        "x4": "1037.933228",
        "y2": "3327.859375",
        "x2": "1460.859253",
        "y3": "3652.074463"
      },
      {
        "x3": "2455.617920",
        "y1": "3346.000000",
        "x1": "2255.000000",
        "y4": "3559.973633",
        "x4": "2251.643066",
        "y2": "3349.199951",
        "x2": "2458.974854",
        "y3": "3563.173584"
      }
    ],
    "wordframe": [
      {
        "y": "53.000000",
        "x": "2048.000000",
        "height": "231.000000",
        "width": "264.000000"
      },
      {
        "y": "342.000000",
        "x": "1979.000000",
        "height": "369.000000",
        "width": "405.000000"
      },
      {
        "y": "255.000000",
        "x": "865.000000",
        "height": "375.000000",
        "width": "416.000000"
      },
      {
        "y": "1192.000000",
        "x": "386.000000",
        "height": "272.000000",
        "width": "254.000000"
      },
      {
        "y": "1257.000000",
        "x": "1488.000000",
        "height": "235.000000",
        "width": "306.000000"
      },
      {
        "y": "1267.000000",
        "x": "2468.000000",
        "height": "313.000000",
        "width": "364.000000"
      },
      {
        "y": "2279.000000",
        "x": "572.000000",
        "height": "290.000000",
        "width": "383.000000"
      },
      {
        "y": "2307.000000",
        "x": "1770.000000",
        "height": "233.000000",
        "width": "204.000000"
      },
      {
        "y": "2334.000000",
        "x": "2790.000000",
        "height": "214.000000",
        "width": "195.000000"
      },
      {
        "y": "3287.000000",
        "x": "1037.000000",
        "height": "366.000000",
        "width": "424.000000"
      },
      {
        "y": "3346.000000",
        "x": "2251.000000",
        "height": "218.000000",
        "width": "208.000000"
      }
    ],
    "wordconfidence": [
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    ]
  }
}
```
# Android Example Object
```json
{
  "foundText" : true,
  "blocks": {
    "blocktext": [
      "Home",
      "Ins",
      "PgUp",
      "PgDn",
      "Del"
    ],
    "blockconfidence": [
      null,
      null,
      null,
      null,
      null
    ],
    "blocklanguages": [
      "[]",
      "[]",
      "[]",
      "[]",
      "[]"
    ],
    "blockpoints": [
      {
        "x1": 270,
        "y1": 346,
        "x2": 652,
        "y2": 346,
        "x3": 652,
        "y3": 468,
        "x4": 270,
        "y4": 468
      },
      {
        "x1": 913,
        "y1": 2459,
        "x2": 1215,
        "y2": 2459,
        "x3": 1215,
        "y3": 2627,
        "x4": 913,
        "y4": 2627
      },
      {
        "x1": 1497,
        "y1": 292,
        "x2": 1907,
        "y2": 292,
        "x3": 1907,
        "y3": 496,
        "x4": 1497,
        "y4": 496
      },
      {
        "x1": 1543,
        "y1": 1722,
        "x2": 1953,
        "y2": 1722,
        "x3": 1953,
        "y3": 1878,
        "x4": 1543,
        "y4": 1878
      },
      {
        "x1": 1659,
        "y1": 2451,
        "x2": 1900,
        "y2": 2451,
        "x3": 1900,
        "y3": 2585,
        "x4": 1659,
        "y4": 2585
      }
    ],
    "blockframe": [
      {
        "x": 270,
        "y": 468,
        "height": 122,
        "width": 382
      },
      {
        "x": 913,
        "y": 2627,
        "height": 168,
        "width": 302
      },
      {
        "x": 1497,
        "y": 496,
        "height": 204,
        "width": 410
      },
      {
        "x": 1543,
        "y": 1878,
        "height": 156,
        "width": 410
      },
      {
        "x": 1659,
        "y": 2585,
        "height": 134,
        "width": 241
      }
    ]
  },
  "lines": {
    "linetext": [
      "Home",
      "Ins",
      "PgUp",
      "PgDn",
      "Del"
    ],
    "lineconfidence": [
      null,
      null,
      null,
      null,
      null
    ],
    "linelanguages": [
      "[]",
      "[]",
      "[]",
      "[]",
      "[]"
    ],
    "linepoints": [
      {
        "x1": 270,
        "y1": 346,
        "x2": 652,
        "y2": 346,
        "x3": 652,
        "y3": 468,
        "x4": 270,
        "y4": 468
      },
      {
        "x1": 913,
        "y1": 2459,
        "x2": 1215,
        "y2": 2459,
        "x3": 1215,
        "y3": 2627,
        "x4": 913,
        "y4": 2627
      },
      {
        "x1": 1497,
        "y1": 292,
        "x2": 1907,
        "y2": 292,
        "x3": 1907,
        "y3": 496,
        "x4": 1497,
        "y4": 496
      },
      {
        "x1": 1543,
        "y1": 1722,
        "x2": 1953,
        "y2": 1722,
        "x3": 1953,
        "y3": 1878,
        "x4": 1543,
        "y4": 1878
      },
      {
        "x1": 1659,
        "y1": 2451,
        "x2": 1900,
        "y2": 2451,
        "x3": 1900,
        "y3": 2585,
        "x4": 1659,
        "y4": 2585
      }
    ],
    "lineframe": [
      {
        "x": 270,
        "y": 468,
        "height": 122,
        "width": 382
      },
      {
        "x": 913,
        "y": 2627,
        "height": 168,
        "width": 302
      },
      {
        "x": 1497,
        "y": 496,
        "height": 204,
        "width": 410
      },
      {
        "x": 1543,
        "y": 1878,
        "height": 156,
        "width": 410
      },
      {
        "x": 1659,
        "y": 2585,
        "height": 134,
        "width": 241
      }
    ]
  },
  "words": {
    "wordtext": [
      "Home",
      "Ins",
      "PgUp",
      "PgDn",
      "Del"
    ],
    "wordconfidence": [
      null,
      null,
      null,
      null,
      null
    ],
    "wordlanguages": [
      "[]",
      "[]",
      "[]",
      "[]",
      "[]"
    ],
    "wordpoints": [
      {
        "x1": 270,
        "y1": 346,
        "x2": 652,
        "y2": 346,
        "x3": 652,
        "y3": 468,
        "x4": 270,
        "y4": 468
      },
      {
        "x1": 913,
        "y1": 2459,
        "x2": 1215,
        "y2": 2459,
        "x3": 1215,
        "y3": 2627,
        "x4": 913,
        "y4": 2627
      },
      {
        "x1": 1497,
        "y1": 292,
        "x2": 1907,
        "y2": 292,
        "x3": 1907,
        "y3": 496,
        "x4": 1497,
        "y4": 496
      },
      {
        "x1": 1543,
        "y1": 1722,
        "x2": 1953,
        "y2": 1722,
        "x3": 1953,
        "y3": 1878,
        "x4": 1543,
        "y4": 1878
      },
      {
        "x1": 1659,
        "y1": 2451,
        "x2": 1900,
        "y2": 2451,
        "x3": 1900,
        "y3": 2585,
        "x4": 1659,
        "y4": 2585
      }
    ],
    "wordframe": [
      {
        "x": 270,
        "y": 468,
        "height": 122,
        "width": 382
      },
      {
        "x": 913,
        "y": 2627,
        "height": 168,
        "width": 302
      },
      {
        "x": 1497,
        "y": 496,
        "height": 204,
        "width": 410
      },
      {
        "x": 1543,
        "y": 1878,
        "height": 156,
        "width": 410
      },
      {
        "x": 1659,
        "y": 2585,
        "height": 134,
        "width": 241
      }
    ]
  }
}
```

