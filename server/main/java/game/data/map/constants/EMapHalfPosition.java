package game.data.map.constants;

public enum EMapHalfPosition {
	ORIGINAL, SHIFTED;

	public EMapHalfPosition getOpposite() {
		if (this.equals(ORIGINAL)) {
			return SHIFTED;
		} else {
			return ORIGINAL;
		}
	}
}
