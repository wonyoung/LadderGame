package com.jssoft.ladder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class Ladder {
	private static final int MAX_LADDERS = 20;
	private Map <Integer, LadderPosition> [] matrixLadder;
	private int size;	
	private final int endPosition;

	@SuppressWarnings("unchecked")
	private Ladder(int numberOfLadders, int endPosition) {
		if (size > MAX_LADDERS)
			throw new IndexOutOfBoundsException();
		
		size = numberOfLadders;
		matrixLadder = new HashMap[MAX_LADDERS];
		for(int i=0;i<size;i++)
			matrixLadder[i] = new HashMap<Integer, LadderPosition>();
		this.endPosition = endPosition;
	}

	public static Ladder create(int ladders, int endPosition) {
		return new Ladder(ladders, endPosition);
	}

	public void addLink(LadderPosition lp1, LadderPosition lp2) {
		if (checkValid(lp1) == false)
			return;
		if (checkValid(lp2) == false)
			return;

		matrixLadder[lp1.ladder].put(lp1.position, lp2);
		matrixLadder[lp2.ladder].put(lp2.position, lp1);
	}
	
	public void addLink() {
		Random random = new Random(System.currentTimeMillis());
		
		int ladder = random.nextInt(size-1);
		int position = random.nextInt(endPosition);
		
		LadderPosition lp1 = new LadderPosition(ladder, position);
		LadderPosition lp2 = new LadderPosition(ladder+1, position);
		
		while (getLinkedPosition(lp1) != null
				|| getLinkedPosition(lp2) != null) {

			ladder = random.nextInt(size-1);
			position = random.nextInt(endPosition);
			
			lp1 = new LadderPosition(ladder, position);
			lp2 = new LadderPosition(ladder+1, position);	
		}
			
		addLink(lp1, lp2);
	}

	private boolean checkValid(LadderPosition lp) {
		return lp.ladder < size && lp.position < endPosition;
	}

	public LadderPosition getLinkedPosition(LadderPosition lp) {
		return matrixLadder[lp.ladder].get(lp.position);
	}
	
	public Set<Entry<Integer,LadderPosition>> getAllLink(int ladder) {
		return matrixLadder[ladder].entrySet();
	}

	public Iterator<LadderPosition> iterator(final int ladder) {
		return new Iterator<LadderPosition> () {
			LadderPosition current = new LadderPosition(ladder, 0);
			LadderPosition next = current;
			boolean moved = false;
			
			@Override
			public boolean hasNext() {
				return current.position < endPosition;
			}

			@Override
			public LadderPosition next() {
				if (current == null) {
					current = new LadderPosition(ladder, 0);
					return current;
				}
				
				current = next;
				next = getLinkedPosition(current);
				LadderPosition nextPositionOnSameLadder = new LadderPosition(current.ladder, current.position+1);
				if (next == null) {
					next = nextPositionOnSameLadder;
				} else {
					if (moved) {
						next = nextPositionOnSameLadder;
						moved = false;
					} else {
						moved = true;
					}
				}

				return current;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

	public int size() {
		return size;
	}
	
	public int height() {
		return endPosition;
	}

	public void increase() {
		matrixLadder[size++] = new HashMap<Integer, LadderPosition>();
		
	}
	
	public void decrease() {
		int last = size-1;
		for(int prev=0;prev<size-2;prev++) {
			Map<Integer, LadderPosition> map = matrixLadder[prev];
			Set<Entry<Integer, LadderPosition>> set = map.entrySet();
			for( Entry<Integer, LadderPosition> e : set) {
				if (e.getValue().ladder == last)
					map.remove(e.getKey());
			}
		}
		matrixLadder[--size] = null;
	}
}
