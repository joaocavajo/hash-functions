package hash.functions.service;

public class LoggerService {

    public void printHash(String firstHash) {
        System.out.printf("The hash function from the first block is: %s.", firstHash);
    }

    public String externalError(String error) {
        System.out.printf("Internal error: %s.", error);
        return "Error trying to process the hash function";
    }

}
