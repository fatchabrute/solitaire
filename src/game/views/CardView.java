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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;

import framework.communication.internal.signal.arguments.AbstractEventArgs;
import framework.communication.internal.signal.arguments.ModelEventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ControllerFactory;
import framework.core.mvc.view.PanelView;
import framework.core.mvc.view.layout.DraggableListener;
import game.application.Application;
import game.controllers.CardController;
import game.models.CardModel;

public final class CardView extends PanelView {

    public static final int CARD_WIDTH = 71;
    
    public static final int CARD_HEIGHT = 100;
    
    /**
     * The controller associated to this view
     */
    private final CardController _controller = AbstractFactory.getFactory(ControllerFactory.class).add(new CardController());
    
    /**
     * Dragging listener
     */
    private final DraggableListener _draggableListener = new DraggableListener(this);
    
    /**
     * This view holds the view of the current card being rendered
     */
    private final PanelView _cardPanelView = new PanelView();
    
    /**
     * This holds the list of cards being rendered that are children of the card panel view 
     */
    private final JLayeredPane _childrenCardsLayeredPane = new JLayeredPane();
    
    /**
     * Constructs a new instance of this class type
     */
    private CardView() {
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        // Card Panel View
        _cardPanelView.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        add(_cardPanelView);
        _cardPanelView.setOpaque(false);
        
        add(_childrenCardsLayeredPane);   
        _childrenCardsLayeredPane.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        _childrenCardsLayeredPane.setOpaque(false);

    }
    
    /**
     * Creates a new instance of this class type
     */
    public CardView(CardModel card) {
        this();
        setPreferredSize(new Dimension(CARD_WIDTH, 1000));
        _controller.setCard(card);
        card.addListeners(this);
    }
    
    @Override public void onViewInitialized() {
        addMouseListener(new MouseAdapter() {
            
            /**
             * The parent associated to this card view
             */
            private JLayeredPane _parentSource;
            
            @Override public void mousePressed(MouseEvent event) {
                
                // Get the parent of this card view, used as a reference to go back to whatever we were coming from
                _parentSource = (JLayeredPane) CardView.this.getParent();
                
                // Get the list of components that the parent owns
                Component[] components =_parentSource.getComponents();
                
                // Find the card that is being dragged and take all the siblings below it and populate them into the
                // layered pane composed by the CardView.this reference
                mainLabel : for(int i = components.length - 1; i >= 0; --i) {
                    
                    if(components[i].equals(CardView.this)) {

                        // Remove the card view reference from it's initial parent
                        _parentSource.remove(CardView.this);
                        Application.instance().add(CardView.this, 0);
                        
                        // Get the siblings of cards within the components list (excluding CardView.this)
                        List<CardView> cardViews = Arrays.asList(Arrays.copyOfRange(components, 0, i, CardView[].class));
                        
                        // Reverse the list because layered panes associate objects closer to layer 0 as being closer to the screen.
                        Collections.reverse(cardViews);
                        
                        // For each sibling add it into the associated layere pane and position it correctly within
                        // the pane, accounting for the fact that CardView.this is the temporary 'root'
                        for(int j = 0; j < cardViews.size(); ++j) {
                            _childrenCardsLayeredPane.add(cardViews.get(j), j);
                            _childrenCardsLayeredPane.setLayer(cardViews.get(j), j);
                            cardViews.get(j).setLocation(new Point(0, PileView.PILE_CARD_OFFSET * (j + 1)));
                            _parentSource.remove(cardViews.get(j));
                        }

                        // Position the card at the same place where the drag was attempted from, because when you
                        // add to the application it will position the component at the origin which is not desired
                        Point initialLocation = CardView.this.getLocation();
                        CardView.this.setBounds(new Rectangle(_parentSource.getParent().getLocation().x + initialLocation.x, _parentSource.getParent().getLocation().y + initialLocation.y, CardView.this.getPreferredSize().width, CardView.this.getPreferredSize().height));

                        
                        // Repaint the application to show the changes
                        Application.instance().repaint();
                        
                        break mainLabel;
                    }
                }
            }
            
            @Override public void mouseReleased(MouseEvent event) {
                
                // Get the list of components associated to the CardView.this reference. This list represents all the children associated
                // to the said CardView.this reference.
                List<Component> components = Arrays.asList(CardView.this._childrenCardsLayeredPane.getComponents());
                
                // Reverse the list because layered panes associate objects closer to layer 0 as being closer to the screen.
                Collections.reverse(components);

                // Get the initial count of the number of components in the layered pane.  It is assumed that they hold only
                // CardView or the calculations wont work properly.
                // This number represents the number of cards that exist within the list after the drag operation had occured, so if
                // you have a pile with 5 cards and you drag three out, then you would be left with 2 cards in the parent, CardView.this would
                // represent the card actually being dragged, and the layered pane associated to CardView.this would attached itself.
                int initialSize = _parentSource.getComponents().length;
                
                // Add this card view to the pane and update the layer within the component that it has been added to
                _parentSource.add(CardView.this, initialSize);
                _parentSource.setLayer(CardView.this, initialSize);
                
                // Set the bounds of this card so that it appears at the right position offset
                CardView.this.setBounds(new Rectangle(0, PileView.PILE_CARD_OFFSET * initialSize, CardView.this.getPreferredSize().width, CardView.this.getPreferredSize().height));
                
                // Remove this card from the application which was used as a temporary measure to support the dragging
                Application.instance().remove(CardView.this);
                
                // Increment the initial size to include the fact that CardView.this was added back to the parent
                ++initialSize;
                
                // Take the remainder of the components held by this card and put them back into the parents pane
                for(int i = 0; i < components.size(); ++i) {
                    
                    // Add this card view to the pane and update the layer within the component that it has been added to
                    _parentSource.add(components.get(i), i + initialSize);
                    _parentSource.setLayer(components.get(i), i + initialSize);
                    
                    // Set the bounds of this card so that it appears at the right position offset                    
                    components.get(i).setBounds(new Rectangle(0, PileView.PILE_CARD_OFFSET * (i + initialSize), components.get(i).getPreferredSize().width, components.get(i).getPreferredSize().height));
                }
                
                // Clear the card views that were added within this cards' layered pane
                CardView.this._childrenCardsLayeredPane.removeAll();
                
                // Repaint the components accordingly
                Application.instance().repaint();
                _parentSource.repaint();
                
                // Reset all reference
                _parentSource = null;
            }
        });
    }
    
    @Override public void render() {
        super.render();
        _cardPanelView.render();
       _controller.refresh();
    }
    
    @Override public void update(AbstractEventArgs event) {
        
        super.update(event);
        if(event instanceof ModelEventArgs) {
            ModelEventArgs args = (ModelEventArgs) event;
            if(args.getSource() instanceof CardModel) {
                _cardPanelView.addRenderableContent((CardModel)args.getSource());
            }
        }
        
        repaint();
    }
    
    
}