package uk.me.paulswilliams.auction.xmpp;

import org.junit.AfterClass;
import org.junit.Test;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LoggingXMPPFailureReporterTest  {
    private final Logger logger = mock(Logger.class);
    private final LoggingXMPPFailureReporter reporter = new LoggingXMPPFailureReporter(logger);

    @AfterClass
    public static void resetLogging() {
        LogManager.getLogManager().reset();
    }

    @Test
    public void writesMessageTranslationFailureToLog() {
        reporter.cannotTranslateMessage("auction id", "bad message", new Exception("bad"));
        verify(logger).severe("<auction id> Could not translate message \"bad message\" because \"java.lang.Exception: bad\"");
    }
}