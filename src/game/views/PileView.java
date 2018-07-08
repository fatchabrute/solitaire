/**
 * Daniel Ricci <thedanny09@gmail.com>
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;

import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import game.models.CardModel;

public final class PileView extends PanelView {

    private final JLayeredPane _layeredPane = new JLayeredPane();

    private PileView() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(71, 96));
        setOpaque(false);
        add(_layeredPane);
    }
    
    public PileView(List<CardModel> cards) {
        this();
        for(int i = 0; i < cards.size(); ++i) {
            
            // Create the card view
            CardView view = ViewFactory.getFactory(ViewFactory.class).add(new CardView(cards.get(i)));
        
            // Add the view to the layered pane
            _layeredPane.add(view, i);
            _layeredPane.setLayer(view, i);
            
            // Set the bounds of the view within the layered pane
            view.setBounds(new Rectangle(0, 12 * i, view.getPreferredSize().width, view.getPreferredSize().height));
        } 
    }
    
    public PileView(CardView[] cardViews) {
        this();
        for(int i = 0; i < cardViews.length; ++i) {

            CardView view = cardViews[i];
            
            // Add the view to the layered pane
            _layeredPane.add(view, i);
            _layeredPane.setLayer(view, i);
            
            // Set the bounds of the view within the layered pane
            view.setBounds(new Rectangle(0, 12 * i, view.getPreferredSize().width, view.getPreferredSize().height));
        }
    }

    @Override public void render() {
        super.render();
        for(Component component : _layeredPane.getComponents()) {
            CardView view = (CardView) component;
            view.render();
        }
    }
}