package com.elevatorproject;

import controlCommand.Basic;

import controlCommand.Smart;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application{
    
    public int floors = 10;
    public Elevator elevator;
    
    private BorderPane borderPane;
    private Canvas canvas;
    private final Timer timer = new Timer();
    
    private Timer timerElevator = new Timer();
    
    enum SystemType{
        Basic, Smart
    }
    
    private void changeSystem(SystemType type){
        if(elevator == null) return;
        elevator.run = false;
        
        elevator = new Elevator(floors);
        
        switch(type){
            case Basic: elevator.commandSystem = new Basic(elevator.motor); break;
            case Smart: elevator.commandSystem = new Smart(elevator.motor); break;
        }
        
        timerElevator.scheduleAtFixedRate(elevator, 1000, 500);
    }
    
    private void drawElevator(double floor) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        double position = (floors - floor) * 60 - 10;
        
        gc.clearRect(0, 0, 300, floors * 70 + 20);
        gc.setFill(Color.DIMGREY);
        gc.fillRect(75, 0, 125, floors * 70 + 20);
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(85, position, 105, 40);
        
        drawFloorLine();
    }
    
    private void drawFloorLine(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        gc.setFill(Color.BLACK);
        for(int i = 1; i <= floors; i++){
            gc.fillRect(190, i*60+30, 40, 5);
            gc.strokeText("N°"+(floors-i), 220, i*60+30);
        }
    }
    
    private Button buildInternalButton(int floor){
        Button button = new Button(floor+"");
        
        button.setStyle("-fx-font: 15 arial; -fx-base: #0c639e;");
        
        button.setOnAction((ActionEvent event) -> {
            elevator.commandSystem.internalButton(floor);
        });
        return button;
    }
    
    private ToggleButton buildEmergencyButton(){
        ToggleButton button = new ToggleButton("STOP");
        
        button.setStyle("-fx-font: 30 arial; -fx-base: #ee2211;");
        
        button.setOnAction((ActionEvent event) -> {
            elevator.commandSystem.emergencyButton();
        });
        return button;
    }
    
    private HBox buildExternalButton(int floor){
        HBox hbox = new HBox();
        Button buttonUp = new Button("/\\");
        Button buttonDown = new Button("\\/");
        Label label = new Label("Etage "+floor);
        
        buttonUp.setOnAction((ActionEvent event) -> {
            elevator.commandSystem.externalButton(floor, false);
        });
        
        buttonDown.setOnAction((ActionEvent event) -> {
            elevator.commandSystem.externalButton(floor, true);
        });
        
        label.setPadding(new Insets(5,10,5,10));
        
        hbox.getChildren().addAll(label, buttonUp, buttonDown);
        
        return hbox;
    }
    
    private Canvas buildElevator(){
        canvas = new Canvas(300, floors * 70 + 20);

        drawElevator(0);
 
        return canvas;
    }
    
    private ChoiceBox buildCommandSystemChooser(){
        ChoiceBox cbox = new ChoiceBox();
        
        cbox.setItems(FXCollections.observableArrayList(SystemType.Basic, SystemType.Smart));
        
        cbox.getSelectionModel().selectedIndexProperty().addListener(
            (ObservableValue<? extends Number> ov, Number oldValue, Number newValue)-> {
                switch(newValue.intValue()){
                    case 0: changeSystem(SystemType.Basic); break;
                    case 1: changeSystem(SystemType.Smart); break;
                }
                   
            }
        );
        
        cbox.setValue(SystemType.Basic);
        
        return cbox;
    }
    
    private void initBorderPane(){
        borderPane = new BorderPane();
        
        VBox vboxInternalButton = new VBox();
        
        VBox vboxExternalButton = new VBox();
        
        VBox vboxElevator = new VBox();
        
        for(int i=floors-1; i >= 0; i--){
            vboxInternalButton.getChildren().add(buildInternalButton(i));
            vboxExternalButton.getChildren().add(buildExternalButton(i));
        }
        
        vboxInternalButton.getChildren().add(buildEmergencyButton());
        vboxInternalButton.getChildren().add(buildCommandSystemChooser());
        
        vboxElevator.getChildren().add(buildElevator());
        
        vboxInternalButton.setAlignment(Pos.CENTER);
        vboxInternalButton.setPadding(new Insets(10));
        vboxInternalButton.setSpacing(10);
        
        vboxExternalButton.setAlignment(Pos.CENTER);
        vboxExternalButton.setPadding(new Insets(10));
        vboxExternalButton.setSpacing(50);
        
        vboxElevator.setAlignment(Pos.CENTER);
        vboxElevator.setPadding(new Insets(10));
        vboxElevator.setSpacing(50);
        
        borderPane.setLeft(vboxInternalButton);
        borderPane.setRight(vboxExternalButton);
        borderPane.setCenter(vboxElevator);
    }
    
    @Override
    public void start(Stage stage) {
        initBorderPane();
        
        var scene = new Scene(borderPane, 640, floors * 70 + 100);
        
        
        
        stage.setTitle("Ultra Elevator");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            elevator.run = false;
            Platform.exit();
            System.exit(0);
        });
        stage.show();
        
        elevator = new Elevator(floors);
        
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                double position = elevator.motor.position;

                drawElevator(position/10);
            }
        }, 1000, 150);
        
        
        timerElevator.scheduleAtFixedRate(elevator, 1000, 500);
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
