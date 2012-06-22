package processing.android.multitouch;

import processing.core.*; 
import android.view.MotionEvent; 
import java.util.*; 

/**
 * Multitouch event example for android using Eclipse and Processing.
 * Copyright (c) 2012 Alex Sanchez. BSD-Licensed.
 * @author Alex Sanchez Brotons  <kniren@gmail.com>
 */
public class AndroidMT extends PApplet {
	/* ============================================================
	 * 	Constants
	 * ============================================================ */
	final int INVALID_POINTER_ID = -1;
	/* ============================================================
	 * 	Variables
	 * ============================================================ */
	private HashedList pointList;

	/**
	 * We create the Hash list that will store the objects that 
	 * contain the info of our touch events.
	 */
	public void setup() {
	  stroke(255); 
	  pointList = new HashedList();
	}

	/**
	 * We fill the background and draw all the events in the list
	 * each time the screen is refreshed.
	 */
	public void draw() {
	  background(18);
	  pointList.drawInfo();
	}

	/**
	 * This methods handles the touch events. If some MotionEvent
	 * is happening, the method get the position and the id of the
	 * event and do the pertinent actions in each case.
	 */
	public boolean surfaceTouchEvent(MotionEvent me) {
		int action = me.getAction(); 
		float x    = me.getX();
		float y    = me.getY();
		int index  = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;	
		int id     = me.getPointerId(index);
		
	    switch (action & MotionEvent.ACTION_MASK) {
	    case MotionEvent.ACTION_DOWN: {
		    pointList.insert(id, x, y);
		    break;
	    }  
	   
        case MotionEvent.ACTION_UP: {
        	pointList.delete(id);
            break;
        }

	    case MotionEvent.ACTION_MOVE: {
	        int numPointers = me.getPointerCount();
	        for (int i=0; i < numPointers; i++) {
	          id = me.getPointerId(i);
	          x  = me.getX(i);
	          y  = me.getY(i);
			  pointList.update(id, x, y);
	        }
		    break;
	    } 
        
	    case MotionEvent.ACTION_POINTER_DOWN: {
		    pointList.insert(id, x, y);
	    	break;
	    }	   
        
        case MotionEvent.ACTION_POINTER_UP: {
        	
        	pointList.delete(id);
            break;
        }

        case MotionEvent.ACTION_CANCEL: {
        	pointList.clearMe();
            id = INVALID_POINTER_ID;
            break;
        }
	    }
	    
	    return super.surfaceTouchEvent(me);
	}
	/**
	 * This class stores all the touch points objects in a Hash List
	 * and manage them.
	 */
	class HashedList {
		private Map<Integer,Points> hashList;

		HashedList () {
			hashList = new HashMap<Integer,Points>();
		}
		
		public synchronized void drawInfo() {
	        Set<Integer> keyList = hashList.keySet();
	        Iterator<Integer> iter = keyList.iterator();
	        int cnt = 0;
	        Points anchor = null;
	        LinkedList<Points> lista = new LinkedList<Points>();
	        while(iter.hasNext()){
	        	anchor = hashList.get(iter.next());
	        	lista.add(anchor);
	        	anchor.drawIt();
	        	cnt++;
	        }
	        
	        /*
	         * We draw now all the lines between nodes
	         */
	        if (lista.size() > 1) {
	        	Object[] arList = lista.toArray();

	        	for (int i = 0; i < arList.length; i++ ) {
	        		for (int j = i+1; j < arList.length; j++) {
	        			drawLine((Points) arList[i], (Points) arList[j]);
	        		}
	        	}
	        }
	        textSize(25);
	        text("Active elements: " + cnt,10,25);

	        return;
		}

		synchronized void drawLine(Points a, Points b) {
			line(a.posX,a.posY,b.posX,b.posY);
		}

		/**
		 * Remove item with the given id from the hashed list.
		 * @param id 
		 */
		synchronized void delete(int id) {
			if ( hashList.get(id) != null )
				hashList.remove(id);
		}
		
		/**
		 * Remove all items from the list. This happens when an ACTION_CANCEL event
		 * occurs.
		 */
		synchronized void clearMe() {
			hashList.clear();
		}

		/**
		 * Check if the given ID is in the list, and if not, inserts it.
		 * @param id
		 * @param x
		 * @param y
		 */
		synchronized void insert(int id, float x, float y) {
			if ( hashList.get(id) == null )
				hashList.put(id, new Points(id,x,y));
		}
		
		/**
		 * Updates the current position of the given ID
		 * @param id
		 * @param x
		 * @param y
		 */
		synchronized void update(int id, float x, float y) {
			hashList.get(id).update(x, y);
		}
	}
	
	/**
	 * This class contains the basic attributes that we are going to use
	 **/
	class Points {
		float posX,posY;
		int pointID;
		int textSize = 12;
		
		Points(int id, float x, float y) {
			pointID = id;
			posX    = x;
			posY    = y;
		}
		
		void update(float x, float y) {
			posX = x;
			posY = y;
		}
		
		void drawIt() {

        	fill(120);
        	textSize(textSize);
        	ellipse(posX,posY,150,150);
        	text("X: " + posX + " Y: " + posY, posX-100, posY-100);
        	text("ID: " + pointID, posX-100, posY-100-textSize);
        	fill(200);
        	ellipse(posX,posY,20,20);
		}
	}
	
}
