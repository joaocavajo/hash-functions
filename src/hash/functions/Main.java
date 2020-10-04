package hash.functions;

import hash.functions.service.HashService;
import hash.functions.service.LoggerService;
import hash.functions.util.ByteService;

/**
 * @author João Vitor Brasil
 */
public class Main {

    public static void main(String[] args) {
        var fileName = "FuncoesResumo.mp4";

        var logger = new LoggerService();
        var byteToString = new ByteService(logger);
        var appService = new HashService(byteToString, logger);

        var firstHash = appService.getFirstBlockHash(fileName);

        logger.printHash(firstHash);

    }
}
