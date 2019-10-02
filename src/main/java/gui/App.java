package gui;

import controlCommand.CommandSystem;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application{
    public int floors = 4;
    
    private BorderPane borderPane;
    CommandSystem commandSystem;
    
    private Button buildInternalButton(int floor){
        Button button = new Button(floor+"");
        
        button.setOnAction((ActionEvent event) -> {
            commandSystem.internalButton(floor);
        });
        return button;
    }
    
    private HBox buildExternalButton(int floor){
        HBox hbox = new HBox();
        Button buttonUp = new Button("/\\");
        Button buttonDown = new Button("\\/");
        
        buttonUp.setOnAction((ActionEvent event) -> {
            commandSystem.externalButton(floor, false);
        });
        
        buttonDown.setOnAction((ActionEvent event) -> {
            commandSystem.externalButton(floor, true);
        });
        
        hbox.getChildren().addAll(buttonUp, buttonDown);
        
        return hbox;
    }
    
    private void initBorderPane(){
        borderPane = new BorderPane();
        
        VBox vboxInternalButton = new VBox();
        
        VBox vboxExternalButton = new VBox();
        
        for(int i=0; i < floors; i++){
            vboxInternalButton.getChildren().add(buildInternalButton(i));
            vboxExternalButton.getChildren().add(buildExternalButton(i));
        }
        
        borderPane.setLeft(vboxInternalButton);
        borderPane.setRight(vboxExternalButton);
        
        
    }
    
    @Override
    public void start(Stage stage) {
        initBorderPane();
        
        var scene = new Scene(borderPane, 640, 480);
        
        stage.setTitle("Ultra Elevator");
        stage.setScene(scene);
        stage.show();
    }
    
    

}
