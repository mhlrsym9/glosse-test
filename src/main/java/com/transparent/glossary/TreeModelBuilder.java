package com.transparent.glossary;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Describes the interaction with an object that can construct a JTree data model from the generated glossary files.
 */
public interface TreeModelBuilder
{
    /**
     * Populates the data tree with information contained in the generated glossary files.
     * @param l1File the L1 glossary file.
     * @param l2File the L2 glossary file.
     * @param aRoot the root of the data tree.
     * @throws ParserConfigurationException if there is a problem setting up the parser.
     * @throws SAXException if there is a problem parsing the files.
     * @throws IOException if there is a problem reading the files.
     */
    void generateTreeModel( final String l1File,
                            final String l2File,
                            final DefaultMutableTreeNode aRoot ) throws ParserConfigurationException, SAXException, IOException;

}
