
public class LadderPosition {
	public int ladder;
	public int position;

	public LadderPosition(int ladder, int position) {
		this.ladder = ladder;
		this.position = position;
	}

	@Override
	public String toString() {
		return "LP("+ladder + ","+position+")";
	}
}
