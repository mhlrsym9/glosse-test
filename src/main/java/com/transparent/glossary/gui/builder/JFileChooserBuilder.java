package com.transparent.glossary.gui.builder;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

/**
 * Simplify the creation of file chooser dialogs.
 */
public class JFileChooserBuilder implements Builder<JFileChooser>
{
    private static final class BuilderState
    {
        private FileSystemView theFileSystemView;
        private int theFileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theFileSystemView = original.theFileSystemView;
            theFileSelectionMode = original.theFileSelectionMode;
        }
    }

    /**
     * Current state of the builder.
     */
    private BuilderState theCurrentState = new BuilderState();

    /**
     * Default state of the builder.
     */
    private BuilderState theDefaultState = new BuilderState();


    @Override
    public JFileChooser build()
    {
        final JFileChooser chooser;
        if ( theCurrentState.theFileSystemView != null )
        {
            chooser = new JFileChooser(  theCurrentState.theFileSystemView );
        }
        else
        {
            chooser = new JFileChooser();
        }
        chooser.setFileSelectionMode( theCurrentState.theFileSelectionMode );
        return chooser;
    }

    @Override
    public void reset()
    {
        theCurrentState = new BuilderState( theDefaultState );
    }

    @Override
    public void takeSnapshot()
    {
        theDefaultState = new BuilderState( theCurrentState );
    }

    public JFileChooserBuilder withFileSystemView( final FileSystemView fileSystemView )
    {
        theCurrentState.theFileSystemView = fileSystemView;
        return this;
    }

    public JFileChooserBuilder withDirectoriesOnly()
    {
        theCurrentState.theFileSelectionMode = JFileChooser.DIRECTORIES_ONLY;
        return this;
    }

    public JFileChooserBuilder withFilesOnly()
    {
        theCurrentState.theFileSelectionMode = JFileChooser.FILES_ONLY;
        return this;
    }
}
