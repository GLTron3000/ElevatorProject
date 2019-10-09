package controlCommand;

import com.elevatorproject.ElevatorMotor;

import java.util.*;

public class SemiSmartcenseur implements CommandSystem {

    private ElevatorMotor motor;
    private ArrayList<Integer> waiting_list = new ArrayList<>();
    private ArrayList<Integer> priority_queue = new ArrayList<>();
    //private int floor = 0;
    private int current_floor=0;

    @Override
    public void internalButton(int floor) {

        if(!waiting_list.contains(floor))
            waiting_list.add(floor);
        verifyList();

    }

    @Override
    public void externalButton(int floor, boolean downward) {

        if(!waiting_list.contains(floor))
            waiting_list.add(floor);

        if(downward == motor.state.equals(ElevatorMotor.State.DOWN))
            verifyList();

        if(!downward == motor.state.equals(ElevatorMotor.State.UP))
            verifyList();

    }

    @Override
    public void emergencyButton() {

        motor.emergencyStop();
        waiting_list.clear();

    }

    @Override
    public void sensor() {
        if(motor.state == ElevatorMotor.State.UP){
            current_floor++;
            if(priority_queue.isEmpty()) return;
            if(current_floor== priority_queue.get(0)-1){
                System.out.println("[BASIC] stop next UP");
                priority_queue.remove(0);
                motor.stopNextFloor();
            }
        }
        else if (motor.state == ElevatorMotor.State.DOWN){
            current_floor--;
            if(priority_queue.isEmpty()) return;
            if(current_floor == priority_queue.get(0)+1){
                System.out.println("[BASIC] stop next DOWN");
                priority_queue.remove(0);
                 motor.stopNextFloor();

            }
        }
    }

    private void verifyList() {

        for(Integer i : waiting_list){

            if(i > current_floor && motor.state.equals(ElevatorMotor.State.UP) ){
                priority_queue.add(i);
                Collections.sort(priority_queue);
            }

            if(i < current_floor && motor.state.equals(ElevatorMotor.State.DOWN) ){
                priority_queue.add(i);
                Collections.sort(priority_queue);
                Collections.reverse(priority_queue);
            }
        }
    }
}
