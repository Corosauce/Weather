package weather;


public class Wind
{
    public float posX;
    public float posY;
    public float posZ;

    public float motionX;
    public float motionY;
    public float motionZ;

    public float direction;
    public float directionGust;
    public float directionBeforeGust;
    public float directionSmooth;
    public float directionSmoothWaves;

    public float strength;
    public float strengthAdjSpeed = 0.1F;
    public float strengthTarget;
    public float strengthSmooth;

    public float yDirection;
    public float yStrength;

    public Wind()
    {
    }
}
