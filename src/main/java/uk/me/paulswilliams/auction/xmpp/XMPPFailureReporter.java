package uk.me.paulswilliams.auction.xmpp;

public interface XMPPFailureReporter {
    void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception);
}
