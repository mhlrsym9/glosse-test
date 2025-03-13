package com.transparent.glossary.gui;

/**
 * Objects interested in the processing engine's result should implement this interface.
 */
public interface ProcessingEngineStatusHandler
{
    /**
     * Called if the engine completes processing normally -- no errors.
     * @param l1File the generated L1 file.
     * @param l2File the generated L2 file.
     */
    void handleNormalTermination( final String l1File, final String l2File );

    /**
     * Called if the engine completes processing abnormally -- throws errors.
     * @param statusMessage status message from the engine.
     */
    void handleAbnormalTermination( final String statusMessage );
}
