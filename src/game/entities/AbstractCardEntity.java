package game.entities;

import java.awt.Image;
import java.util.UUID;
import java.util.logging.Level;

import framework.core.entity.AbstractDataEntity;
import framework.utils.logging.Tracelog;

import generated.DataLookup;
import generated.DataLookup.LAYER;


public abstract class AbstractCardEntity extends AbstractDataEntity {

    /**
     * The backside entity associated to this card
     */
    protected final BacksideCardEntity backsideCardEntity = new BacksideCardEntity();
    
    /**
     * The layer associated to this entity
     */
    protected final LAYER layer;
    
    /**
     * The UUID associated to the entity data
     */
    protected final UUID identifier;
    
    /**
     * The ordinal associated to the entity data
     */
    protected final int ordinal;
    
    /**
     * Constructs a new instance of this class type
     */
    protected AbstractCardEntity() {
        layer = null;
        identifier = null;
        ordinal = -1;
    }
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param layer The layer to set this card entity to
     * @param ordinal The oridinal position of the card
     * @param the uuid associated to the card
     */
    protected AbstractCardEntity(LAYER layer, int ordinal, UUID identifier) {
        this.layer = layer;
        this.identifier = identifier;
        this.ordinal = ordinal;
        
        setActiveData(identifier);
    }
    
    /**
     * Indicates if this card is before the one specified. A card that is before is one that has one rank higher than the one specified
     * 
     * Note: This method is suite agnostic
     * 
     * @param card The card to check rank against
     * 
     * @return TRUE if the card is ranked before the one specified, false otherwise
     */
    public final boolean isCardRankedBefore(AbstractCardEntity card) {
        return ordinal + 1 == card.ordinal;
    }
    
    /**
     * Indicates if this card's suite is opposite to the card specified. An opposite suite is one that is of different color
     *  
     * @return TRUE if this card's suite is opposite to the one specified, FALSE otherwise
     */
    public abstract boolean isOppositeSuite(AbstractCardEntity card);

    /**
     * Indicates if this card's suite is the same as the specified card
     * 
     * @param card The card check suite against
     * 
     * @return TRUE if this card and the card specified are of the SAME suit, FALSE otherwise
     */
    public final boolean isSameSuite(AbstractCardEntity card) {
        return card.layer.equals(layer);
    }
    
    /**
     * Sets the visibility of the cards' backside
     * 
     * @param isVisible TRUE if the backside of the card is visible, FALSE otherwise
     */
    public final void setBacksideVisible(boolean isVisible) {
        backsideCardEntity.setIsBacksideShowing(isVisible);
    }
    
    /**
     * Gets the visibility of the cards' backside
     * @return
     */
    public boolean getBacksideVisible() {
        return backsideCardEntity.getIsBacksideShowing();
    }
    
    @Override public Image getRenderableContent() {
        if(getBacksideVisible()) {
            return backsideCardEntity.getRenderableContent();
        }
        return super.getRenderableContent();
    }
    
    @Override public String toString() {
        return layer.toString() + ": " + (ordinal + 1);
    }
    
}