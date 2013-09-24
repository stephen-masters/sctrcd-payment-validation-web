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
import org.drools.runtime.StatelessKnowledgeSession;
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
public class StatelessKnowledgeEnvironment {

    private static Logger log = LoggerFactory.getLogger(StatelessKnowledgeEnvironment.class);

    private DroolsResource[] resources;

    private KnowledgeBase knowledgeBase;
    private StatelessKnowledgeSession knowledgeSession;
    private TrackingAgendaEventListener agendaEventListener;
    private TrackingWorkingMemoryEventListener workingMemoryEventListener;

    /**
     * Constructor supporting setting up a knowledge environment using just a
     * list of resources, which may be local. Particularly useful when testing
     * DRL files.
     * 
     * @param resources
     */
    public StatelessKnowledgeEnvironment(DroolsResource[] resources) {
        initialise(resources);
    }

    /**
     * Constructor.
     * 
     * @param url The URL of the package via the Guvnor REST API.
     */
    public StatelessKnowledgeEnvironment(String url) {
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
    public StatelessKnowledgeEnvironment(String url, String username, String password) {
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
            this.knowledgeSession = knowledgeBase.newStatelessKnowledgeSession();
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
     * Attaches a {@link WorkingMemoryEventListener} to the session.
     * 
     * @param listener The listener to be attached.
     */
    public void addEventListener(WorkingMemoryEventListener listener) {
        knowledgeSession.addEventListener(listener);
    }
    
    /**
     * Disconnects a {@link WorkingMemoryEventListener} from the session.
     * 
     * @param listener The listener to be disconnected.
     */
    public void removeEventListener(WorkingMemoryEventListener listener) {
        knowledgeSession.removeEventListener(listener);
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
    
    public List<Activation> getActivationList() {
        return this.agendaEventListener.getActivationList();
    }

    /**
     * 
     * @return A String detailing the packages and rules in this knowledge base.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (KnowledgePackage p : knowledgeBase.getKnowledgePackages()) {
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
