package uk.me.paulswilliams.auction.xmpp;

public class MissingValueException extends Exception {
    public MissingValueException(String fieldName) {
        super("Missing value for " + fieldName);
    }
}
