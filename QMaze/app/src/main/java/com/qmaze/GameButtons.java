package com.qmaze;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class GameButtons implements Parcelable {

    private double x_coord, y_coord, distance;
    private int buttonId;
    private boolean buttonIsWall;
    private boolean visitedButton, backtrackButton;
    private boolean startButtonState, endButtonState;

    public static final Parcelable.Creator<GameButtons> CREATOR
            = new Parcelable.Creator<GameButtons>() {
        public GameButtons createFromParcel(Parcel in) {
            return new GameButtons(in);
        }

        public GameButtons[] newArray(int size) {
            return new GameButtons[size];
        }
    };

    public GameButtons(Parcel in){
        setButtonId(in.readInt());
        int isWall = in.readInt();
        if(isWall == 1){setButtonIsWall(true);}
        else if(isWall == 0){setButtonIsWall(false);}
        int startState = in.readInt();
        if(startState == 1){setStartButtonState(true);}
        else if(startState == 0){setStartButtonState(false);}
        int endState = in.readInt();
        if(endState == 1){setEndButtonState(true);}
        else if(endState == 0){setEndButtonState(false);}
        int isVisible = in.readInt();
        if(isVisible == 1){setVisitedState(true);}
        else if(isVisible == 0){setVisitedState(false);}
        int backtrack = in.readInt();
        if(backtrack == 1){setBacktrackButtonState(true);}
        else if(backtrack == 0){setBacktrackButtonState(false);}
        setXCoord(in.readDouble());
        setYCoord(in.readDouble());
    }
    public GameButtons(int id){
        setButtonId(id);
        setStartButtonState(false);
        setEndButtonState(false);
        setButtonIsWall(false);
        setVisitedState(false);
    }

    public void setButtonId(int id){buttonId = id;}
    public int getButtonId(){return buttonId;}
    public void setButtonIsWall(boolean isWall){buttonIsWall = isWall;}
    public boolean getButtonIsWall(){return buttonIsWall;}
    public void setStartButtonState(boolean start){startButtonState = start;}
    public boolean getStartButtonState(){return startButtonState;}
    public void setEndButtonState(boolean end){endButtonState = end;}
    public boolean getEndButtonState(){return endButtonState;}
    public void setVisitedState(boolean visited){visitedButton = visited;}
    public boolean getVisitedState(){return visitedButton;}
    public void setBacktrackButtonState(boolean backtrack){backtrackButton = backtrack;}
    public boolean getBacktrackButtonState(){return backtrackButton;}
    public void setXCoord(double x){x_coord = x;}
    public void setYCoord(double y){y_coord = y;}
    public double getXCoord(){return  x_coord;}
    public double getYCoord(){return y_coord;}
    public void setDistance(double dist){distance = dist;}
    public double getDistance(){return distance;}
    public void computeDistance(GameButtons currBtn, GameButtons endBtn){
        /* Computing Manhattan Distance (#of tiles in x direction plus # of tiles in y direction */
        double curr_x = currBtn.getXCoord();
        double curr_y = currBtn.getYCoord();
        double end_x = endBtn.getXCoord();
        double end_y = endBtn.getYCoord();
        double distance2 = Math.pow((curr_x - end_x), 2) + Math.pow((curr_y - end_y), 2);
        double currDistance = Math.sqrt(distance2);
        setDistance(currDistance);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {                 // booleans 0 for false and 1 for true
        dest.writeInt(getButtonId());                                   // writing button id
        if(getButtonIsWall() == true){dest.writeInt(1);}                // writing button is wall
        else if(getButtonIsWall() == false){dest.writeInt(0);}
        if(getStartButtonState() == true){dest.writeInt(1);}            // writing start button state
        else if(getStartButtonState() == false){dest.writeInt(0);}
        if(getEndButtonState() == true){dest.writeInt(1);}              // writing end button state
        else if(getEndButtonState() == false){dest.writeInt(0);}
        if(getVisitedState() == true){dest.writeInt(1);}                // writing button is visible
        else if (getVisitedState() == false){dest.writeInt(0);}
        if(getBacktrackButtonState() == true){dest.writeInt(1);}        // writing button is visible
        else if (getBacktrackButtonState() == false){dest.writeInt(0);}
        dest.writeDouble(getXCoord());                                  // writing button x coordinate
        dest.writeDouble(getYCoord());                                  // writing button y coordinate
    }
}
