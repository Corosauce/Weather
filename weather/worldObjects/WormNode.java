package weather.worldObjects;

import weather.blocks.MovingBlock;
import net.minecraft.src.World;

public class WormNode
{
    public WormNode nextNode;
    public WormNode prevNode;
    public MovingBlock bodyPiece;
    public World worldRef;
}
