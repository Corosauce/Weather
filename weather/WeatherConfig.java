package weather;


public class WeatherConfig
{
    public float chanceOfRainEvent = 0F;
    public float chanceOfWindEvent = 0F;
    public float chanceOfWindGustEvent = 0F;
    public float chanceOfHailEvent = 0F;

    public int rainEventTimeRand = 10000;
    public int windEventTimeRand = 700;
    public int windGustEventTimeRand = 60;
    public boolean alwaysMinWind = false;

    public float minRain = 0F;
    public float maxRain = 0F;
    public float minWind = 0F;
    public float maxWind = 0F;
    public float minHail = 0F;
    public float maxHail = 0F;

    void WeatherConfig()
    {
    }
}
