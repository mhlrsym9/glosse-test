package com.transparent.glossary.gui.builder;

/**
 * Object builder must adhere to this interface.
 */
public interface Builder<T>
{
    /**
     * Create a new instance of the specified type.
     *
     * @return a new object instance.
     */
    T build();

    /**
     * Resets the builder back to its default values or what ever state is stored in the snapshot.
     */
    void reset();

    /**
     * From this point on, the builder will reset to what the builder's current state.  The idea
     * is that you can get the builder into the state you want and then freeze it using this method.
     */
    void takeSnapshot();
}
