package com.macortesn.androidtictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AndroidTicTacToeActivity extends AppCompatActivity {
    private TicTacToeGame mGame;
    private Button mBoardButtons[];
    private TextView mInfoTextView;
    private TextView mInfoEmptyTextView;
    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_RESET_ID = 2;
    private boolean mGameOver;

    private BoardView mBoardView;



    private TextView mNumberHuman;
    private TextView mNumberTie;
    private TextView mNumberAndroid;
    private TextView mEmpty;



    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;
    private MediaPlayer mStart;

    private int mAndroidWins;
    private int mHumanWins;
    private int mTies;
    private boolean mGoFirst;
    private boolean mInitialSound;

    private SharedPreferences mPrefs;


    private boolean mSoundOn;


    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computer);
        mStart = MediaPlayer.create(getApplicationContext(), R.raw.inicio);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
        mStart.release();
    }

    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate();   // Redraw the board
            return true;
        }
        return false;
    }
    private void results(){
        int winner = mGame.checkForWinner();

        if (winner == 0 && mGoFirst){
            mInfoTextView.setText("It's Android turn.");
            winner = mGame.checkForWinner();
            mGoFirst=false;
        }
        else if (winner == 0 && !mGoFirst){


            mInfoTextView.setText("It's your turn.");
            winner = mGame.checkForWinner();
            mGoFirst=true;
        }
        else if (winner == 1) {
            mTies++;
            mNumberTie.setText("Ties : " + mTies);
            mInfoTextView.setText("It's a tie!");
            mGameOver=true;
        }
        else if (winner == 2) {
            mHumanWins++;
            mNumberHuman.setText("Human : " + Integer.toString(mHumanWins));
            String defaultMessage = getResources().getString(R.string.result_human_wins);
            mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
            mGameOver=true;
        }
        else {
            mAndroidWins++;
            mNumberAndroid.setText("Android : " + mAndroidWins);
            mInfoTextView.setText("Android won!");
            mGameOver=true;
        }

    }

    private void resetScore(){

        mTies=0;
        mNumberTie.setText("Ties : " + mTies);

        mHumanWins=0;
        mNumberHuman.setText("Human : " + mHumanWins);

        mAndroidWins=0;
        mNumberAndroid.setText("Android : " + mAndroidWins);
        //mEmpty.setText("Sonido : " + Boolean.toString(mSoundOn));


    }




    private View.OnTouchListener mTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, MotionEvent event) {

            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            Handler handler = new Handler();

            v.setEnabled(false);
            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos))	{
                v.setEnabled(false);
                if(mSoundOn)
                    mHumanMediaPlayer.start( );

                // If no winner yet, let the computer make a move

                int winner = mGame.checkForWinner( );

                if( winner == 0 ){
                    results();

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            int move = mGame.getComputerMove();
                            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                            if(mSoundOn)
                                mComputerMediaPlayer.start( );
                            mBoardView.invalidate();
                            results();
                        }
                    }, 2000);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            v.setEnabled(true);
                        }
                    }, 4000);

                }
                else {

                    mBoardView.invalidate();
                    results();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            v.setEnabled(true);
                        }
                    }, 2000);

                }

            }
            v.setEnabled(false);
            return false;
        }
    };

    private void startNewGame(){


        mNumberTie.setText("Ties : " + mTies);

        mNumberHuman.setText("Human : " + mHumanWins);

        mNumberAndroid.setText("Android : " + mAndroidWins);
        //mEmpty.setText("Sonido : " + Boolean.toString(mSoundOn));


        mInitialSound=true;

        mGame.clearBoard();
        mBoardView.invalidate();
        mGameOver=false;
        mGoFirst=true;
        mInfoTextView.setText("You go first.");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (mInitialSound) {
                    if(mSoundOn)
                        mStart.start();
                    mInitialSound = false;
                }
            }
        }, 2000);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.reset:
                showDialog(DIALOG_RESET_ID);
                return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CANCELED) {
// Apply potentially new settings
            mSoundOn = mPrefs.getBoolean("sound", true);
            String difficultyLevel = mPrefs.getString("difficulty_level",
            getResources().getString(R.string.difficulty_harder));
            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case DIALOG_DIFFICULTY_ID:

                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                // selected is the radio button that should be selected.

                int selected = 0;
                TicTacToeGame.DifficultyLevel selectedDif = mGame.getDifficultyLevel();
                if(selectedDif == TicTacToeGame.DifficultyLevel.Easy)
                    selected = 0;
                else if(selectedDif == TicTacToeGame.DifficultyLevel.Harder)
                    selected = 1;
                else
                    selected = 2;

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();   // Close dialog

                                // TODO: Set the diff level of mGame based on which item was selected.
                                if(item == 0 ){
                                    startNewGame();
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                }

                                else if(item == 1){
                                    startNewGame();
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                }

                                else
                                {
                                    startNewGame();
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);


                                }


                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();

                break;
            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;

            case DIALOG_RESET_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToeActivity.this.resetScore();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;
        }
        return dialog;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putBoolean("mInitialSound", mInitialSound);
        outState.putInt("mHumanWins", Integer.valueOf(mHumanWins));
        outState.putInt("mAndroidWins", Integer.valueOf(mAndroidWins));
        outState.putInt("mTies", Integer.valueOf(mTies));
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putBoolean("mGoFirst", mGoFirst);
        outState.putBoolean("sound", mSoundOn);

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mInitialSound=savedInstanceState.getBoolean("mInitialSound");
        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        mHumanWins = savedInstanceState.getInt("mHumanWins");
        mAndroidWins = savedInstanceState.getInt("mAndroidWins");
        mTies = savedInstanceState.getInt("mTies");
        mGoFirst = savedInstanceState.getBoolean("mGoFirst");
        mSoundOn = savedInstanceState.getBoolean("mSoundOn");
    }

    private void displayScores() {
        mNumberAndroid.setText("Android : " + Integer.toString(mAndroidWins));
        mNumberHuman.setText("Human : " + Integer.toString(mHumanWins));
        mNumberTie.setText("Ties : " + Integer.toString(mTies));
        //mEmpty.setText("Sonido : " + Boolean.toString(mSoundOn));
    }


    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mHumanWins);
        ed.putInt("mAndroidWins", mAndroidWins);
        ed.putInt("mTies", mTies);

        //for(int i=0; i<9;i++){
            //ed.putString(String.valueOf(i+1) , Character.toString(mGame.getBoardOccupant(i)));
        //}

        ed.commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        mGame = new TicTacToeGame();

        mPrefs=getSharedPreferences("ttt_prefs",MODE_PRIVATE);


        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

        // Restore the scores
        mHumanWins = mPrefs.getInt("mHumanWins", 0);
        mAndroidWins = mPrefs.getInt("mAndroidWins", 0);
        mTies = mPrefs.getInt("mTies", 0);



        mNumberHuman = (TextView) findViewById(R.id.humani);
        mNumberTie = (TextView) findViewById(R.id.tiei);
        mNumberAndroid = (TextView) findViewById(R.id.androidi);
        mEmpty = (TextView) findViewById(R.id.empty);

        mInfoTextView = (TextView) findViewById(R.id.information);

        mBoardView=(BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener( mTouchListener );
        startNewGame();




    }


    public void StartGameOnline(View view){
        Intent intent =new Intent(this,OnlineLoginActivity.class);
        startActivity(intent);
    }
}
