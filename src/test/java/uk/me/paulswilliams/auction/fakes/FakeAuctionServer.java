package uk.me.paulswilliams.auction.fakes;

public class FakeAuctionServer {
    public static final String XMPP_HOSTNAME = "localhost";
    private String itemId;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        
    }

    public void startSellingItem() {

    }

    public void hasReceivedJoinRequestFromSniper() {
    }

    public void announceClosed() {
    }

    public void stop() {
    }

    public String getItemId() {
        return itemId;
    }
}
