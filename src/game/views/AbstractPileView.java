/**
 * Daniel Ricci <thedanny09@icloud.com>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package game.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JLayeredPane;

import framework.core.mvc.view.PanelView;

/**
 * Abstract representation of a pile view
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public abstract class AbstractPileView extends PanelView {
        
    /**
     * Specifies the offset of each card within this view
     */
    public int CARD_OFFSET = 12;
    
    /**
     * The layered pane that holds the list of cards
     */
    protected final JLayeredPane layeredPane = new JLayeredPane();

    /**
     * Constructs a new instance of this class type
     */
    protected AbstractPileView() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(CardView.CARD_WIDTH, this.getPreferredSize().height));
        setOpaque(false);
        add(layeredPane, BorderLayout.CENTER);
    }

    /**
     * Removes the highlights from this view and its underlying cards
     */
    public void removeHighlight() {
        for(Component comp : layeredPane.getComponents()) {
            CardView cardView = (CardView)comp;
            cardView.setIsHighlighted(false);
        }
        setIsHighlighted(false);
        repaint();
    }

    /**
     * @return The last ordered card held within this pile view
     */
    public CardView getLastCard() {
        if(layeredPane.getComponentCount() == 0) {
            return null;
        }
        return (CardView)layeredPane.getComponents()[0];
    }
        
    /**
     * Adds the specified card view to this pile
     *
     * @param card The card to add to this pile
     * @param position The position of the card
     * 
     */
    public void addCard(CardView cardView) {
        // Hold onto a reference of the parent for repainting reasons
        Container parentCardView = cardView.getParent();
        
        // Get the list of components associated to the card view.
        // This list represents all the children associated to the said CardView.this reference.
        List<Component> components = new ArrayList<Component>(Arrays.asList(cardView.layeredPane.getComponents()));
        
        // Add the card view component, this will add it to the end
        components.add(cardView);
        
        // Reverse the list because layered panes associate objects closer to layer 0 as being closer to the screen.
        Collections.reverse(components);

        // Calculate the number of component withint this pile to use
        // as the layer identifier offset
        int position = layeredPane.getComponents().length;
        
        // Add the cards to this pile view
        for(Component comp : components) {
            layeredPane.add(comp);
            layeredPane.setLayer(comp, position);
            comp.setBounds(new Rectangle(
                0, 
                this.CARD_OFFSET * position,  
                comp.getPreferredSize().width, 
                comp.getPreferredSize().height)
            );
            
            ++position;
        }
        
        parentCardView.repaint();
        repaint();
    }
        
    @Override public void render() {
        super.render();
        for(Component component : layeredPane.getComponents()) {
            if(component instanceof CardView) {
                CardView view = (CardView) component;
                view.render();
            }
        }
        repaint();
    }
    
    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        String header = "========" + this.getClass().getSimpleName().toUpperCase() + "========";
        builder.append(header + System.getProperty("line.separator"));
        
        for(Component comp : layeredPane.getComponents()) {
            builder.append(comp + System.getProperty("line.separator"));
        }
        builder.append(new String(new char[header.length()]).replace("\0", "="));
        
        return builder.toString();
    }
}