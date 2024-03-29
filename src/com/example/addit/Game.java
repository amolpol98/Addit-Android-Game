package com.example.addit;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Game extends Activity {
	private static final String TAG = "Sudoku" ;
	
	public static final String KEY_DIFFICULTY =
	"com.example.sudoku.difficulty" ;
	private static final String PREF_PUZZLE = "puzzle" ;
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	protected static final int DIFFICULTY_CONTINUE = -1;
	
	private int puzzle[] = new int[9 * 9];
	
	private PuzzleView puzzleView;
	
	private final String easyPuzzle =
			"000000000" +
			"064231810" +
			"011114210" +
			"071461110" +
			"021111110" +
			"011113120" +
			"011911110" +
			"017148310" +
			"000000000" ;
//			"360000000004230800000004200" +
	//		"070460003820000014500013020" +
		//	"001900000007048300000000045" ;
	private final String mediumPuzzle =
			"650000070000506000014000005" +
			"007009000002314700000700800" +
			"500000630000201000030000097" ;
	private final String hardPuzzle =
			"009000000080605020501078000" +
			"000000700706040102004000000" +
			"000720903090301080000000600" ;
	
	private int blanks_made=32;
	
	private int[] getPuzzle(int diff) {
		String puz;
		// TODO: Continue last game
		switch (diff) {
		case DIFFICULTY_CONTINUE:
			puz = getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE,
					easyPuzzle);
			break;
		case DIFFICULTY_HARD:
			puz = hardPuzzle;
			break;
		case DIFFICULTY_MEDIUM:
			puz = mediumPuzzle;
			break;
		case DIFFICULTY_EASY:
		default:
			puz = easyPuzzle;
			break;
		}
		return fromPuzzleString(puz);
	}
	
	static private String toPuzzleString(int[] puz) {
		StringBuilder buf = new StringBuilder();
		for (int element : puz) {
			buf.append(element);
		}
		return buf.toString();
	}
	
	static protected int[] fromPuzzleString(String string) {
		int[] puz = new int[string.length()];
		for (int i = 0; i < puz.length; i++) {
			puz[i] = string.charAt(i) - '0' ;
		}
		return puz;
	}
	
	private int getTile(int x, int y) {
		return puzzle[y * 9 + x];
	}
	
	private void setTile(int x, int y, int value) {
		puzzle[y * 9 + x] = value;
	}
	
	protected int getblanksmade(){
		return blanks_made; 
	}
	
	protected void myfunction1(int x,int y){
		if(getTile(x, y)==0){
		}
	}
	
	protected int myfunction2(int x,int y,int tile){
		int neighbourSum = 0;
		if(getTile(x, y)==0){
			int startPosX = (x - 1 < 0) ? x : x-1;
			int startPosY = (y - 1 < 0) ? y : y-1;
			int endPosX =   (x + 1 > 8) ? x : x+1;
			int endPosY =   (y + 1 > 8) ? y : y+1;


			// See how many are alive
			for (int rowNum=startPosX; rowNum<=endPosX; rowNum++) {
			    for (int colNum=startPosY; colNum<=endPosY; colNum++) {
			    	if((rowNum!=x)||(colNum!=y)){
			    		neighbourSum+=getTile(rowNum, colNum);			    		
			    	}
			    }
			}
			if(neighbourSum==tile){
				for (int rowNum=startPosX; rowNum<=endPosX; rowNum++) {
				    for (int colNum=startPosY; colNum<=endPosY; colNum++) {
				    	if(getTile(rowNum, colNum)!=0){
				    		blanks_made++;
				    		setTile(rowNum, colNum, 0);
				    	}
				    }
				}				
			}
			else{
				setTile(x, y, tile);
				blanks_made--;
			}
			return 1;
		}
		else {
			return 0;
		}
	}
	
	protected int getTilevalue(int x,int y){
		return getTile(x, y);
	}
	
	protected String getTileString(int x, int y) {
		int v = getTile(x, y);
	//	Log.d(TAG, "tile's value = "+v );
		if (v == 0)
			return "" ;
		else
			return String.valueOf(v);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate" );

		int diff = getIntent().getIntExtra(KEY_DIFFICULTY,
				DIFFICULTY_EASY);
		puzzle = getPuzzle(diff);

		puzzleView = new PuzzleView(this);
		setContentView(puzzleView);
		puzzleView.requestFocus();
		
		// If the activity is restarted, do a continue next time
		getIntent().putExtra(KEY_DIFFICULTY, DIFFICULTY_CONTINUE);
	}
	// ...
		
	protected boolean setTileIfValid(int x, int y, int value) {
		int tiles[] = getUsedTiles(x, y);
		if (value != 0) {
			for (int tile : tiles) {
				if (tile == value)
				return false;
			}
		}
		setTile(x, y, value);
		return true;
	}
	
	private final int used[][][] = new int[9][9][];

	protected int[] getUsedTiles(int x, int y) {
		return used[x][y];
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Music.play(this, R.raw.game);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause" );
		Music.stop(this);
		
		// Save the current puzzle
		getPreferences(MODE_PRIVATE).edit().putString(PREF_PUZZLE,
				toPuzzleString(puzzle)).commit();
	}
}	