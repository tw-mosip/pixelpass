import { NativeModules, Platform } from '../node_modules/react-native';

const LINKING_ERROR =
  `The package 'react-native' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const RNPixelpass = NativeModules.RNPixelpass
  ? NativeModules.RNPixelpass
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export default RNPixelpass;
