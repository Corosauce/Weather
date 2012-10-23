package weather.storm;

class StormConfig
{
    public int TYPE_SPOUT = 0;
    public int TYPE_TORNADO = 1;
    public int TYPE_HURRICANE = 2;
    public int type = 1;
    public float tornadoInitialSpeed;
    public float tornadoPullRate;
    public float tornadoLiftRate;
    public int relTornadoSize;
    public int tornadoBaseSize;
    public float tornadoWidthScale;
    public double grabDist = 120.0D;
    public int tornadoTime = 1500;
}
