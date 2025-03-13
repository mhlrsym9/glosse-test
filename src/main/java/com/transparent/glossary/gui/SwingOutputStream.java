package com.transparent.glossary.gui;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Adapts an output stream so that it can be displayed in a Swing control.
 */
public class SwingOutputStream extends OutputStream
{
    /**
     * The component we want to display messages to.
     */
    private final JTextArea theTextArea;

    public SwingOutputStream( final JTextArea theTextArea )
    {
        this.theTextArea = theTextArea;
    }

    @Override
    public void write( final int b ) throws IOException
    {
        final String character = String.valueOf( (char)b );
        if ( SwingUtilities.isEventDispatchThread() )
        {
            theTextArea.append( character  );
        }
        else
        {
            SwingUtilities.invokeLater( new ThreadFriendlyUpdate( character ) );
        }
    }

    private final class ThreadFriendlyUpdate implements Runnable
    {
        private final String theCharacter;

        private ThreadFriendlyUpdate( final String character )
        {
            theCharacter = character;
        }

        @Override
        public void run()
        {
            theTextArea.append( theCharacter );
        }
    }
}
