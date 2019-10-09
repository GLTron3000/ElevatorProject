package controlCommand;

import com.elevatorproject.ElevatorMotor;
import com.elevatorproject.ElevatorMotor.State;
import java.util.PriorityQueue;
import java.util.Queue;

public class Basic implements CommandSystem{
    private ElevatorMotor motor;
    private Queue<Integer> queue = new PriorityQueue<>();
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
        System.out.println("[BASIC] sensor f:"+floor+" queue:"+queue+" s:"+motor.state);
        if(motor.state == State.UP || (motor.state == State.NEXT && motor.wayUp)){
            floor++;
            if(queue.isEmpty()) return;
            if(floor == queue.peek()-1){
                System.out.println("[BASIC] stop next UP");
                queue.poll();
                motor.stopNextFloor();
            } 
        }
        else if (motor.state == State.DOWN || (motor.state == State.NEXT && !motor.wayUp)){
            floor--;
            if(queue.isEmpty()) return;
            if(floor == queue.peek()+1){
                System.out.println("[BASIC] stop next DOWN");
                queue.poll();
                motor.stopNextFloor();
            }
        }
        
        if(motor.state == State.STOP) goToNextInQueue();
        
        System.out.println("[BASIC 2] sensor f:"+floor);
    }
    
    private void goToNextInQueue(){
        if(queue.isEmpty()) return;
        if(queue.peek() < floor) motor.goDown();
        else motor.goUp();
    }
    
    private void addCall(int floor){
        if(floor == this.floor) return;
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
