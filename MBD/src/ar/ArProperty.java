package ar;

public interface ArProperty extends ArCloneable {
	
	public void GeneratePath(String path);
	
	public String GetGeneratedPath();
	
	public String ToScript();
	
}
