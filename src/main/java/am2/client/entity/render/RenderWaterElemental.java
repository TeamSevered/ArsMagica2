package am2.client.entity.render;

import am2.common.entity.EntityWaterElemental;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderWaterElemental extends RenderBiped<EntityWaterElemental>{
	private static final ResourceLocation rLoc = new ResourceLocation("arsmagica2", "textures/mobs/mobWaterElemental.png");

	public RenderWaterElemental(RenderManager renderManager){
		super(renderManager, new ModelZombie(), 0.5f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWaterElemental par1Entity){
		return rLoc;
	}
}
