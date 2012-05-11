import java.util.Iterator;

import org.junit.Test;


public class LadderGame {

	@Test
	public void makeLadder() {
		Ladder ladder = Ladder.createLadder(4, 5);
		
		ladder.addLink(new LadderPosition(2,1), new LadderPosition(3,4));
		ladder.increase();
		
		
		for(int i=0;i<ladder.size();i++) {
			System.out.println("----------"+i);
			Iterator<LadderPosition> it = ladder.iterator(i);
			while(it.hasNext()) {
				System.out.println(it.next());
			}
			
		}
	}
}
