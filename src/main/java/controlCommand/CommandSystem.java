package controlCommand;


public interface CommandSystem {
    
    public void internalButton(int floor);
    
    public void externalButton(int floor, boolean downward);
    
    public void emergencyButton();
    
    public void sensor();
}
