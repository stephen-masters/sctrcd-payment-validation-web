package com.sctrcd.drools.util;

import java.util.Arrays;
import java.util.List;
import org.kie.api.KieBase;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initialises and encapsulates the various components required for the rules engine.
 * Includes initialising the knowledge base, creating a stateless session and attaching
 * listeners for the default events.
 *
 * @author Stephen Masters
 */
public final class StatelessKnowledgeEnvironment {

    private static Logger log = LoggerFactory.getLogger(StatelessKnowledgeEnvironment.class);

    private DroolsResource[] resources;

    private KieBase knowledgeBase;
    private StatelessKieSession knowledgeSession;
    private TrackingAgendaEventListener agendaEventListener;
    private TrackingWorkingMemoryEventListener workingMemoryEventListener;

    /**
     * Constructor supporting setting up a knowledge environment using just a
     * list of resources, which may be local. Particularly useful when testing
     * DRL files.
     *
     * @param resources The classpath or file resources to build the knowledge base from.
     */
    public StatelessKnowledgeEnvironment(DroolsResource[] resources) {
        initialise(resources);
    }

    /**
     * Initialises the knowledge environment with multiple
     * {@link DroolsResource} locations.
     *
     * @param resources An array of {@link DroolsResource}.
     */
    public void initialise(DroolsResource[] resources) {
        this.resources = resources;
        initialise();
    }

    /**
     * Initialises the knowledge environment with multiple
     * {@link DroolsResource} locations, which should have been defined
     * previously in the constructor.
     */
    public void initialise() {
        log.info("Initialising KnowledgeEnvironment with resources: " + Arrays.toString(this.resources));
        this.knowledgeBase = createKnowledgeBase(this.resources);

        // Log a description of the new knowledge base.
        log.info(toString());

        initialiseSession();
    }

    /**
     * Starts up a new stateless session, and attaches a number of working
     * memory listeners.
     */
    public void initialiseSession() {
        log.info("Initialising session...");
        if (this.knowledgeSession == null) {
            this.knowledgeSession = knowledgeBase.newStatelessKieSession();
            this.agendaEventListener = new TrackingAgendaEventListener();
            this.knowledgeSession.addEventListener(this.agendaEventListener);
            this.workingMemoryEventListener = new TrackingWorkingMemoryEventListener();
            this.knowledgeSession.addEventListener(this.workingMemoryEventListener);
        } else {
            clearListeners();
        }
    }

    /**
     * Remove the existing working memory listeners, and set up some fresh ones.
     */
    public void clearListeners() {
        this.knowledgeSession.removeEventListener(this.agendaEventListener);
        this.knowledgeSession.removeEventListener(this.workingMemoryEventListener);

        this.agendaEventListener = new TrackingAgendaEventListener();
        this.workingMemoryEventListener = new TrackingWorkingMemoryEventListener();

        this.knowledgeSession.addEventListener(this.agendaEventListener);
        this.knowledgeSession.addEventListener(this.workingMemoryEventListener);
    }

    public void setGlobal(String name, Object fact) {
        this.knowledgeSession.setGlobal(name, fact);
    }

    public void execute(Iterable<Object> facts) {
        this.knowledgeSession.execute(facts);
    }

    /**
     * Attaches an {@link AgendaEventListener} to the session.
     *
     * @param listener The listener to be attached.
     */
    public void addEventListener(AgendaEventListener listener) {
        knowledgeSession.addEventListener(listener);
    }

    /**
     * Disconnects an {@link AgendaEventListener} from the session.
     *
     * @param listener The listener to be disconnected.
     */
    public void removeEventListener(AgendaEventListener listener) {
        knowledgeSession.removeEventListener(listener);
    }

    /**
     * Attaches a {@link RuleRuntimeEventListener} to the session.
     *
     * @param listener The listener to be attached.
     */
    public void addEventListener(RuleRuntimeEventListener listener) {
        knowledgeSession.addEventListener(listener);
    }

    /**
     * Disconnects a {@link RuleRuntimeEventListener} from the session.
     *
     * @param listener The listener to be disconnected.
     */
    public void removeEventListener(RuleRuntimeEventListener listener) {
        knowledgeSession.removeEventListener(listener);
    }

    /**
     * Creates a new knowledge base using a collection of resources.
     *
     * @param resources
     *            An array of {@link DroolsResource} indicating where the
     *            various resources should be loaded from. These could be
     *            classpath or file resources.
     * @return A new knowledge base.
     */
    public KieBase createKnowledgeBase(DroolsResource[] resources) {
        return DroolsUtil.createKnowledgeBase(resources, EventProcessingOption.STREAM);
    }

    public List<Activation> getActivationList() {
        return this.agendaEventListener.getActivationList();
    }

    /**
     *
     * @return A String detailing the packages and rules in this knowledge base.
     */
    @Override
    public String toString() {
        return DroolsUtil.knowledgeBaseDetails(knowledgeBase);
    }

    /**
     * Iterates through the facts currently in working memory, and logs their details.
     *
     * @param session The session to search for facts.
     */
    public void printFacts(KieSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n************************************************************");
        sb.append("\nThe following facts are currently in the system...");
        for (Object fact : session.getObjects()) {
            sb.append("\n\nFact: " + DroolsUtil.objectDetails(fact));
        }
        sb.append("\n************************************************************\n");
        log.info(sb.toString());
    }

    public TrackingAgendaEventListener getAgendaEventListener() {
        return agendaEventListener;
    }

    public void setAgendaEventListener(TrackingAgendaEventListener agendaEventListener) {
        this.agendaEventListener = agendaEventListener;
    }

    public TrackingWorkingMemoryEventListener getWorkingMemoryEventListener() {
        return workingMemoryEventListener;
    }

    public void setWorkingMemoryEventListener(TrackingWorkingMemoryEventListener workingMemoryEventListener) {
        this.workingMemoryEventListener = workingMemoryEventListener;
    }
}
