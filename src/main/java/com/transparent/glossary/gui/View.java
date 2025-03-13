package com.transparent.glossary.gui;

import javax.swing.tree.TreeNode;

/**
 * Describes the required behaviors of the View implementations.
 */
public interface View
{

    /**
     * Instructs the view to make itself visible.
     */
    void display();

    /**
     * Who should be called when the user requests an exit from the program.
     * @param handler object to call when a shutdown in occurring.
     */
    void registerShutdownHandler( final ShutdownHandler handler );

    /**
     * Who should be called when the user selects the location of the content directory.
     * @param handler object to call when the selection is made.
     */
    void registerContentSelectionHandler( final ContentDirectorySelectionHandler handler );

    /**
     * Who should be called when the user selects the location of the destination directory.
     * @param handler object to call when the selection is made.
     */
    void registerDestinationSelectionHandler( final DestinationDirectorySelectionHandler handler );

    /**
     * Who should be called when the user selects the location of the configuration file.
     * @param handler object to call when the selection is made.
     */
    void registerConfigurationFileSelectionHandler( final ConfigurationFileSelectionHandler handler );

    /**
     * Who should be called when the user selects the location of the internal list map file.
     * @param handler object to call when the selection is made
     */
    void registerInternalListNameMapFileSelectionHandler(final InternalListNameMapFileSelectionHandler handler);

    /**
     * Who should be called when the user wishes to generate glossary files.
     * @param handler object to call when the decision is made.
     */
    void registerGlossaryGenerationHandler( final GlossaryGenerationHandler handler );

    /**
     * Who should be called when the user changes his mind about MARSOC support.
     * @param handler object to call when the decision is made.
     */
    void registerMarsocSelectionHandler( final MarsocSelectionHandler handler );

    /**
     * Who should be called when the user changes his mind about internal list name map support.
     * @param handler object to call when the decision is made.
     */
    void registerInternalListNameMapSelectionHandler(final InternalListNameMapSelectionHandler handler);

    /**
     * Who should be called when the user selects the stop word mode to use.
     * @param handler object to call when the decision is made.
     */
    void registerStopWordSelectionHandler( final StopWordSelectionHandler handler );

    /**
     * Who should be called when the user selects the extraction approach mode to use.
     * @param handler object to call when the decision is made.
     */
    void registerExtractionApproachSelectionHandler( final ExtractionApproachSelectionHandler handler);

    /**
     * Who should be called when the user selects reset from the menu.
     * @param handler object to call when the decision is made.
     */
    void registerResetSelectionHandler( final ResetSelectionHandler handler );

    /**
     * Instructs the view to display the content path to the user.
     * @param path path to display.
     */
    void displayContentDirectory( final String path );

    /**
     * Instructs the view to display the destination path to the user.
     * @param path path to display.
     */
    void displayDestinationDirectory( final String path );

    /**
     * Instructs the view to display the configuration file to the user.
     * @param path path to display.
     */
    void displayConfigurationFile( final String path );

    /**
     * Instructs the view to display the internal list name file to the user.
     * @param name name to display
     */
    void displayInternalListNameMapFile(final String name);

    /**
     * Instructs the view to display an error dialog to the user.
     * @param message text to display to the user.
     */
    void displayError( final String message );

    /**
     * Instructs the view to display a completion dialog to the user.
     * @param message text to display to the user.
     */
    void displayCompletionMessage( final String message );

    /**
     * Instructs the view to enable/disable components that allow the user to start the glossary generation process.
     * @param state if true, then enable generation, disable otherwise.
     */
    void enableGeneration( final boolean state );

    /**
     * Instructs the view to enable/disable the internal list name map check box
     * @param state if true, then enable check box, disable otherwise.
     */
    void enableInternalListNameMapCheckBox(final boolean state);

    /**
     * Instructs the view to enable/disable the browse button to select the internal list name map file.
     * @param state if true, then enable button, disable otherwise.
     */
    void enableInternalListNameMapBrowseButton(final boolean state);

    /**
     * Instructs the view to reset the components back to their original starting state.
     */
    void reset();

    /**
     * Instructs the view to display the glossary data to the user in a tree structure.
     * @param root tip of the data tree.
     */
    void displayGlossary( final TreeNode root );
}
