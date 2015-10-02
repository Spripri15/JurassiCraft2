package org.jurassicraft.client.model.animation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.timeless.animationapi.client.DinosaurAnimator;
import net.timeless.unilib.client.model.tools.MowzieModelRenderer;
import org.jurassicraft.client.model.ModelDinosaur;
import org.jurassicraft.common.dinosaur.DinosaurHerrerasaurus;
import org.jurassicraft.common.entity.EntityHerrerasaurus;
import org.jurassicraft.common.entity.base.EntityDinosaur;

@SideOnly(Side.CLIENT)
public class AnimationHerrerasaurus extends DinosaurAnimator
{
    public AnimationHerrerasaurus()
    {
        super(new DinosaurHerrerasaurus());
    }

    @Override
    protected void performMowzieAnimations(ModelDinosaur parModel, float f, float f1, float rotation, float rotationYaw, float rotationPitch, float partialTicks, EntityDinosaur parEntity)
    {
        EntityHerrerasaurus entity = (EntityHerrerasaurus) parEntity;

        float scaleFactor = 0.77F;
        float height = 2F * f1;

        MowzieModelRenderer head = parModel.getCube("Head");

        MowzieModelRenderer neck1 = parModel.getCube("Neck 1");
        MowzieModelRenderer neck2 = parModel.getCube("Neck 2");
        MowzieModelRenderer neck3 = parModel.getCube("Neck 3");
        MowzieModelRenderer neck4 = parModel.getCube("Neck 4");
        MowzieModelRenderer neck5 = parModel.getCube("Neck 5");
        MowzieModelRenderer neck6 = parModel.getCube("Neck 6");

        MowzieModelRenderer tail1 = parModel.getCube("Tail 1");
        MowzieModelRenderer tail2 = parModel.getCube("Tail 2");
        MowzieModelRenderer tail3 = parModel.getCube("Tail 3");
        MowzieModelRenderer tail4 = parModel.getCube("Tail 4");
        MowzieModelRenderer tail5 = parModel.getCube("Tail 5");
        MowzieModelRenderer tail6 = parModel.getCube("Tail 6");

        MowzieModelRenderer upperArmLeft = parModel.getCube("Upper Arm LEFT");
        MowzieModelRenderer upperArmRight = parModel.getCube("Upper Arm Right");

        MowzieModelRenderer lowerArmLeft = parModel.getCube("Lower Arm LEFT");
        MowzieModelRenderer lowerArmRight = parModel.getCube("Lower Arm Right");

        MowzieModelRenderer handLeft = parModel.getCube("hand left");
        MowzieModelRenderer handRight = parModel.getCube("hand right");

        MowzieModelRenderer[] leftArmParts = new MowzieModelRenderer[]{handLeft, lowerArmLeft, upperArmLeft};
        MowzieModelRenderer[] rightArmParts = new MowzieModelRenderer[]{handRight, lowerArmRight, upperArmRight};

        MowzieModelRenderer[] tailParts = new MowzieModelRenderer[]{tail6, tail5, tail4, tail3, tail2, tail1};

        MowzieModelRenderer body1 = parModel.getCube("Body 1");
        MowzieModelRenderer body2 = parModel.getCube("Body 2");
        MowzieModelRenderer body3 = parModel.getCube("Body 3");

        MowzieModelRenderer leftThigh = parModel.getCube("Left Thigh");
        MowzieModelRenderer rightThigh = parModel.getCube("Right Thigh");

        MowzieModelRenderer leftCalf1 = parModel.getCube("Left Calf 1");
        MowzieModelRenderer rightCalf1 = parModel.getCube("Right Calf 1");

        MowzieModelRenderer leftCalf2 = parModel.getCube("Left Calf 2");
        MowzieModelRenderer rightCalf2 = parModel.getCube("Right Calf 2");

        MowzieModelRenderer footLeft = parModel.getCube("Foot Left");
        MowzieModelRenderer footRight = parModel.getCube("Foot Right");

        parModel.bob(body1, 1F * scaleFactor, height, false, f, f1);
        parModel.bob(leftThigh, 1F * scaleFactor, height, false, f, f1);
        parModel.bob(rightThigh, 1F * scaleFactor, height, false, f, f1);
        parModel.bob(neck1, 1F * scaleFactor, height / 2, false, f, f1);

        parModel.walk(neck1, 1F * scaleFactor, 0.25F, false, 1F, 0.1F, f, f1);
        parModel.walk(head, 1F * scaleFactor, 0.25F, true, 1F, -0.1F, f, f1);
        parModel.walk(body1, 1F * scaleFactor, 0.1F, true, 0F, 0.05F, f, f1);

        parModel.walk(leftThigh, 0.5F * scaleFactor, 0.8F, false, 0F, 0.4F, f, f1);
        parModel.walk(leftCalf1, 0.5F * scaleFactor, 0.5F, true, 1F, 0F, f, f1);
        parModel.walk(leftCalf2, 0.5F * scaleFactor, 0.5F, false, 0F, 0F, f, f1);
        parModel.walk(footLeft, 0.5F * scaleFactor, 1.5F, true, 0.5F, 1F, f, f1);

        parModel.walk(rightThigh, 0.5F * scaleFactor, 0.8F, true, 0F, 0.4F, f, f1);
        parModel.walk(rightCalf1, 0.5F * scaleFactor, 0.5F, false, 1F, 0F, f, f1);
        parModel.walk(rightCalf2, 0.5F * scaleFactor, 0.5F, true, 0F, 0F, f, f1);
        parModel.walk(footRight, 0.5F * scaleFactor, 1.5F, false, 0.5F, 1F, f, f1);

        parModel.chainSwing(tailParts, 0.5F * scaleFactor, -0.1F, 2, f, f1);
        parModel.chainWave(tailParts, 1F * scaleFactor, -0.03F, 2, f, f1);
        parModel.chainWave(rightArmParts, 1F * scaleFactor, -0.3F, 4, f, f1);
        parModel.chainWave(leftArmParts, 1F * scaleFactor, -0.3F, 4, f, f1);

        // Idling
        int ticksExisted = entity.ticksExisted;

        parModel.chainWave(tailParts, 0.1F, -0.05F, 2, ticksExisted, 1.0F);
        parModel.walk(neck1, 0.1F, 0.07F, false, -1F, 0F, ticksExisted, 1.0F);
        parModel.walk(head, 0.1F, 0.07F, true, 0F, 0F, ticksExisted, 1.0F);
        parModel.walk(body1, 0.1F, 0.05F, false, 0F, 0F, ticksExisted, 1.0F);
        parModel.chainWave(rightArmParts, 0.1F, -0.1F, 4, ticksExisted, 1.0F);
        parModel.chainWave(leftArmParts, 0.1F, -0.1F, 4, ticksExisted, 1.0F);
        parModel.chainSwing(tailParts, 0.1F, -0.1F, 3, ticksExisted, 1.0F);

        entity.tailBuffer.applyChainSwingBuffer(tailParts);
    }
}
