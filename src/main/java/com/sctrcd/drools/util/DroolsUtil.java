package com.sctrcd.drools.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.UrlResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sctrcd.beans.BeanMatcher;


/**
 * 
 * @author Stephen Masters
 */
public class DroolsUtil {

    private static Logger log = LoggerFactory.getLogger(DroolsUtil.class);
    
    private static BeanMatcher matcher = new BeanMatcher();

    public static KnowledgeBase createKnowledgeBase(
            DroolsResource[] resources) {
        return createKnowledgeBase(resources, EventProcessingOption.STREAM);
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
    public static KnowledgeBase createKnowledgeBase(
            DroolsResource[] resources,
            EventProcessingOption eventProcessingOption) {
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
                    log.info("Setting authentication for: "
                            + resource.getUsername());
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

        KnowledgeBaseConfiguration conf = KnowledgeBaseFactory
                .newKnowledgeBaseConfiguration();
        conf.setOption(eventProcessingOption);

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory
                .newKnowledgeBase(conf);
        knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());

        // Output the packages in this knowledge base.
        Collection<KnowledgePackage> packages = knowledgeBase
                .getKnowledgePackages();

        StringBuilder sb = new StringBuilder();
        for (KnowledgePackage p : packages) {
            sb.append("\n  Package : " + p.getName());
            for (Rule r : p.getRules()) {
                sb.append("\n    Rule: " + r.getName());
            }
        }
        log.info("Knowledge base built with packages: " + sb.toString());

        return knowledgeBase;
    }
    
    /**
     * Return a string containing the packages used to build the knowledge base.
     */
    public static String knowledgeBaseDetails(KnowledgeBase kbase) {
        if (kbase == null) {
            return "Knowledge Base is null.";
        } else {
            StringBuilder sb = new StringBuilder(
                    "Knowledge base built from the following packages:");
            Collection<KnowledgePackage> packages = kbase
                    .getKnowledgePackages();
            for (KnowledgePackage kp : packages) {
                sb.append("\n    Package: [" + kp.getName() + "]");
                for (Rule rule : kp.getRules()) {
                    sb.append("\n        Rule: [" + rule.getName() + "]");
                }
            }
            return sb.toString();
        }
    }

    public static String objectDetails(Object o) {
        StringBuilder sb = new StringBuilder(o.getClass().getSimpleName());

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> objectProperties = BeanUtils.describe(o);
            for (String k : objectProperties.keySet()) {
                sb.append(", " + k + "=\"" + objectProperties.get(k) + "\"");
            }
        } catch (IllegalAccessException e) {
            return "IllegalAccessException attempting to parse object.";
        } catch (InvocationTargetException e) {
            return "InvocationTargetException attempting to parse object.";
        } catch (NoSuchMethodException e) {
            return "NoSuchMethodException attempting to parse object.";
        }

        return sb.toString();
    }

    public static Object findInsertedFact(List<ObjectInsertedEvent> insertions,
            String factType, String[] filters) {
        for (ObjectInsertedEvent event : insertions) {
            Object fact = event.getObject();

            if (factType.equals(fact.getClass().getSimpleName())) {
                if (matcher.matches(fact, filters)) {
                    return fact;
                }
            }
        }
        return null;
    }

    /**
     * Search for an activation by rule name. Note that when using decision
     * tables, the rule name is generated as <code>Row N Rule_Name</code>. This
     * means that we can't just search for exact matches. This method will
     * therefore return true if an activation ends with the ruleName argument.
     * 
     * @param ruleName
     *            The name of the rule we're looking for.
     */
    public static boolean ruleFired(List<Activation> activations,
            String ruleName) {
        for (Activation activation : activations) {
            if (activation.getRuleName().toUpperCase()
                    .endsWith(ruleName.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a list of rule activations and a list of rule names, did all of the
     * rules in the list provided get fired? This is a utility method designed
     * primarily to help with testing.
     * 
     * @param ruleName
     *            The list of rule names we're looking for.
     */
    public static boolean allRulesFired(List<Activation> activations,
            String[] ruleNames) {
        for (String ruleName : ruleNames) {
            if (!ruleFired(activations, ruleName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a <code>String</code> showing the rule names that were expected
     * to fire, and whether or not they fired, ready to be logged.
     * 
     * @param ruleName
     *            The list of rule names we're looking for.
     */
    public static String prettyRulesFired(List<Activation> activations,
            String[] ruleNames) {
        StringBuilder sb = new StringBuilder();
        for (String ruleName : ruleNames) {
            sb.append("\n    " + ruleName + " : "
                    + (ruleFired(activations, ruleName) ? "Y" : "N"));
        }
        return sb.toString();
    }

}
