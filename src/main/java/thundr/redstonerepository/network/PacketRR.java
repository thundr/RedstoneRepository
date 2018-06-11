package thundr.redstonerepository.network;

import cofh.core.network.PacketBase;
import cofh.core.network.PacketCore;
import cofh.core.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.gui.ContainerFeeder;
import thundr.redstonerepository.items.ItemFeeder;

public class PacketRR extends PacketBase {
	public enum PacketTypes {
		ADD_FOOD
	}
	@Override
	public void handlePacket(EntityPlayer player, boolean isServer){
		try{
			int type = getByte();
			switch (PacketTypes.values()[type]){
				case ADD_FOOD:
					if(player.openContainer instanceof ContainerFeeder){
						ItemStack stack = ((ContainerFeeder) player.openContainer).getContainerStack();
						if(stack.getItem() instanceof ItemFeeder){
							ItemFeeder feeder = (ItemFeeder) stack.getItem();
							int hungerAdded = getInt();
							feeder.receiveHungerPoints(stack, hungerAdded, false);
							player.openContainer.inventorySlots.get(player.openContainer.inventorySlots.size() - 1).decrStackSize(getInt());
						}
					}
					return;
				default:
					RedstoneRepository.LOG.error("Unknown Packet Type " + type);
			}
		}
		catch (Exception e){
			RedstoneRepository.LOG.error("Packet malformed!");
			e.printStackTrace();
		}
	}

	public static void sendAddFood(int hunger, int stackSizeDec){
		PacketHandler.sendToServer(getPacket(PacketTypes.ADD_FOOD).addInt(hunger).addInt(stackSizeDec));
	}

	public static PacketBase getPacket(PacketTypes theType) {
		return new PacketRR().addByte(theType.ordinal());
	}
}
