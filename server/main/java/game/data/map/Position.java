package game.data.map;

import java.util.Objects;

public class Position {
	private int xCoordinate;
	private int yCoordinate;

	public Position() {
	}

	public Position(int x, int y) {
		this.xCoordinate = x;
		this.yCoordinate = y;
	}

	public int getXCoordinate() {
		return xCoordinate;
	}

	public int getYCoordinate() {
		return yCoordinate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(xCoordinate, yCoordinate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		return xCoordinate == other.xCoordinate && yCoordinate == other.yCoordinate;
	}

}
