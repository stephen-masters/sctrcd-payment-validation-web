package com.sctrcd.drools.util;

import org.kie.api.io.ResourceType;

/**
 * A simple representation of a Drools resource.
 *
 * @author Stephen Masters
 */
public class DroolsResource {

    private String path;
    private ResourcePathType pathType;
    private ResourceType type;

    /**
     *
     * @param path
     *            The path to this resource.
     * @param pathType
     *            The type of path (CLASSPATH or FILE).
     * @param type
     *            The type of resource (DRL, DSL, decision table, etc)
     */
    public DroolsResource(String path, ResourcePathType pathType, ResourceType type) {
        this.path = path;
        this.pathType = pathType;
        this.type = type;
    }

    /**
     * @return The path to this resource.
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     *            The path to this resource.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return The type of path (CLASSPATH or FILE).
     */
    public ResourcePathType getPathType() {
        return pathType;
    }

    /**
     * @param pathType
     *            The type of path (CLASSPATH or FILE).
     */
    public void setPathType(ResourcePathType pathType) {
        this.pathType = pathType;
    }

    /**
     * @return The type of resource (DRL, DSL, decision table, etc)
     */
    public ResourceType getType() {
        return type;
    }

    /**
     * @param type
     *            The type of resource (DRL, DSL, decision table, etc)
     */
    public void setType(ResourceType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DroolsResource[type=" + type + ", pathType=" + pathType + ", path=" + path + "]";
    }
}
