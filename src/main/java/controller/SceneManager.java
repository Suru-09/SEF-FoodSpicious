package controller;

import java.io.IOException;
import java.util.HashMap;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SceneManager {
    private static SceneManager instance = null;
    private static Stage rootStage = null;

    private Pane[] loadedPanes;
    private Scene[] scenes;
    private FXMLLoader[] loaders;

//    private HashMap<String, Scene> elems = new HashMap<>();

    public enum States {
        LOGIN("fxml/login.fxml"),
        REGISTER("fxml/register.fxml"),
        ADMIN_LOGGED("fxml/adminLogged.fxml"),
        CUSTOMER_LOGGED("fxml/customerLogged.fxml");

        public final String url;

        States(String url) {
            this.url = url;
        }
    }

    private SceneManager() {
        this.loadedPanes = new Pane[States.values().length];
        this.scenes = new Scene[States.values().length];
        this.loaders = new FXMLLoader[States.values().length];

        try {
            int i = 0 ;
            for(States state: States.values()) {
                loaders[i] = new FXMLLoader(getClass().getClassLoader().getResource(state.url));
                loadedPanes[i] = loaders[i].load();
                scenes[i] = new Scene(loadedPanes[i]);
                ++i;
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }


        rootStage.setScene(scenes[0]);
        rootStage.setResizable(false);
        rootStage.show();
    }

    public static SceneManager getInstance() {
        if(instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public static void setUp(Stage stage) {
        SceneManager.rootStage = stage;
    }

    public void switchScene(States state) {

        //putIfAbsent(state);
        rootStage.setScene(scenes[state.ordinal()]);
    }

    public Scene getScene(States state) {
        return scenes[state.ordinal()];
    }

    public FXMLLoader getFXML(States state) {

        //putIfAbsent(state);
        return loaders[state.ordinal()];
    }

//    private void putIfAbsent(States state) {
//        elems.computeIfAbsent(state.url, e -> {
//            int i = state.ordinal();
//            loaders[i] = new FXMLLoader(getClass().getClassLoader().getResource(state.url));
//            try {
//                loadedPanes[i] = loaders[i].load();
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//            scenes[i] = new Scene(loadedPanes[i]);
//            return scenes[i];
//        });
//    }
}