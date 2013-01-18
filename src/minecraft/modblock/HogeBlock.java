package modblock;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;

@modclassloader.TargetClass("net.minecraft.src.Block")
public class HogeBlock extends Block {
	
	public HogeBlock(int par1, int par2) {
		
		super(par1, 1, Material.rock);
		super.setBlockName("hogeblo");
		super.setHardness(20.3F);
		super.setStepSound(soundGravelFootstep);
		
		
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
}
