package kirsan122.assemblyexpansion.api.radiation;

public class Radiation implements IRadiation {
    private int radiation = 0;

    @Override
    public int getRadiation() { return this.radiation; }

    @Override
    public void setRadiation(int value) { this.radiation = Math.max(0, value); }

    @Override
    public void addRadiation(int value) { this.radiation = Math.max(0, this.radiation + value); }
}
