import SwiftUI
import pixelpass
struct QRCodeView: View {
@State private var inputString = "Test QR Code Generation"
@State private var qrImageData: Data?

var pixelPass = PixelPass()

var body: some View {
    VStack {
        if let qrImageData = qrImageData, let uiImage = UIImage(data: qrImageData) {
            Image(uiImage: uiImage)
                .resizable()
                .interpolation(.none)
                .scaledToFit()
                .frame(width: 200, height: 200)
            Text("QR Code generated")
        } else {
            Text("Generating QR Code...")
                .onAppear {
                    qrImageData = pixelPass.generateQRCode(from: inputString, ecc: .M)
                }
        }
    }
}
}


@main
struct QrApp: App {
    var body: some Scene {
        WindowGroup {
            QRCodeView()
        }
    }
}
