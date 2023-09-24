/*
 * This file is part of GraphStream <http://graphstream-project.org>.
 *
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 *
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */

/**
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * Modified for dendroloj by:
 * @author FeldrinH
 * See comments starting with 'DENDROLOJ EDIT' for information about the changes made.
 */

package org.graphstream.ui.swing.renderer.shape.swing;

import org.graphstream.ui.swing.Backend;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;

@SuppressWarnings("unused")
class SwingTextBox extends TextBox {

    Font font;
    Color textColor;
    Color bgColor;
    boolean rounded;
    double padx;
    double pady;

    TextLayout[] text;
    double width;
    double height;

    public SwingTextBox(Font font, Color textColor, Color bgColor, boolean rounded, double padx, double pady) {
        this.font = font;
        this.textColor = textColor;
        this.bgColor = bgColor;
        this.rounded = rounded;
        this.padx = padx;
        this.pady = pady;

        this.text = null;
        this.textData = null;
        this.width = 0.0;
        this.height = 0.0;
    }


    /**
     * Changes the text and compute its bounds. This method tries to avoid recomputing bounds
     * if the text does not really changed.
     */
    public void setText(String text, Backend backend) {
        // DENDROLOJ EDIT:
        // Add support for rendering multiple lines of text.

        if (text == null || text.isBlank()) {
            this.textData = null;
            this.text = null;
            this.width = 0.0;
            this.height = 0.0;
            return;
        }
        if (Objects.equals(text, textData)) {
            return;
        }

        // As the text is not rendered using the default affine transform, but using
        // the identity transform, and as the FontRenderContext uses the current
        // transform, we use a predefined default font render context initialized
        // with an identity transform here.

        // Note: There must be at least one line because zero lines would mean that the text was blank, which was checked before.
        String[] lines = text.split("\n");
        this.textData = text;
        this.text = new TextLayout[lines.length];
        this.width = 0.0;
        this.height = 0.0;
        for (int i = 0; i < lines.length; i++) {
            TextLayout line = new TextLayout(lines[i], font, TextBox.defaultFontRenderContext);
            this.text[i] = line;
            this.width = Math.max(this.width, line.getBounds().getWidth());
            this.height += line.getAscent() + line.getDescent();
        }
    }

    @Override
    public String getText() {
        return textData;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public double getDescent() {
        if (text != null)
            return text[0].getDescent();
        else
            return 0;
    }

    @Override
    public double getAscent() {
        if (text != null)
            return text[0].getAscent();
        else
            return 0;
    }

    @Override
    public void render(Backend backend, double xLeft, double yBottom) {
        if (text != null) {
            // DENDROLOJ EDIT:
            // Add support for rendering multiple lines of text.

            Graphics2D g = backend.graphics2D();

            if (bgColor != null) {
                double a = getAscent();
                double h = getHeight();

                g.setColor(bgColor);
                if (rounded) {
                    g.fill(new RoundRectangle2D.Double(xLeft - padx, yBottom - (a + pady), getWidth() + 1 + (padx + padx), h + (pady + pady), 6, 6));
                } else {
                    g.fill(new Rectangle2D.Double(xLeft - padx, yBottom - (a + pady), getWidth() + 1 + (padx + padx), h + (pady + pady)));
                }
            }

            g.setColor(textColor);
            yBottom -= getAscent();
            for (TextLayout line : text) {
                yBottom += line.getAscent();
                line.draw(g, (float) xLeft, (float) yBottom);
                yBottom += line.getDescent();
            }
        }
    }

}
