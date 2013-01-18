package modblock;

import modclassloader.TargetClass;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;

@modclassloader.TargetClass("net.minecraft.src.Block")
public class AAAHogeBlock extends Block {
	
	public AAAHogeBlock(int par1, int par2) {
		
		super(par1, 2, Material.ground);
		super.setBlockName("hogehoge");
		super.setHardness(200.3F);
		super.setStepSound(soundGravelFootstep);
		
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

}
