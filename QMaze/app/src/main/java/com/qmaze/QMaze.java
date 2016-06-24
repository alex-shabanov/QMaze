package com.qmaze;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class QMaze extends AppCompatActivity {

    private Thread gameThread;
    private Handler handle;
    private GameButtons gameButton, startButton, endButton;
    private Button aButton;
    private RelativeLayout relativeLayout;
    private RelativeLayout.LayoutParams params;
    private int width, height;
    private int buttonWidth, buttonHeight, layoutWidth, layoutHeight;
    private boolean startGame, endGame, gameStart, gameState, exitThread, resetGame;
    private ArrayList<Button> buttonList;
    private ArrayList<GameButtons> gameButtonList, bestPathList;
    public static final String TAG = Constants.QMAZE_ACTIVITY;
    public void setStartGame(boolean start){startGame = start;}
    public void setEndGame(boolean end){endGame = end;}
    public boolean getStartGame(){return startGame;}
    public boolean getEndGame(){return endGame;}
    public void setStartButton(GameButtons startBtn){startButton = startBtn;}
    public GameButtons getStartButton(){return startButton;}
    public void setEndButton(GameButtons endBtn){endButton = endBtn;}
    public GameButtons getEndButton(){return endButton;}
    public void setGameStart(boolean start){gameStart = start;}
    public boolean getGameStart(){return gameStart;}
    public void setGameState(boolean state){gameState = state;}
    public boolean getGameState(){return gameState;}
    public void setExitThread(boolean exit){exitThread = exit;}
    public boolean getExitThread(){return  exitThread;}
    public void setResetGame(boolean reset){resetGame = reset;}
    public boolean getResetGame(){return resetGame;}
    public void disableButtonClickState(){
        for(int i = 0; i < buttonList.size(); i++){
            buttonList.get(i).setEnabled(false);
        }
    }
    public void enableButtonClickState(){
        for(int i = 0; i < buttonList.size(); i++){
            buttonList.get(i).setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qmaze);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.mipmap.qmaze2);
        relativeLayout = (RelativeLayout) findViewById(R.id.qMazeRelativeLayout);

        if(savedInstanceState == null){
            setStartGame(false);
            setEndGame(false);
            setGameState(false);
            setExitThread(false);
            setResetGame(true);
            buttonList = new ArrayList<>();
            bestPathList = new ArrayList<>();
            gameButtonList = new ArrayList<>();
            initGameButtons();
        }
        else if(savedInstanceState != null){
            buttonList = new ArrayList<>();
            bestPathList = new ArrayList<>();
            boolean game_start = savedInstanceState.getBoolean(Constants.GAME_START);
            boolean game_state = savedInstanceState.getBoolean(Constants.GAME_STATE);
            boolean game_reset = savedInstanceState.getBoolean(Constants.GAME_RESET);
            setGameStart(game_start);
            setResetGame(game_reset);
            setGameState(game_state);
            gameButtonList = savedInstanceState.getParcelableArrayList(Constants.GAME_BUTTON_LIST);
            bestPathList = savedInstanceState.getParcelableArrayList(Constants.BEST_PATH_LIST);
            /* Setting the start and end buttons */
            for(int i = 0; i < gameButtonList.size(); i++) {
                if(gameButtonList.get(i).getEndButtonState() == true){
                    setEndButton(gameButtonList.get(i));
                }
                if(gameButtonList.get(i).getStartButtonState() == true){
                    setStartButton(gameButtonList.get(i));
                }
            }
            if(bestPathList.size() != 0 && getGameStart() == true){
                GameButtons currStartButton;
                int size = bestPathList.size();
                currStartButton = bestPathList.get(size - 1);
                gameThread = new Thread(new GameThread(currStartButton, getEndButton()));
                gameThread.start();
            }
        }

        handle = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                int currBtnId = (Integer) msg.obj;
                if(currBtnId == 200){    // make buttons clickable when solution is found
                    setGameStart(false);
                    setGameState(false);
                    setResetGame(false); // game can now be reset
                    setExitThread(true);
                    enableButtonClickState();
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.goal_reached, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(currBtnId == 300){
                    int btnId = msg.arg1;
                    setButtonState(btnId, Constants.BACKTRACKING);
                }
                else if(currBtnId == -5){
                    int btnId = msg.arg1;
                    setButtonState(btnId, Constants.BACKTRACKING);
                }
                else if(currBtnId == 400){ // make buttons clickable when solution is not found
                    setGameStart(false);
                    setGameState(false);
                    setResetGame(false);   // game can now be reset
                    setExitThread(true);
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.goal_not_reached, Toast.LENGTH_SHORT);
                    toast.show();
                    enableButtonClickState();
                    return;
                }
                setButtonState(currBtnId, Constants.VISITED_SQUARE);
            }
        };

        /* Checking if the android device is in the portrait or land orientation */
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            width = Constants.WIDTH_PORTRAIT;
            height = Constants.HEIGHT_PORTRAIT;
            buttonWidth = Constants.BUTTON_PORTRAIT_WIDTH;
            buttonHeight = Constants.BUTTON_PORTRAIT_HEIGHT;
            layoutWidth = Constants.LAYOUT_PORTRAIT_WIDTH;
            layoutHeight = Constants.LAYOUT_PORTRAIT_HEIGHT;
            drawVerticalMode();
        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            width = Constants.WIDTH_PORTRAIT;
            height = Constants.HEIGHT_PORTRAIT;
            buttonWidth = Constants.BUTTON_LAND_WIDTH;
            buttonHeight = Constants.BUTTON_LAND_HEIGHT;
            layoutWidth = Constants.LAYOUT_LAND_WIDTH;
            layoutHeight = Constants.LAYOUT_LAND_HEIGHT;
            drawHorizontalMode();
        }
    }

    public void initGameButtons(){
        GameButtons gameButton;
        for(int i = 0; i < Constants.TOTAL_NUMBER_OF_BUTTONS; i++){
            gameButton = new GameButtons(i);
            gameButtonList.add(gameButton);
        }
    }

    public void drawVerticalMode(){
        int count = 0;
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                aButton = new Button(this);
                gameButton = gameButtonList.get(count);
                gameButtonList.get(count).setXCoord(j);
                gameButtonList.get(count).setYCoord(i);
                params = new RelativeLayout.LayoutParams(layoutWidth, layoutHeight);
                int buttonId = gameButton.getButtonId();
                aButton.setId(buttonId);
                buttonList.add(aButton);  // adding button to list of buttons
                final int btnId = aButton.getId();
                aButton.setWidth(buttonWidth);
                aButton.setHeight(buttonHeight);
                aButton.setTextSize(Constants.BUTTON_TEXT_SIZE);
                aButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_shape5, null));
                params.topMargin = layoutWidth * i;
                params.leftMargin = layoutHeight * j;
                if(aButton.getParent()!= null) {
                    ((ViewGroup) aButton.getParent()).removeView(aButton);
                }
                relativeLayout.addView(aButton, params);
                aButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        setButtonState(btnId, Constants.NOT_VISITED_SQUARE);
                    }
                });
                count += 1;
            }
        }
        if(getGameState() == true){
            disableButtonClickState();
        }
        else if(getGameState() == false){
            enableButtonClickState();
        }
        updateUI();
    }

    public void drawHorizontalMode(){
        int count = 0;
        for(int i = height - 1; i >= 0; i--) {
            for (int j = 0 ; j < width ; j++) {
                aButton = new Button(this);
                gameButton = gameButtonList.get(count);
                gameButtonList.get(count).setXCoord(j);
                gameButtonList.get(count).setYCoord((height - 1) - i);
                params = new RelativeLayout.LayoutParams(layoutWidth, layoutHeight);
                int buttonId = gameButton.getButtonId();
                aButton.setId(buttonId);
                buttonList.add(aButton);  // adding button to list of buttons
                final int btnId = aButton.getId();
                aButton.setWidth(buttonWidth);
                aButton.setHeight(buttonHeight);
                aButton.setTextSize(Constants.BUTTON_TEXT_SIZE);
                aButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_shape5, null));
                params.topMargin = layoutWidth * j;
                params.leftMargin = layoutHeight * i;
                if (aButton.getParent() != null) {
                    ((ViewGroup) aButton.getParent()).removeView(aButton);
                }
                relativeLayout.addView(aButton, params);
                aButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        setButtonState(btnId, Constants.NOT_VISITED_SQUARE);
                    }
                });
                count += 1;
            }
        }
        if(getGameState() == true){
            disableButtonClickState();
        }
        else if(getGameState() == false){
            enableButtonClickState();
        }
        updateUI();
    }

    public void setButtonState(int id, String str) {
        Button tempButton;
        GameButtons tempButtonClass;
        GameButtons startButton, endButton;
        endButton = getEndButton();
        for (int i = 0; i < gameButtonList.size(); i++) {
            tempButton = buttonList.get(i);
            tempButtonClass = gameButtonList.get(i);
            startButton = getStartButton();
            int btnId = tempButtonClass.getButtonId();
            boolean isWall = tempButtonClass.getButtonIsWall();
            if(btnId == id && startButton == null && isWall == false){
                if(endButton == null){
                    tempButtonClass.setStartButtonState(true);
                    setStartButton(tempButtonClass);
                    tempButtonClass.setVisitedState(true);
                    tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.start_button, null));
                    return;
                }
                else if(endButton != null){
                    if(endButton.getButtonId() != id){
                        tempButtonClass.setStartButtonState(true);
                        setStartButton(tempButtonClass);
                        tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.start_button, null));
                    }
                    return;
                }
            }
            if(btnId == id && endButton == null && startButton != null && isWall == false){
                if(startButton.getButtonId() != id){
                    tempButtonClass.setEndButtonState(true);
                    setEndButton(tempButtonClass);
                    tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.end_button, null));
                }
                return;
            }
            if(btnId == id && startButton != null && getGameStart() == false){
                if(startButton.getButtonId() == id){
                    setStartButton(null);
                    tempButtonClass.setStartButtonState(false);
                    tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_shape5, null));
                    return;
                }
            }
            if(btnId == id && endButton != null && getGameStart() == false){
                if(endButton.getButtonId() == id) {
                    setEndButton(null);
                    tempButtonClass.setEndButtonState(false);
                    tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_shape5, null));
                    return;
                }
            }
            if(btnId == id && isWall == false && startButton != null && endButton != null
                    && getGameStart() == false&& !str.equalsIgnoreCase(Constants.BACKTRACKING)){
                if(startButton.getButtonId() != id && endButton.getButtonId() != id){
                    tempButtonClass.setButtonIsWall(true);
                    tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.wall_button, null));
                    return;
                }
            }
            if(btnId == id && isWall == true && startButton != null && endButton != null){
                tempButtonClass.setButtonIsWall(false);
                tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_shape5, null));
                return;
            }
            if(startButton != null && endButton != null) {
                if(btnId == id && id != getStartButton().getButtonId() && id != getEndButton().getButtonId() &&
                        str.equalsIgnoreCase(Constants.VISITED_SQUARE)) {
                    tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.best_path_button, null));
                }
            }
            if(startButton != null && str.equalsIgnoreCase(Constants.BACKTRACKING)){
                if(btnId == id && startButton.getButtonId() != id){
                    tempButtonClass.setBacktrackButtonState(false);
                    tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_shape5, null));
                }
            }
        }
    }

    public void updateUI(){
        Button tempButton;
        GameButtons tempGameButton;
        for(int i = 0; i < gameButtonList.size(); i++){
            tempButton = buttonList.get(i);
            tempGameButton = gameButtonList.get(i);
            boolean isWall = tempGameButton.getButtonIsWall();
            boolean isStartBtn = tempGameButton.getStartButtonState();
            boolean isEndBtn = tempGameButton.getEndButtonState();
            boolean isBacktracked = tempGameButton.getBacktrackButtonState();

            if(isStartBtn == true){
                tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.start_button, null));
            }
            else if(isEndBtn == true){
                tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.end_button, null));
            }
            else if(isWall == true){
                tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.wall_button, null));
            }
            else if(isBacktracked == true){
                tempButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.best_path_button, null));
            }
        }
    }

    private class GameThread implements Runnable {

        private Message message;
        private LinkedList<GameButtons> listBtn;
        GameButtons tempButton, nextButton;
        private GameButtons startButtonClass, endButtonClass;
        public GameThread(GameButtons startBtn, GameButtons endBtn){
            setStartButtonClass(startBtn);
            setEndButtonClass(endBtn);
        }
        public void setStartButtonClass(GameButtons startBtn){startButtonClass = startBtn;}
        public void setEndButtonClass(GameButtons endBtn){endButtonClass = endBtn;}
        public GameButtons getStartButtonClass(){return startButtonClass;}
        public GameButtons getEndButtonClass(){return endButtonClass;}
        
        @Override
        public void run() {
            Looper.prepare();
            BFS(getStartButtonClass(), getEndButtonClass());
            Looper.loop();
        }

        public void BFS(GameButtons startBtn, GameButtons endBtn){
            int currBtnId = -5, endBtnId;
            GameButtons currButton, otherButton;
            listBtn = new LinkedList<>();
            listBtn.addLast(startBtn);  // appends element to the back of the linked list
            while(!listBtn.isEmpty()){  // && getExitThread() == false
                if(getExitThread() == true){return;}
                currButton = listBtn.getFirst();  // get the first button in the list
                currBtnId = currButton.getButtonId();
                endBtnId = endBtn.getButtonId();
                if(currBtnId == endBtnId){
                    message = Message.obtain();
                    message.obj = 200;
                    handle.sendMessage(message);
                    return;
                }
                listBtn.removeFirst();  // remove the first button in the list
                try {
                    Thread.sleep(700);
                } catch (Exception e) {
                    e.getLocalizedMessage();
                }
                LinkedList<GameButtons> arrayList = new LinkedList<>();
                for(int i = 0; i < gameButtonList.size(); i++){
                    otherButton = gameButtonList.get(i);
                    if(containsEdge(currButton, otherButton) == true && otherButton.getVisitedState() == false){
                        arrayList.add(otherButton);
                    }
                }
                if(arrayList.size() != 0) {
                    getOptimalLocation(arrayList, endBtn);
                }
                else {
                    if(nextButton != null) {
                        nextButton.setBacktrackButtonState(false);
                    }
                    backtrack(bestPathList);
                }
            }
            // 400 message is send when no solution path has been found, queue is empty
            message = Message.obtain();
            message.obj = 400;
            message.arg1 = currBtnId;
            handle.sendMessage(message);
        }

        public void backtrack(ArrayList<GameButtons> list){
            int buttonId;
            int size = list.size();
            if(size == 0 || size == 1){return;}
            tempButton = list.get(size - 1);
            nextButton = list.get(size - 2);
            buttonId = tempButton.getButtonId();
            tempButton.setBacktrackButtonState(false);
            listBtn.add(nextButton);
            list.remove(size - 1);
            message = Message.obtain();
            message.obj = 300;
            message.arg1 = buttonId;
            handle.sendMessage(message);
        }

        public void getOptimalLocation(LinkedList<GameButtons> currList, GameButtons endBtn){
            double minPosition, currPosition;
            GameButtons currLocation;
            GameButtons bestLocation = currList.get(0);
            bestLocation.computeDistance(bestLocation, endBtn);
            minPosition = bestLocation.getDistance();
            for(int i = 0; i < currList.size(); i++){ // gets the best closest location according to the distance function heuristic
                currLocation = currList.get(i);
                currLocation.computeDistance(currLocation, endBtn);
                currPosition = currLocation.getDistance();
                if(currPosition < minPosition){
                    bestLocation = currLocation;
                    minPosition = currPosition;
                }
                else if(currPosition == minPosition){
                    /*Breaking the tie if Manhattan distances are equal */
                    if((Math.abs(currLocation.getXCoord() - endBtn.getXCoord()) < Math.abs(bestLocation.getXCoord() - endBtn.getXCoord())) ||
                            (Math.abs(currLocation.getYCoord() - endBtn.getYCoord()) < Math.abs(bestLocation.getYCoord() - endBtn.getYCoord()))){
                        bestLocation = currLocation;
                        minPosition = currPosition;
                    }
                }
            }
            bestLocation.setVisitedState(true);
            bestLocation.setBacktrackButtonState(true);
            listBtn.addLast(bestLocation);  // adding new location to the queue which is represented as linked list
            bestPathList.add(bestLocation); // adding locations to the path list
            int bestBtnId = bestLocation.getButtonId();
            if(getExitThread() == true){
                bestPathList.get(bestPathList.size() - 1).setVisitedState(false);
            }
            message = Message.obtain();
            message.obj = bestBtnId;
            handle.sendMessage(message);
        }
        
        public boolean containsEdge(GameButtons currBtn, GameButtons btn){
            int btnId = currBtn.getButtonId();
            int id = btn.getButtonId();
            double curr_x = currBtn.getXCoord();
            double curr_y = currBtn.getYCoord();
            double btn_x = btn.getXCoord();
            double btn_y = btn.getYCoord();
            boolean isWall = btn.getButtonIsWall();
            if(Math.ceil(Math.abs(curr_x - btn_x)) == Constants.NUMBER_OF_X_SQUARES){  // 9 to not allow path to flip over the other side for portrait orientation
                return false;
            }
            if(Math.ceil(Math.abs(curr_y - btn_y)) == Constants.NUMBER_OF_Y_SQUARES){  // 9 to not allow path to flip over the other side for land orientation
                return false;
            }
            if(Math.abs(btnId - id) == Constants.NUMBER_ONE_SQUARE && isWall == false){ // 1 checking if square (tile) is a wall
                return true;
            }
            else if(Math.abs(btnId - id) == Constants.NUMBER_OF_Y_SQUARES && isWall == false){  // 10 checking if square (tile) is a wall
                return true;
            }
            return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        boolean game_start = getGameStart();
        boolean game_state = getGameState();
        boolean game_reset = getResetGame();
        setExitThread(true);  // to end the thread so that on rotation the new thread with saved states can start
        savedInstanceState.putBoolean(Constants.GAME_START, game_start);
        savedInstanceState.putBoolean(Constants.GAME_STATE, game_state);
        savedInstanceState.putBoolean(Constants.GAME_RESET, game_reset);
        savedInstanceState.putParcelableArrayList(Constants.GAME_BUTTON_LIST, gameButtonList);
        savedInstanceState.putParcelableArrayList(Constants.BEST_PATH_LIST, bestPathList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qmaze, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            return true;
        }
        else if(id == R.id.start_game_button){
            GameButtons startBtn = getStartButton();
            GameButtons endBtn = getEndButton();
            if(startBtn == null || endBtn == null) {  // start and end locations must be set
                Toast.makeText(getApplicationContext(), R.string.start_end_location_select, Toast.LENGTH_SHORT).show();
                return false;
            }
            if(getGameState() == true){
                /* this make sures that when the game search is in progress another thread is not lunched,
                 * that is, only one thread per puzzle search */
                Toast.makeText(getApplicationContext(), R.string.search_in_progress, Toast.LENGTH_SHORT).show();
                return false;
            }
            if(getResetGame() == false){ // this make sures that reset button must be pressed to play new game
                Toast.makeText(getApplicationContext(), R.string.reset_button_new_game, Toast.LENGTH_SHORT).show();
                return false;
            }
            else if(getGameState() == false){
                setGameState(true);
            }
            setGameStart(true);
            setExitThread(false);
            setResetGame(false);
            disableButtonClickState();
            bestPathList.add(getStartButton());
            gameThread = new Thread(new GameThread(startBtn, endBtn));
            gameThread.start();
        }
        else if(id == R.id.reset_game_button){
            if(getGameState() == true){
                Toast.makeText(getApplicationContext(), R.string.game_in_progress, Toast.LENGTH_SHORT).show();
                return false;
            }
            /* Resetting to a new game */
            Toast.makeText(getApplicationContext(), R.string.start_new_game, Toast.LENGTH_SHORT).show();
            setStartButton(null);
            setEndButton(null);
            setExitThread(true);
            setResetGame(true);
            setGameStart(false);
            onDestroy();    // calling on destroy so that the old game settings are deleted and new game repainted (squares) starts
            onCreate(null); // calling on create immediately so that new game board and start default settings are set for new game
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
