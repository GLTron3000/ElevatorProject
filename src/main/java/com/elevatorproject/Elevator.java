package com.elevatorproject;

import controlCommand.Basic;
import controlCommand.CommandSystem;
import static java.lang.Thread.sleep;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Elevator extends TimerTask{
    public ElevatorMotor motor;
    public CommandSystem commandSystem;
    public boolean run = true;
    
    public Elevator(int floors){
        motor = new ElevatorMotor(floors);
        commandSystem = new Basic(motor);
    }
    
    @Override
    public void run() {
        while(run){
            motor.step();
            sensor();          
            
            try {
                sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Elevator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void sensor(){
        if(motor.position%10 == 0) commandSystem.sensor();
    }
    
}
