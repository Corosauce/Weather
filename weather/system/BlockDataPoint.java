package weather.system;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import weather.WeatherMod;

public class BlockDataPoint
{
    public final int xCoord;
    public final int yCoord;
    public final int zCoord;
    public final int hash;
    public int index;

    //Data
    public float angle; //2D
    public float speed = 1F;
    public float depth = 0F; //for water: y -> ground, for air if used: y -> ground/water
    public float height = 0F;
    public float ySpeed = 0F;

    public boolean isWater = false;

    public World worldRef = null;

    /*
    public float totalPathDistance;
    public float distanceToNext;
    public float distanceToTarget;
    public VecPoint previous;
    public boolean isFirst;*/

    public BlockDataPoint(int i, int j, int k)
    {
        worldRef = WeatherMod.proxy.getSidesWorld();
        index = -1;
        //isFirst = false;
        xCoord = i;
        yCoord = j;
        zCoord = k;
        hash = makeHash(i, j, k);
        angle = (new Random()).nextInt(360);
        speed = 0.2F;
        updateCache();
    }

    public void updateCache()
    {
        if (worldRef == null)
        {
            worldRef = WeatherMod.proxy.getSidesWorld();
        }
        else
        {
            if (isBlockWater(worldRef.getBlockId(xCoord, yCoord, zCoord)))
            {
                isWater = true;
                //angle = calcShoreAngle();
            }
        }
    }

    public float calcShoreAngle()
    {
        int xStart = xCoord;
        int zStart = zCoord;
        /*int xFinalTop = xCoord;
        int xFinalBottom = xCoord;
        int zFinalTop = zCoord;
        int zFinalBottom = zCoord;*/
        int maxdist = 30;
        int curdist = 0;
        Boolean[] foundLand = new Boolean[4];
        int[] finalDists = new int[4];
        Arrays.fill(foundLand, Boolean.FALSE);

        while (curdist <= maxdist)
        {
            curdist++;

            if (foundLand[0] && foundLand[1] && foundLand[2] && foundLand[3])
            {
                break;
            }

            if (!foundLand[0] && !isBlockWater(worldRef.getBlockId(xCoord + curdist, yCoord, zCoord)))
            {
                foundLand[0] = true;
                finalDists[0] = curdist;
            }

            if (!foundLand[1] && !isBlockWater(worldRef.getBlockId(xCoord, yCoord, zCoord + curdist)))
            {
                foundLand[1] = true;
                finalDists[1] = curdist;
            }

            if (!foundLand[2] && !isBlockWater(worldRef.getBlockId(xCoord - curdist, yCoord, zCoord)))
            {
                foundLand[2] = true;
                finalDists[2] = curdist;
            }

            if (!foundLand[3] && !isBlockWater(worldRef.getBlockId(xCoord, yCoord, zCoord - curdist)))
            {
                foundLand[3] = true;
                finalDists[3] = curdist;
            }
        }

        double finalAngle = 0F;
        //if ((foundLand[0] || foundLand[2]) && (foundLand[1] || foundLand[3])) {
        int xComp = finalDists[0] < finalDists[2] ? finalDists[0] : 0 - finalDists[2];
        int zComp = finalDists[1] < finalDists[3] ? finalDists[1] : 0 - finalDists[3];

        if (xComp < 0)
        {
            xComp *= -1;
        }

        if (zComp < 0)
        {
            zComp *= -1;
        }

        xComp++;
        zComp++;
        //finalAngle = Math.sqrt(xComp * xComp + zComp * zComp);
        finalAngle = Math.atan(-1) * (xComp / zComp);
        finalAngle = Math.atan(xComp / zComp);
        finalAngle = finalAngle / ((float)Math.PI / 180.0F);
        finalAngle -= 90F;
        //finalAngle = Math.sqrt(xComp * xComp + zComp * zComp);
        //System.out.println("x: " + xComp + " - z: " + zComp);
        //System.out.println("finalAngle: " + finalAngle);
        //}
        return (float)finalAngle;
    }

    public boolean isBlockWater(int id)
    {
        return ((Block.blocksList[id] != null && Block.blocksList[id].blockMaterial == Material.water));
    }

    public static int makeHash(int i, int j, int k)
    {
        return j & 0xff | (i & 0x7fff) << 8 | (k & 0x7fff) << 24 | (i >= 0 ? 0 : 0x80000000) | (k >= 0 ? 0 : 0x8000);
    }

    public float distanceTo(BlockDataPoint pathpoint)
    {
        float f = pathpoint.xCoord - xCoord;
        float f1 = pathpoint.yCoord - yCoord;
        float f2 = pathpoint.zCoord - zCoord;
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof BlockDataPoint)
        {
            BlockDataPoint pathpoint = (BlockDataPoint)obj;
            return hash == pathpoint.hash && xCoord == pathpoint.xCoord && yCoord == pathpoint.yCoord && zCoord == pathpoint.zCoord;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return hash;
    }

    public boolean isAssigned()
    {
        return index >= 0;
    }

    public String toString()
    {
        return (new StringBuilder()).append(xCoord).append(", ").append(yCoord).append(", ").append(zCoord).toString();
    }
}
