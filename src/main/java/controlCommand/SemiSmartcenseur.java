package controlCommand;

import com.elevatorproject.ElevatorMotor;

import java.util.*;

public class SemiSmartcenseur implements CommandSystem {

    private ElevatorMotor motor;
    private ArrayList<Integer> waiting_list = new ArrayList<>();
    private ArrayList<Integer> priority_queue = new ArrayList<>();
    //private int floor = 0;
    private int current_floor=0;

    public SemiSmartcenseur(ElevatorMotor motor) {
        this.motor = motor;
    }

    @Override
    public void internalButton(int floor) {
        if(!waiting_list.contains(floor))
        {
            System.out.println("[SMART] floor added" + " " +floor);
            waiting_list.add(floor);
            verifyList();
        }
        System.out.println("[SMART] internal button pressed");
        goToNextInQueue();
    }

    @Override
    public void externalButton(int floor, boolean downward) {

        if(!waiting_list.contains(floor))
            waiting_list.add(floor);

        if(downward == motor.state.equals(ElevatorMotor.State.DOWN)){
            verifyList();
            goToNextInQueue();
            System.out.println("[SMART] going DOWN");
        }

        if(!downward == motor.state.equals(ElevatorMotor.State.UP)) {
            verifyList();
            goToNextInQueue();
            System.out.println("[SMART] going UP");
        }
        System.out.println("[SMART] external button pressed");
    }

    @Override
    public void emergencyButton() {
        motor.emergencyStop();
        System.out.println("[SMART] emergency stop");
        priority_queue.clear();
        waiting_list.clear();
    }

    @Override
    public void sensor() {
        if(motor.state == ElevatorMotor.State.UP){
            current_floor++;
            if(priority_queue.isEmpty()) return;
            if(current_floor== priority_queue.get(0)-1){
                System.out.println("[SMART] stop next UP");
                priority_queue.remove(0);
                motor.stopNextFloor();
                current_floor++;
            }
        }
        else if (motor.state == ElevatorMotor.State.DOWN){
            current_floor--;
            if(priority_queue.isEmpty())
                return;
            if(current_floor == priority_queue.get(0)+1){
                System.out.println("[SMART] stop next DOWN");
                priority_queue.remove(0);
                motor.stopNextFloor();
                current_floor--;
            }
        }
        if(motor.state == ElevatorMotor.State.STOP){
            if(priority_queue.isEmpty() && waiting_list.isEmpty()) {
                System.out.println("[SMART] empty + current_floor" + " " + current_floor + " state" + " " + motor.wayUp);
                affiche_queue();
            }
            if(priority_queue.isEmpty() && !waiting_list.isEmpty()) {
                System.out.println(" [SMART] direction changed ");
                verifyList();
                goToNextInQueue();
            }
            if(!priority_queue.isEmpty()){
                if(current_floor == priority_queue.get(0))
                    priority_queue.remove(0);
                goToNextInQueue();
            }
        }
    }

    private void affiche_queue(){
        System.out.println(" priority queue : " + " " + priority_queue.toString());
        System.out.println(" waiting queue : " + " " + waiting_list.toString());
    }

    private void goToNextInQueue(){
        if(priority_queue.isEmpty()) {
            System.out.println(" [SMART] c'est vide ");
            return;
        }

        if(current_floor < priority_queue.get(0) && !motor.wayUp)
            motor.goUp();
        else if(current_floor > priority_queue.get(0) && motor.wayUp)
            motor.goDown();

        if(current_floor == priority_queue.get(0) -1){
            motor.goUp();
            motor.stopNextFloor();
            priority_queue.remove(0);
            current_floor++;
            System.out.println("[SMART] state : " + motor.state);
        }
        else if(current_floor == priority_queue.get(0) +1){
            motor.goDown();
            motor.stopNextFloor();
            priority_queue.remove(0);
            current_floor--;
            System.out.println("[SMART] state : " + motor.state);
        }
        else if(priority_queue.get(0) < current_floor) {
            motor.goDown();
        }
        else {
            motor.goUp();
        }
        affiche_queue();
    }

    private void verifyList() {
        ArrayList<Integer> tmp = new ArrayList<>(waiting_list);
        for(Integer i : tmp){
            System.out.println("[SMART] i checked : "+ i );
            if(i > current_floor && (motor.state.equals(ElevatorMotor.State.UP) || motor.state.equals(ElevatorMotor.State.STOP)) && !priority_queue.contains(i) ){
                priority_queue.add(i);
                System.out.println("[SMART] i added : "+ i );
                waiting_list.remove(i);
            }
            Collections.sort(priority_queue);
            if(i < current_floor && (motor.state.equals(ElevatorMotor.State.DOWN) || motor.state.equals(ElevatorMotor.State.STOP)) && !priority_queue.contains(i)){
                priority_queue.add(i);
                System.out.println("[SMART] i added : "+ i );
                waiting_list.remove(i);
                Collections.sort(priority_queue);
                Collections.reverse(priority_queue);
            }
            if( i == current_floor){
                priority_queue.remove(i);
                waiting_list.remove(i);
            }
        }
    }

}
