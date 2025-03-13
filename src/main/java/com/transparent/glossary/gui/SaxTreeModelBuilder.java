package com.transparent.glossary.gui;

import com.transparent.glossary.TreeModelBuilder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Using a SAX parser, this object will build a data tree out of the information contained in the glossary files.
 */
public class SaxTreeModelBuilder implements TreeModelBuilder
{
    /**
     * XML parser to use.
     */
    private final SAXParser theParser;

    public SaxTreeModelBuilder() throws SAXException, ParserConfigurationException
    {
        theParser = SAXParserFactory.newInstance().newSAXParser();
    }

    @Override
    public void generateTreeModel( final String l1File,
                                   final String l2File,
                                   final DefaultMutableTreeNode aRoot ) throws ParserConfigurationException, SAXException, IOException
    {
        final GlossaryContentHandler handler = new GlossaryContentHandler( aRoot );
        theParser.parse( constructUTF8Reader( l1File ), handler );
        theParser.parse( constructUTF8Reader( l2File ), handler );
    }

    private InputSource  constructUTF8Reader( final String file ) throws FileNotFoundException, UnsupportedEncodingException
    {
        final String encoding = "UTF-8";
        final InputStream inputStream= new FileInputStream( file );
        final Reader reader = new InputStreamReader( inputStream, encoding );
        final InputSource inputSource = new InputSource( reader );
        inputSource.setEncoding( encoding );
        return inputSource;
    }
}
