package controlCommand;

import com.elevatorproject.ElevatorMotor;
import java.util.LinkedList;

public class Smart implements CommandSystem{
    private ElevatorMotor motor;
    private LinkedList<Integer> queue = new LinkedList<>();
    private int floor = 0;
    
    public Smart(ElevatorMotor motor) {
        this.motor = motor;
    }
    
    @Override
    public void internalButton(int floor) {
        if(motor.state == ElevatorMotor.State.DOWN){
            if((floor < this.floor) && (floor > queue.peek())) addCallInBetween(floor, true);
        }
        else if(motor.state == ElevatorMotor.State.UP){
            if((floor > this.floor) && (floor < queue.peek())) addCallInBetween(floor, false);
        }
        else addCall(floor);
    }

    @Override
    public void externalButton(int floor, boolean downward) {
        if(motor.state == ElevatorMotor.State.DOWN && downward){
            if((floor < this.floor) && (floor > queue.peek())) addCallInBetween(floor, true);
        }
        else if(motor.state == ElevatorMotor.State.UP && !downward){
            if((floor > this.floor) && (floor < queue.peek())) addCallInBetween(floor, false);
        }
        else addCall(floor);
    }

    @Override
    public void emergencyButton() {
        motor.emergencyStop();
        queue.clear();
    }

    @Override
    public void sensor() {
        System.out.println("[SMART A] sensor f:"+floor+" queue:"+queue+" s:"+motor.state);
        if(motor.state == ElevatorMotor.State.UP || (motor.state == ElevatorMotor.State.NEXT && motor.wayUp)){
            floor++;
            if(queue.isEmpty()) return;
            if(floor == queue.peek()-1){
                System.out.println("[SMART] stop next UP");
                motor.stopNextFloor();
            } 
        }
        else if (motor.state == ElevatorMotor.State.DOWN || (motor.state == ElevatorMotor.State.NEXT && !motor.wayUp)){
            floor--;
            if(queue.isEmpty()) return;
            if(floor == queue.peek()+1){
                System.out.println("[SMART] stop next DOWN");
                motor.stopNextFloor();
            }
        }
        
        if(motor.state == ElevatorMotor.State.STOP){
            if(queue.isEmpty()) return;
            if(floor == queue.peek()) queue.poll();
            goToNextInQueue();
        }
        
        System.out.println("[SMART B] sensor f:"+floor+" queue:"+queue+" s:"+motor.state);
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
        if(queue.contains(floor)) return;
        if(floor == this.floor && motor.state == ElevatorMotor.State.STOP) return;
        
        
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
        
        
        if(motor.state == ElevatorMotor.State.STOP) goToNextInQueue();
    }

    private void addCallInBetween(int floor, boolean downward) {
        int indexToAdd = 0;
        
        if(downward){
            while(queue.get(indexToAdd) > floor) indexToAdd++;
        }else{
            while(queue.get(indexToAdd) < floor) indexToAdd++;
        }
        
        queue.add(indexToAdd, floor);
    }
    
}
