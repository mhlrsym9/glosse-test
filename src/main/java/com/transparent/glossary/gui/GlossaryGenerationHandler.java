package com.transparent.glossary.gui;

/**
 * Objects that implement this interface are interested when the user says he wants to generate glossary files.
 */
public interface GlossaryGenerationHandler
{
    /**
     * Invoked when the user has indicated she wants generation to begin.
     * @param filePrefix what to name file, may be null if the user doesn't want to override the defaults.
     * @param l1Suffix what to suffix the L1 file with, may be null if the user doesn't want to override the defaults.
     * @param l2Suffix what to suffix the L2 file with, may be null if the user doesn't want to override the defaults.
     */
    void handleGenerateGlossarySelected( final String filePrefix, final String l1Suffix, final String l2Suffix );
}
