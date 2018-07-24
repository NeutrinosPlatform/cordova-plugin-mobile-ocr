#import "Textocr.h"
#import <Photos/Photos.h>

@implementation Textocr
#define NORMFILEURI ((int) 0)
#define NORMNATIVEURI ((int) 1)
#define FASTFILEURI ((int) 2)
#define FASTNATIVEURI ((int) 3)
#define BASE64 ((int) 4)

#define BLOCKS ((int) 0)
#define LINES ((int) 1)
#define WORDS ((int) 2)
#define ALL ((int) 3)

- (void)recText:(CDVInvokedUrlCommand*)command
{
    NSLog(@"%s","I AM HERE AT THE STAAAAAART!!!");
    [self.commandDelegate runInBackground:^{
        NSLog(@"%s","I AM bkg!!");
        _commandglo = command;
        int stype = NORMFILEURI; // sourceType
        int rtype = ALL; //returnType
        NSString* name;
        self.image = NULL;
        @try {
            NSString *st = [[_commandglo arguments] objectAtIndex:0];
            stype = [st intValue];
            // 0 NORMFILEURI
            // 1 NORMNATIVEURI
            // 2 FASTFILEURI
            // 3 FASTNATIVEURI
            // 4 BASE64
            
            NSString *rt = [[_commandglo arguments] objectAtIndex:1];
            rtype = [rt intValue];
            // 0 BLOCKS
            // 1 LINES
            // 2 WORDS
            // 3 ALL
            
            name = [[_commandglo arguments] objectAtIndex:2];

        }
        @catch (NSException *exception) {
            CDVPluginResult* result = [CDVPluginResult
                                       resultWithStatus:CDVCommandStatus_ERROR
                                       messageAsString:@"argument/parameter type mismatch error"];
            [self.commandDelegate sendPluginResult:result callbackId:_commandglo.callbackId];
        }
        
        if (stype == NORMFILEURI || stype == NORMNATIVEURI || stype == FASTFILEURI || stype == FASTNATIVEURI)
        {
            if (stype==NORMFILEURI)
            {
                NSData *imageData = [NSData dataWithContentsOfURL:[NSURL URLWithString:name]];
                self.image = [UIImage imageWithData:imageData];
            }
            else if (stype==NORMNATIVEURI)
            {
                NSString *urlString = [NSString stringWithFormat:@"%@", name];
                NSURL *url = [NSURL URLWithString:[urlString stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]]];
                NSData *imageData = [self retrieveAssetDataPhotosFramework:url];
                self.image = [UIImage imageWithData:imageData];
            }
            else if (stype==FASTFILEURI)
            {
                NSData *imageData = [NSData dataWithContentsOfURL:[NSURL URLWithString:name]];
                self.image = [UIImage imageWithData:imageData];
                self.image = [self resizeImage:self.image];
            }
            else if (stype==FASTNATIVEURI)
            {
                NSString *urlString = [NSString stringWithFormat:@"%@", name];
                NSURL *url = [NSURL URLWithString:[urlString stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]]];
                NSData *imageData = [self retrieveAssetDataPhotosFramework:url];
                self.image = [UIImage imageWithData:imageData];
                self.image = [self resizeImage:self.image];
            }
            
        }
        else if (stype==BASE64)
        {
            NSData *data = [[NSData alloc]initWithBase64EncodedString:name options:NSDataBase64DecodingIgnoreUnknownCharacters];
            self.image = [UIImage imageWithData:data];
        }
        else
        {
            CDVPluginResult* result = [CDVPluginResult
                                       resultWithStatus:CDVCommandStatus_ERROR
                                       messageAsString:@"sourceType argument should be 0,1,2,3 or 4"];
            [self.commandDelegate sendPluginResult:result callbackId:_commandglo.callbackId];
        }
        
        
        if (self.image!=NULL)
        {
            self.textDetector = [GMVDetector detectorOfType:GMVDetectorTypeText options:nil];
            NSArray<GMVTextBlockFeature *> *features = [self.textDetector featuresInImage:self.image
                                                                                  options:nil];
            
            int count = 0;
            NSMutableString* blocks = [NSMutableString string];
            NSMutableString* lines = [NSMutableString string];
            NSMutableString* words = [NSMutableString string];
            NSMutableString* all = [NSMutableString string];
            // Iterate over each text block.
            for (GMVTextBlockFeature *textBlock in features) {
                count++;
                [blocks appendString:[textBlock.value mutableCopy]];
                [blocks appendString:@"\n"];
                [blocks appendString:@"\n"];
                
                // For each text block, iterate over each line.
                for (GMVTextLineFeature *textLine in textBlock.lines) {               
                    [lines appendString:[textLine.value mutableCopy]];
                    [lines appendString:@"\n"];
                    // For each line, iterate over each word.
                    for (GMVTextElementFeature *textElement in textLine.elements) {
                        [words appendString:[textElement.value mutableCopy]];
                        [words appendString:@","];  
                    }
                }
            }
            if (count==0) {
                CDVPluginResult* result = [CDVPluginResult
                                           resultWithStatus:CDVCommandStatus_ERROR
                                           messageAsString:@"No text in image"];
                [self.commandDelegate sendPluginResult:result callbackId:_commandglo.callbackId];
            }
            else {
                NSString* message;
                if (rtype==BLOCKS)
                {
                    message = blocks;
                }
                
                else if (rtype==LINES)
                {
                    message = lines;
                }
                
                else if (rtype==WORDS)
                {
                    message = words;
                }
                
                else if (rtype==ALL)
                {
                    [all appendString:blocks];
                    [all appendString:@("\n")];
                    [all appendString:lines];
                    [all appendString:@("\n")];
                    [all appendString:words];
                    message = all;
                }
                else
                {
                    CDVPluginResult* result = [CDVPluginResult
                                               resultWithStatus:CDVCommandStatus_ERROR
                                               messageAsString:@"Return Type can only be 0,1,2 oe 3"];
                    [self.commandDelegate sendPluginResult:result callbackId:_commandglo.callbackId];
                }
                CDVPluginResult* result = [CDVPluginResult
                                           resultWithStatus:CDVCommandStatus_OK
                                           messageAsString:message];
                [self.commandDelegate sendPluginResult:result callbackId:_commandglo.callbackId];
            }
        }
        else
        {
            CDVPluginResult* result = [CDVPluginResult
                                       resultWithStatus:CDVCommandStatus_ERROR
                                       messageAsString:@"Image was null"];
            [self.commandDelegate sendPluginResult:result callbackId:_commandglo.callbackId];
        }
    }];
}


-(UIImage *)resizeImage:(UIImage *)image
{
    float actualHeight = image.size.height;
    float actualWidth = image.size.width;
    float maxHeight = 600;
    float maxWidth = 600;
    float imgRatio = actualWidth/actualHeight;
    float maxRatio = maxWidth/maxHeight;
    float compressionQuality = 0.50;//50 percent compression
    
    if (actualHeight > maxHeight || actualWidth > maxWidth)
    {
        if(imgRatio < maxRatio)
        {
            //adjust width according to maxHeight
            imgRatio = maxHeight / actualHeight;
            actualWidth = imgRatio * actualWidth;
            actualHeight = maxHeight;
        }
        else if(imgRatio > maxRatio)
        {
            //adjust height according to maxWidth
            imgRatio = maxWidth / actualWidth;
            actualHeight = imgRatio * actualHeight;
            actualWidth = maxWidth;
        }
        else
        {
            actualHeight = maxHeight;
            actualWidth = maxWidth;
        }
    }
    
    CGRect rect = CGRectMake(0.0, 0.0, actualWidth, actualHeight);
    UIGraphicsBeginImageContext(rect.size);
    [image drawInRect:rect];
    UIImage *img = UIGraphicsGetImageFromCurrentImageContext();
    NSData *imageData = UIImageJPEGRepresentation(img, compressionQuality);
    UIGraphicsEndImageContext();
    return [UIImage imageWithData:imageData];
    
}

-(NSData *)retrieveAssetDataPhotosFramework:(NSURL *)urlMedia
{
    __block NSData *iData = nil;
    
    PHFetchResult *result = [PHAsset fetchAssetsWithALAssetURLs:@[urlMedia] options:nil];
    PHAsset *asset = [result firstObject];
    if (asset != nil)
    {
        PHImageManager *imageManager = [PHImageManager defaultManager];
        PHImageRequestOptions *options = [[PHImageRequestOptions alloc]init];
        options.synchronous = YES;
        options.version = PHImageRequestOptionsVersionCurrent;
        
        @autoreleasepool {
            [imageManager requestImageDataForAsset:asset options:options resultHandler:^(NSData *imageData, NSString *dataUTI, UIImageOrientation orientation, NSDictionary *info) {
                iData = [imageData copy];          
            }];
        }
        //assert(iData.length != 0);
        return iData;
    }
    else
    {
        return NULL;
    }
    
}

@end
