package io.github.gravetii.util;

import javafx.scene.image.Image;

import java.util.Objects;

public class GridUnit {

  private Alphabet alphabet;
  private GridPoint gridPoint;
  private String imgPath;
  private Image img;

  public GridUnit(Alphabet alphabet, GridPoint gridPoint) {
    this.alphabet = alphabet;
    this.gridPoint = gridPoint;
    this.imgPath = "images/" + this.alphabet.get() + ".png";
  }

  public Alphabet getAlphabet() {
    return alphabet;
  }

  public String getLetter() {
    return alphabet.get();
  }

  public int getPoints() {
    return alphabet.getPoints();
  }

  public int getWeight() {
    return alphabet.getWeight();
  }

  public GridPoint getGridPoint() {
    return gridPoint;
  }

  public boolean isNeighbor(GridUnit unit) {
    return this.getGridPoint().isNeighbor(unit.getGridPoint());
  }

  public Image getImage() {
    if (this.img == null) {
      this.img = new Image(ClassLoader.getSystemResourceAsStream(this.imgPath));
    }

    return this.img;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof GridUnit) {
      GridUnit unit = (GridUnit) object;
      return this.alphabet == unit.alphabet && this.gridPoint.equals(unit.gridPoint);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(alphabet, gridPoint);
  }
}
