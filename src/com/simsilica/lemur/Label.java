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

import com.simsilica.lemur.style.StyleDefaults;
import com.simsilica.lemur.style.Attributes;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.style.StyleAttribute;
import com.simsilica.lemur.style.Styles;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;


import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.component.TextComponent;

/**
 *
 *  @author    Paul Speed
 */
public class Label extends Panel
{
    public static final String ELEMENT_ID = "label";
    
    public static final String KEY_TEXT = "text";
    public static final String KEY_SHADOW_TEXT = "shadowText";

    private TextComponent text;
    private TextComponent shadow;

    public Label( String s )
    {
        this( s, true, new ElementId(ELEMENT_ID), null );
    }
    
    public Label( String s, String style )
    {
        this( s, true, new ElementId(ELEMENT_ID), style );
    }                 

    public Label( String s, ElementId elementId, String style )
    {
        this( s, true, elementId, style );
    }                 

    protected Label( String s, boolean applyStyles, ElementId elementId, String style )
    {
        super(false, elementId, style);
            
        Styles styles = GuiGlobals.getInstance().getStyles();
        this.text = new TextComponent( s, styles.getAttributes(elementId.getId(), style).get( "font", BitmapFont.class ) );
        getControl(GuiControl.class).addComponent(KEY_TEXT, text);
        
        if( applyStyles )        
            styles.applyStyles( this, elementId.getId(), style );        
    }

    @StyleDefaults(ELEMENT_ID)
    public static void initializeDefaultStyles( Attributes attrs )
    {
    }    

    @StyleAttribute(value="text", lookupDefault=false)   
    public void setText( String s )
    {
        text.setText(s);
        if( shadow != null )
            shadow.setText(s);   
    }
    
    public String getText()
    {
        return text == null ? null : text.getText();
    }
 
    @StyleAttribute(value="textVAlignment", lookupDefault=false)   
    public void setTextVAlignment( VAlignment a )
    {
        text.setVAlignment(a);
        if( shadow != null )
            shadow.setVAlignment(a);
    }
 
    public VAlignment getTextVAlignment()
    {
        return text.getVAlignment();
    }
    
    @StyleAttribute(value="textHAlignment", lookupDefault=false)   
    public void setTextHAlignment( HAlignment a )
    {
        text.setHAlignment(a);
        if( shadow != null )
            shadow.setHAlignment(a);
    } 

    public HAlignment getTextHAlignment()
    {
        return text.getHAlignment();
    }
     
    public BitmapFont getFont()
    {
        return text.getFont();
    }
 
    @StyleAttribute("color")   
    public void setColor( ColorRGBA color )
    {
        text.setColor(color);
    }
    
    public ColorRGBA getColor()
    {
        return text == null ? null : text.getColor();
    }

    @StyleAttribute("fontSize")   
    public void setFontSize( float f )
    {
        text.setFontSize(f);
        if( shadow != null )
            shadow.setFontSize(f);
    }
    
    public float getFontSize()
    {
        return text == null ? 0 : text.getFontSize();
    }
    
    @StyleAttribute(value="shadowColor", lookupDefault=false)   
    public void setShadowColor( ColorRGBA color )
    {
        if( shadow == null )
            {
            if( color == null )
                return;
 
            // Else we need to create the shadow
            this.shadow = new TextComponent( getText(), getFont() );
            shadow.setOffset( 1, -1, 1f );
            shadow.setFontSize( getFontSize() );
            getControl(GuiControl.class).insertComponent( shadow, text );
            }
        else if( color == null )
            {
            // Need to remove it
            getControl(GuiControl.class).removeComponent(shadow);
            shadow = null;
            return;
            }
                    
        shadow.setColor(color);
    }
    
    public ColorRGBA getShadowColor()
    {
        if( shadow == null )
            return null;
        return shadow.getColor();
    }
    
    public String toString()
    {
        return getClass().getName() + "[text=" + getText() + ", color=" + getColor() + "]";
    }
}