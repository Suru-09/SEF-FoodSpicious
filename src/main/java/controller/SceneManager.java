package controller;

import java.io.IOException;

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
        try {
            this.loadedPanes = new Pane[States.values().length];
            this.scenes = new Scene[States.values().length];
            this.loaders = new FXMLLoader[States.values().length];

            int i = 0 ;
            for(States state: States.values()) {
                loaders[i] = new FXMLLoader(getClass().getClassLoader().getResource(state.url));
                loadedPanes[i] = loaders[i].load();
                scenes[i] = new Scene(loadedPanes[i]);
                ++i;
            }


            rootStage.setScene(scenes[0]);
            rootStage.setResizable(false);
            rootStage.show();
        } catch(IOException e) {
            e.printStackTrace();
        }
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
        rootStage.setScene(scenes[state.ordinal()]);
    }

    public Scene getScene(States state) {
        return scenes[state.ordinal()];
    }

    public FXMLLoader getFXML(States state) {
        return loaders[state.ordinal()];
    }
}