package weather.entities;

import net.minecraft.world.World;


public class WormNode
{
    public WormNode nextNode;
    public WormNode prevNode;
    public MovingBlock bodyPiece;
    public World worldRef;
}
