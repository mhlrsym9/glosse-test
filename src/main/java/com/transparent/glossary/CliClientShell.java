/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary;

import com.transparent.glossary.io.GlossaryInputDataReader;
import com.transparent.glossary.model.SortObject;
import com.transparent.glossary.processor.GlossaryProcessor;
import com.transparent.glossary.processor.StopWordProcessingType;
import com.transparent.glossary.sort.SortDirection;
import com.transparent.glossary.sort.SortType;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.List;

/**
 * This is a simple shell to wrap glossary processor property setting and execution.
 *
 * User: plitvak
 * Date: Aug 6, 2009
 * Time: 2:03:11 PM
 */
public class CliClientShell
{
    private static final String DEFAULT_FILE_PREFIX = "glossary";
    private static final String DEFAULT_L1_SUFFIX = "L1";
    private static final String DEFAULT_L2_SUFFIX = "L2";
    private static final StopWordProcessingType DEFAULT_STOP_WORD_TYPE = StopWordProcessingType.ALL;

    /**
     * Message logger to use for "normal" operating messages.
     */
    private final Logger theStatusLogger = Logger.getLogger( "normal.messages" );

    /**
     * Message logger to use.
     */
    private final Logger theLogger = Logger.getLogger( CliClientShell.class );

	//===================================== property assessors ==========================================================
	public ApplicationContext getContext() {
		return context;
	}

	public void setContext( final ApplicationContext context ) {
		this.context = context;
	}

	public File getInputDataRootFolder() {
		return inputDataRootFolder;
	}

	public void setInputDataRootFolder( final File inputDataRootFolder ) {
		this.inputDataRootFolder = inputDataRootFolder;
	}

	public File getOutputDataFolder() {
		return outputDataFolder;
	}

	public void setOutputDataFolder( final File outputDataFolder ) {
		this.outputDataFolder = outputDataFolder;
	}

	public String getGlossaryL1Suffix() {
		return glossaryL1Suffix;
	}

	public void setGlossaryL1Suffix( final String glossaryL1Suffix ) {
		this.glossaryL1Suffix = glossaryL1Suffix;
	}

	public String getGlossaryL2Suffix() {
		return glossaryL2Suffix;
	}

	public void setGlossaryL2Suffix( final String glossaryL2Suffix ) {
		this.glossaryL2Suffix = glossaryL2Suffix;
	}

	public String getGlossaryFilePrefix() {
		return glossaryFilePrefix;
	}

	public void setGlossaryFilePrefix( final String glossaryFilePrefix ) {
		this.glossaryFilePrefix = glossaryFilePrefix;
	}

	public StopWordProcessingType getStopWordProcessingType() {
		return stopWordProcessingType;
	}

	public void setStopWordProcessingType( final StopWordProcessingType stopWordProcessingType ) {
		this.stopWordProcessingType = stopWordProcessingType;
	}

    public void setInMarsocMode( final boolean inMarsocMode )
    {
        this.inMarsocMode = inMarsocMode;
    }

	public void setInInternalMode(final boolean inInternalMode) {this.inInternalMode = inInternalMode;}

	public void setInternalListNameMapSelected(final boolean internalListNameMapSelected) {this.internalListNameMapSelected = internalListNameMapSelected;}

	public void setInternalListNameMapFile(final File internalListNameMapFile) {this.internalListNameMapFile = internalListNameMapFile;}

	//===================================== property assessors ==========================================================

	/**
	 * This method configures glossary processor and builds the glossary.
	 */
	public void buildGlossary()
	{
		GlossaryProcessor processor	= (GlossaryProcessor)context.getBean( "glossaryProcessor" );
		processor.setInputRootFolder( inputDataRootFolder );
		processor.setStopWordProcessingType( stopWordProcessingType );

		if (inInternalMode)
		{
			final GlossaryInputDataReader b4xReader = context.getBean( "b4xReader", GlossaryInputDataReader.class );
			b4xReader.setApplyInternalRules( true );
			if (internalListNameMapSelected)
			{
				b4xReader.setApplyInternalListNameMap(true);
				b4xReader.setInternalListNameMapFile(internalListNameMapFile);
			}
		}
        else if ( inMarsocMode )
        {
            final GlossaryInputDataReader b4xReader = context.getBean( "b4xReader", GlossaryInputDataReader.class );
            b4xReader.setApplyMarsocRules( true );
        }
        theStatusLogger.info( "Reading files under: "+ inputDataRootFolder );
		List<SortObject> unsortedData = processor.collectUnsortedGlossaryData();

		if (0 == unsortedData.size())
		{
			theStatusLogger.error("No entries found to process. Perhaps the content folder contained just B4U files?");
		}
		else
		// create output folder if doesn't exist
		{
			if (!outputDataFolder.exists()) {
				final boolean successful = outputDataFolder.mkdirs();
				if (!successful) {
					theLogger.fatal("Unable to create directory: " + outputDataFolder.getPath());
				}
			}

			theL1File = String.format( "%s/%s_%s.xml", outputDataFolder.getAbsolutePath(), glossaryFilePrefix, glossaryL1Suffix );
			theStatusLogger.info( "Creating L1 glossary as: " + theL1File );
			theStatusLogger.info( "sorting..." );
			processor.sort( unsortedData, SortType.L1, SortDirection.ASC );
			theStatusLogger.info( "writing file..." );
			processor.writeGlossary( new File( theL1File ), SortType.L1, unsortedData );

			theL2File = String.format( "%s/%s_%s.xml", outputDataFolder.getAbsolutePath(), glossaryFilePrefix, glossaryL2Suffix );
			theStatusLogger.info( "Creating L2 glossary as: " + theL2File );
			theStatusLogger.info( "sorting..." );
			processor.sort( unsortedData, SortType.L2, SortDirection.ASC );
			theStatusLogger.info( "writing file..." );
			processor.writeGlossary( new File( theL2File ), SortType.L2, unsortedData );
		}
	}

    public String getL2File()
    {
        return theL2File;
    }

    public String getL1File()
    {
        return theL1File;
    }

    public void reset()
    {
        context = null;
        inputDataRootFolder = null;
        outputDataFolder = null;
        glossaryFilePrefix = DEFAULT_FILE_PREFIX;
        glossaryL1Suffix = DEFAULT_L1_SUFFIX;
        glossaryL2Suffix = DEFAULT_L2_SUFFIX;
        stopWordProcessingType = DEFAULT_STOP_WORD_TYPE;
        inMarsocMode = false;
        theL1File = null;
        theL2File = null;
    }

	//------------------------------------------------------------------------------------------------------------------
	private ApplicationContext context 	= null;
	private File inputDataRootFolder	= null;
	private File outputDataFolder		= null;
	private String glossaryFilePrefix	= DEFAULT_FILE_PREFIX;
	private String glossaryL1Suffix		= DEFAULT_L1_SUFFIX;
	private String glossaryL2Suffix		= DEFAULT_L2_SUFFIX;
	private StopWordProcessingType stopWordProcessingType
										= DEFAULT_STOP_WORD_TYPE;
    private boolean inMarsocMode        = false;
	private boolean inInternalMode = false;
	private boolean internalListNameMapSelected = false;
	private File internalListNameMapFile = null;

    /**
     * Full path to the generated L1 file.
     */
    private String theL1File = null;

    /**
     * Full path to the generated L2 file.
     */
    private String theL2File = null;
	//------------------------------------------------------------------------------------------------------------------

}
