package com.transparent.glossary.gui;

/**
 * Defines how the system should shutdown.
 */
public interface ExitStrategy
{
    /**
     * Will shutdown the system and close out open resources.
     */
    void shutdown();
}
