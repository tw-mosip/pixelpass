//
//  Zlib.swift
//
//
//  Created by Abhishek Paul on 16/04/24.
//

import Foundation
import Compression
class Zlib{
    func decompress(_ data: Data) -> Data? {
        if data.count <= 2 {
            return .init()
        }
        let size = 4 * data.count + 8 * 1024
        let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: size)
        let result = data.subdata(in: 2 ..< data.count).withUnsafeBytes {
            let read = compression_decode_buffer(buffer, size, $0.baseAddress!.bindMemory(to: UInt8.self, capacity: 1), data.count - 2, nil, COMPRESSION_ZLIB)
            return Data(bytes: buffer, count: read)
        } as Data
        buffer.deallocate()
        return result
    }
    
    
    func compress(data: String, algorithm: compression_algorithm = COMPRESSION_ZLIB) -> Data? {
        var sourceBuffer = Array(data.utf8)
        let destinationBufferSize = sourceBuffer.count + (sourceBuffer.count / 1000) + 12
        let destinationBuffer = UnsafeMutablePointer<UInt8>.allocate(capacity: destinationBufferSize)
        defer {
            destinationBuffer.deallocate()
        }
        let compressedSize = compression_encode_buffer(destinationBuffer, destinationBufferSize,
                                                       &sourceBuffer, sourceBuffer.count,
                                                       nil,
                                                       algorithm)
        if compressedSize == 0 {
            return nil
        }
        var compressedData = Data()
        compressedData.append(contentsOf: [0x78, 0x01])
        compressedData.append(Data(bytes: destinationBuffer, count: compressedSize))
        let checksum = Zlib.calculateAdler32(for: Data(sourceBuffer))
        var bigEndianChecksum = checksum.bigEndian
        compressedData.append(Data(bytes: &bigEndianChecksum, count: MemoryLayout<UInt32>.size))
        
        return compressedData
    }
    
    private static func calculateAdler32(for data: Data) -> UInt32 {
        let base: UInt32 = 65521
        var a: UInt32 = 1, b: UInt32 = 0
        for byte in data {
            a = (a + UInt32(byte)) % base
            b = (b + a) % base
        }
        
        return (b << 16) | a
    }
    
}
