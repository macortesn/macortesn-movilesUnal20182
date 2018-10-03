package com.macortesn.androidtictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;

import android.os.Handler;
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
    private boolean mGameOver;

    private BoardView mBoardView;


    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;
    private MediaPlayer mStart;


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

        if (winner == 0) {
            mInfoTextView.setText("It's Android's turn.");

            winner = mGame.checkForWinner();
        }

        if (winner == 0)
            mInfoTextView.setText("It's your turn.");
        else if (winner == 1) {
            mInfoTextView.setText("It's a tie!");
            mGameOver=true;
        }
        else if (winner == 2) {
            mInfoTextView.setText("You won!");
            mGameOver=true;
        }
        else {
            mInfoTextView.setText("Android won!");
            mGameOver=true;
        }
    }



    private View.OnTouchListener mTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos))	{
                mHumanMediaPlayer.start( );

                // If no winner yet, let the computer make a move

                int winner = mGame.checkForWinner( );
                if( winner == 0 ){

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            int move = mGame.getComputerMove();
                            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                            mComputerMediaPlayer.start( );

                            mBoardView.invalidate();
                            results();
                        }
                    }, 2000);



                }
                else {
                    results();
                    mBoardView.invalidate();
                }

            }
            return false;
        }
    };

    private void startNewGame(){

        mGame.clearBoard();
        mBoardView.invalidate();
        mGameOver=false;
        mInfoTextView.setText("You go first.");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                mStart.start( );


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
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
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
        }
        return dialog;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);



        mInfoTextView = (TextView) findViewById(R.id.information);
        mInfoEmptyTextView = (TextView) findViewById(R.id.empty);
        mGame = new TicTacToeGame();
        mBoardView=(BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener( mTouchListener );


        startNewGame();
    }
}
