
#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(RNPixelpass,NSObject)

RCT_EXTERN_METHOD(decode:(NSString *)parameter
                        resolve:(RCTPromiseResolveBlock)resolve
                         reject:(RCTPromiseRejectBlock)reject)


@end


