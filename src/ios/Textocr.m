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
            NSLog(@"%s","Iniiiiiiii!!!");
            
            //stype = (int)[[_commandglo arguments] objectAtIndex:0];
            NSString *st = [[_commandglo arguments] objectAtIndex:0];
            NSLog(@"st : %@",st);
            stype = [st intValue];
            //stype = 2;
            NSLog(@"stype : %d",stype);
            // 0 NORMFILEURI
            // 1 NORMNATIVEURI
            // 2 FASTFILEURI
            // 3 FASTNATIVEURI
            // 4 BASE64
            
            //rtype = (int)[[_commandglo arguments] objectAtIndex:1];
            NSString *rt = [[_commandglo arguments] objectAtIndex:1];
            NSLog(@"rt : %@",rt);
            rtype = [rt intValue];
            NSLog(@"rtype : %d",rtype);
            // 0 BLOCKS
            // 1 LINES
            // 2 WORDS
            // 3 ALL
            
            name = [[_commandglo arguments] objectAtIndex:2];
            //Imagedata that is either a uri or base64
            NSLog(@"%s","Ini done!!!");
        }
        @catch (NSException *exception) {
            CDVPluginResult* result = [CDVPluginResult
                                       resultWithStatus:CDVCommandStatus_ERROR
                                       messageAsString:@"argument/parameter type mismatch error"];
            [self.commandDelegate sendPluginResult:result callbackId:_commandglo.callbackId];
        }
        
        if (stype == NORMFILEURI || stype == NORMNATIVEURI || stype == FASTFILEURI || stype == FASTNATIVEURI)
        {
            NSLog(@"%s","file mesertyuiosing!!!");
            //            @try // remove file:// from outputs of camera plugin and scan plugin to do
            //            {
            //                NSString *filesubString = [name substringWithRange:NSMakeRange(0,7)];
            //                NSLog(@"filesubString: %@", filesubString);
            //                if ([filesubString  isEqual: @("file://")]) {
            //                    //name = [name stringByReplacingCharactersInRange:NSMakeRange(0,7) withString:@("")];
            //                }
            //                NSLog(@"NAMEURL: %@", name);
            //            }
            //            @catch (NSException *exception) {
            //                CDVPluginResult* result = [CDVPluginResult
            //                                           resultWithStatus:CDVCommandStatus_ERROR
            //                                           messageAsString:@"uri Or Base string manipulation error"];
            //                [self.commandDelegate sendPluginResult:result callbackId:_commandglo.callbackId];
            //            }
            NSLog(@"NAMEURL: %@", name);
            NSLog(@"%s","what am i??!!!");
            if (stype==NORMFILEURI)
            {
                NSLog(@"%s","JUST NORMAL!!!");
                NSData *imageData = [NSData dataWithContentsOfURL:[NSURL URLWithString:name]];
                self.image = [UIImage imageWithData:imageData];
                NSLog(@"%s","OR MAYBE NOTT!!!");
            }
            else if (stype==NORMNATIVEURI)
            {
                NSString *urlString = [NSString stringWithFormat:@"%@", name];
                NSURL *url = [NSURL URLWithString:[urlString stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]]];
                NSLog(@"urlmediaiswahwah :- (%@)", url);
                NSData *imageData = [self retrieveAssetDataPhotosFramework:url];
                NSLog(@"urlmediaiswahwah :- (%@)", imageData);
                NSLog(@"%s","NORMNATIVEURI huhu!!!");
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
                NSLog(@"%s","NORMNATIVEURI huhu!!!");
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
            NSLog(@"%s","DETECTORRRRRR!!!");
            int count = 0;
            NSMutableString* blocks = [NSMutableString string];
            NSMutableString* lines = [NSMutableString string];
            NSMutableString* words = [NSMutableString string];
            NSMutableString* all = [NSMutableString string];
            // Iterate over each text block.
            for (GMVTextBlockFeature *textBlock in features) {
                count++;
                
                NSLog(@"Text Block: %@", NSStringFromCGRect(textBlock.bounds));
                NSLog(@"lang: %@ value: %@", textBlock.language, textBlock.value);
                
                [blocks appendString:[textBlock.value mutableCopy]];
                [blocks appendString:@"\n"];
                [blocks appendString:@"\n"];
                NSLog(@"blocks: %@",blocks);
                // For each text block, iterate over each line.
                for (GMVTextLineFeature *textLine in textBlock.lines) {
                    NSLog(@"Text Line: %@", NSStringFromCGRect(textLine.bounds));
                    NSLog(@"lang: %@ value: %@", textLine.language, textLine.value);
                    
                    [lines appendString:[textLine.value mutableCopy]];
                    [lines appendString:@"\n"];
                    NSLog(@"lines: %@",lines);
                    
                    // For each line, iterate over each word.
                    for (GMVTextElementFeature *textElement in textLine.elements) {
                        NSLog(@"Text Element: %@", NSStringFromCGRect(textElement.bounds));
                        NSLog(@"value: %@", textElement.value);
                        
                        [words appendString:[textElement.value mutableCopy]];
                        [words appendString:@","];
                        NSLog(@"words: %@",words);
                        
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
                NSLog(@"%s","It worked!!!");
                NSString* message;
                if (rtype==BLOCKS)
                {
                    NSLog(@"%d",rtype);
                    
                    message = blocks;
                    NSLog(@"%@",blocks);
                    NSLog(@"%@",message);
                }
                
                else if (rtype==LINES)
                {
                    message = lines;
                    NSLog(@"%@",lines);
                    NSLog(@"%@",message);
                }
                
                else if (rtype==WORDS)
                {
                    message = words;
                    NSLog(@"%@",words);
                    NSLog(@"%@",message);
                }
                
                else if (rtype==ALL)
                {
                    [all appendString:blocks];
                    [all appendString:@("\n")];
                    [all appendString:lines];
                    [all appendString:@("\n")];
                    [all appendString:words];
                    message = all;
                    NSLog(@"%@",all);
                    NSLog(@"%@",message);
                }
                else
                {
                    CDVPluginResult* result = [CDVPluginResult
                                               resultWithStatus:CDVCommandStatus_ERROR
                                               messageAsString:@"Return Type can only be 0,1,2 oe 3"];
                    [self.commandDelegate sendPluginResult:result callbackId:_commandglo.callbackId];
                }
                NSLog(@"%@",message);
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
    NSLog(@"urlmediaiswahwah :- (%@)", urlMedia);
    
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
                NSLog(@"requestImageDataForAsset returned info(%@)", iData);
                NSLog(@"requestImageDataForAsset returned info(%@)", info);
                
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
