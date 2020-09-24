package thaumcraft.api.research;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

@SuppressWarnings("Convert2Diamond")
public class ResearchCategoryList {
	
	/** Is the smallest column used on the gui. */
    public int minDisplayColumn;

    /** Is the smallest row used on the gui. */
    public int minDisplayRow;

    /** Is the biggest column used on the gui. */
    public int maxDisplayColumn;

    /** Is the biggest row used on the gui. */
    public int maxDisplayRow;
    
    /** display variables **/
    public final ResourceLocation icon;
    public final ResourceLocation background;
	
	public ResearchCategoryList(ResourceLocation icon, ResourceLocation background) {
		this.icon = icon;
		this.background = background;
	}

	//Research
	public final Map<String, ResearchItem> research = new HashMap<String,ResearchItem>();
		
		
	
	
}
