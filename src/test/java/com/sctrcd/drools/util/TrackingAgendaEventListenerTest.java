package com.sctrcd.drools.util;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.definition.rule.Rule;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.PropagationContext;
import org.junit.Test;

public class TrackingAgendaEventListenerTest {

    @Test
    public void shouldDetermineIfRuleFired() {
        TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        listener.afterActivationFired(createStubAfterActivationFiredEvent("A rule"));
        assertTrue(listener.isRuleFired("A rule"));
        assertFalse(listener.isRuleFired("Another rule"));
    }
    
    private AfterActivationFiredEvent createStubAfterActivationFiredEvent(final String ruleName) {
        AfterActivationFiredEvent event = new AfterActivationFiredEvent() {
            @Override
            public KnowledgeRuntime getKnowledgeRuntime() {
                return null;
            }
            @Override
            public Activation getActivation() {
                return new Activation() {
                    @Override
                    public boolean isActive() {
                        return false;
                    }
                    @Override
                    public Rule getRule() {
                        return new Rule() {
                            @Override
                            public String getNamespace() {
                                return null;
                            }
                            @Override
                            public KnowledgeType getKnowledgeType() {
                                return null;
                            }
                            @Override
                            public String getId() {
                                return null;
                            }
                            @Override
                            @Deprecated
                            public Collection<String> listMetaAttributes() {
                                return null;
                            }
                            @Override
                            public String getPackageName() {
                                return null;
                            }
                            @Override
                            public String getName() {
                                return ruleName;
                            }
                            @Override
                            public Map<String, Object> getMetaData() {
                                return new HashMap<String, Object>();
                            }
                            @Override
                            @Deprecated
                            public Map<String, Object> getMetaAttributes() {
                                return null;
                            }
                            @Override
                            @Deprecated
                            public String getMetaAttribute(String key) {
                                return null;
                            }
                        };
                    }
                    @Override
                    public PropagationContext getPropagationContext() {
                        return null;
                    }
                    @Override
                    public List<Object> getObjects() {
                        return null;
                    }
                    @Override
                    public List<? extends FactHandle> getFactHandles() {
                        return null;
                    }
                    @Override
                    public Object getDeclarationValue(String declarationId) {
                        return null;
                    }
                    @Override
                    public List<String> getDeclarationIDs() {
                        return null;
                    }
                };
            }
        };
        return event;
    }

}
