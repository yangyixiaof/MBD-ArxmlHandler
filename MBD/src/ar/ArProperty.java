package ar;

public interface ArProperty extends ArCloneable {
	
	public void GeneratePath();
	
	public String GetGeneratedPath();
	
	public String ToScript();
	
}
