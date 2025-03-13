/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary;

import com.transparent.glossary.processor.StopWordProcessingType;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

/**
 * This is a command line client for glossary generation tool.
 * The command line options are:
 *
 * <pre>
 usage: glottal
 -version					 print the version information and exit
 -inputFolderRoot [folder]   Required content input folder root for a
                             language
 -outputFolder [folder]      Required glossary output folder
 -swType [type]              Type of stop words to process, accepted
                             values are: ALL, WORDS, PHRASES and NONE.
                             ALL is the default.
 -configFile [file]          Configuration file
 -prefix [prefix]            Prefix to give to the glossary file name,
                             e.g. glossary
 -suffixL1 [suffix]          Suffix to give to the glossary L1 file name,
                             e.g. L1
 -suffixL2 [suffix]          Suffix to give to the glossary L2 file name,
                             e.g. L2
 *</pre>
 *
 * User: plitvak
 * Date: Aug 6, 2009
 * Time: 2:01:02 PM
 */
public class CliClient
{
	public static void main( String[] args )
	{
		// read and parse command line
		CommandLine line			= null;
		CommandLineParser parser	= new GnuParser();
		try {
			line = parser.parse( getOptions(), args );
		}
		catch( Exception exp )
		{
			System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
			System.exit( 1 );
		}
		//------------------------------

		if( line.hasOption( "version" ) )
		{
			printVersionInfo();
			System.exit( 0 );
		}

        if( line.hasOption( "marsoc" ) )
        {
            System.out.println( "MARSOC processing enabled!" );
        }

		// configure shell with command line options
		CliClientShell shell = new CliClientShell();
		configureShell( shell, line );

		// build the glossary
		try
		{
			System.out.println( "Started..." );
			shell.buildGlossary();
			System.out.println( "done!" );
		}
		catch( Throwable t )
		{
			StringWriter sw = new StringWriter();
			t.printStackTrace( new PrintWriter( sw ) );
			Logger.getLogger( CliClient.class ).error( sw.getBuffer().toString() );

			System.out.println( "Error, please see log file!" );
			System.out.println( sw.getBuffer().toString() );
			System.exit( 1 );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the version number of the glossary tool
	 */
	private static void printVersionInfo()
	{
		Properties p = new Properties();
		try
		{
			p.load( Thread.currentThread().getContextClassLoader().getResourceAsStream( "version.properties" ) );
			System.out.println( String.format( "Gloss-E ver. %s Copyright (c) 2008-2011 Transparent Language, Inc.  All rights reserved.",
												p.getProperty( "version" ) ) );
		}
		catch( Exception e )
		{
			System.err.println( "Failed.  Reason: " + e.getMessage() );
			System.exit( 1 );
		}
	}


	/**
	 * This method creates command line options set.
	 */
	private static Options getOptions()
	{
		// required option
		Option inputFolderRoot  = OptionBuilder.withArgName( "folder" )
												.hasArg()
												.withDescription( "Required content input folder root for a language" )
												.create( "inputFolderRoot" );
		// required option
		Option outputFolder		= OptionBuilder.withArgName( "folder" )
												.hasArg()
												.withDescription( "Required glossary output folder" )
												.create( "outputFolder" );

		Option stopWordsType	= OptionBuilder.withArgName( "type" )
												.hasArg()
												.withDescription( "Type of stop words to process, accepted values are: ALL, WORDS, PHRASES and NONE.\nALL is the default." )
												.create( "swType" );

		Option configFile		= OptionBuilder.withArgName( "file" )
												.hasArg()
												.withDescription( "Configuration file" )
												.create( "configFile" );

		Option glossaryPrefix	= OptionBuilder.withArgName( "prefix" )
												.hasArg()
												.withDescription( "Prefix to give to the glossary file name, e.g. glossary" )
												.create( "prefix" );

		Option glossaryL1Suffix	= OptionBuilder.withArgName( "sufix" )
												.hasArg()
												.withDescription( "Sufix to give to the glossary L1 file name, e.g. L1" )
												.create( "suffixL1" );

		Option glossaryL2Suffix	= OptionBuilder.withArgName( "sufix" )
												.hasArg()
												.withDescription( "Sufix to give to the glossary L2 file name, e.g. L2" )
												.create( "suffixL2" );

		Option version = new Option( "version", "print the version information and exit" );

        Option marsoc = new Option( "marsoc", "enable special B4X folder naming convention support" );

		Options options = new Options();
		options.addOption( version );
		options.addOption( inputFolderRoot );
		options.addOption(outputFolder);
		options.addOption( stopWordsType );
		options.addOption( configFile );
		options.addOption( glossaryPrefix );
		options.addOption( glossaryL1Suffix );
		options.addOption( glossaryL2Suffix );
        options.addOption( marsoc );

		return options;
	}

	/**
	 * This method configures shecll with command line options
	 */
	private static void configureShell( final CliClientShell shell, final CommandLine line )
	{
		// set required input root property
		if( line.hasOption( "inputFolderRoot" ) ) {
			shell.setInputDataRootFolder(new File(line.getOptionValue("inputFolderRoot")));
		}
		else
		{
			printHelp();
			System.exit( 1 );
		}

		// set required output folder property
		if( line.hasOption( "outputFolder" ) ) {
			shell.setOutputDataFolder( new File( line.getOptionValue( "outputFolder" ) ) );
		}
		else
		{
			printHelp();
			System.exit(1);
		}

		// set the type of stop wors processing
		if( line.hasOption( "swType" ) ) {
			shell.setStopWordProcessingType( StopWordProcessingType.valueOf( line.getOptionValue("swType") ) );
		}

		// set optional config file option, if not set the one from the classpath will be used.
		ApplicationContext context = null;
		if( line.hasOption( "configFile" ) ) {
			context = new FileSystemXmlApplicationContext( new String[]{line.getOptionValue( "configFile" )}, true );
		}
		else {
			context = new ClassPathXmlApplicationContext( new String[]{ "glottal-config.xml" }, true );
		}
		shell.setContext( context );

		// set optional glossary file prefix option, of not set the "glossary" will be used
		if( line.hasOption( "prefix" ) ) {
			shell.setGlossaryFilePrefix(line.getOptionValue("prefix"));
		}

		// set optional L1 glossary file suffix option, of not set the "L1" will be used
		if( line.hasOption( "suffixL1" ) ) {
			shell.setGlossaryL1Suffix( line.getOptionValue( "suffixL1" ) );
		}

		// set optional L2 glossary file suffix option, of not set the "L2" will be used
		if( line.hasOption( "suffixL2" ) ) {
			shell.setGlossaryL1Suffix(line.getOptionValue("suffixL2"));
		}

		if (line.hasOption("internal"))
		{
			shell.setInInternalMode(true);

			if (line.hasOption("internalListNameMap"))
			{
				shell.setInternalListNameMapSelected(true);
				shell.setInternalListNameMapFile(new File(line.getOptionValue("internalListNameMap")));
			}
		}

		if ( line.hasOption( "marsoc" ) )
        {
            shell.setInMarsocMode( true );
        }
	}

	private static void printHelp()
	{
		HelpFormatter formatter	= new HelpFormatter();
		Options options			= getOptions();

		printVersionInfo();
		formatter.printHelp( "glottal", options );
	}
}
