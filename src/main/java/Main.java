import controller.SceneManager;
import domain.*;
import javafx.application.Application;
import javafx.stage.Stage;
import repository.OrderRepository;
import repository.ProductRepository;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;


public class Main extends Application {

    private Stage stage;
    private SceneManager sceneManager;

    @Override
    public void start(Stage stage) throws Exception {

        String url = "jdbc:postgresql://tai.db.elephantsql.com:5432/ktlzkben";
        String username = "ktlzkben";
        String password = "U85A51ME0gW4yVaPXdY--oJgSCm313Rn";




        //TODO: delete all testing things after finishing with them

        /* TESTING */
        SceneManager.setUp(stage);
        SceneManager.getInstance().switchScene(SceneManager.States.LOGIN);


    }

    public static void main(String[] args){
        launch(args);
    }




}