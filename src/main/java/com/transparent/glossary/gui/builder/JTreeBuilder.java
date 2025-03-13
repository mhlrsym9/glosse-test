package com.transparent.glossary.gui.builder;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;

/**
 * Simplifies the construction of JTrees.
 */
public class JTreeBuilder implements Builder<JTree>
{
    private static final class BuilderState
    {
        private TreeNode theRoot;
        private boolean  theTooltipState;
        private TreeCellRenderer theTreeCellRenderer;

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theRoot = original.theRoot;
            theTooltipState = original.theTooltipState;
            theTreeCellRenderer = original.theTreeCellRenderer;
        }
    }

    /**
     * The current state of the builder.
     */
    private BuilderState theCurrentState = new BuilderState();

    /**
     * The default state of the builder.
     */
    private BuilderState theDefaultState = new BuilderState();



    @Override
    public JTree build()
    {
        final JTree tree;
        if ( theCurrentState.theRoot != null )
        {
            tree = new JTree( theCurrentState.theRoot );
        }
        else
        {
            tree = new JTree();
        }
        if ( theCurrentState.theTooltipState )
        {
            ToolTipManager.sharedInstance().registerComponent( tree );
        }
        if ( theCurrentState.theTreeCellRenderer != null )
        {
            tree.setCellRenderer( theCurrentState.theTreeCellRenderer );
        }
        return tree;
    }

    @Override
    public void reset()
    {
        theCurrentState = new BuilderState( theDefaultState );
    }

    @Override
    public void takeSnapshot()
    {
        theDefaultState = new BuilderState( theCurrentState );
    }

    public JTreeBuilder withRoot( final TreeNode root )
    {
        theCurrentState.theRoot = root;
        return this;
    }

    public JTreeBuilder withToolTips( final boolean state )
    {
        theCurrentState.theTooltipState = state;
        return this;
    }

    public JTreeBuilder withRenderer( final TreeCellRenderer renderer )
    {
        theCurrentState.theTreeCellRenderer = renderer;
        return this;
    }
}
