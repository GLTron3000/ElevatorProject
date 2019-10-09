package controlCommand;

import com.elevatorproject.ElevatorMotor;
import com.elevatorproject.ElevatorMotor.State;
import java.util.LinkedList;
import java.util.Queue;

public class Basic implements CommandSystem{
    private ElevatorMotor motor;
    private Queue<Integer> queue = new LinkedList<>();
    private int floor = 0;

    public Basic(ElevatorMotor motor) {
        this.motor = motor;
    }
    
    @Override
    public void internalButton(int floor) {
        addCall(floor);
    }

    @Override
    public void externalButton(int floor, boolean downward) {
        addCall(floor);
    }

    @Override
    public void emergencyButton() {
        motor.emergencyStop();
        queue.clear();
    }

    @Override
    public void sensor() {
        System.out.println("[BASIC A] sensor f:"+floor+" queue:"+queue+" s:"+motor.state);
        if(motor.state == State.UP || (motor.state == State.NEXT && motor.wayUp)){
            floor++;
            if(queue.isEmpty()) return;
            if(floor == queue.peek()-1){
                System.out.println("[BASIC] stop next UP");
                motor.stopNextFloor();
            } 
        }
        else if (motor.state == State.DOWN || (motor.state == State.NEXT && !motor.wayUp)){
            floor--;
            if(queue.isEmpty()) return;
            if(floor == queue.peek()+1){
                System.out.println("[BASIC] stop next DOWN");
                motor.stopNextFloor();
            }
        }
        
        if(motor.state == State.STOP){
            if(queue.isEmpty()) return;
            if(floor == queue.peek()) queue.poll();
            goToNextInQueue();
        }
        
        System.out.println("[BASIC B] sensor f:"+floor+" queue:"+queue+" s:"+motor.state);
    }
    
    private void goToNextInQueue(){
        if(queue.isEmpty()) return;
        
        if(floor == queue.peek() -1){
            motor.goUp();
            motor.stopNextFloor();
        }
        else if(floor == queue.peek() +1){
            motor.goDown();
            motor.stopNextFloor();
        }
        else if(queue.peek() < floor) motor.goDown();
        else motor.goUp();
    }
    
    private void addCall(int floor){
        if(floor == this.floor && motor.state == State.STOP) return;
        if(queue.isEmpty()){
            if(this.floor == floor -1){
                motor.goUp();
                motor.stopNextFloor();
            }
            else if(this.floor == floor +1){
                motor.goDown();
                motor.stopNextFloor();
            }
            else queue.add(floor);
        }
        else queue.add(floor);
        if(motor.state == State.STOP) goToNextInQueue();
    }
    
}
