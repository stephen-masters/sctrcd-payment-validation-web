package com.sctrcd.drools.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.kie.api.KieBase;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initialises and encapsulates the various components required for the rules engine.
 * Includes initialising the knowledge base, creating a stateful session and attaching
 * listeners for the default events.
 *
 * @author Stephen Masters
 */
public final class KnowledgeEnvironment {

    private static Logger log = LoggerFactory.getLogger(KnowledgeEnvironment.class);

    private DroolsResource[] resources;

    private KieBase kbase;
    private KieSession ksession;
    private TrackingAgendaEventListener agendaEventListener;
    private TrackingWorkingMemoryEventListener workingMemoryEventListener;

    /**
     * Constructor supporting setting up a knowledge environment using just a
     * list of resources, which may be local. Particularly useful when testing
     * DRL files.
     *
     * @param resources The classpath or file resources to build the knowledge base from.
     */
    public KnowledgeEnvironment(DroolsResource[] resources) {
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
        this.kbase = DroolsUtil.createKnowledgeBase(this.resources, EventProcessingOption.STREAM);

        // Log a description of the new knowledge base.
        log.info(toString());

        initialiseSession();
    }

    /**
     * Starts up a new stateful session, and attaches a number of working
     * memory listeners.
     */
    public void initialiseSession() {
        log.info("Initialising session...");
        if (this.ksession == null) {
            this.ksession = kbase.newKieSession();
            this.agendaEventListener = new TrackingAgendaEventListener();
            this.ksession.addEventListener(this.agendaEventListener);
            this.workingMemoryEventListener = new TrackingWorkingMemoryEventListener();
            this.ksession.addEventListener(this.workingMemoryEventListener);
        } else {
            retractAll();
            clearListeners();
        }
    }

    public void fireAllRules() {
        ksession.fireAllRules();
    }

    public void fireAllRules(AgendaFilter filter) {
        ksession.fireAllRules(filter);
    }

    /**
     * Remove the existing working memory listeners, and set up some fresh ones.
     */
    public void clearListeners() {
        this.ksession.removeEventListener(this.agendaEventListener);
        this.ksession.removeEventListener(this.workingMemoryEventListener);

        this.agendaEventListener = new TrackingAgendaEventListener();
        this.workingMemoryEventListener = new TrackingWorkingMemoryEventListener();

        this.ksession.addEventListener(this.agendaEventListener);
        this.ksession.addEventListener(this.workingMemoryEventListener);
    }

    public List<FactHandle> insert(Object... objects) {
        List<FactHandle> handles = new ArrayList<FactHandle>();
        for (Object o : objects) {
            handles.add(ksession.insert(o));
        }
        return handles;
    }

    public FactHandle insert(Object o) {
        return this.ksession.insert(o);
    }

    public List<FactHandle> insert(Collection<Object> facts) {
        List<FactHandle> handles = new ArrayList<FactHandle>();
        for (Object fact : facts) {
            handles.add(this.ksession.insert(fact));
        }
        return handles;
    }

    public void update(FactHandle handle, Object o) {
        this.ksession.update(handle, o);
    }

    /**
     * Attaches an {@link AgendaEventListener} to the session.
     *
     * @param listener The listener to be attached.
     */
    public void addEventListener(AgendaEventListener listener) {
        ksession.addEventListener(listener);
    }

    /**
     * Disconnects an {@link AgendaEventListener} from the session.
     *
     * @param listener The listener to be disconnected.
     */
    public void removeEventListener(AgendaEventListener listener) {
        ksession.removeEventListener(listener);
    }

    public void retractAll() {
        log.info("Retracting all facts...");
        for (FactHandle handle : getFactHandles()) {
            retract(handle);
        }
    }

    /**
     * Retract all fact handles from working memory, which match an
     * {@link ObjectFilter}. For example, to retract all facts of a
     * class called "MyObject":
     *
     * <pre>
     * retractAll(object -&gt; object.getClass().getSimpleName()
     *         .equals(MyObject.class.getSimpleName()));
     * </pre>
     *
     * @param filter
     *            The {@link ObjectFilter}.
     */
    public void retractAll(ObjectFilter filter) {
        log.info("Retracting all facts matching filter...");
        for (FactHandle handle : getFactHandles(filter)) {
            retract(handle);
        }
    }

    /**
     * The insert method accepts a list of arguments and returns a list of fact
     * handles. Therefore this is retract method which can accept such a list.
     *
     * @param handles
     *            The fact handles you wish to retract.
     */
    public void retract(List<FactHandle> handles) {
        for (FactHandle handle : handles) {
            retract(handle);
        }
    }

    public void retract(FactHandle handle) {
        ksession.delete(handle);
    }

    public Object getObject(FactHandle handle) {
        if (handle == null) {
            return null;
        } else {
            return ksession.getObject(handle);
        }
    }

    public Collection<Object> getObjects(ObjectFilter filter) {
        return new ArrayList<Object>(ksession.getObjects(filter));
    }

    /**
     * Find all handles to facts in working memory.
     *
     * @return A collection of fact handles.
     */
    public Collection<FactHandle> getFactHandles() {
        return new ArrayList<FactHandle>(ksession.getFactHandles());
    }

    /**
     * Find all handles to facts in working memory matching an
     * {@link ObjectFilter}. For example, to find all facts of a class called
     * "MyObject":
     *
     * <pre>
     * getFactHandles(object -&gt; object.getClass().getSimpleName()
     *         .equals(MyObject.class.getSimpleName()));
     * </pre>
     *
     * @param filter
     *            The {@link ObjectFilter}.
     * @return A collection of facts matching the filter.
     */
    public Collection<FactHandle> getFactHandles(ObjectFilter filter) {
        return new ArrayList<FactHandle>(ksession.getFactHandles(filter));
    }

    public List<Activation> findActivations() {
        return agendaEventListener.getActivationList();
    }

    /**
     * Attaches a {@link RuleRuntimeEventListener} to the session.
     *
     * @param listener The listener to be attached.
     */
    public void addEventListener(RuleRuntimeEventListener listener) {
        ksession.addEventListener(listener);
    }

    /**
     * Disconnects a {@link RuleRuntimeEventListener} from the session.
     *
     * @param listener The listener to be disconnected.
     */
    public void removeEventListener(RuleRuntimeEventListener listener) {
        ksession.removeEventListener(listener);
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

    /**
     *
     * @return A String detailing the packages and rules in this knowledge base.
     */
    @Override
    public String toString() {
        return DroolsUtil.knowledgeBaseDetails(kbase);
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

    public DroolsResource[] getResources() {
        return resources;
    }

    public KieBase getKnowledgeBase() {
        return kbase;
    }

    public KieSession getKnowledgeSession() {
        return ksession;
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
