package thaumcraft.api.internal;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;


@SuppressWarnings("Convert2Diamond")
public class WeightedRandomLoot extends WeightedRandom.Item {
	
	/** The Item/Block ID to generate in the bag. */
    public final ItemStack item;

    public WeightedRandomLoot(ItemStack stack, int weight)
    {
        super(weight);
        this.item = stack;
    }
    
    public static final ArrayList<WeightedRandomLoot> lootBagCommon = new ArrayList<WeightedRandomLoot>();
    public static final ArrayList<WeightedRandomLoot> lootBagUncommon = new ArrayList<WeightedRandomLoot>();
    public static final ArrayList<WeightedRandomLoot> lootBagRare = new ArrayList<WeightedRandomLoot>();
    
}
