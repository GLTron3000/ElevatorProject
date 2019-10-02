package com.elevatorproject;

import java.util.logging.Level;
import java.util.logging.Logger;


public class ElevatorMotor extends Thread{
    public State state;
    public int position;
    private final int maxPosition;
    
    public enum State 
    { 
        UP, DOWN, NEXT, STOP; 
    } 
    
    
    public ElevatorMotor(int floors) {
        this.maxPosition = floors * 10;
    }
    
    
    
    public void goUp(){
        state = State.UP;
    }
    
    public void goDown(){
        state = State.DOWN;
    }
    
    public void stopNextFloor(){
        state = State.NEXT;
    }
    
    public void emergencyStop(){
        state = State.STOP;
    }

    @Override
    public void start(){
        while(true){
            try {
                sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(ElevatorMotor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            switch(state){
                case UP : if(position != maxPosition) position++; else state = State.STOP; break;
                case DOWN : if(position != 0) position--; else state = State.STOP; break;
                case NEXT : break;
                case STOP : break;
            }
            
        }
    }
}
