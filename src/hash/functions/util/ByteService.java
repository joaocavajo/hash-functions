package hash.functions.util;

import static java.lang.Character.digit;
import static java.lang.Integer.toHexString;

import hash.functions.service.LoggerService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ByteService {

    private final LoggerService logger;

    public ByteService(LoggerService logger) {
        this.logger = logger;
    }

    public String bytesToHex(byte[] blockByte) {
        var hexValue = new StringBuilder(blockByte.length * 2);

        for (byte value : blockByte) {
            var v = value & 0xff;

            if (v < 16) {
                hexValue.append('0');
            }

            hexValue.append(toHexString(v));
        }

        return hexValue.toString();
    }
    
    public byte[] hexToBytes(String hexValue) {
        var hexValueSize = hexValue.length();
        var blockByte = new byte[hexValueSize / 2];
        
        for (int i = 0; i < hexValueSize; i = i + 2) {
            blockByte[i / 2] = (byte) ((digit(hexValue.charAt(i), 16) << 4) + digit(hexValue.charAt(i + 1), 16));
        }
        
        return blockByte;
    }

    public byte[] fileToBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            var message = logger.externalError(e.getMessage());
            throw new RuntimeException(message);
        }
    }

}