package me.ghluka.camel.mixin;

import com.google.common.base.Predicates;
import me.ghluka.camel.MainMod;
import me.ghluka.camel.module.modules.combat.Reach;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

import static me.ghluka.camel.MainMod.mc;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Shadow
    private Entity pointedEntity;

    /**
     */
    @Overwrite
    public void getMouseOver(float p_getMouseOver_1_) {
        if (mc.thePlayer == null)
            return;
        final Reach reach = (Reach) MainMod.INSTANCE.moduleManager.getModuleByName("Reach");

        if (reach.getDefaultCombatPage().result())
            return;

        Entity entity = mc.getRenderViewEntity();
        if(entity != null && mc.theWorld != null) {
            mc.mcProfiler.startSection("pick");
            mc.pointedEntity = null;

            double d0 = reach.getModuleEnabled() ? reach.getMaxRange() : (double) mc.playerController.getBlockReachDistance();
            mc.objectMouseOver = entity.rayTrace(//reach.getModuleEnabled() ? reach.getBuildReachValue().get() :
                    d0, p_getMouseOver_1_);
            double d1 = d0;
            Vec3 vec3 = entity.getPositionEyes(p_getMouseOver_1_);
            boolean flag = false;
            if(mc.playerController.extendedReach()) {
                d0 = 6.0D;
                d1 = 6.0D;
            }else if(d0 > 3.0D) {
                flag = true;
            }

            if(mc.objectMouseOver != null) {
                d1 = mc.objectMouseOver.hitVec.distanceTo(vec3);
            }

            if(reach.getModuleEnabled()) {
                d1 = reach.getMaxRange();

                final MovingObjectPosition movingObjectPosition = entity.rayTrace(d1, p_getMouseOver_1_);

                if(!reach.getHitThroughWalls() && movingObjectPosition != null) d1 = movingObjectPosition.hitVec.distanceTo(vec3);
            }

            Vec3 vec31 = entity.getLook(p_getMouseOver_1_);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            this.pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0f;
            AxisAlignedBB boundingBox = entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double) f, (double) f, (double) f);
            boundingBox = boundingBox.expand(reach.getExpand(), reach.getExpand(), reach.getExpand());
            List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, boundingBox, Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (Entity entity1 : list) {
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double) f1, (double) f1, (double) f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        this.pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        if (entity1 == entity.ridingEntity && !entity.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                this.pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            this.pointedEntity = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (this.pointedEntity != null && flag && vec3.distanceTo(vec33) > (reach.getModuleEnabled() ? reach.getMaxRange() : 3.0D)) {
                this.pointedEntity = null;
                mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }

            if(this.pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null)) {
                mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec33);
                if(this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
                    mc.pointedEntity = this.pointedEntity;
                }
            }

            mc.mcProfiler.endSection();
        }
    }
}
