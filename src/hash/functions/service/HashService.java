package hash.functions.service;

import static java.lang.Math.min;
import static java.lang.System.arraycopy;
import static java.security.MessageDigest.getInstance;
import static java.util.Arrays.copyOfRange;
import static java.util.Optional.ofNullable;

import hash.functions.util.ByteService;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;

public class HashService {

    private final ByteService byteService;
    private final LoggerService logger;

    public HashService(ByteService byteService, LoggerService logger) {
        this.byteService = byteService;
        this.logger = logger;
    }


    public String getFirstBlockHash(String fileName) {
        // Get file path
        var path = Paths.get(fileName);

        // Get bytes from a file
        var fileBytes = byteService.fileToBytes(path);

        // Split bytes with the minor chunk size and convert to hex
        var stack = getHexStack(fileBytes);

        // Merge each block with the previous until be the first one, then return it
        return getHashByFileStack(stack);
    }

    private String getHashByFileStack(LinkedList<String> stack) {
        byte[] fileHash = null;

        while (!stack.isEmpty()) {
            var block = byteService.hexToBytes(stack.removeFirst());

            if (fileHash != null) {
                block = mergePreviousBlockAndHash(block, fileHash);
            }

            fileHash = calculateBlockHash(block);
        }

        return ofNullable(fileHash)
                .map(byteService::bytesToHex)
                .orElseThrow(() -> new RuntimeException("Error trying to process the hash function"));
    }

    private byte[] calculateBlockHash(byte[] block) {
        try {
            var digest = getInstance("SHA-256");
            digest.update(block, 0, block.length);

            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            var message = logger.externalError(e.getMessage());
            throw new RuntimeException(message);
        }

    }

    private byte[] mergePreviousBlockAndHash(byte[] block, byte[] fileHash) {
        byte[] mergedBlock = new byte[block.length + fileHash.length];

        arraycopy(block, 0, mergedBlock, 0, block.length);
        arraycopy(fileHash, 0, mergedBlock, block.length, fileHash.length);

        return mergedBlock;
    }

    private LinkedList<String> getHexStack(byte[] fileBytes) {
        var hexStack = new LinkedList<String>();

        var chunkSplit = splitByChunkSize(fileBytes);

        chunkSplit.forEach(a ->
                hexStack.push(byteService.bytesToHex(a)));

        return hexStack;
    }

    private ArrayList<byte[]> splitByChunkSize(byte[] allFileBytes) {
        var chunkSplit = new ArrayList<byte[]>();
        var chunkSize = 1024;

        for (int i = 0; i < allFileBytes.length; i = i + chunkSize) {
            var minorChunk = min(allFileBytes.length, i + chunkSize);
            chunkSplit.add(copyOfRange(allFileBytes, i, minorChunk));
        }

        return chunkSplit;
    }
}