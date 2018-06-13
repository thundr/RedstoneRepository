package thundr.redstonerepository.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.items.ItemCoreRF;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemRingEffect extends ItemCoreRF implements IBauble {

    public ItemRingEffect() {
        super(RedstoneRepository.NAME);
    }

    @Optional.Method(modid = "baubles")
    public BaubleType getBaubleType(ItemStack itemstack){
        return BaubleType.RING;
    }

}
