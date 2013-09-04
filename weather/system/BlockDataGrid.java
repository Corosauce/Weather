package weather.system;

import java.util.HashMap;


public class BlockDataGrid
{
    public HashMap<Integer, BlockDataPoint> grid;

    public BlockDataGrid()
    {
        grid = new HashMap();
    }

    public int getHash(int i, int j, int k)
    {
        return j & 0xff | (i & 0x7fff) << 8 | (k & 0x7fff) << 24 | (i >= 0 ? 0 : 0x80000000) | (k >= 0 ? 0 : 0x8000);
    }

    public float getVecAngle(int i, int j, int k)
    {
        return getPoint(i, j, k).angle;
    }

    public float getVecSpeed(int i, int j, int k)
    {
        return getPoint(i, j, k).speed;
    }
    
    public BlockDataPoint getPoint(int i, int j, int k, boolean forceUpdate)
    {
    	
    	int hash = getHash(i, j, k);
    	//if (grid.get(hash) != null && grid.get(hash).height == 1F) {
	    	BlockDataPoint newVec = new BlockDataPoint(i, j, k);
	        grid.put(newVec.hash, newVec);
	        return newVec;
    	/*} else {
    		return getPoint(i, j, k);
    	}*/
    }

    public BlockDataPoint getPoint(int i, int j, int k)
    {
        int hash = getHash(i, j, k);

        if (!grid.containsKey(hash))
        {
            BlockDataPoint newVec = new BlockDataPoint(i, j, k);
            grid.put(newVec.hash, newVec);
            return newVec;
        }
        else
        {
            return grid.get(hash);
        }
    }

    public void newPoint(int i, int j, int k)
    {
    }

    public void tick()
    {
    }
}
