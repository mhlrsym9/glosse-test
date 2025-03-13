package com.transparent.glossary.gui;

import com.transparent.glossary.CliClientShell;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * An implementation of the ProcessingEngine interface that interacts with the glossary generation engine.
 */
public class ConcreteProcessor implements ProcessingEngine
{
    /**
     * Tells Spring to interpret the path as absolute instead of relative.
     */
    private static final String ABSOLUTE_PATH_PREFIX = "file:";

    /**
     * How long to wait before checking on the status of the processing engine.
     */
    private static final int DELAY = 2;

    /**
     * Message logger to use.
     */
    private final Logger theLogger = Logger.getLogger( ConcreteProcessor.class );

    /**
     * Message logger to use for "normal" operating messages.
     */
    private final Logger theStatusLogger = Logger.getLogger( "normal.messages" );

    /**
     * CLI access to the processing engine.
     */
    private final CliClientShell theShell;

    /**
     * Pools threads and schedules tasks.
     */
    private final ScheduledThreadPoolExecutor theExecutor;

    /**
     * Who to call when the engine completes.
     */
    private ProcessingEngineStatusHandler theStatusHandler;

    public ConcreteProcessor( final CliClientShell shell,
                              final ScheduledThreadPoolExecutor executor )
    {
        theShell = shell;
        theExecutor = executor;
    }

    public void setStatusHandler( final ProcessingEngineStatusHandler statusHandler )
    {
        theStatusHandler = statusHandler;
    }

    @Override
    public void generateFiles( final Model model )
    {
        theLogger.trace( "generateFiles called" );
        setEngineParameters( model );
        final Future<Boolean> processorStatus = startProcessingEngine();
        scheduleStatusCheck( processorStatus );
    }

    private Future<Boolean> startProcessingEngine()
    {
        return theExecutor.submit( new ProcessorTask() );
    }

    private void scheduleStatusCheck( final Future<Boolean> processorStatus )
    {
        theExecutor.schedule( new StatusTimer( processorStatus ), DELAY, TimeUnit.SECONDS );
    }

    private void setEngineParameters( final Model model )
    {
        theShell.setInputDataRootFolder( new File( model.getTheSourcePath() ) );
        theShell.setOutputDataFolder( new File( model.getDestinationPath() ) );
        if (model.supportInternal()) {
            theShell.setInInternalMode(true);
            if (model.supportInternalListNameMap()) {
                theShell.setInternalListNameMapSelected(true);
                theShell.setInternalListNameMapFile(new File(model.getTheInternalListNameMapAbsolutePath()));
            }
        }
        theShell.setInMarsocMode( model.supportMarsoc() );
        theShell.setStopWordProcessingType( model.getStopWordMode() );

        if ( goodString( model.getFilePrefix() ) )
        {
            theShell.setGlossaryFilePrefix( model.getFilePrefix() );
        }

        if ( goodString( model.getL1Suffix() ) )
        {
            theShell.setGlossaryL1Suffix( model.getL1Suffix() );
        }

        if ( goodString( model.getL2Suffix() ) )
        {
            theShell.setGlossaryL2Suffix( model.getL2Suffix() );
        }

        installConfiguration(model);
    }

    @Override
    public void reset()
    {
        theShell.reset();
    }

    private boolean goodString( final String string )
    {
        return (string != null) && !string.isEmpty();
    }

    private void installConfiguration( final Model model )
    {
        if ( null != model.getCustomConfigurationPath() )
        {
            final File file = new File( model.getCustomConfigurationPath() );
            if ( file.exists() )
            {
                final StringBuilder builder = new StringBuilder( ABSOLUTE_PATH_PREFIX );
                final String path = file.getAbsolutePath();
                builder.append( path );
                theShell.setContext( new FileSystemXmlApplicationContext( builder.toString() ) );
                theStatusLogger.info( "Using " + path + " instead of default configuration." );
            }
            else
            {
                installDefaultConfiguration();
            }
        }
        else
        {
            installDefaultConfiguration();
        }
    }

    private void installDefaultConfiguration()
    {
        theShell.setContext( new ClassPathXmlApplicationContext( "glottal-config.xml" ) );
    }

    private final class StatusTimer implements Callable<Boolean>
    {
        private final Future<Boolean> theStatus;

        private StatusTimer( final Future<Boolean> status )
        {
            theStatus = status;
        }

        @Override
        public Boolean call() throws Exception
        {
            if ( theStatus.isDone() )
            {
                try
                {
                    theStatus.get();
                    theStatusHandler.handleNormalTermination(  theShell.getL1File(), theShell.getL2File() );
                }
                catch( InterruptedException e )
                {
                    theLogger.error( "Processing was interrupted!", e );
                    theStatusHandler.handleAbnormalTermination( e.getMessage() );
                }
                catch( ExecutionException e )
                {
                    theLogger.error( "The engine had an error!", e );
                    theStatusHandler.handleAbnormalTermination( e.getMessage() );
                }
            }
            else
            {
                theLogger.info( "Scheduling another status check" );
                scheduleStatusCheck( theStatus );
            }
            return true;
        }

    }

    private final class ProcessorTask implements Callable<Boolean>
    {
        @Override
        public Boolean call() throws Exception
        {
            theStatusLogger.info( "Started..." );
            theShell.buildGlossary();
            theStatusLogger.info( "done!" );
            return true;
        }
    }

}
