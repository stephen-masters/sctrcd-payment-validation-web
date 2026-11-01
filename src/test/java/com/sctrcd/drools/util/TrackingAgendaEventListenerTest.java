package com.sctrcd.drools.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.rule.Match;

public class TrackingAgendaEventListenerTest {

    @Test
    public void shouldDetermineIfRuleFired() {
        TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        listener.afterMatchFired(createStubAfterMatchFiredEvent("A rule"));
        assertTrue(listener.isRuleFired("A rule"));
        assertFalse(listener.isRuleFired("Another rule"));
    }

    private AfterMatchFiredEvent createStubAfterMatchFiredEvent(final String ruleName) {
        Rule rule = mock(Rule.class);
        when(rule.getName()).thenReturn(ruleName);
        when(rule.getMetaData()).thenReturn(new HashMap<String, Object>());

        Match match = mock(Match.class);
        when(match.getRule()).thenReturn(rule);

        AfterMatchFiredEvent event = mock(AfterMatchFiredEvent.class);
        when(event.getMatch()).thenReturn(match);
        return event;
    }
}
