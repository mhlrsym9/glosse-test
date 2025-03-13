package com.transparent.glossary.gui;

/**
 * Abstracts the interaction between the Glossary generator and the GUI.
 */
public interface ProcessingEngine
{
    /**
     * Tells the engine to use the state contained in the model and to initiate processing.
     * @param model settings the engine should use.
     */
    void generateFiles( final Model model );

    /**
     * Reset the engine's state back to initial values.
     */
    void reset();
}
