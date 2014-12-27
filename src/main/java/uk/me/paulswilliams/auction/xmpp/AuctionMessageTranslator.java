package uk.me.paulswilliams.auction.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import uk.me.paulswilliams.auction.AuctionEventListener;
import uk.me.paulswilliams.auction.AuctionEventListener.PriceSource;

import java.util.HashMap;
import java.util.Map;

import static uk.me.paulswilliams.auction.AuctionEventListener.PriceSource.FromOtherBidder;
import static uk.me.paulswilliams.auction.AuctionEventListener.PriceSource.FromSniper;

public class AuctionMessageTranslator implements MessageListener{
    private final String sniperId;
    private final AuctionEventListener listener;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.sniperId = sniperId;
        this.listener = listener;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        try {
            translate(message);
        } catch (Exception parseException) {
            listener.auctionFailed();
        }
    }

    private void translate(Message message) throws MissingValueException {
        AuctionEvent event = AuctionEvent.from(message.getBody());
        String eventType = event.type();
        if ("CLOSE".equals(eventType)) {
            listener.auctionClosed();
        } else if ("PRICE".equals(eventType)){
            listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId) );
        }
    }

    private static class AuctionEvent {
        HashMap<String, String> event = new HashMap<String, String>();
        private final Map<String, String> fields = new HashMap<String, String>();

        public String type() throws MissingValueException { return get("Event"); }
        public int currentPrice() throws MissingValueException { return getInt("CurrentPrice");}
        public int increment() throws MissingValueException { return getInt("Increment");}
        public PriceSource isFrom(String sniperId) throws MissingValueException {  return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder; }

        private String bidder() throws MissingValueException { return get("Bidder");}

        private int getInt(String fieldName) throws MissingValueException {return Integer.parseInt(get(fieldName));}
        private String get(String fieldName) throws MissingValueException {
            String value = fields.get(fieldName);
            if (null == value) {
                throw new MissingValueException(fieldName);
            }
            return value;
        }

        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();
            for (String field : fieldsIn(messageBody)) {
                event.addField(field);
            }
            return event;
        }

        private static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }

        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }
    }
}
