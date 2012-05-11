import java.util.Iterator;

import org.junit.Test;


public class LadderGame {

	@Test
	public void makeLadder() {
		Ladder ladder = Ladder.createLadder(4, 5);
		
		ladder.addLink(new LadderPosition(2,1), new LadderPosition(3,4));
		ladder.increase();
		ladder.addLink(new LadderPosition(1,2), new LadderPosition(4,3));
		ladder.decrease();
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
