|[Introduction](#cordova-plugin-mobile-ocr) | [Supported_Platforms](#supported-platforms) | [Installation_Steps](#installation-steps) | [Plugin_Usage](#plugin-usage) | [Working_Examples](#working-examples) | [More_about_us!](#more-about-us)|
|:---:|:------:|:---:|:---:|:---:|:---:|


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

`textocr.recText(sourceType, returnType, uriOrBase, successCallback, errorCallback)`
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
      textocr.recText(0, 3, imageData, onSuccess, onFail);
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
      textocr.recText(0, 3, imageURI, onSuccess, onFail); 
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

