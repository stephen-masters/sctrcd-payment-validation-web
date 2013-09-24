package com.sctrcd.drools.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.UrlResource;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.AgendaFilter;
import org.drools.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initialises and encapsulates the various components required for the rules engine.
 * Includes initialising the knowledge base, creating a stateful session and attaching 
 * listeners for the default events.
 * 
 * @author Stephen Masters
 */
public class KnowledgeEnvironment {

    private static Logger log = LoggerFactory.getLogger(KnowledgeEnvironment.class);

    private DroolsResource[] resources;

    private KnowledgeBase kbase;
    private StatefulKnowledgeSession ksession;
    private TrackingAgendaEventListener agendaEventListener;
    private TrackingWorkingMemoryEventListener workingMemoryEventListener;

    /**
     * Constructor supporting setting up a knowledge environment using just a
     * list of resources, which may be local. Particularly useful when testing
     * DRL files.
     * 
     * @param resources
     */
    public KnowledgeEnvironment(DroolsResource[] resources) {
        initialise(resources);
    }

    /**
     * Constructor.
     * 
     * @param url The URL of the package via the Guvnor REST API.
     */
    public KnowledgeEnvironment(String url) {
        initialise(url);
    }

    /**
     * This constructor sets up a user name and password. Handy if you're
     * connecting to Guvnor and it's locked down.
     * 
     * @param url The URL of the package via the Guvnor REST API.
     * @param username The Guvnor user name.
     * @param password The Guvnor password.
     */
    public KnowledgeEnvironment(String url, String username, String password) {
        initialise(url, username, password);
    }

    /**
     * Initialises the knowledge environment by downloading the package from the
     * Guvnor REST interface, at the location defined in the URL.
     * 
     * @param url The URL of the package via the Guvnor REST API.
     */
    public void initialise(String url) {
        this.resources = new DroolsResource[] { 
            new DroolsResource(url,
                    ResourcePathType.URL, 
                    ResourceType.PKG
        )};
        initialise();
    }

    /**
     * Initialises the knowledge environment by downloading the package from the
     * Guvnor REST interface, at the location defined in the URL.
     * 
     * @param url The URL of the package via the Guvnor REST API.
     * @param username The Guvnor user name.
     * @param password The Guvnor password.
     */
    public void initialise(String url, String username, String password) {
        this.resources = new DroolsResource[] { 
                new DroolsResource(url, 
                        ResourcePathType.URL, 
                        ResourceType.PKG, 
                        username, 
                        password
        )};
        initialise();
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
        log.info("Initialising KnowledgeEnvironment with resources: " + this.resources);
        this.kbase = DroolsUtil.createKnowledgeBase(
                this.resources, 
                EventProcessingOption.STREAM);

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
        if (this.ksession == null) {
            this.ksession = kbase.newStatefulKnowledgeSession();
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
        log.info("Retracting all facts matching filter...");
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
     * retractAll(new ObjectFilter() {
     *     public boolean accept(Object object) {
     *         return object.getClass().getSimpleName()
     *                 .equals(MyObject.class.getSimpleName());
     *     }
     * });
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
        ksession.retract(handle);
    }


    public Object getObject(FactHandle handle) {
        if (handle == null) {
            return null;
        } else {
            return ksession.getObject(handle);
        }
    }

    public Collection<Object> getObjects(ObjectFilter filter) {
        return ksession.getObjects(filter);
    }

    /**
     * Find all handles to facts in working memory.
     * 
     * @return A collection of fact handles.
     */
    public Collection<FactHandle> getFactHandles() {
        return ksession.getFactHandles();
    }
    
    /**
     * Find all handles to facts in working memory matching an
     * {@link ObjectFilter}. For example, to find all facts of a class called
     * "MyObject":
     * 
     * <pre>
     * getFactHandles(new ObjectFilter() {
     *     public boolean accept(Object object) {
     *         return object.getClass().getSimpleName()
     *                 .equals(MyObject.class.getSimpleName());
     *     }
     * });
     * </pre>
     * 
     * @param filter
     *            The {@link ObjectFilter}.
     * @return A collection of facts matching the filter.
     */
    public Collection<FactHandle> getFactHandles(ObjectFilter filter) {
        return ksession.getFactHandles(filter);
    }

    public List<Activation> findActivations() {
        return agendaEventListener.getActivationList();
    }

    /**
     * Attaches a {@link WorkingMemoryEventListener} to the session.
     * 
     * @param listener The listener to be attached.
     */
    public void addEventListener(WorkingMemoryEventListener listener) {
        ksession.addEventListener(listener);
    }

    /**
     * Disconnects a {@link WorkingMemoryEventListener} from the session.
     * 
     * @param listener The listener to be disconnected.
     */
    public void removeEventListener(WorkingMemoryEventListener listener) {
        ksession.removeEventListener(listener);
    }

    /**
     * Creates a new knowledge base using a collection of resources.
     * 
     * @param resources
     *            An array of {@link DroolsResource} indicating where the
     *            various resources should be loaded from. These could be
     *            classpath, file or URL resources.
     * @return A new knowledge base.
     */
    public KnowledgeBase createKnowledgeBase(DroolsResource[] resources) {
        KnowledgeBuilder builder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();

        for (DroolsResource resource : resources) {
            log.info("Resource: " + resource.getType() + ", path type="
                    + resource.getPathType() + ", path=" + resource.getPath());
            switch (resource.getPathType()) {
            case CLASSPATH:
                builder.add(ResourceFactory.newClassPathResource(resource
                        .getPath()), resource.getType());
                break;
            case FILE:
                builder.add(
                        ResourceFactory.newFileResource(resource.getPath()),
                        resource.getType());
                break;
            case URL:
                UrlResource urlResource = (UrlResource) ResourceFactory
                        .newUrlResource(resource.getPath());
                
                if (resource.getUsername() != null) {
                    log.info("Setting authentication for: " + resource.getUsername());
                    urlResource.setBasicAuthentication("enabled");
                    urlResource.setUsername(resource.getUsername());
                    urlResource.setPassword(resource.getPassword());
                }
                
                builder.add(urlResource, resource.getType());
                
                break;
            default:
                throw new IllegalArgumentException(
                        "Unable to build this resource path type.");
            }
        }

        if (builder.hasErrors()) {
            throw new RuntimeException(builder.getErrors().toString());
        }

        KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption(EventProcessingOption.STREAM);

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(conf);
        knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());

        return knowledgeBase;
    }

    /**
     * 
     * @return A String detailing the packages and rules in this knowledge base.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (KnowledgePackage p : kbase.getKnowledgePackages()) {
            sb.append("\n  Package : " + p.getName());
            for (Rule r : p.getRules()) {
                sb.append("\n    Rule: " + r.getName());
            }
        }
        return "Knowledge base built with packages: " + sb.toString();
    }

    /**
     * Iterates through the facts currently in working memory, and logs their details.
     * 
     * @param session The session to search for facts.
     */
    public void printFacts(StatefulKnowledgeSession session) {
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

    public KnowledgeBase getKnowledgeBase() {
        return kbase;
    }

    public StatefulKnowledgeSession getKnowledgeSession() {
        return ksession;
    }

    public TrackingAgendaEventListener getAgendaEventListener() {
        return agendaEventListener;
    }

    public void setAgendaEventListener(
            TrackingAgendaEventListener agendaEventListener) {
        this.agendaEventListener = agendaEventListener;
    }

    public TrackingWorkingMemoryEventListener getWorkingMemoryEventListener() {
        return workingMemoryEventListener;
    }

    public void setWorkingMemoryEventListener(
            TrackingWorkingMemoryEventListener workingMemoryEventListener) {
        this.workingMemoryEventListener = workingMemoryEventListener;
    }

}
