package com.sctrcd.drools.util;

import static org.junit.Assert.*;

import org.drools.builder.ResourceType;
import org.drools.runtime.ObjectFilter;
import org.junit.Test;

import com.sctrcd.drools.util.DroolsResource;
import com.sctrcd.drools.util.KnowledgeEnvironment;
import com.sctrcd.drools.util.ResourcePathType;


public class KnowledgeEnvironmentTest {

    public static final ObjectFilter MESSAGE_FILTER = new ObjectFilter() {
        public boolean accept(Object object) {
            return object.getClass().getSimpleName()
                    .equals("Message");
        }
    };

    @Test
    public void shouldReloadOnCommand() {
        KnowledgeEnvironment kenv = new KnowledgeEnvironment(
                new DroolsResource[] {
            new DroolsResource("sctrcd/drools/util/KnowledgeEnvironmentTest.drl", ResourcePathType.CLASSPATH, ResourceType.DRL)});

        assertEquals(0, kenv.getFactHandles(MESSAGE_FILTER).size());
        kenv.insert(new Message("Hello Droolers!"));
        assertEquals(1, kenv.getFactHandles(MESSAGE_FILTER).size());
        kenv.initialise();
        assertEquals(0, kenv.getFactHandles(MESSAGE_FILTER).size());
        kenv.insert(new Message("Hello Droolers!"));
        assertEquals(1, kenv.getFactHandles(MESSAGE_FILTER).size());
    }

    public class Message {
        private String text;

        public Message(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
