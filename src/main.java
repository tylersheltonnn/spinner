
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;

@ScriptManifest(author = "tyler", info = "My first script", name = "Lumby Spinner", version = 0, logo = "")
public class main extends Script {
    private static final int DEPOSIT = 1;
    private static final int WITHDRAW = 2;
    private static final int SPIN = 3;
    private static final int SPINNINGING = 4;
    private static final int TAKING_PATH_1_TO_BANK = 5;
    private static final int TAKING_PATH_2_TO_BANK = 6;
    private static final int TAKING_PATH_1_TO_SPOOLER = 7;
    private static final int TAKING_PATH_2_TO_SPOOLER = 8;
    private static final int IDLE = 9;
    private static final int MOVE_TO_SPINNER_AND_SPIN=10;

    private int previous;

    private static final String string = "Bow string";
    private static final String flax = "Flax";

    @Override
    public void onStart() {

    }


    private int getState(int previousState) throws InterruptedException {
        log("getState");
        if (previousState == WITHDRAW){
            int random = random(0, 1000);
            if (random<900){
                log("Taking path 1 to spooler");
                return TAKING_PATH_1_TO_SPOOLER;
            } else {
                log("Taking path 2 to spooler");
                return  TAKING_PATH_2_TO_SPOOLER;
            }
        }
        if (previousState == TAKING_PATH_1_TO_SPOOLER){
            if(isOnSecondFloor()) {
                log("move to spinner and spin from path one");
                return MOVE_TO_SPINNER_AND_SPIN;
            }
        }

        if (getInventory().contains(flax) && isOnSecondFloor()){
            log("Moving to spinner to spin");
            return MOVE_TO_SPINNER_AND_SPIN;
        }
        if(getInventory().contains(flax) && !myPlayer().isAnimating() && PlayerInFrontOfSpinner()){
            log("Spin");
            return SPIN;
        } else if (!getInventory().contains(flax) && getInventory().contains(string)){
            log("deposit");
            return DEPOSIT;
        } else if (!getInventory().contains(string) && !getInventory().contains(flax)) {
            log("withdraw");
            return WITHDRAW;
        }
        log("IDLE");
        return IDLE;
    }

    private boolean PlayerInFrontOfSpinner() {
        return myPlayer().getLocalX() == 3209 && myPlayer().getLocalY() == 3213 && isOnSecondFloor();
    }

    private void spin(int State) throws InterruptedException {
        log("INSIDE SPIN");
        int random1 = random(600,1300);
        Item flax = inventory.getItem("Flax");
        Entity spinningWheel = objects.closest("Spinning wheel");
        if (random1 > 50) {
            flax.interact("Use");
            sleep(random(300, 500));
            spinningWheel.interact("Use");
            sleep(random(600, 800));
            RS2Widget logClick = widgets.get(270, 14);
            if (logClick != null){
                logClick.interact();
                sleep(random(57000, 72000));
            } if (State == MOVE_TO_SPINNER_AND_SPIN){
                sleep(5000);
            }
        } else {
            spinningWheel.interact("Spin");
            sleep(random(400,800));
            RS2Widget logClick = widgets.get(270, 17);
            if (logClick != null){
                logClick.interact();
                sleep(random(57000, 72000));
            } if (State == MOVE_TO_SPINNER_AND_SPIN){
                sleep(5000);
            }
        }



    }


    private boolean openBank() throws InterruptedException {
        if (getBank().isOpen()) {
            return true;
        }
        RS2Object bank = getObjects().closest("Bank booth");
        if (bank != null) {
            while (getBank().isOpen()) {
                bank.interact("Bank");
                sleep(random(900,1900));
            }
            return true;
        }
        return false;
    }

    private void closeBank() throws InterruptedException {
        while (getBank().isOpen()) {
            getBank().close();
            sleep(random(950, 2000));
        }
    }

    private void withdraw() throws InterruptedException {
        if (openBank()) {
            while (!getInventory().contains(flax)) {
                sleep(random(925, 2300));
                getBank().withdrawAll(flax);
                sleep(random(925,2100));
            }
            closeBank();
        }
    }

    private void deposit() throws InterruptedException {
        getBank().open();
        sleep(random(925,2000));
        getBank().depositAll();

    }
    @Override
    public int onLoop() throws InterruptedException {
        switch (getState(previous)) {
            case DEPOSIT: // pickup
                deposit();
                previous=DEPOSIT;
                break;
            case WITHDRAW: //kill
                withdraw();
                previous = WITHDRAW;
                break;
            case TAKING_PATH_1_TO_SPOOLER: // waii
                takePathOneToSpool();
                previous = TAKING_PATH_1_TO_SPOOLER;
                break;
            case TAKING_PATH_2_TO_SPOOLER: // waii
                takePathITwoToSpool();
                previous = TAKING_PATH_2_TO_SPOOLER;
                break;
            case TAKING_PATH_1_TO_BANK: // waii
                takePathIOneToBank();
                previous = TAKING_PATH_1_TO_BANK;
                break;
            case TAKING_PATH_2_TO_BANK: // waii
                takePathITwoToBank();
                previous = TAKING_PATH_2_TO_BANK;
                break;
            case SPIN: // waii
                spin(SPIN);
                sleep(random(1000, 3000));
                previous = SPIN;
                break;
            case MOVE_TO_SPINNER_AND_SPIN: // waii
                log("wait");
                spin(MOVE_TO_SPINNER_AND_SPIN);
                sleep(random(5000, 6000));
                break;
        }

        return random(500, 2000);
    }

    private void takePathIOneToBank() {

    }

    private void takePathITwoToBank() {

    }

    private void takePathITwoToSpool() {
    }

    private void takePathOneToSpool() throws InterruptedException {
        log("inside path1");
        if(isOnSecondFloor()) {
            int random = random(0, 1000);
            if (random > 520) {
                Position position = new Position(random(2308, 3210), random(3213, 3215), 1);
                getWalking().walk(position);
                sleep(random(500, 1000));
            } else{
                return;
            }
        }
        else {
            int random = random(0, 1000);
            if (random > 520) {
                log("walking then stairs");
                Position position = new Position(random(3205, 3206), random(3209, 3211), 2);
                getWalking().walk(position);
                sleep(random(1000, 2000));
                Entity stairs = objects.closest("Staircase");
                stairs.interact("Climb-down");
                sleep(random(3000, 5000));
            } else {
                log("stairs");
                Entity stairs = objects.closest(new Area(3205,3209,3205,3209),"Staircase");
                stairs.interact("Climb-down");
                sleep(random(3000, 5000));
            }
        }
    }

    private boolean isOnSecondFloor() {
        return getFloorLevel()==1;
    }

    private int getFloorLevel() {
        return myPlayer().getZ();
    }

    @Override
    public void onExit() {
        log("Thanks for running my Tea Thiever!");
    }

    @Override
    public void onPaint(Graphics2D g) {

    }

}