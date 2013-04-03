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

package com.simsilica.lemur;

import com.simsilica.lemur.core.GuiMaterial;
import com.simsilica.lemur.core.UnshadedMaterialAdapter;
import com.simsilica.lemur.core.LightingMaterialAdapter;
import com.simsilica.lemur.style.Styles;
import com.simsilica.lemur.event.KeyListener;
import com.simsilica.lemur.event.KeyInterceptState;
import com.simsilica.lemur.event.MouseAppState;
import com.simsilica.lemur.event.FocusManagerState;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.font.BitmapFont;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.simsilica.lemur.input.InputMapper;


/**
 *
 *  @author    Paul Speed
 */
public class GuiGlobals
{
    private static GuiGlobals instance;
 
    private AssetManager assets;
    private InputMapper  inputMapper;
    private KeyInterceptState keyInterceptor;
    private MouseAppState mouseState;
    private FocusManagerState focusState;
    private String iconBase;
 
    private Styles styles;
 
    public static void initialize( Application app )
    {
        setInstance( new GuiGlobals(app) );
    }
 
    public static void setInstance( GuiGlobals globals )
    {
        instance = globals;
    }
    
    public static GuiGlobals getInstance()
    {
        return instance;
    }

    protected GuiGlobals( Application app )
    {
        this.assets = app.getAssetManager();
        this.inputMapper = new InputMapper(app.getInputManager());
        this.keyInterceptor = new KeyInterceptState();
        this.mouseState = new MouseAppState();
        this.focusState = new FocusManagerState();
        app.getStateManager().attach( keyInterceptor );  
        app.getStateManager().attach( mouseState );
        app.getStateManager().attach( focusState );
 
        styles = new Styles();
        setDefaultStyles();
        
        iconBase = getClass().getPackage().getName().replace( '.', '/' ) + "/icons";
 
        ViewPort main = app.getViewPort();
        setupGuiComparators(main);
        /*RenderQueue rq = main.getQueue();
               
        rq.setGeometryComparator( Bucket.Opaque, new LayerComparator( rq.getGeometryComparator(Bucket.Opaque) ) );
        rq.setGeometryComparator( Bucket.Transparent, new LayerComparator( rq.getGeometryComparator(Bucket.Transparent), -1 ) );
        rq.setGeometryComparator( Bucket.Gui, new LayerComparator( rq.getGeometryComparator(Bucket.Gui) ) );*/
                                      
    }

    public void setupGuiComparators( ViewPort view )
    {
        RenderQueue rq = view.getQueue();
               
        rq.setGeometryComparator( Bucket.Opaque, new LayerComparator( rq.getGeometryComparator(Bucket.Opaque) ) );
        rq.setGeometryComparator( Bucket.Transparent, new LayerComparator( rq.getGeometryComparator(Bucket.Transparent), -1 ) );
        rq.setGeometryComparator( Bucket.Gui, new LayerComparator( rq.getGeometryComparator(Bucket.Gui) ) );
    }

    protected void setDefaultStyles()
    {
        styles.setDefault( loadFont("Interface/Fonts/Default.fnt") );
        styles.setDefault( ColorRGBA.LightGray );  
 
        // Setup some default styles for the "DEFAULT" Style
        styles.getSelector( null ).set( "color", ColorRGBA.White );        
    }

    public Styles getStyles()
    {
        return styles;
    }

    public InputMapper getInputMapper()
    {
        return inputMapper;
    }

    public void fixFont( BitmapFont font )
    {
        for( int i = 0; i < font.getPageSize(); i++ )
            {
            Material m = font.getPage(i);
            m.getAdditionalRenderState().setAlphaTest(true);
            m.getAdditionalRenderState().setAlphaFallOff(0.1f);
            }
    }

    private Texture getTexture( Material mat, String name ) 
    {
        MatParam mp = mat.getParam(name);
        if( mp == null ) {
            return null;
        }
        return (Texture)mp.getValue(); 
    }

    public void lightFont( BitmapFont font )
    {
        Material[] pages = new Material[font.getPageSize()];
        for( int i = 0; i < pages.length; i++ )
            {
            Material original = font.getPage(i);
            Material m = new Material(assets, "Common/MatDefs/Light/Lighting.j3md");
            m.setTexture( "DiffuseMap", getTexture(original, "ColorMap") );
            pages[i] = m;            
            }
        font.setPages(pages);
    }    

    public BitmapFont loadFont( String path )
    {
        BitmapFont result = assets.loadFont(path);
        fixFont(result);
        return result;
    } 

    public GuiMaterial createMaterial( boolean lit )
    {
        if( lit )
            return new LightingMaterialAdapter(new Material(assets, "Common/MatDefs/Light/Lighting.j3md"));
        else
            return new UnshadedMaterialAdapter(new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md"));
    }

    public GuiMaterial createMaterial( ColorRGBA color, boolean lit )
    {
        GuiMaterial mat = createMaterial(lit);
        mat.setColor(color);
        return mat;
    }

    public GuiMaterial createMaterial( Texture texture, boolean lit )
    {
        GuiMaterial mat = createMaterial(lit);
        mat.setTexture(texture);
        return mat;
    }

    public Texture loadDefaultIcon( String name )
    {
        return loadTexture( iconBase + "/" + name, false, false );
    }

    public Texture loadTexture( String path, boolean repeat, boolean generateMips )
    {
        TextureKey key = new TextureKey(path);
        key.setGenerateMips(generateMips);
        
        Texture t = assets.loadTexture(key);
        if( t == null )
            throw new RuntimeException( "Error loading texture:" + path );
 
        if( repeat )
            {
            t.setWrap( Texture.WrapMode.Repeat );
            }
        else
            {
            t.setWrap(Texture.WrapMode.Clamp); 
            }                
                     
        return t;
    }
    
    public void requestFocus( Spatial s )
    {
        focusState.setFocus(s);
    }
    
    public void addKeyListener( KeyListener l )
    {
        keyInterceptor.addKeyListener(l);
    }
    
    public void removeKeyListener( KeyListener l )
    {
        keyInterceptor.removeKeyListener(l);
    }
    
    public ViewPort getCollisionViewPort( Spatial s )
    {
        return mouseState.findViewPort(s);
    }
    
    public Vector3f getScreenCoordinates( Spatial relativeTo, Vector3f pos )
    {
        ViewPort vp = getCollisionViewPort( relativeTo );
        if( vp == null )
            throw new RuntimeException( "Could not find viewport for:" + relativeTo );
 
        // Calculate the world position relative to the spatial
        pos = relativeTo.localToWorld(pos, null);
        
        Camera cam = vp.getCamera();
        if( cam.isParallelProjection() )
            return pos.clone();
        
        return cam.getScreenCoordinates(pos);        
    }
}
