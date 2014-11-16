package uk.me.paulswilliams.auction.fakes;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SingleMessageListener implements MessageListener {
	private ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(1);

	@Override
	public void processMessage(Chat chat, Message message) {
		messages.add(message);
	}

	public void receivesAMessage(Matcher<? super String> messageMatcher) throws
			InterruptedException {
		final Message message = messages.poll(5, TimeUnit.SECONDS);
		assertThat("Message", message, is(CoreMatchers.notNullValue()));
		assertThat(message.getBody(), messageMatcher);
	}
}
