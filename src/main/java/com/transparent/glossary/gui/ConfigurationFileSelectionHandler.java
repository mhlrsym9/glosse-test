package com.transparent.glossary.gui;

import java.io.File;

/**
 * Objects that implement this interface are interested when the user picks a configuration file to use.
 */
public interface ConfigurationFileSelectionHandler
{
    /**
     * Callback for when the user picks a file.
     * @param selection selected file.
     */
    void handleConfigurationFileSelected( final File selection );
}
