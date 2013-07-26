/*
 * $Id$
 *
 * Copyright (c) 2012-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.simsilica.lemur.component;

import java.util.*;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.core.GuiLayout;

/**
 *  A layout that manages children on a grid that automatically
 *  resizes to fit the components along the major or minor
 *  axes (row and column be default).  Each row and column is
 *  sized large enough to fit the largest child within that row
 *  or column.
 *
 *  @author    Paul Speed
 */
public class SpringGridLayout extends AbstractGuiComponent
                              implements GuiLayout, Cloneable {
    private GuiControl parent;
    private Axis mainAxis;
    private Axis minorAxis;
    private Axis altAxis;
    private FillMode mainFill;
    private FillMode minorFill;

    private int rowCount;
    private int columnCount;
    private float[] rowPrefs;
    private float[] colPrefs;

    private Map<Integer,Map<Integer,Entry>> children = new HashMap<Integer, Map<Integer,Entry>>();
    private Map<Node, Entry> lookup = new HashMap<Node, Entry>();

    private Vector3f lastPreferredSize = new Vector3f();

    public SpringGridLayout() {
        this(Axis.Y, Axis.X, FillMode.EVEN, FillMode.EVEN);
    }

    public SpringGridLayout( Axis mainAxis, Axis minorAxis, FillMode mainFill, FillMode minorFill ) {
        this.mainAxis = mainAxis;
        this.minorAxis = minorAxis;
        this.mainFill = mainFill;
        this.minorFill = minorFill;

        for( Axis a : Axis.values() ) {
            if( a != mainAxis && a != minorAxis ) {
                this.altAxis = a;
                break;
            }
        }
    }

    @Override
    public SpringGridLayout clone() {
        // Easier and better to just instantiate with the proper
        // settings
        SpringGridLayout result = new SpringGridLayout(mainAxis, minorAxis, mainFill, minorFill);
        return result;
    }

    @Override
    protected void invalidate() {
        if( parent != null ) {
            parent.invalidate();
        }
    }

    protected float getMajor( Vector3f v ) {
        switch( mainAxis ) {
            case X:
                return v.x;
            case Y:
                return v.y;
            case Z:
                return v.z;
        }
        return 0;
    }

    protected float getMinor( Vector3f v ) {
        switch( minorAxis ) {
            case X:
                return v.x;
            case Y:
                return v.y;
            case Z:
                return v.z;
        }
        return 0;
    }

    protected float getAlternate( Vector3f v ) {
        switch( altAxis ) {
            case X:
                return v.x;
            case Y:
                return v.y;
            case Z:
                return v.z;
        }
        return 0;
    }

    protected void setMajor( Vector3f v, float f ) {
        switch( mainAxis ) {
            case X:
                v.x = f;
                break;
            case Y:
                v.y = f;
                break;
            case Z:
                v.z = f;
                break;
        }
    }

    protected void setMinor( Vector3f v, float f ) {
        switch( minorAxis ) {
            case X:
                v.x = f;
                break;
            case Y:
                v.y = f;
                break;
            case Z:
                v.z = f;
                break;
        }
    }

    protected void setAlternate( Vector3f v, float f ) {
        switch( altAxis ) {
            case X:
                v.x = f;
                break;
            case Y:
                v.y = f;
                break;
            case Z:
                v.z = f;
                break;
        }
    }

    protected void addMajor( Vector3f v, float f ) {
        switch( mainAxis ) {
            case X:
                v.x += f;
                break;
            case Y:
                v.y += f;
                break;
            case Z:
                v.z += f;
                break;
        }
    }

    protected void addMinor( Vector3f v, float f ) {
        switch( minorAxis ) {
            case X:
                v.x += f;
                break;
            case Y:
                v.y += f;
                break;
            case Z:
                v.z += f;
                break;
        }
    }

    protected void addAlternate( Vector3f v, float f ) {
        switch( altAxis ) {
            case X:
                v.x += f;
                break;
            case Y:
                v.y += f;
                break;
            case Z:
                v.z += f;
                break;
        }
    }

    /**
     *  Recalculates the internal arrays that keep track of the
     *  preferred sizes for each row and collumn.  The preferred
     *  sizes are based on the maximum preferred size of every
     *  component in the row or column.
     *  This returns the "alternate axis" max size for all contained
     *  components.  In a standard row, column setup where rows
     *  are vertical and columns are horizontal, the alternate axis
     *  is depth.
     */
    protected float refreshRowColPrefs() {
        // Find the preferred size for each column
        // and the preferred size for each row.
        // Then add 'em up.
        if( rowPrefs == null || rowPrefs.length != rowCount ) {
            rowPrefs = new float[rowCount];
        } else {
            Arrays.fill(rowPrefs, 0);
        }
        if( colPrefs == null || colPrefs.length != columnCount ) {
            colPrefs = new float[columnCount];
        } else {
            Arrays.fill(colPrefs, 0);
        }

        float maxAlternate = 0;
        for( Map.Entry<Integer, Map<Integer,Entry>> rowEntry : children.entrySet() ) {
            int row = rowEntry.getKey();
            for( Map.Entry<Integer, Entry> colEntry : rowEntry.getValue().entrySet() ) {
                int col = colEntry.getKey();
                Entry e = colEntry.getValue();
                Vector3f v = e.child.getControl(GuiControl.class).getPreferredSize();
                rowPrefs[row] = Math.max(rowPrefs[row], getMajor(v));
                colPrefs[col] = Math.max(colPrefs[col], getMinor(v));
                maxAlternate = Math.max(getAlternate(v), maxAlternate);
            }
        }
        return maxAlternate;
    }

    public void calculatePreferredSize( Vector3f size ) {
        float maxAlternate = refreshRowColPrefs();

        lastPreferredSize.set(0,0,0);
        for( float f : rowPrefs ) {
            addMajor(lastPreferredSize, f);
        }

        for( float f : colPrefs ) {
            addMinor(lastPreferredSize, f);
        }
        addAlternate(lastPreferredSize, maxAlternate);
        size.addLocal(lastPreferredSize);
    }

    protected float weighted(int index, float pref, float totalSize, float totalPref, int count, FillMode fill, Axis axis) {
        switch( fill ) {
            case NONE:
                return pref;
            case EVEN:
                // Even means that they all grow evenly... not
                // that they are forced to be the same size.  So
                // we take the total difference and divide it evenly
                // among the children.
                return pref + (totalSize - totalPref)/count;
            case PROPORTIONAL:
                // All children expand proportional to their relation
                // to the overall preferred size.  Bigger components get more
                // share.
                float relation = pref / totalPref;
                return relation * totalSize;
        }
        return pref;
    }

    protected void distribute(float[] sizes, float[] prefs, float totalSize, float totalPref, FillMode fill, Axis axis) {
        for( int i = 0; i < sizes.length; i++ ) {
            sizes[i] = weighted(i, prefs[i], totalSize, totalPref, sizes.length, fill, axis);
        }
    }

    public void reshape(Vector3f pos, Vector3f size) {
        // This is somewhat trickier because placement requires
        // knowing all of the rows/cols prior and we don't really
        // process them in order.  Ah... can just precalculate
        // the sizes and positions, I guess.

        // Make sure the preferred size book-keeping is up to date.
        calculatePreferredSize(new Vector3f());

        // We could keep these arrays around but I think the GC churn
        // pales in comparison to the distribute calls if reshape is called a lot.
        float[] rowSizes = new float[rowCount];
        distribute(rowSizes, rowPrefs, getMajor(size), getMajor(lastPreferredSize), mainFill, mainAxis);

        float[] colSizes = new float[columnCount];
        distribute(colSizes, colPrefs, getMinor(size), getMinor(lastPreferredSize), minorFill, minorAxis);

        float[] rowOffsets = new float[rowCount];
        float f = 0;
        for( int i = 0; i < rowOffsets.length; i++ ) {
            rowOffsets[i] = f;
            f += rowSizes[i];
        }

        float[] colOffsets = new float[columnCount];
        f = 0;
        for( int i = 0; i < colOffsets.length; i++ ) {
            colOffsets[i] = f;
            f += colSizes[i];
        }

        // Now we can process the actual children
        for( Map<Integer, Entry> r : children.values() ) {
            for( Entry e : r.values() ) {
                Vector3f offset = new Vector3f();
                addMajor(offset, rowOffsets[e.row]);
                addMinor(offset, colOffsets[e.col]);
                offset.y *= -1;
                e.child.setLocalTranslation(pos.add(offset));

                Vector3f childSize = size.clone();
                setMajor(childSize, rowSizes[e.row]);
                setMinor(childSize, colSizes[e.col]);

                e.child.getControl(GuiControl.class).setSize(childSize);
            }
        }
    }

    protected Map<Integer, Entry> getRow( int row, boolean create ) {
        Map<Integer, Entry> result = children.get(row);
        if( result == null && create ) {
            result = new HashMap<Integer, Entry>();
            children.put(row, result);
        }
        return result;
    }

    public <T extends Node> T addChild( int row, int column, T n ) {
        if( n.getControl(GuiControl.class) == null )
            throw new IllegalArgumentException( "Child is not GUI element." );

        Map<Integer, Entry> rowMap = getRow(row, true);
        Entry existing = rowMap.get(column);
        if( existing != null ) {
            existing.child.removeFromParent();
        }

        Entry previous = lookup.get(n);
        if( previous != null ) {
            remove(previous);
        }
        Entry entry = new Entry(row, column, n);
        rowMap.put(column, entry);
        lookup.put(n, entry);

        rowCount = Math.max(rowCount, row + 1);
        columnCount = Math.max(columnCount, column + 1);

        if( parent != null ) {
            // We are attached
            parent.getNode().attachChild(n);
        }

        invalidate();
        return n;
    }

    public <T extends Node> T addChild( T n, Object... constraints ) {
        int row = -1;
        int col = -1;
        for( Object o : constraints ) {
            if( !(o instanceof Number) )
                throw new IllegalArgumentException( "Unknown border layout constraint:" + o );
            Number num = (Number)o;
            if( row == -1 ) {
                row = num.intValue();
            } else if( col == -1 ) {
                col = num.intValue();
            } else {
                throw new IllegalArgumentException( "Extra constraint not recognized:" + o );
            }
        }
        if( row == -1 ) {
            row = rowCount;
        }
        if( col == -1 ) {
            col = getRow(row, true).size();
        }

        // Determine the next natural location
        addChild(row, col, n);
        return n;
    }

    public void removeChild( Node n ) {
        // No fast way to do this right now
        Entry entry = lookup.remove(n);
        if( entry != null ) {
            remove(entry);
        }
    }

    protected void remove( Entry e ) {
        if( parent != null ) {
            parent.getNode().detachChild(e.child);
        }

        Map<Integer, Entry> rowMap = getRow(e.row, false);
        if( rowMap == null )
            return;

        rowMap.remove(e.col);

        // Really need to recalculate the max row and col

        invalidate();
    }

    @Override
    public void attach( GuiControl parent ) {
        this.parent = parent;
        Node self = parent.getNode();
        for( Map<Integer, Entry> r : children.values() ) {
            for( Entry e : r.values() ) {
                self.attachChild(e.child);
            }
        }
    }

    @Override
    public void detach( GuiControl parent ) {
        this.parent = null;
        for( Map<Integer, Entry> r : children.values() ) {
            for( Entry e : r.values() ) {
                e.child.removeFromParent();
            }
        }
    }

    protected class Entry {
        int row;
        int col;
        Node child;

        public Entry( int row, int col, Node child ) {
            this.row = row;
            this.col = col;
            this.child = child;
        }
    }
}
