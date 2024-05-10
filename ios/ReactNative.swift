import Foundation
import pixelpass
import React
@objc(RNPixelpass)
class RNPixelpass: NSObject, RCTBridgeModule {
    static func moduleName() -> String {
        return "RNPixelpass"
    }
    
    @objc(decode:resolver:rejecter:)
    func decode(_ parameter: String, resolve:  RCTPromiseResolveBlock, reject:  RCTPromiseRejectBlock) {
        do {
            let pixelPass = PixelPass()
          guard let result = pixelPass.decode(data:parameter) else { return resolve("Failed") }
          resolve(String(data:result, encoding: .ascii))
        } catch {
            reject("ERROR", "Failed to de code", error)
        }
    }
   
  @objc
  static func requiresMainQueueSetup() -> Bool {
    return true;
  }
}



