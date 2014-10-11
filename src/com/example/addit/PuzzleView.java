package com.example.addit;

import java.util.Random;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Parcelable;

public class PuzzleView extends View {
	private static final String TAG = "Sudoku" ;
	
	private static final String SELX = "selX"; 
	private static final String SELY = "selY";
	private static final String VIEW_STATE = "viewState";
	private static final int ID = 42; 
	   
	private float width;    // width of one tile
	private float height;   // height of one tile
	private int selX;       // X index of selection
	private int selY;       // Y index of selection
	private final Rect selRect = new Rect();
	float x1, x2, y1, y2, dx, dy;
	float x1_2, x2_2, y1_2, y2_2;
	Random ran;
	int[] nextdigits = new int[3];  
	int tiles_finished;
	
	private final Game game;
	public PuzzleView(Context context) {
		super(context);
		this.game = (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
        ran = new Random();
		for (int i=0; i<3; i++) {
	        int i1 = ran.nextInt(10 - 1) + 1;
	        nextdigits[i] = i1;
		}    		
		tiles_finished = 32;
		setId(ID);
	}
	// ...
	
	@Override
	protected Parcelable onSaveInstanceState() { 
		Parcelable p = super.onSaveInstanceState();
	    Log.d(TAG, "onSaveInstanceState");
	    Bundle bundle = new Bundle();
	    bundle.putInt(SELX, selX);
	    bundle.putInt(SELY, selY);
	    bundle.putParcelable(VIEW_STATE, p);
	    return bundle;
	}
	   
	@Override
	protected void onRestoreInstanceState(Parcelable state) { 
		Log.d(TAG, "onRestoreInstanceState");
	    Bundle bundle = (Bundle) state;
	    select(bundle.getInt(SELX), bundle.getInt(SELY));
	    super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
	   }
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / 9f;
		height = h / 10f;
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged: width " + width + ", height "
		+ height);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private void getRect(int x, int y, Rect rect) {
		rect.set((int) (x * width), (int) (y * height), (int) (x
				* width + width), (int) (y * height + height));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		// Draw the background...
		Paint background = new Paint();
		background.setColor(getResources().getColor(
		R.color.puzzle_background));
		canvas.drawRect(0, 0, getWidth(), getHeight()-height, background);
		
		
		// Draw the board...
		
		// Define colors for the grid lines
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));

		
		// Draw the major grid lines
		for (int i = 0; i < 10; i++) {
			canvas.drawLine(0, i * height, getWidth(), i * height,
					dark);
			canvas.drawLine(0, i * height + 1, getWidth(), i * height
					+ 1, dark);
			canvas.drawLine(i * width, 0, i * width, getHeight()-height, dark);
			canvas.drawLine(i * width + 1, 0, i * width + 1,
					getHeight()-height, dark);
		}
		
		
		// Draw the numbers...
		
		// Define color and style for numbers
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(
		R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);
		
		// Draw the number in the center of the tile
		FontMetrics fm = foreground.getFontMetrics();
		// Centering in X: use alignment (and X at midpoint)
		float x = width / 2;
		// Centering in Y: measure ascent/descent first
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				canvas.drawText(this.game.getTileString(i, j), i
					* width + x, j * height + y, foreground);
			}
		}
				
		// Draw the selection...
		Log.d(TAG, "selRect=" + selRect);
		Paint selected = new Paint();
		
        
        int startPosX = (selX - 1 < 0) ? selX : selX-1;
		int startPosY = (selY - 1 < 0) ? selY : selY-1;
		int endPosX =   (selX + 1 > 8) ? selX : selX+1;
		int endPosY =   (selY + 1 > 8) ? selY : selY+1;


		for (int rowNum=startPosX; rowNum<=endPosX; rowNum++) {
		    for (int colNum=startPosY; colNum<=endPosY; colNum++) {
		    	if((rowNum!=selX)||(colNum!=selY)){
		    		getRect(rowNum, colNum, selRect);
					selected.setColor(getResources().getColor(
					R.color.puzzle_selected));
					canvas.drawRect(selRect, selected);
		    	}
		    }
		}

		getRect(selX, selY, selRect);
        selected.setStyle(Paint.Style.STROKE);
        selected.setColor(Color.BLACK);
        selected.setStrokeWidth(5);
        canvas.drawRect(selRect, selected);
		
        Paint paint = new Paint(); 
        paint.setColor(Color.BLACK); 
        paint.setTextSize(height * 0.40f);  
        		        
		x = (width)*2;		
		y = height*9+(height*3)/4;
        canvas.drawText("Next Digits:", x, y, paint);
        
		for (int i=0; i<3; i++) {
			canvas.drawText(String.valueOf(nextdigits[i]), x+(width)*(4+i), y, paint);
		}      
	}
	
	
	private void select(int x, int y) {
		invalidate();
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "again entered here");
		
		switch(event.getAction()) {
		    case(MotionEvent.ACTION_DOWN):
		        x1 = event.getX();
		        y1 = event.getY();
		        break;
		    case(MotionEvent.ACTION_UP):
		        x2 = event.getX();
		        y2 = event.getY();
		        dx = x2-x1;
		            dy = y2-y1;

		        x1_2 = (int)(x1/width);
		        y1_2 = (int)(y1/height);
		        x2_2 = (int)(x2/width);
		        y2_2 = (int)(y2/height);
		        		
		        if((x1==x2)&&(y1==y2)){

		        	if(game.myfunction2(selX, selY,nextdigits[0])==1){
		    			for (int i=0; i<3; i++) {
		    				if(i!=2){
		    					nextdigits[i] = nextdigits[i+1];
		    				}
		    				else{
		    					nextdigits[i] = ran.nextInt(10 - 1) + 1; 
		    				}
		    			}   
		    			invalidate();
		    			if(game.getblanksmade()==81){
		    				// PRINT HERE GAME IS OVER
		    			}
		    		}
		        	break;
		        }
		            // Use dx and dy to determine the direction
		        if(Math.abs(dx) > Math.abs(dy)) {
		            if(dx>0){ 
		            	if(selX!=8)
		            		selX = selX+1;
		            	invalidate();
		            }
		            else{
		            	if(selX!=0)
		            		selX = selX-1;
		            	invalidate();
		            }
		        } else {
		            if(dy>0) {
		            	if(selY!=8)
		            		selY = selY+1;
		            	invalidate();

		            }
		            else {
		            	if(selY!=0)
		            		selY = selY-1;
		            	invalidate();
		            }
		        }
		    }
		

		Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
		return true;
	}
	
	public void setSelectedTile(int tile) {
		if (game.setTileIfValid(selX, selY, tile)) {
			invalidate();// may change hints
		} else {
			// Number is not valid for this tile         
	        Log.d(TAG, "setSelectedTile: invalid: " + tile);
	         
	        startAnimation(AnimationUtils.loadAnimation(game,
	        		R.anim.shake));
		}
	}
	
	public void myfunction3(int tile){
		game.myfunction2(selX, selY,tile);
		invalidate();
	}
}