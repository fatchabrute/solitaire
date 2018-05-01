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

package application;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;

import engine.core.navigation.MenuBuilder;
import engine.core.system.AbstractApplication;
import engine.core.system.EngineProperties;
import engine.core.system.EngineProperties.Property;
import engine.utils.globalisation.Localization;
import menu.AboutMenuItem;
import menu.DebugInsertCardMenuItem;
import menu.DebugNewGameMenuItem;
import menu.DeckMenuItem;
import menu.ExitMenuItem;
import menu.NewGameMenuItem;
import menu.OptionsMenuItem;
import menu.UndoMenuItem;
import resources.LocalizedStrings;

public class Application extends AbstractApplication {

    public Application() {
        // Set the default size of the application to be the size of the screen
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();  
        setSize(env.getMaximumWindowBounds().width, env.getMaximumWindowBounds().height);
    }

    @Override public void destructor() {
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {

                // Get the debug mode state based on the arguments passed into the application
                boolean debugMode = false;
                for(String arg : args) {
                    if(arg.trim().equalsIgnoreCase("-debug")) {
                        debugMode = true;
                        break;
                    }
                }

                // Initialize the application
                Application.initialize(Application.class, debugMode);
            }
        });
    }

    private void populateMenu() {
        
        // Game Menu
        MenuBuilder.start(getJMenuBar())
        .addMenu(Localization.instance().getLocalizedString("Game"))
        .addMenuItem(NewGameMenuItem.class)
        .addSeparator()
        .addMenuItem(UndoMenuItem.class)
        .addMenuItem(DeckMenuItem.class)
        .addMenuItem(OptionsMenuItem.class)
        .addSeparator()
        .addMenuItem(ExitMenuItem.class);
        
        // Debug Menu
        if(isDebug()) {
            MenuBuilder.start(getJMenuBar())
            .addMenu(Localization.instance().getLocalizedString("Debug"))
            .addMenuItem(DebugNewGameMenuItem.class)
            .addSeparator()
            .addMenuItem(DebugInsertCardMenuItem.class);
        }
        
        // Help Menu
        MenuBuilder.start(getJMenuBar())
        .addMenu(Localization.instance().getLocalizedString("Help"))
        .addMenuItem(AboutMenuItem.class);
    }

    @Override protected void onBeforeEngineDataInitialized() {
        //EngineProperties.instance().setProperty(Property.DATA_PATH_XML, "/generated/tilemap.xml");
        //EngineProperties.instance().setProperty(Property.DATA_PATH_SHEET, "/generated/tilemap.png");
        //EngineProperties.instance().setProperty(Property.LOG_DIRECTORY,  System.getProperty("user.home") + File.separator + "desktop" + File.separator);
        EngineProperties.instance().setProperty(Property.ENGINE_OUTPUT, "true");
        EngineProperties.instance().setProperty(Property.LOCALIZATION_PATH_CVS, "resources/localization_solitaire.csv");
    }

    @Override protected void onWindowInitialized() {
        super.onWindowInitialized();

        // Set the title
        setTitle(Localization.instance().getLocalizedString(LocalizedStrings.Title));

        // Populate the menu system
        populateMenu();
    }

    @Override public void clear() {
    }
}