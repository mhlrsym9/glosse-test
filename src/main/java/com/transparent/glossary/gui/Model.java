package com.transparent.glossary.gui;

import com.transparent.glossary.processor.StopWordProcessingType;

/**
 * Holds the data captured by the GUI from the user.
 */
public class Model
{
    /**
     * Location of the source files.
     */
    private String theSourcePath;

    /**
     * Location of the destination directory.
     */
    private String theDestinationPath;

    /**
     * Location of the internal list name map file.
     */
    private String theInternalListNameMapAbsolutePath;

    /**
     * If true, MARSOC support is enabled.
     */
    private boolean supportMarsoc;

    /**
     * If true, reading POS & Reference from B4X is enabled
     */
    private boolean supportInternal = true;

    /**
     * If true, translating B4X references using an excel file is enable
     */
    private boolean supportInternalListNameMap;

    /**
     * Location of the configuration file the user wishes to use -- overrides the baked in configuration.
     */
    private String theCustomConfigurationPath;

    /**
     * String to pre-pend to the generated files.
     */
    private String theFilePrefix;

    /**
     * String to append to the generated L1 file.
     */
    private String theL1Suffix;

    /**
     * String to append to the generated L2 file.
     */
    private String theL2Suffix;

    /**
     * Indicates how the engine should process stop words.
     */
    private StopWordProcessingType theStopWordMode = StopWordProcessingType.ALL;

    public String getCustomConfigurationPath()
    {
        return theCustomConfigurationPath;
    }

    public void setCustomConfigurationPath( final String aCustomConfigurationPath )
    {
        theCustomConfigurationPath = aCustomConfigurationPath;
    }

    public String getTheSourcePath()
    {
        return theSourcePath;
    }

    public void setSourcePath( final String aSourcePath )
    {
        theSourcePath = aSourcePath;
    }

    public String getDestinationPath()
    {
        return theDestinationPath;
    }

    public void setDestinationPath( final String aDestinationPath )
    {
        theDestinationPath = aDestinationPath;
    }

    public String getTheInternalListNameMapAbsolutePath()
    {
        return theInternalListNameMapAbsolutePath;
    }

    public void setTheInternalListNameMapAbsolutePath(final String anInternalListNameMapAbsolutePath)
    {
        theInternalListNameMapAbsolutePath = anInternalListNameMapAbsolutePath;
    }

    public boolean supportMarsoc()
    {
        return supportMarsoc;
    }

    public void setSupportMarsoc( final boolean state )
    {
        supportMarsoc = state;
    }

    public boolean supportInternal() { return supportInternal; }

    public void setSupportInternal( final boolean state ) {supportInternal = state;}

    public boolean supportInternalListNameMap() {return supportInternalListNameMap;}

    public void setSupportInternalListNameMap(final boolean state) {supportInternalListNameMap = state;}

    public String getFilePrefix()
    {
        return theFilePrefix;
    }

    public void setFilePrefix( final String aFilePrefix )
    {
        theFilePrefix = aFilePrefix;
    }

    public String getL1Suffix()
    {
        return theL1Suffix;
    }

    public void setL1Suffix( final String aSuffix )
    {
        theL1Suffix = aSuffix;
    }

    public String getL2Suffix()
    {
        return theL2Suffix;
    }

    public void setL2Suffix( final String aSuffix )
    {
        theL2Suffix = aSuffix;
    }

    public StopWordProcessingType getStopWordMode()
    {
        return theStopWordMode;
    }

    public void setStopWordMode( final StopWordProcessingType aStopWordMode )
    {
        theStopWordMode = aStopWordMode;
    }

    /**
     * Resets the model to its initial state.
     */
    public void reset()
    {
        theSourcePath = null;
        theDestinationPath = null;
        supportMarsoc = false;
        supportInternal = true;
        supportInternalListNameMap = false;
        theInternalListNameMapAbsolutePath = null;
        theCustomConfigurationPath = null;
        theFilePrefix = null;
        theL1Suffix = null;
        theL2Suffix = null;
        theStopWordMode = StopWordProcessingType.ALL;
    }
}
