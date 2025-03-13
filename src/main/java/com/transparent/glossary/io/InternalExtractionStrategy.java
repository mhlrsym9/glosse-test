package com.transparent.glossary.io;

import org.apache.log4j.Logger;
import org.apache.commons.io.FilenameUtils;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;

import org.w3c.dom.Node;

/**
 * Created by MDennehy on 4/9/2015.
 */
public class InternalExtractionStrategy extends StandardExtractionStrategy
{
    private final XPath xPath;

    private final String theListName;

    private final Node theNode;

    private final String theSideTwoPartOfSpeechXPath;

    public InternalExtractionStrategy( final String listName,
                                       final XPath aXPath,
                                       final Node node,
                                       final String sideTwoPartOfSpeechXPath,
                                       final File b4xFile )
    {
        super(b4xFile);

        theListName = listName;
        xPath = aXPath;
        theNode = node;
        theSideTwoPartOfSpeechXPath = sideTwoPartOfSpeechXPath;
    }

    @Override
    public String determineUnitNumber()
    {
        return theListName;
    }

    @Override
    public String determinePartOfSpeech()
    {
        try {
            return xPath.evaluate(theSideTwoPartOfSpeechXPath, theNode).trim();
        }
        catch(XPathExpressionException e)
        {
            return "unclassified";
        }
    }

    @Override
    public String buildSoundPath( final String soundPath )
    {
        final StringBuilder builder = new StringBuilder();
        final String basePath = super.buildSoundPath(soundPath);
        if ((null != basePath) && !basePath.isEmpty())
            builder.append("../").append(basePath);
        return builder.toString().trim();
    }
}
