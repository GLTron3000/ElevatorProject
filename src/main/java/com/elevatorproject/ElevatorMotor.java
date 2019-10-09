package com.elevatorproject;

public class ElevatorMotor{
    public State state = State.STOP;
    public int position;
    private final int maxPosition;
    public boolean wayUp;
    public enum State 
    { 
        UP, DOWN, NEXT, STOP; 
    }
    
    private boolean nextLag=true;
       
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

    public void step(){
        System.out.println("[MOTOR] s:"+state+" p:"+position+" l:"+nextLag);

        switch(state){
            case UP : if(position != maxPosition) position++; else state = State.STOP; break;
            case DOWN : if(position != 0) position--; else state = State.STOP; break;
            case NEXT : next(); break;
            //case NEXT : if(position%10 == 0) state = State.STOP; else if(wayUp) position++; else position--; break;
            case STOP : break;
        }
    }
    
    private void next(){
        if(nextLag)nextLag = false;
        else if(position%10 == 0){
            state = State.STOP;
            nextLag = true;
            return;
        }
        if(wayUp) position++; 
        else position--;
    }
}
