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

package game.gameplay;

import game.views.FoundationPileView;
import game.views.TableauPileView;
import game.views.TalonPileView;

/**
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public enum MovementType {
    TALON,
    FOUNDATION,
    TABLEAU, 
    NONE;
    
    public static MovementType fromClass(Object clazz) {
        if(clazz instanceof TalonPileView) {
            return MovementType.TALON;
        }
        else if(clazz instanceof FoundationPileView) {
            return MovementType.FOUNDATION;
        }
        else if(clazz instanceof TableauPileView) {
            return MovementType.TABLEAU;
        }
        
        return null;
    }
}