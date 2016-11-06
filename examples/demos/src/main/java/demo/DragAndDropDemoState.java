/*
 * $Id$
 * 
 * Copyright (c) 2016, Simsilica, LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the 
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package demo;

import java.util.*;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.event.*;
import com.jme3.light.*;
import com.jme3.material.*;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.*;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.*;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.*;

import com.simsilica.lemur.*;
import com.simsilica.lemur.dnd.*;
import com.simsilica.lemur.core.GuiMaterial;
import com.simsilica.lemur.event.*;

/**
 *  Demo and test of the drag-and-drop support in Lemur.
 *
 *  @author    Paul Speed
 */
public class DragAndDropDemoState extends BaseAppState {

    private Node dndRoot;

    private ColorRGBA containerColor = new ColorRGBA(1, 1, 0, 0.5f);
    private ColorRGBA containerHighlight = new ColorRGBA(0, 1, 0, 0.5f);

    private ContainerNode container1;    
    private ContainerNode container2;

    public DragAndDropDemoState() {
    }
    
    protected Node getRoot() {
        return dndRoot;
    }

    @Override 
    protected void initialize( Application app ) {
        dndRoot = new Node("dndRoot");
 
        DirectionalLight sun = new DirectionalLight(new Vector3f(1, -2, -5).normalizeLocal(),
                                                    ColorRGBA.White);
        dndRoot.addLight(sun);
        
        AmbientLight ambient = new AmbientLight(ColorRGBA.Gray);
        dndRoot.addLight(ambient);
            
        //WireBox box = new WireBox(1, 1, 1);
        /*Box box = new Box(1, 1, 1);
        container1 = new Geometry("container1", box);
        GuiMaterial mat = GuiGlobals.getInstance().createMaterial(containerColor, true);
        mat.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha); 
        mat.getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Front);
        mat.getMaterial().setColor("Ambient", containerColor);
        container1.setMaterial(mat.getMaterial());
        container1.setLocalTranslation(-3, 0, 0);
        //container1.rotate(0, 0.5f, 0);
        container1.setQueueBucket(Bucket.Transparent);
        MouseEventControl.addListenersToSpatial(container1, new HighlightListener(mat, containerHighlight, containerColor));
        
        dndRoot.attachChild(container1);*/
        
        container1 = new ContainerNode("container1", containerColor);
        container1.setLocalTranslation(-3, 0, -4); //-2.5f, 0);
System.out.println("Adding stack control...");        
        container1.addControl(new DragAndDropControl(new StackContainerListener(container1)));
        container1.addControl(new StackControl());
System.out.println("Stack control added.");        
        MouseEventControl.addListenersToSpatial(container1, 
                                                new HighlightListener(container1.material, 
                                                                      containerHighlight, 
                                                                      containerColor));
        dndRoot.attachChild(container1);
        
        container2 = new ContainerNode("container2", containerColor);
        container2.setSize(3, 3, 1);
        container2.setLocalTranslation(2f, -0.5f, -4);
        MouseEventControl.addListenersToSpatial(container2, 
                                                new HighlightListener(container2.material, 
                                                                      containerHighlight, 
                                                                      containerColor));
System.out.println("Adding grid control...");                                                                      
        container2.addControl(new GridControl(3));
System.out.println("GridControl added.");        
        container2.addControl(new DragAndDropControl(new GridContainerListener(container2)));
        dndRoot.attachChild(container2);
        
        //container1.attachChild(geom);
        container1.getControl(StackControl.class).addChild(createItem());
        container1.getControl(StackControl.class).addChild(createItem());
        
        container2.getControl(GridControl.class).setCell(0, 0, createItem());  
        container2.getControl(GridControl.class).setCell(2, 1, createItem());  
    }
 
    private Spatial createItem() {
        Sphere sphere = new Sphere(12, 24, 1);
        Geometry geom = new Geometry("item", sphere);
        Material mat = GuiGlobals.getInstance().createMaterial(ColorRGBA.Blue, true).getMaterial();
        //mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Ambient", ColorRGBA.Blue);
        geom.setMaterial(mat);
        return geom;
    }
    
    @Override 
    protected void cleanup( Application app ) {
    }
    
    @Override
    protected void onEnable() {        
        ((DemoLauncher)getApplication()).getRootNode().attachChild(dndRoot);
    }
    
    @Override
    protected void onDisable() {
        dndRoot.removeFromParent();
    }
    
    @Override
    public void update( float tpf ) {
        //System.out.println("-------- update -----");
    }
 
    /**
     *  Just to encapsulate the visuals needed to have both a wireframe
     *  view but an actual box for picking.
     */   
    private class ContainerNode extends Node {
 
        private GuiMaterial material;
        private WireBox wire;
        private Geometry wireGeom;
        private Box box;
        private Geometry boxGeom;
        
        public ContainerNode( String name, ColorRGBA color ) {
            super(name);
            material = GuiGlobals.getInstance().createMaterial(containerColor, false);
                   
            wire = new WireBox(1, 1, 1);
            wireGeom = new Geometry(name + ".wire", wire);
            wireGeom.setMaterial(material.getMaterial());
            attachChild(wireGeom);
            
            box = new Box(1, 1, 1);
            boxGeom = new Geometry(name + ".box", box);
            boxGeom.setMaterial(material.getMaterial()); // might as well reuse it
            boxGeom.setCullHint(CullHint.Always); // invisible
            attachChild(boxGeom);
        }
        
        private void setSize( float x, float y, float z ) {
            wire.updatePositions(x, y, z);
            box.updateGeometry(Vector3f.ZERO, x, y, z);
            box.clearCollisionData();
            
            wireGeom.updateModelBound();
            boxGeom.updateModelBound();
        }
    }
    
    private class StackControl extends AbstractControl {
    
        private ContainerNode node;
        private List<Spatial> model = new ArrayList<>();
 
        public StackControl() {
        }
 
        @Override       
        public void setSpatial( Spatial s ) {
System.out.println(getClass().getSimpleName() + ".setSpatial(" + s + ")");
            super.setSpatial(s);
            this.node = (ContainerNode)s;
            updateLayout();
        }
    
        public void addChild( Spatial child ) {
System.out.println("Stack addChild(" + child + ")");        
            model.add(child);
            node.attachChild(child);
            updateLayout();
        }
        
        public void removeChild( Spatial child ) {
System.out.println("Stack removeChild(" + child + ")");        
            model.remove(child);
            if( child.getParent() == node ) {
                node.detachChild(child);
            }            
            updateLayout();
        }
 
        protected void reset( Spatial child ) {
System.out.println("Stack reset(" + child + ")");        
            node.attachChild(child);
            updateLayout();
        }
 
        protected void updateLayout() {
System.out.println("Stack updateLayout()");        
            int count = Math.max(1, model.size());
            node.setSize(1, count, 1);
            float yStart = -(count - 1);
            for( Spatial s : model ) {
                s.setLocalTranslation(0, yStart, 0);
                yStart += 2;
            }   
        }
    
        @Override
        protected void controlUpdate( float tpf ) {
        }
    
        @Override
        protected void controlRender( RenderManager rm, ViewPort vp ) {
        }
    }

    private class GridControl extends AbstractControl {
 
        private ContainerNode node;
        private int gridSize;
        private Spatial[][] grid;
    
        public GridControl( int gridSize ) {
            this.gridSize = gridSize;
            this.grid = new Spatial[gridSize][gridSize];
        }
    
        @Override       
        public void setSpatial( Spatial s ) {
System.out.println(getClass().getSimpleName() + ".setSpatial(" + s + ")");
            super.setSpatial(s);
            this.node = (ContainerNode)s;
            updateLayout();
        }
        
        public Spatial getCell( int x, int y ) {
            return grid[x][y];
        }
        
        public void setCell( int x, int y, Spatial child ) {
System.out.println("Grid setCell(" + x + ", " + y + ", " + child + ")");        
            if( grid[x][y] != null ) {
                grid[x][y].removeFromParent();
            }
            grid[x][y] = child;
            if( child != null ) {
                node.attachChild(child);
            }
            updateLayout();
        }
        
        public void removeChild( Spatial child ) {
System.out.println("Grid removeChild(" + child + ")");        
            for( int x = 0; x < gridSize; x++ ) {
                for( int y = 0; y < gridSize; y++ ) {
                    if( child == grid[x][y] ) {
System.out.println("  found child:" + grid[x][y]);                    
                        if( child.getParent() == node ) {
System.out.println("  removing child:" + child + "  from parent:" + child.getParent());                        
                            child.removeFromParent();
                        }
                        grid[x][y] = null;
                    }
                }
            }
            updateLayout();
        }

        protected void updateLayout() {
System.out.println(getClass().getSimpleName() + ".updateLayout() node:" + node);        
            node.setSize(gridSize, gridSize, 1);
            for( int x = 0; x < gridSize; x++ ) {
                for( int y = 0; y < gridSize; y++ ) {
                    Spatial child = grid[x][y];
                    if( child != null ) {
                        child.setLocalTranslation(-(gridSize - 1) + x * 2, (gridSize - 1) - y * 2, 0);
                    }
                }
            }
        }
    
        protected void reset( Spatial child ) {
System.out.println("Grid reset(" + child + ")");        
            node.attachChild(child);
            updateLayout();
        }
        
        @Override
        protected void controlUpdate( float tpf ) {
        }
    
        @Override
        protected void controlRender( RenderManager rm, ViewPort vp ) {
        }
    }
    
    /**
     *  Listens for enter/exit events and changes the color of
     *  the geometry accordingly.
     */   
    private class HighlightListener extends DefaultMouseListener {
        private GuiMaterial material;
        private ColorRGBA enterColor;
        private ColorRGBA exitColor;
        
        public HighlightListener( GuiMaterial material, ColorRGBA enterColor, ColorRGBA exitColor ) {
            this.material = material;
            this.enterColor = enterColor;
            this.exitColor = exitColor;
        }
        
        public void mouseEntered( MouseMotionEvent event, Spatial target, Spatial capture ) {
            material.setColor(enterColor);
        }

        public void mouseExited( MouseMotionEvent event, Spatial target, Spatial capture ) {
            material.setColor(exitColor);
        }        
    }

    private class StackContainerListener implements DragAndDropListener {    

        private Spatial container;

        public StackContainerListener( Spatial container ) {
            this.container = container;            
        }

        private int getIndex( Vector3f world ) {
            world = container.worldToLocal(world, null);
            
            System.out.println("Stack Hit:" + world);
            
            // Calculate the index
            float y = (container.getControl(StackControl.class).model.size() + world.y) / 2;
            
            System.out.println("Index:" + y);
            return (int)y;
        }
        
        public Draggable onDragDetected( DragEvent event ) {
        
            System.out.println("Stack.onDragDetected(" + event + ")");
 
            // Find the child we collided with
            StackControl control = container.getControl(StackControl.class);
            
            // For now just use the first one
            if( control.model.isEmpty() ) {
                return null;
            }
            
            int index = getIndex(event.getCollision().getContactPoint());
            
            Spatial item = control.model.get(index);
            event.getSession().set(DragSession.ITEM, item);
        
            // Since we are reusing the item for our draggable, we need to
            // switch it to the root node
            Vector3f world = item.getWorldTranslation();
            Quaternion rot = item.getWorldRotation();
            getRoot().attachChild(item);
            item.setLocalTranslation(world);
            item.setLocalRotation(rot);
        
            return new ColoredDraggable(event.getViewPort(), item, event.getLocation());
        } 
    
        public void onDragEnter( DragEvent event ) {
            System.out.println("++++++++ Stack.onDragEnter(" + event + ")");
        }
          
        public void onDragExit( DragEvent event ) {
            System.out.println("-------- Stack.onDragExit(" + event + ")");
        }
        
        public void onDragOver( DragEvent event ) {
            System.out.println("Stack.onDragOver(" + event + ")");
            
            // Any location is valid on the stack
            event.getSession().setDragStatus(DragStatus.ValidTarget);
        }  
    
        // Target specific
        public void onDrop( DragEvent event ) {
            System.out.println("Stack.onDrop(" + event + ")");
 
            // Grab the payload we stored during drag start
            Spatial draggedItem = event.getSession().get(DragSession.ITEM, null);
 
            // Back door reset the color just in case.  We wouldn't have
            // to do this if we cloned the item for dragging.
            ColoredDraggable draggable = (ColoredDraggable)event.getSession().getDraggable();            
            draggable.updateDragStatus(DragStatus.ValidTarget);
 
            if( event.getSession().getDragSource() != container ) {                                  
                // Add the item to this stack
                container.getControl(StackControl.class).addChild(draggedItem);
            } else {
                // Just reset it... we don't want to add it because we won't
                // be removing it.
                container.getControl(StackControl.class).reset(draggedItem);
            }            
        }
    
        // Source specific  
        public void onDragDone( DragEvent event ) {
            System.out.println("Stack.onDragDone(" + event + ")");

            // Grab the payload we stored during drag start
            Spatial draggedItem = event.getSession().get(DragSession.ITEM, null);
            
System.out.println("Us:" + container + "  drop target:" + event.getSession().getDropTarget());
            
            if( event.getSession().getDropTarget() == null ) {               
                // Then the drag failed and we need to suck the item back
                // Back door reset the color since we know we're going to
                // stick it back in our model properly
                ColoredDraggable draggable = (ColoredDraggable)event.getSession().getDraggable();
                draggable.updateDragStatus(DragStatus.ValidTarget);
                container.getControl(StackControl.class).reset(draggedItem);
            } else if( event.getSession().getDropTarget() != container ) {           
                // Then we need to remove the item from ourselves
                container.getControl(StackControl.class).removeChild(draggedItem);            
            } else {
                // If the target is ourselves then we don't need to
                // do anything as the drop already did it.
            }
        }
    }  
    
    private class GridContainerListener implements DragAndDropListener {    

        private Spatial container;

        public GridContainerListener( Spatial container ) {
            this.container = container;            
        }

        private Vector2f getCellLocation( Vector3f world ) {
            world = container.worldToLocal(world, null);
            
            System.out.println("Hit:" + world);
            
            // Calculate the cell location
            float x = (3 + world.x) / 2;
            float y = (3 - world.y) / 2;
            
            // This will look a little off to the user towards the right edge because
            // clicking on the surface of the box in the center cell will actually project
            // into the sphere in the last column.  But it works for a demo.  We could
            // also have made a ray and done collideWith() on the childre but I wanted
            // to show model-cell interaction instead of picking.
            int xCell = (int)x;
            int yCell = (int)y; 
 
            System.out.println("Cell:" + x + ", " + y);
            return new Vector2f(xCell, yCell);
        }

        public Draggable onDragDetected( DragEvent event ) {
        
            System.out.println("Grid.onDragDetected(" + event + ")");
 
            // Find the child we collided with
            GridControl control = container.getControl(GridControl.class);
 
            // See where we hit
            Vector2f hit = getCellLocation(event.getCollision().getContactPoint());
 
            Spatial item = control.getCell((int)hit.x, (int)hit.y);
            if( item != null ) {
                event.getSession().set(DragSession.ITEM, item);
                
                // Since we are reusing the item for our draggable, we need to
                // switch it to the root node
                Vector3f world = item.getWorldTranslation();
                Quaternion rot = item.getWorldRotation();
                getRoot().attachChild(item);
                item.setLocalTranslation(world);
                item.setLocalRotation(rot);
                
                return new ColoredDraggable(event.getViewPort(), item, event.getLocation());
            }       
            return null;
        } 
    
        public void onDragEnter( DragEvent event ) {
            System.out.println("+++++++ Grid.onDragEnter(" + event + ")");
        }
          
        public void onDragExit( DragEvent event ) {
            System.out.println("------- Grid.onDragExit(" + event + ")");
        }
        
        public void onDragOver( DragEvent event ) {
            System.out.println("Grid.onDragOver(" + event + ")");
            GridControl control = container.getControl(GridControl.class);
            
            // Grab the payload we stored during drag start
            Spatial draggedItem = event.getSession().get(DragSession.ITEM, null);

            //ColoredDraggable draggable = (ColoredDraggable)event.getSession().getDraggable();
            
            Vector2f hit = getCellLocation(event.getCollision().getContactPoint()); 
            Spatial item = control.getCell((int)hit.x, (int)hit.y);
            if( item == null ) {
                // An empty cell is a valid target
                event.getSession().setDragStatus(DragStatus.ValidTarget);
            } else if( item == draggedItem ) {
                // Our old slot is also a valid target
                event.getSession().setDragStatus(DragStatus.ValidTarget);
            } else {
                // A filled slot is not
                event.getSession().setDragStatus(DragStatus.InvalidTarget);
            }
        }  
    
        // Target specific
        public void onDrop( DragEvent event ) {
            System.out.println("Grid.onDrop(" + event + ")");
            
            Spatial draggedItem = event.getSession().get(DragSession.ITEM, null);
            GridControl control = container.getControl(GridControl.class);           
            //ColoredDraggable draggable = (ColoredDraggable)event.getSession().getDraggable();

            Vector2f hit = getCellLocation(event.getCollision().getContactPoint()); 
            Spatial item = control.getCell((int)hit.x, (int)hit.y);
            if( item == null || item == draggedItem ) {
                // Then we can stick the new child right in
                // Remove it from ourselves first just to clear it from old locations
                control.removeChild(draggedItem);
                control.setCell((int)hit.x, (int)hit.y, draggedItem);
            } else {
                // It wasn't really a valid drop
                event.getSession().setDragStatus(DragStatus.InvalidTarget);   
            }
        }
    
        // Source specific  
        public void onDragDone( DragEvent event ) {
            System.out.println("Grid.onDragDone(" + event + ")");
            
            DragSession session = event.getSession();
            Spatial draggedItem = session.get(DragSession.ITEM, null);
            
            if( session.getDropTarget() == container ) {
                // It's us... so we don't have to do anything
            } else if( session.getDropTarget() != null ) {
                // Then remove the item from our children
                container.getControl(GridControl.class).removeChild(draggedItem);
            } else {
                // We need to snap it back to where it was
                ColoredDraggable draggable = (ColoredDraggable)session.getDraggable();
                draggable.updateDragStatus(DragStatus.ValidTarget);
                container.getControl(GridControl.class).reset(draggedItem);
            } 
        }
    }  
    
    private class ColoredDraggable extends DefaultDraggable {
 
        private ColorRGBA color = ColorRGBA.Blue; 
        private ColorRGBA none = ColorRGBA.Gray;
        private ColorRGBA invalid = ColorRGBA.Red;
        private Material mat;
    
        public ColoredDraggable( ViewPort view, Spatial spatial, Vector2f start ) {
            super(view, spatial, start);
 
            this.mat = ((Geometry)spatial).getMaterial();
        }
 
        protected void setColor( ColorRGBA color ) {
            mat.setColor("Diffuse", color);
            mat.setColor("Ambient", color);
        }
 
        @Override       
        public void updateDragStatus( DragStatus status ) {
System.out.println("updateDragStatus(" + status + ")");        
            switch( status ) {
                case InvalidTarget:
                    setColor(invalid);
                    break;
                case ValidTarget:
                    setColor(color);
                    break;  
                case NoTarget:
                default:
                    setColor(none);
                    break;
            }
        }        
    }
}
