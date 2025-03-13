package com.transparent.glossary.gui;

import com.transparent.glossary.TreeModelBuilder;
import com.transparent.glossary.processor.StopWordProcessingType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
Manages the interactions between the View and the rest of the system.
 */
public class Mediator implements ShutdownHandler,
                                 ContentDirectorySelectionHandler,
                                 DestinationDirectorySelectionHandler,
                                 ConfigurationFileSelectionHandler,
                                 GlossaryGenerationHandler,
                                 MarsocSelectionHandler,
                                 ProcessingEngineStatusHandler,
                                 ResetSelectionHandler,
                                 StopWordSelectionHandler, ExtractionApproachSelectionHandler, InternalListNameMapFileSelectionHandler, InternalListNameMapSelectionHandler
{
    /**
     * Message logger to use.
     */
    private final Logger theLogger = Logger.getLogger( Mediator.class );

    /**
     * The graphic portion of the user interface.
     */
    private final View theView;

    /**
     * Holds the data being shuttled between the GUI and processing engine.
     */
    private final Model theModel;

    /**
     * Interacts with the glossary processing engine.
     */
    private ProcessingEngine theEngine;

    /**
     * Indicates whether or not the source directory has been selected or not.
     */
    private boolean theSourceSelected = false;

    /**
     * Indicates whether or not the destination directory has been selected or not.
     */
    private boolean theDestinationSelected = false;

    /**
     * Indicates whether or not the internal list name map file has been selected or not.
     */
    private boolean theInternalListNameMapSelected = false;

    /**
     * Handles the closing of resources and shutting down of the system.
     */
    private final ExitStrategy theExitStrategy;

    /**
     * Constructs the tree model from the glossary files.
     */
    private final TreeModelBuilder theTreeModelBuilder;

    /**
     * The root of the glossary data tree.
     */
    private DefaultMutableTreeNode theRoot = new DefaultMutableTreeNode( "Glossary" );

    public Mediator( final View aView,
                     final Model aModel,
                     final ExitStrategy aStrategy,
                     final TreeModelBuilder aTreeModelBuilder )
    {
        theView = aView;
        theModel = aModel;
        theExitStrategy = aStrategy;
        theTreeModelBuilder = aTreeModelBuilder;
        theView.registerShutdownHandler(this);
        theView.registerContentSelectionHandler(this);
        theView.registerDestinationSelectionHandler(this);
        theView.registerConfigurationFileSelectionHandler(this);
        theView.registerGlossaryGenerationHandler(this);
//        theView.registerMarsocSelectionHandler( this );
        theView.registerStopWordSelectionHandler( this );
        theView.registerExtractionApproachSelectionHandler(this);
        theView.registerResetSelectionHandler(this);
        theView.registerInternalListNameMapFileSelectionHandler(this);
        theView.registerInternalListNameMapSelectionHandler(this);
    }

    public void setEngine( final ProcessingEngine engine )
    {
        theEngine = engine;
    }

    /**
     * Instructs the mediator to bring up the UI.
     */
    public void start()
    {
        theView.display();
    }

    @Override
    public void handleShutdownRequest()
    {
        theLogger.trace( "Shutdown has been requested" );
        theExitStrategy.shutdown();
    }

    @Override
    public void handleContentDirectorySelected( final File selection )
    {
        theSourceSelected = true;
        theModel.setSourcePath( selection.getPath().trim() );
        theView.displayContentDirectory(theModel.getTheSourcePath());
        theView.enableGeneration(requiredDataProvided());
    }

    @Override
    public void handleDestinationDirectorySelected( final File selection )
    {
        theDestinationSelected = true;
        theModel.setDestinationPath(selection.getPath().trim());
        theView.displayDestinationDirectory(theModel.getDestinationPath());
        theView.enableGeneration(requiredDataProvided());
    }

    @Override
    public void handleConfigurationFileSelected( final File selection )
    {
        final String path = selection.getPath().trim();
        theModel.setCustomConfigurationPath(path);
        theView.displayConfigurationFile(path);
    }

    @Override
    public void handleInternalListNameMapFileSelected(final File selection)
    {
        theInternalListNameMapSelected = true;

        final String absolutePath = selection.getAbsolutePath().trim();
        theModel.setTheInternalListNameMapAbsolutePath(absolutePath);

        final String name = selection.getName().trim();
        theView.displayInternalListNameMapFile(name);
        theView.enableGeneration(requiredDataProvided());
    }

    @Override
    public void handleInternalListNameMapChange(final boolean state)
    {
        theModel.setSupportInternalListNameMap(state);
        theView.enableInternalListNameMapBrowseButton(state);
        theView.enableGeneration(requiredDataProvided());
    }

    @Override
    public void handleGenerateGlossarySelected( final String filePrefix, final String l1Suffix, final String l2Suffix )
    {
        if ( isDirectoryValid( theModel.getTheSourcePath(), "The source path has not been selected.  Please do so now." ) )
        {
            if ( isDirectoryValid( theModel.getDestinationPath(), "The destination path has not been selected.  Please do so now." ) )
            {
                if ( !theModel.supportInternalListNameMap() || isAbsolutePathValid(theModel.getTheInternalListNameMapAbsolutePath(), "The internal list name map file has not been selected. Please do so now.")) {
                    copyFilePrefix(filePrefix);
                    copyL1Suffix(l1Suffix);
                    copyL2Suffix(l2Suffix);
                    theView.enableGeneration(false);
                    theEngine.generateFiles(theModel);
                }
            }
        }
    }

    @Override
    public void handleResetSelected()
    {
        reset();
        theModel.reset();
        theView.reset();
        theEngine.reset();
        resetTreeModel();
    }

    @Override
    public void handleNormalTermination( final String l1File, final String l2File )
    {
        try
        {
            resetTreeModel();
            theTreeModelBuilder.generateTreeModel( l1File, l2File, theRoot );
            theView.displayGlossary( theRoot );
            theView.displayCompletionMessage( "Processing complete." );
            theView.enableGeneration( true );
        }
        catch( ParserConfigurationException e )
        {
            theLogger.warn( "Unable to parse generated file.", e );
            theView.displayError( "An error has occurred.  Please check the developer messages for more details." );
        }
        catch( SAXException e )
        {
            theLogger.warn( "Unable to parse generated file.", e );
            theView.displayError( "An error has occurred.  Please check the developer messages for more details." );
        }
        catch( FileNotFoundException e )
        {
            theLogger.warn( "Unable to parse generated file.", e );
            theView.displayError( "An error has occurred.  Please check the developer messages for more details." );
        }
        catch( IOException e )
        {
            theLogger.warn( "Unable to parse generated file.", e );
            theView.displayError( "An error has occurred.  Please check the developer messages for more details." );
        }
    }

    private void resetTreeModel()
    {
        theRoot.removeAllChildren();
    }

    @Override
    public void handleAbnormalTermination( final String statusMessage )
    {
        theView.displayError("An error has occurred.  Please check the developer messages for more details.");
    }

    private void reset()
    {
        theSourceSelected = false;
        theDestinationSelected = false;
        theInternalListNameMapSelected = false;
    }

    private void copyL2Suffix( final String l2Suffix )
    {
        if ( nonEmptyString( l2Suffix ) )
        {
            theModel.setL2Suffix(l2Suffix);
        }
    }

    private void copyL1Suffix( final String l1Suffix )
    {
        if ( nonEmptyString( l1Suffix ) )
        {
            theModel.setL1Suffix(l1Suffix);
        }
    }

    private void copyFilePrefix( final String filePrefix )
    {
        if ( nonEmptyString( filePrefix ) )
        {
            theModel.setFilePrefix(filePrefix);
        }
    }

    @Override
    public void handleMarsocChange( final boolean state )
    {
        theModel.setSupportMarsoc( state );
    }

    @Override
    public void handleStopWordSelected( final String mode )
    {
        if ( nonEmptyString( mode ) )
        {
            theModel.setStopWordMode( StopWordProcessingType.valueOf( mode.toUpperCase() ) );
        }
        else
        {
            theLogger.warn( "The file prefix is invalid. Ignoring." );
        }
    }

    @Override
    public void handleExtractionApproachSelected(final String mode)
    {
        if (mode.equals("Original"))
        {
            theModel.setSupportMarsoc(false);
            theModel.setSupportInternal(false);
            theView.enableInternalListNameMapCheckBox(false);
            theView.enableInternalListNameMapBrowseButton(false);
        }
        else if (mode.equals("MARSOC"))
        {
            theModel.setSupportMarsoc(true);
            theModel.setSupportInternal(false);
            theView.enableInternalListNameMapCheckBox(false);
            theView.enableInternalListNameMapBrowseButton(false);
        }
        else
        {
            theModel.setSupportMarsoc(false);
            theModel.setSupportInternal(true);
            theView.enableInternalListNameMapCheckBox(true);
            theView.enableInternalListNameMapBrowseButton(theModel.supportInternalListNameMap());
        }
    }

    private boolean nonEmptyString( final String string )
    {
        return (null != string) && (!string.isEmpty());
    }

    private boolean requiredDataProvided()
    {
        return theSourceSelected &&
                theDestinationSelected &&
                (!theModel.supportInternalListNameMap() || theInternalListNameMapSelected);
    }

    private boolean isDirectoryValid( final String path, final String message )
    {
        boolean isValid = true;
        if ( null == path )
        {
            isValid = false;
        }
        else if ( path.isEmpty()  )
        {
            isValid = false;
        }
        else
        {
            final File file = new File(path);
            isValid = file.exists() && file.isDirectory();
        }

        if (!isValid)
            theView.displayError( message );

        return isValid;
    }

    private boolean isAbsolutePathValid(final String path, final String message)
    {
        boolean isValid = true;
        if (null == path)
        {
            isValid = false;
        }
        else if (path.isEmpty())
        {
            isValid = false;
        }
        else
        {
            final File file = new File(path);
            isValid = file.exists() && file.isFile();
        }

        if (!isValid)
            theView.displayError( message );

        return isValid;
    }
}
