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
        System.out.println("[SMART] internal f:"+floor);
        
        if(queue.isEmpty()) addCall(floor);
        
        if(isGoingDown()){
            if((floor < this.floor) && (floor > queue.peek())) addCallInBetween(floor, true);
            else addCall(floor);
        }
        else if(isGoingUp()){
            if((floor > this.floor) && (floor < queue.peek())) addCallInBetween(floor, false);
            else addCall(floor);
        }
        else addCall(floor);
    }

    @Override
    public void externalButton(int floor, boolean downward) {
        System.out.println("[SMART] external f:"+floor+" d:"+downward);
        if(isGoingDown()){
            if(!downward) addAtTail(floor, false);
            else if((floor < this.floor)) addCallInBetween(floor, true);
        }
        else if(isGoingUp()){
            if(downward) addAtTail(floor, true);
            else if((floor > this.floor)) addCallInBetween(floor, false);
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
        if(isGoingUp()){
            floor++;
            if(queue.isEmpty()) return;
            if(floor == queue.peek()-1){
                System.out.println("[SMART] stop next UP");
                motor.stopNextFloor();
            } 
        }
        else if (isGoingDown()){
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
        System.out.println("[SMART] addCallInBetween"+floor);
        int indexToAdd = 0;
        
        if(downward){
            while((queue.size() > indexToAdd) &&queue.get(indexToAdd) > floor) indexToAdd++;
        }else{
            while((queue.size() > indexToAdd) &&queue.get(indexToAdd) < floor) indexToAdd++;
        }
        
        queue.add(indexToAdd, floor);
    }
    
    private void addAtTail(int floor, boolean downward) {
        int indexToAdd = 0;
        
        if(downward){
            indexToAdd = indexOfMax();
            
            while((queue.size() > indexToAdd) && (queue.get(indexToAdd) > floor)) indexToAdd++;            
        }else{
            indexToAdd = indexOfMin();

            while((queue.size() > indexToAdd) && (queue.get(indexToAdd) < floor)) indexToAdd++;
        }
        
        if(indexToAdd >= queue.size()) queue.add(floor);
        else queue.add(indexToAdd, floor);
    }
 
    private boolean isGoingUp(){
        return motor.state == ElevatorMotor.State.UP || (motor.state == ElevatorMotor.State.NEXT && motor.wayUp);
    }
    
    private boolean isGoingDown(){
        return motor.state == ElevatorMotor.State.DOWN || (motor.state == ElevatorMotor.State.NEXT && !motor.wayUp);
    }
    
    private int indexOfMin(){
        int indexOfMin = -1;
        int valueOfMin = 99999;

        for(int i=0; i < queue.size(); i++) if(queue.get(i) < valueOfMin) indexOfMin = i;
        
        return indexOfMin;
    }
    
    private int indexOfMax(){
        int indexOfMax = -1;
        int valueOfMax = -1;

        for(int i=0; i < queue.size(); i++) if(queue.get(i) > valueOfMax) indexOfMax = i;

        return indexOfMax;
    }
}
