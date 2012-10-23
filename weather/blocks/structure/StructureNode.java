package weather.blocks.structure;

public class StructureNode {

	public int relX;
	public int relY;
	public int relZ;
	
	public int id;
	public int meta;
	
	public boolean needBuild;
	public boolean render;
	
	public StructureNode(int parX, int parY, int parZ, int parID, int parMeta) {
		relX = parX;
		relY = parY;
		relZ = parZ;
		id = parID;
		meta = parMeta;
		
		render = true;
		needBuild = true;
	}
	
}
