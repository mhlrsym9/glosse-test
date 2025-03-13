package com.transparent.glossary;

import com.transparent.glossary.gui.Mediator;
import com.transparent.glossary.gui.SwingOutputStream;
import java.io.PrintStream;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Drives the application using a GUI.
 */
public class GuiClientShell
{
    public static void main( final String[] args )
    {
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext( "gloss-e-ui-context.xml" );
        context.registerShutdownHook();

        //replaceConsoleStreams( context );

        final Mediator mediator = context.getBean( Mediator.class );
        mediator.start();
    }

    private static void replaceConsoleStreams( final ClassPathXmlApplicationContext context )
    {
        final SwingOutputStream outputStream = context.getBean( SwingOutputStream.class );
        final PrintStream out = new PrintStream( outputStream );
        System.setOut( out );
        System.setErr( out );
    }
}
