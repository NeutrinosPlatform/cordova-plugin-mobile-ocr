#import <Cordova/CDV.h>
@import GoogleMobileVision;

@interface Textocr : CDVPlugin

@property CDVInvokedUrlCommand* commandglo;
@property GMVDetector* textDetector;
@property UIImage* image;

- (void) recText:(CDVInvokedUrlCommand*)command;
- (UIImage *)resizeImage:(UIImage *)image;
- (NSData *)retrieveAssetDataPhotosFramework:(NSURL *)urlMedia;

@end
