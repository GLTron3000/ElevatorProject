package com.elevatorproject;

import static java.lang.Thread.sleep;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ElevatorMotor extends TimerTask{
    public State state = State.STOP;
    public int position;
    private final int maxPosition;
    private boolean wayUp;
    public enum State 
    { 
        UP, DOWN, NEXT, STOP; 
    } 
       
    public ElevatorMotor(int floors) {
        this.maxPosition = (floors * 10) - 10;
    }
      
    public void goUp(){
        state = State.UP;
        wayUp = true;
    }
    
    public void goDown(){
        state = State.DOWN;
        wayUp = false;
    }
    
    public void stopNextFloor(){
        state = State.NEXT;
    }
    
    public void emergencyStop(){
        state = State.STOP;
    }

    @Override
    public void run(){
        while(true){
            System.out.println("[MOTOR] s:"+state);
            
            try {
                sleep(150);
            } catch (InterruptedException ex) {
                Logger.getLogger(ElevatorMotor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            switch(state){
                case UP : if(position != maxPosition) position++; else state = State.STOP; break;
                case DOWN : if(position != 0) position--; else state = State.STOP; break;
                case NEXT : if(position%10 == 0) state = State.STOP; else if(wayUp) position++; else position--; break;
                case STOP : break;
            }
            
        }
    }
}
