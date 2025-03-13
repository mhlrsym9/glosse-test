package com.transparent.glossary.gui;

import java.util.LinkedList;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Custom SAX event handler that understands the generated glossary files and can construct a data model from them
 * that is suitable for a JTree.
 */
final class GlossaryContentHandler extends DefaultHandler
{
    /**
     * Message logger to use.
     */
    private final Logger theLogger = Logger.getLogger( GlossaryContentHandler.class );

    /**
     * Holds data as we parse through the XML.
     */
    private final LinkedList<DefaultMutableTreeNode> stack = new LinkedList<DefaultMutableTreeNode>();

    /**
     * The root of the glossary data tree.
     */
    private final DefaultMutableTreeNode theRoot;


    GlossaryContentHandler( final DefaultMutableTreeNode aRoot )
    {
        theRoot = aRoot;
    }

    @Override
    public void startElement( final String uri, final String localName, final String qName, final Attributes atts ) throws SAXException
    {
        final Tag tag = new Tag();
        tag.name = qName;
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode( tag );
        stack.addFirst( node );
        if ( "glossary".equals( qName ) )
        {
            tag.data = atts.getValue( "language" );
        }
        else if ( "letter".equals( qName ) )
        {
            tag.data = atts.getValue( "name" );
        }
        else if ( "words".equals( qName ) )
        {
            tag.data = qName;
        }
        else if ( "phrases".equals( qName ) )
        {
            tag.data = qName;
        }
    }

    @Override
    public void endElement( final String uri, final String localName, final String qName ) throws SAXException
    {
        final DefaultMutableTreeNode popped = stack.removeFirst();
        final Tag poppedTag = (Tag)popped.getUserObject();
        if ( stack.isEmpty() )
        {
            sumGrandChildrenCounts( popped, poppedTag );
            appendChildCountToData( poppedTag );
            theRoot.add( popped );
        }
        else
        {
            final DefaultMutableTreeNode top = stack.peekFirst();
            final Tag topTag = (Tag)top.getUserObject();
            if ( "key".equals( poppedTag.name ) )
            {
                topTag.data = poppedTag.data;
            }
            else if ( "translation".equals( poppedTag.name ) )
            {
                poppedTag.tooltip = "The translation of the word";
                top.add( popped );
            }
            else if ( "word".equals( poppedTag.name ) )
            {
                poppedTag.childCount = popped.getChildCount();
                top.add( popped );
            }
            else if ( "words".equals( poppedTag.name ) )
            {
                poppedTag.childCount = popped.getChildCount();
                addChildThatHasChildren( popped, top );
                appendChildCountToData( poppedTag );
            }
            else if ( "letter".equals( poppedTag.name ) )
            {
                // skip over the letters tag and just add the letter to the language tag
                final int index = stack.indexOf( top );
                final DefaultMutableTreeNode topsParent = stack.get( index + 1 );
                sumGrandChildrenCounts( popped, poppedTag );
                appendChildCountToData( poppedTag );
                topsParent.add( popped );
            }
            else if ( "phrase".equals( poppedTag.name ) )
            {
                poppedTag.childCount = popped.getChildCount();
                top.add( popped );
            }
            else if ( "phrases".equals( poppedTag.name ) )
            {
                poppedTag.childCount = popped.getChildCount();
                addChildThatHasChildren( popped, top );
                appendChildCountToData( poppedTag );
            }
            else if ( "referencedBy".equals( poppedTag.name ) )
            {
                poppedTag.tooltip = "The lesson the word is used in";
                addChildThatHasData( popped, top, poppedTag );
            }
            else if ( "partOfSpeech".equals( poppedTag.name ) )
            {
                poppedTag.tooltip = "The part-of-speech the word belongs to";
                addChildThatHasData( popped, top, poppedTag );
            }
            else if ( "sideTwoSoundfile".equals( poppedTag.name ) )
            {
                poppedTag.tooltip = "The sound file associated with the word";
                addChildThatHasData( popped, top, poppedTag );
            }
        }
    }

    private void sumGrandChildrenCounts( final DefaultMutableTreeNode node, final Tag tag )
    {
        for( int i = 0; i < node.getChildCount(); i++ )
        {
            final DefaultMutableTreeNode childAt = (DefaultMutableTreeNode)node.getChildAt( i );
            final Tag childTag = (Tag)childAt.getUserObject();
            tag.childCount += childTag.childCount;
        }
    }

    private void appendChildCountToData( final Tag tag )
    {
        final StringBuilder builder = new StringBuilder( tag.data );
        tag.data = builder.append( " (" ).append( tag.childCount ).append( ")" ).toString();
    }

    private void addChildThatHasData( final DefaultMutableTreeNode child, final DefaultMutableTreeNode parent, final Tag childData )
    {
        final boolean hasData = (childData.data != null) && (!childData.data.isEmpty());
        if ( hasData )
        {
            parent.add( child );
        }
    }

    private void addChildThatHasChildren( final DefaultMutableTreeNode child, final DefaultMutableTreeNode parent )
    {
        if ( child.getChildCount() > 0 )
        {
            parent.add( child );
        }
    }

    @Override
    public void characters( final char[] ch, final int start, final int length ) throws SAXException
    {

        final String text = new String( ch, start, length ).trim();
        if ( !text.isEmpty() )
        {
            final DefaultMutableTreeNode top = stack.peekFirst();
            final Tag tag = (Tag)top.getUserObject();
            tag.data = text;
        }
    }

    @Override
    public void warning( final SAXParseException exception ) throws SAXException
    {
        theLogger.warn( "Problems parsing the XML file.", exception );
    }

    @Override
    public void error( final SAXParseException exception ) throws SAXException
    {
        theLogger.warn( "Problems parsing the XML file.", exception );
    }

    @Override
    public void fatalError( final SAXParseException exception ) throws SAXException
    {
        theLogger.warn( "Problems parsing the XML file.", exception );
    }

    /**
     * Simple structure to hold the data captured from the XML and shown in the glossary tree.
     */
    final static class Tag
    {
        String name;
        String data;
        String tooltip;
        int childCount;

        @Override
        public String toString()
        {
            return data;
        }
    }
}
