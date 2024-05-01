package map;

import java.util.Objects;

public class Position implements Comparable<Position> {
	private int xCoordinate;
	private int yCoordinate;
	private boolean hasMyCastle = false;

	public Position(int xValue, int yValue) {
		this.xCoordinate = xValue;
		this.yCoordinate = yValue;
	}

	public int getXCoordinate() {
		return xCoordinate;
	}

	public int getYCoordinate() {
		return yCoordinate;
	}

	public void putCastle() {
		this.hasMyCastle = true;
	}

	public boolean hasMyCastle() {
		return hasMyCastle;
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

	@Override
	public int compareTo(Position other) {
		if (this.getYCoordinate() == other.getYCoordinate()) {
			return Integer.compare(this.getXCoordinate(), other.getXCoordinate());
		}
		return Integer.compare(this.getYCoordinate(), other.getYCoordinate());
	}

}
