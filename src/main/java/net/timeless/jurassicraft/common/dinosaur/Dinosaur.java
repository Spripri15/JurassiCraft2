package net.timeless.jurassicraft.common.dinosaur;

import net.timeless.jurassicraft.common.entity.base.EntityDinosaur;
import net.timeless.jurassicraft.common.entity.base.EnumGrowthStage;
import net.timeless.jurassicraft.common.period.EnumTimePeriod;

public abstract class Dinosaur implements Comparable<Dinosaur>
{
    public abstract String getName(int geneticVariant);

    public abstract Class<? extends EntityDinosaur> getDinosaurClass();

    public abstract int getEggPrimaryColor();

    public abstract EnumTimePeriod getPeriod();

    public abstract int getEggSecondaryColor();

    public abstract double getBabyHealth();

    public abstract double getAdultHealth();

    public abstract double getBabySpeed();

    public abstract double getAdultSpeed();

    public abstract double getBabyStrength();

    public abstract double getAdultStrength();

    public abstract double getBabyKnockback();

    public abstract double getAdultKnockback();

    public abstract float getBabySizeX();

    public abstract float getBabySizeY();

    public abstract float getAdultSizeX();

    public abstract float getAdultSizeY();

    public abstract float getBabyEyeHeight();

    public abstract float getAdultEyeHeight();

    public abstract int getMaximumAge();

    public abstract String[] getMaleTextures(int geneticVariant, EnumGrowthStage stage);

    public abstract String[] getFemaleTextures(int geneticVariant, EnumGrowthStage stage);

    public String[] getMaleOverlayTextures(int geneticVariant, EnumGrowthStage stage)
    {
        return new String[0];
    }

    public String[] getFemaleOverlayTextures(int geneticVariant, EnumGrowthStage stage)
    {
        return new String[0];
    }

    public double getAttackSpeed()
    {
        return 0.5D;
    }

    public boolean shouldRegister()
    {
        return true;
    }

    protected String getDinosaurTexture(String subtype, int geneticVariant)
    {
        String dinosaurName = getName(geneticVariant).toLowerCase().replaceAll(" ", "_");

        String texture = "jurassicraft:textures/entities/" + dinosaurName + "/" + dinosaurName;

        if (subtype != "")
            texture += "_" + subtype;

        return texture + ".png";
    }

    protected String getDinosaurTexture(String subtype)
    {
        return getDinosaurTexture(subtype, 0);
    }

    @Override
    public int hashCode()
    {
        return getName(0).hashCode();
    }

    protected int fromDays(int days)
    {
        return (days * 24000) / 32;
    }

    @Override
    public int compareTo(Dinosaur dinosaur)
    {
        return this.getName(0).compareTo(dinosaur.getName(0));
    }

    public int getGeneticVariants()
    {
        return 1;
    }

    public boolean isMarineAnimal()
    {
        return false;
    }
}
