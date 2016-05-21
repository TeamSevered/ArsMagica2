package am2.entities.ai;

import am2.LogHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EntityAIApplyPotionOnCollide extends EntityAIBase{
	World worldObj;
	EntityCreature attacker;
	EntityLivingBase entityTarget;

	/**
	 * An amount of decrementing ticks that allows the entity to attack once the tick reaches 0.
	 */
	int attackTick;
	float field_75440_e;
	boolean field_75437_f;
	PathEntity field_75438_g;
	Class classTarget;
	private int field_75445_i;
	private PotionEffect _template;

	public EntityAIApplyPotionOnCollide(EntityCreature par1EntityLiving, Class par2Class, float par3, PotionEffect template, boolean par4){
		this(par1EntityLiving, par3, par4);
		this.classTarget = par2Class;
		this._template = template;
	}

	public EntityAIApplyPotionOnCollide(EntityCreature par1EntityLiving, float par2, boolean par3){
		this.attackTick = 0;
		this.attacker = par1EntityLiving;
		this.worldObj = par1EntityLiving.worldObj;
		this.field_75440_e = par2;
		this.field_75437_f = par3;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute(){
		EntityLivingBase var1 = this.attacker.getAttackTarget();

		if (var1 == null){
			return false;
		}else if (this.classTarget != null && !this.classTarget.isAssignableFrom(var1.getClass())){
			return false;
		}else{
			this.entityTarget = var1;
			this.field_75438_g = this.attacker.getNavigator().getPathToEntityLiving(this.entityTarget);
			return this.field_75438_g != null;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting(){
		EntityLivingBase var1 = this.attacker.getAttackTarget();
		return var1 != null && (this.entityTarget.isEntityAlive() && (!this.field_75437_f ? !this.attacker.getNavigator().noPath() : this.attacker.isWithinHomeDistance(MathHelper.floor_double(this.entityTarget.posX), MathHelper.floor_double(this.entityTarget.posY), MathHelper.floor_double(this.entityTarget.posZ))));
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting(){
		this.attacker.getNavigator().setPath(this.field_75438_g, this.field_75440_e);
		this.field_75445_i = 0;
	}

	/**
	 * Resets the task
	 */
	public void resetTask(){
		this.entityTarget = null;
		this.attacker.getNavigator().clearPathEntity();
	}

	/**
	 * Updates the task
	 */
	public void updateTask(){
		this.attacker.getLookHelper().setLookPositionWithEntity(this.entityTarget, 30.0F, 30.0F);

		if ((this.field_75437_f || this.attacker.getEntitySenses().canSee(this.entityTarget)) && --this.field_75445_i <= 0){
			this.field_75445_i = 4 + this.attacker.getRNG().nextInt(7);
			this.attacker.getNavigator().tryMoveToEntityLiving(this.entityTarget, this.field_75440_e);
		}

		this.attackTick = Math.max(this.attackTick - 1, 0);
		double var1 = (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F);

		if (this.attacker.getDistanceSq(this.entityTarget.posX, this.entityTarget.boundingBox.minY, this.entityTarget.posZ) <= var1){
			if (this.attackTick <= 0){
				this.attackTick = 20;
				Constructor<PotionEffect> ctor = null;
				try{
					ctor = (Constructor<PotionEffect>)_template.getClass().getConstructor(int.class, int.class, int.class);
				}catch (NoSuchMethodException e1){
					LogHelper.trace("Entity AI Potion On Collide Error {0}", e1.getStackTrace().toString());
				}catch (SecurityException e1){
					LogHelper.trace("Entity AI Potion On Collide Error {0}", e1.getStackTrace().toString());
				}
				if (ctor != null){
					PotionEffect pe;
					try{
						pe = ctor.newInstance(_template.getPotionID(), _template.getDuration(), _template.getAmplifier());
						this.entityTarget.addPotionEffect(pe);
					}catch (InstantiationException e){
						LogHelper.trace("Entity AI Potion On Collide Error {0}", e.getStackTrace().toString());
					}catch (IllegalAccessException e){
						LogHelper.trace("Entity AI Potion On Collide Error {0}", e.getStackTrace().toString());
					}catch (IllegalArgumentException e){
						LogHelper.trace("Entity AI Potion On Collide Error {0}", e.getStackTrace().toString());
					}catch (InvocationTargetException e){
						LogHelper.trace("Entity AI Potion On Collide Error {0}", e.getStackTrace().toString());
					}
				}
			}
		}
	}
}
