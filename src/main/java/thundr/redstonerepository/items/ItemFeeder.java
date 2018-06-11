package thundr.redstonerepository.items;

import thundr.redstonerepository.RedstoneRepository;

import static thundr.redstonerepository.RedstoneRepository.NAME;


//@TODO rename this to ItemEndoscopicGastrostomizer?
public class ItemFeeder extends ItemCoreRF {
    public int feederCapacity;
    public int hungerPointsMax;

    public ItemFeeder() {
        super(NAME);
        setMaxStackSize(1);
        setCreativeTab(RedstoneRepository.tabCommon);
    }

    public ItemFeeder(int hungerPointsMax, int feederCapacity) {
        super(NAME);
        setMaxStackSize(1);
        setCreativeTab(RedstoneRepository.tabCommon);
        this.hungerPointsMax = hungerPointsMax;
        this.feederCapacity = feederCapacity;
    }

}
