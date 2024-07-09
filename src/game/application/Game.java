package game.application;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.UIManager;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.MenuBuilder;
import framework.core.system.Application;
import framework.core.system.EngineProperties;
import framework.core.system.EngineProperties.Property;
import framework.utils.globalisation.Localization;

import game.config.OptionsPreferences;
import game.menu.AboutMenuItem;
import game.menu.DeckMenuItem;
import game.menu.ExitMenuItem;
import game.menu.GitHubMenuItem;
import game.menu.NewGameMenuItem;
import game.menu.OnTopMenuItem;
import game.menu.OptionsMenuItem;
import game.menu.UndoMenuItem;
import game.views.FoundationPileView;
import game.views.GameView;
import game.views.TableauPileView;
import game.views.TalonPileView;
import game.views.helpers.DeckAnimationHelper;
import game.views.helpers.WinAnimationHelper;

import resources.LocalizationStrings;

/**
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 */
public final class Game extends Application {

    /**
     * Constructs a new instance of this class type
     * 
     * @param isDebug The debug mode flag
     */
    private Game(boolean isDebug) {
        super(isDebug);
        setMinimumSize(new Dimension(620, 436));
        
        OptionsPreferences options = new OptionsPreferences();
        options.load();
        setLocationRelativeTo(null);
        setAlwaysOnTop(options.alwaysOnTop);
        setIconImage(Localization.instance().getLocalizedData(LocalizationStrings.GAME_ICON));
        if(isDebug) {
            addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent event) {
                    ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
                    if(event.getKeyCode() == KeyEvent.VK_F1) {
                        event.consume();
                        OptionsPreferences options = new OptionsPreferences();
                        options.load();
                        System.out.println(options);
                        
                        System.out.println(viewFactory.get(TalonPileView.class).toString());
                        
                        List<TableauPileView> pileViews =  viewFactory.getAll(TableauPileView.class);
                        for(int i = pileViews.size() - 1; i >= 0; --i) {
                            System.out.println(pileViews.get(i));
                        }
                        List<FoundationPileView> foundationViews =  viewFactory.getAll(FoundationPileView.class);
                        for(int i = foundationViews.size() - 1; i >= 0; --i) {
                            System.out.println(foundationViews.get(i));
                        }
                    }
                    else if(event.getKeyCode() == KeyEvent.VK_F3) {
                        event.consume();
                        System.out.println(viewFactory.get(TalonPileView.class).toString());
                    }
                }
            });
        }
        
        addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent event) {
                boolean _locked = false;
                
                //Alt + Shift + 2
                if(event.getKeyCode() == KeyEvent.VK_2 && event.getModifiersEx() == (KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK)) {
                    if(!_locked) { 
                        _locked = true;
                        GameView.forceGameWin();
                        _locked = false;
                    }
                }
            }
        });
    }
    
    /**
     * Main entry-point method
     * 
     * @param args The arguments associated to the application entry point
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                boolean debugMode = false;
                for(String arg : args) {
                    switch(arg.trim()) {
                    case "debug": {
                        debugMode = true;
                        break;
                    }
                    }
                }
                
                if(debugMode) {
                    EngineProperties.instance().setProperty(Property.LOG_DIRECTORY,  System.getProperty("user.dir") + File.separator);
                    EngineProperties.instance().setProperty(Property.ENGINE_OUTPUT, Boolean.toString(true));
                    EngineProperties.instance().setProperty(Property.DISPLAY_EXCEPTIONS, Boolean.toString(true));    
                }
                
                EngineProperties.instance().setProperty(Property.DATA_PATH_XML, "/generated/tilemap.xml");
                EngineProperties.instance().setProperty(Property.DATA_PATH_SHEET, "/generated/tilemap.png");
                EngineProperties.instance().setProperty(Property.LOCALIZATION_PATH_CVS, "/resources/Localization.csv");
                EngineProperties.instance().setProperty(Property.SUPPRESS_SIGNAL_REGISTRATION_OUTPUT, Boolean.toString(false));
                EngineProperties.instance().setProperty(Property.DISABLE_TRANSLATIONS_PLACEHOLDER, Boolean.toString(!debugMode));
                
                Game game = new Game(debugMode);
                game.setVisible(true);   
            }
        });
    }
    
    @Override public void onRestart() {
        super.onRestart();
        
        if(AbstractFactory.isRunning()) {
            
            // Clear the factory of it's contents
            AbstractFactory.clearFactories();
            
            // Remove everything from the application UI
            Application.instance.getContentPane().removeAll();
            
            // Indicate that the application is no longer in a restart state
            isRestarting = false;
        }

        WinAnimationHelper.clear();
        DeckAnimationHelper.getInstance().clear();
        
        // Spawn a new game view and render its contents
        GameView gameView = AbstractFactory.getFactory(ViewFactory.class).add(new GameView(), true);
        instance.setContentPane(gameView);
        gameView.render();
    }


    
    @Override public void windowOpened(WindowEvent windowEvent) {
    	System.out.println("Window Opened");    	
    	super.windowOpened(windowEvent);
    	
    	   // Set the title
        setTitle(Localization.instance().getLocalizedString(LocalizationStrings.TITLE));
        
        // Always show mnemonics in the menu system
        UIManager.put("Button.showMnemonics", Boolean.TRUE);
        
        // Game Menu
        MenuBuilder.start(getJMenuBar())
        .addMenu(Localization.instance().getLocalizedString(LocalizationStrings.GAME), KeyEvent.VK_G)
        .addMenuItem(NewGameMenuItem.class)
        .addSeparator()
        .addMenuItem(UndoMenuItem.class)
        .addMenuItem(DeckMenuItem.class)
        .addMenuItem(OptionsMenuItem.class)
        .addSeparator()
        .addMenuItem(ExitMenuItem.class);
                
        // Extras
        MenuBuilder.start(getJMenuBar())
        .addMenu("Extras")
        .addMenuItem(OnTopMenuItem.class);

        // Help Menu
        MenuBuilder.start(getJMenuBar())
        .addMenu(Localization.instance().getLocalizedString(LocalizationStrings.HELP), KeyEvent.VK_H)
        .addMenuItem(GitHubMenuItem.class)
        .addSeparator()
        .addMenuItem(AboutMenuItem.class);

        // Perform a new game programmatically
        MenuBuilder.search(getJMenuBar(), NewGameMenuItem.class).getComponent(AbstractButton.class).doClick();
    }
}