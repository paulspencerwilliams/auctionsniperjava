package uk.me.paulswilliams.auction;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import uk.me.paulswilliams.auction.AuctionEventListener.PriceSource;

import java.util.HashMap;
import java.util.Map;

import static uk.me.paulswilliams.auction.AuctionEventListener.PriceSource.*;

public class AuctionMessageTranslator implements MessageListener {
    private final String sniperId;
    private AuctionEventListener listener;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.sniperId = sniperId;
        this.listener = listener;
    }

    public void processMessage(Chat chat, Message message) {
        AuctionEvent event = AuctionEvent.from(message.getBody());

        String eventType = event.type();
        if ("CLOSE".equals(eventType)) {
            listener.auctionClosed();
        }
        else if("PRICE".equals(eventType)) {
            listener.currentPrice(event.currentPrice(),
                    event.increment(),
                    event.isFrom(sniperId));
        }
    }

    private static class AuctionEvent {

        private final Map<String, String> fields = new HashMap<String,
                String>();
        public String type() {return get("Event");}
        public int currentPrice() {return getInt("CurrentPrice");}
        public int increment() {return getInt("Increment");}
        public PriceSource isFrom(String sniperId) {
            return sniperId.equals(bidder())? FromSniper : FromOtherBidder;
        }

        private String bidder() {
            return get("Bidder");
        }

        private int getInt(String fieldName) {
            return Integer.parseInt(get(fieldName));
        }

        private String get(String fieldName) {
            return fields.get(fieldName);
        }

        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();
            for(String field : fieldsIn(messageBody)) {
                event.addField(field);
            }
            return event;
        }

        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }

        static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }


    }
}
