package weather.worldObjects;

import net.minecraft.world.World;

import weather.blocks.MovingBlock;

public class WormNode
{
    public WormNode nextNode;
    public WormNode prevNode;
    public MovingBlock bodyPiece;
    public World worldRef;
}
