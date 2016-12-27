package org.liquidengine.legui.render.nvg.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.component.optional.TextState;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.context.LeguiContext;
import org.liquidengine.legui.render.nvg.NvgLeguiComponentRenderer;
import org.liquidengine.legui.util.ColorConstants;
import org.liquidengine.legui.util.Util;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGGlyphPosition;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.liquidengine.legui.util.ColorUtil.oppositeBlackOrWhite;
import static org.liquidengine.legui.util.NVGUtils.rgba;
import static org.liquidengine.legui.util.NvgRenderUtils.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by Shcherbin Alexander on 5/20/2016.
 */
public class NvgTextInputRenderer extends NvgLeguiComponentRenderer {
    public static final  String                  PRATIO        = "pratio";
    public static final  String                  POFFSET       = "poffset";
    private static final Logger                  LOGGER        = LogManager.getLogger();
    private final        Vector4f                caretColor    = new Vector4f(0, 0, 0, 0.5f);
    private final        int                     maxGlyphCount = 1024;
    private              NVGGlyphPosition.Buffer glyphs        = NVGGlyphPosition.create(maxGlyphCount);
    private              NVGColor                colorA        = NVGColor.create();

    @Override
    public void render(Component component, LeguiContext leguiContext, long context) {
        createScissor(context, component);
        {
            TextInput agui    = (TextInput) component;
            Vector2f  pos     = Util.calculatePosition(component);
            Vector2f  size    = component.getSize();
            boolean   enabled = agui.isEnabled();
            Vector4f  bc      = new Vector4f(agui.getBackgroundColor());

            if (enabled && agui.getState().isFocused()) {
                bc.w *= 1.1f;
            } else if (!enabled) {
                bc.w *= 0.5f;
            }
            if (!agui.isEditable()) {
                bc.w *= 0.3f;
            }
            drawBackground(context, pos.x, pos.y, size.x, size.y, agui.getCornerRadius(), bc);

            TextState textState = agui.getTextState();
            Vector4f  p         = new Vector4f(textState.getPadding()).add(2,2,2,2);

            Vector4f intersectRect = new Vector4f(pos.x + p.x, pos.y + p.y, size.x - p.x - p.z, size.y - p.y - p.w);
//            drawRectStroke(context, ColorConstants.red(),intersectRect, 0,1);
            intersectScissor(context, intersectRect);
            renderTextNew(leguiContext, context, agui, size, intersectRect, bc);
        }
        resetScissor(context);

        createScissor(context, component);
        {
            renderBorder(component, leguiContext);
        }
        resetScissor(context);
    }

    private void renderTextNew(LeguiContext leguiContext, long context, TextInput gui, Vector2f size, Vector4f rect, Vector4f bc) {
        TextState           textState           = gui.getTextState();
        String              text                = textState.getText();
        String              font                = textState.getFont();
        float               fontSize            = textState.getFontSize();
        Vector4f            highlightColor      = gui.getSelectionColor();
        HorizontalAlign     halign              = textState.getHorizontalAlign();
        VerticalAlign       valign              = textState.getVerticalAlign();
        Vector4f            textColor           = textState.getTextColor();
        int                 caretPosition       = gui.getCaretPosition();
        Map<String, Object> metadata            = gui.getMetadata();
        int                 startSelectionIndex = gui.getStartSelectionIndex();
        int                 endSelectionIndex   = gui.getEndSelectionIndex();
        boolean             focused             = gui.getState().isFocused();


        // initially configure text rendering
        alignTextInBox(context, halign, valign);
        nvgFontSize(context, fontSize);
        nvgFontFace(context, font);
        nvgFillColor(context, rgba(textColor, colorA));

        if (!focused) {
            caretPosition = 0;
        }

        float[] textBounds = calculateTextBoundsRect(context, rect.x, rect.y, rect.z, rect.w, text, halign, valign);

        // calculate caret coordinate and mouse caret coordinate
        float      caretx             = 0;
        float      startSelectionX    = 0;
        float      endSelectionX      = 0;
        float      mouseCaretX        = 0;
        int        mouseCaretPosition = 0;
        ByteBuffer textBytes          = null;
        try {
            // allocate ofheap memory and fill it with text
            textBytes = memUTF8(text);
            // align text for calculations
            alignTextInBox(context, HorizontalAlign.LEFT, VerticalAlign.MIDDLE);
            int ng = nnvgTextGlyphPositions(context, textBounds[4], 0, memAddress(textBytes), 0, memAddress(glyphs), maxGlyphCount);

//            for (int i = 0; i < ng; i++) {
//                NVGGlyphPosition g    = glyphs.get(i);
//                float            x    = g.x();
//                float            maxx = g.maxx();
//
//                drawRectangle(context, new Vector4f(0, 1, 0, 0.5f), x, rect.y, maxx - x, rect.w);
//            }

            // get caret position on screen based on caret position in text
            // and get x position of first and last selection
            caretx = calculateCaretPos(caretPosition, textBounds, ng);
            startSelectionX = calculateCaretPos(startSelectionIndex, textBounds, ng);
            endSelectionX = calculateCaretPos(endSelectionIndex, textBounds, ng);


            // calculate text offset in text field based on caret position on screen
            // (caret always should be inside text field bounds)
            float offsetX = 0;
            if (caretx > rect.z + rect.x) {
                offsetX = caretx - rect.x - rect.z;
            } else if (caretx < rect.x) {
                offsetX = caretx - rect.x;
            }

            // get previous offset
            Float poffset = offsetX;
            if (metadata.containsKey(POFFSET)) poffset = (Float) metadata.get(POFFSET);
            else metadata.put(POFFSET, poffset);

            // get previous ratio
            float rat    = size.y * size.x;
            Float pratio = rat;
            if (metadata.containsKey(PRATIO)) pratio = (Float) metadata.get(PRATIO);
            else metadata.put(PRATIO, pratio);

            // we should recalculate offsets if ratio is changed
            if (pratio != rat) {
                poffset = offsetX;
            } else {
                // and if ratio is the same we should check if we need to update offset
                if (caretx - poffset > rect.z + rect.x) {
                    poffset = poffset + (caretx - poffset - rect.z - rect.x);
                } else if (caretx - poffset < rect.x) {
                    poffset = poffset + (caretx - poffset - rect.x);
                }
            }
            // put last offset and ration to metadata
            metadata.put(POFFSET, poffset);
            metadata.put(PRATIO, rat);

            // calculate mouse caret position
            if (text.length() == 0) {
                mouseCaretX = caretx;
            } else {
                float mx = leguiContext.getCursorPosition().x + poffset;
                if (mx <= glyphs.get(0).minx()) {
                    mouseCaretPosition = 0;
                    mouseCaretX = glyphs.get(0).minx();
                } else if (mx >= glyphs.get(ng - 1).maxx()) {
                    mouseCaretPosition = ng;
                    mouseCaretX = glyphs.get(ng - 1).maxx();
                } else {
                    // binary search mouse caret position
                    int     upper = ng;
                    int     lower = 0;
                    boolean found = false;
                    do {
                        int   index = (upper + lower) / 2;
                        float left  = index == 0 ? glyphs.get(index).minx() : glyphs.get(index).x();
                        float right = index == ng ? glyphs.get(index).maxx() : glyphs.get(index + 1).x();
                        float mid   = (left + right) / 2f;
                        if (mx >= left && mx < right) {
                            found = true;
                            if (mx > mid) {
                                mouseCaretPosition = index + 1;
                                mouseCaretX = right;
                            } else {
                                mouseCaretPosition = index;
                                mouseCaretX = left;
                            }
                        } else if (mx >= right) {
                            if (index != ng) {
                                lower = index + 1;
                            } else {
                                found = true;
                                mouseCaretPosition = ng;
                                mouseCaretX = right;
                            }
                        } else if (mx < left) {
                            if (index != 0) {
                                upper = index;
                            } else {
                                found = true;
                                mouseCaretPosition = 0;
                                mouseCaretX = left;
                            }
                        }
                    } while (!found);
                }
            }
            mouseCaretX -= poffset;
            float nCaretX = caretx - poffset;

            if (focused) {
                // calculate caret color based on time
                oppositeBlackOrWhite(bc, caretColor);
                caretColor.w = (float) Math.abs(GLFW.glfwGetTime() % 1 * 2 - 1);

                // draw selection
                if (startSelectionIndex != endSelectionIndex) {
                    drawRectangle(context, highlightColor, startSelectionX - poffset, rect.y, endSelectionX - startSelectionX, rect.w);
                }
            }
            // render text
            renderTextLineToBounds(context, textBounds[4] - poffset, textBounds[5], textBounds[6], textBounds[7], fontSize, font, textColor, text, HorizontalAlign.LEFT, VerticalAlign.MIDDLE, false);

            if (focused) {
                // render caret
                drawRectStroke(context, nCaretX - 1, rect.y, 1, rect.w, caretColor, 0, 1);
            }
            // render mouse caret
            if (leguiContext.isDebugEnabled()) {
                Vector4f cc = new Vector4f(this.caretColor);
                cc.x = 1;
                drawRectStroke(context, mouseCaretX, rect.y, 1, rect.w, cc, 0, 1);
            }

        } finally {
            // free allocated memory
            if (textBytes != null) {
                memFree(textBytes);
            }
        }
        gui.setMouseCaretPosition(mouseCaretPosition);
    }

    private float calculateCaretPos(int caretPosition, float[] textBounds, int ng) {
        float caretx = 0;
        if (caretPosition < ng) {
            try {
                caretx = caretPosition == 0 ? glyphs.get(caretPosition).minx() : glyphs.get(caretPosition).x();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else {
            if (ng > 0) {
                caretx = glyphs.get(ng - 1).maxx();
            } else {
                caretx = textBounds[4];
            }
        }
        return caretx;
    }

    private void drawBackground(long context, float x, float y, float w, float h, float br, Vector4f bc) {
        if (bc.w != 0) {
            nvgSave(context);
            nvgBeginPath(context);
            nvgRoundedRect(context, x, y, w, h, br);
            nvgFillColor(context, rgba(bc, colorA));
            nvgFill(context);
        }
    }

}