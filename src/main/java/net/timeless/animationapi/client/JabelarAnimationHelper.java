package net.timeless.animationapi.client;

import net.timeless.jurassicraft.client.model.ModelDinosaur;
import net.timeless.jurassicraft.common.entity.EntityTherizinosaurus;
import net.timeless.jurassicraft.common.entity.base.EntityDinosaur;
import net.timeless.unilib.client.model.json.TabulaModelHelper;
import net.timeless.unilib.client.model.tools.MowzieModelRenderer;


/**
 * @author jabelar
 *
 * This class is used to hold per-entity animation variables for use with
 * Jabelar's animation tweening system.
 */
public class JabelarAnimationHelper
{
    protected String[] modelAssetPathArray;
    protected String[] partNameArray;
    protected int[][] animationSequenceArray;
    
    protected ModelDinosaur modelTarget;

    protected ModelDinosaur[] modelArray; 

    protected MowzieModelRenderer[][] modelRendererArray;
    protected MowzieModelRenderer[] passedInModelRendererArray;
    protected MowzieModelRenderer[] targetPose;

    public float[][] rotationArray ;
    public float[][] positionArray ;
    public float[][] offsetArray ;

    protected int numParts;
    protected int currentSequenceStep;
    protected int currentSequenceStepModifier; // this is used to desync entities of same type
    public int numSequenceSteps;
    public int targetModelIndex; 
    public int stepsInTween;
    public int currentTweenStep;
    public boolean finishedTween = false;
    protected boolean randomSequence = false;
    
    public JabelarAnimationHelper(ModelDinosaur parModel, String[] parAssetPathArray, String[] parPartNameArray, int[][] parAnimSequenceArray, boolean parRandomInitialSequence, boolean parRandomSequence)
    {
        // transfer static animation info to instance
        modelAssetPathArray = parAssetPathArray;
        partNameArray = parPartNameArray;
        animationSequenceArray = parAnimSequenceArray;
        randomSequence = parRandomSequence;

        modelTarget = getTabulaModel(modelAssetPathArray[0], 0);
        
        numParts = parPartNameArray.length;
        setNumSequenceSteps(animationSequenceArray.length);
        
        passedInModelRendererArray = new MowzieModelRenderer[numParts];
        convertPassedInModelToModelRendererArray(parModel);

        // initialize custom models array
        modelArray = new ModelDinosaur[modelAssetPathArray.length];
        for (int i = 0; i < modelAssetPathArray.length; i++)
        {
            modelArray[i] = getTabulaModel(modelAssetPathArray[i], 0);
        }

        // initialize custom model renderer arrays (first index is model, second is part within model)
        modelRendererArray = new MowzieModelRenderer[modelAssetPathArray.length][numParts];
        for (int i = 0; i < modelAssetPathArray.length; i++)
        {
            // DEBUG
            if (partNameArray == null) System.out.println("Part name array is null");
            if (modelRendererArray[i] == null) System.out.println("model renderer array element i is null");
            if (modelRendererArray == null) System.out.println("model renderer array is null");
            modelRendererArray[i] = new MowzieModelRenderer[numParts];
            // fill in the model renderer arrays
            for (int j = 0; j < numParts; j++) 
            {
                // fill in custom pose arrays
                modelRendererArray[i][j] = modelArray[i].getCube(partNameArray[j]);
            }
        }
        
        rotationArray = new float[numParts][3];
        positionArray = new float[numParts][3];
        offsetArray = new float[numParts][3];
        for (int i = 0; i < numParts; i++)
        {
        	for (int j = 0; j < 3; j++)
        	{
        		rotationArray[i][j] = 0.0F;
        		positionArray[i][j] = 0.0F;
        		offsetArray[i][j] = 0.0F;
        	}
        }
        copyModelRendererArrayToCurrent(passedInModelRendererArray);
 
        if (parRandomInitialSequence)
        {
            setRandomSequenceStep();
        }
        else
        {
            currentSequenceStep = 0;
        }
        targetModelIndex = animationSequenceArray[currentSequenceStep][0]; 
        numSequenceSteps = animationSequenceArray.length;
        stepsInTween = animationSequenceArray[currentSequenceStep][1];
        currentTweenStep = 0;
        finishedTween = false;

        targetPose = modelRendererArray[targetModelIndex];
    }
    
    public void copyModelRendererArrayToCurrent(MowzieModelRenderer[] parModelRendererArray)
    {
        for (int i = 0; i < numParts; i++)
        {
            rotationArray[i][0] = parModelRendererArray[i].rotateAngleX;
            rotationArray[i][1] = parModelRendererArray[i].rotateAngleY;
            rotationArray[i][2] = parModelRendererArray[i].rotateAngleZ;
            positionArray[i][0] = parModelRendererArray[i].rotationPointX;
            positionArray[i][1] = parModelRendererArray[i].rotationPointY;
            positionArray[i][2] = parModelRendererArray[i].rotationPointZ;
            offsetArray[i][0] = parModelRendererArray[i].offsetX;
            offsetArray[i][1] = parModelRendererArray[i].offsetY;
            offsetArray[i][2] = parModelRendererArray[i].offsetZ;
        }           
    }
    
    public void performJabelarAnimations(ModelDinosaur parModel, float f, float f1, float rotation, float rotationYaw, float rotationPitch, float partialTicks, EntityTherizinosaurus parEntity)
    {
        convertPassedInModelToModelRendererArray(parModel);
        
        // initialize current pose arrays if first tick
        if (parEntity.ticksExisted <= 10)
        {
        	// DEBUG
        	System.out.println("Initializing current model array for new enitity with passed in value like "+passedInModelRendererArray[0].rotateAngleX);
            copyModelRendererArrayToCurrent(passedInModelRendererArray);
            setNextTween(parEntity);
        }
        
        performNextTweenStep(parEntity, passedInModelRendererArray, true);
        
        // check for end of animation and set next target in sequence
        if (finishedTween) 
        {
            handleFinishedTween(parEntity);
        }
    }
    
    protected void convertPassedInModelToModelRendererArray(ModelDinosaur parModel)
    {
        for (int i = 0; i < numParts; i++) 
        {
            passedInModelRendererArray[i] = parModel.getCube(partNameArray[i]); 

            // DEBUG
            if (passedInModelRendererArray[i] == null) 
            {
                System.out.println(partNameArray[i]+" parsed as null");
            }
        }
    }

    protected void setNextTween(EntityDinosaur parEntity)
    {              
        targetModelIndex = 
                animationSequenceArray[getSequenceStep()][0];
        targetPose = modelRendererArray[targetModelIndex];
        stepsInTween = animationSequenceArray[getSequenceStep()][1];
        currentTweenStep = 0;
//        // DEBUG
//        System.out.println("set next tween for entity id = "+parEntity.getEntityId()+" steps in tween = "+currentAnimation.get(parEntity.getEntityId()).stepsInTween);
    }
   
    protected void performNextTweenStep(EntityDinosaur parEntity, MowzieModelRenderer[] parPassedInModelRendererArray, boolean parInertial)
    {
        // tween the passed in model towards target pose
        for (int i = 0; i < numParts; i++)
        {
            if (parInertial)
            {
                nextInertialTweenRotations(parEntity, parPassedInModelRendererArray, i);
                nextInertialTweenPositions(parEntity, parPassedInModelRendererArray, i);
                nextInertialTweenOffsets(parEntity, parPassedInModelRendererArray, i);  
            }
            else
            {
                nextTweenRotations(parEntity, parPassedInModelRendererArray, i);
                nextTweenPositions(parEntity, parPassedInModelRendererArray, i);
                nextTweenOffsets(parEntity, parPassedInModelRendererArray, i);  
            }
        }
        
        // update current position tracking arrays
        copyModelRendererArrayToCurrent(parPassedInModelRendererArray);
        
        incrementTweenStep();        
    }

    /*
     * Linear tween of the rotateAngles between current angles and target angles
     */
    protected void nextTweenRotations(EntityDinosaur parEntity, MowzieModelRenderer[] parPassedInModelRendererArray, int parPartIndex)
    {
//        // DEBUG
//    	if (parPartIndex == partIndexFromName("Neck 4"))
//    	{
//    		System.out.println("For entity ID = "+parEntity.getEntityId()+" current rotation = "+rotationArray[parPartIndex][0]);
//    	}
    
        parPassedInModelRendererArray[parPartIndex].rotateAngleX =
                rotationArray[parPartIndex][0] + 
                (targetPose[parPartIndex].rotateAngleX - rotationArray[parPartIndex][0])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[parPartIndex].rotateAngleY =
                rotationArray[parPartIndex][1] + 
                (targetPose[parPartIndex].rotateAngleY - rotationArray[parPartIndex][1])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[parPartIndex].rotateAngleZ =
                rotationArray[parPartIndex][2] + 
                (targetPose[parPartIndex].rotateAngleZ - rotationArray[parPartIndex][2])
                / (stepsInTween - currentTweenStep);
    }

    /*
     * Linear tween of the rotatePoints between current positions and target positions
     */
    protected void nextTweenPositions(EntityDinosaur parEntity, MowzieModelRenderer[] parPassedInModelRendererArray, int parPartIndex)
    {
        parPassedInModelRendererArray[parPartIndex].rotationPointX =
                positionArray[parPartIndex][0] + 
                (targetPose[parPartIndex].rotationPointX - positionArray[parPartIndex][0])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[parPartIndex].rotationPointY =
                positionArray[parPartIndex][1] + 
                (targetPose[parPartIndex].rotationPointY - positionArray[parPartIndex][1])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[parPartIndex].rotationPointZ = 
                positionArray[parPartIndex][2] + 
                (targetPose[parPartIndex].rotationPointZ - positionArray[parPartIndex][2])
                / (stepsInTween - currentTweenStep);
    }

    /*
     * Linear tween of the offsets between current offsets and target offsets
     */
    protected void nextTweenOffsets(EntityDinosaur parEntity, MowzieModelRenderer[] parPassedInModelRendererArray, int partPartIndex)
    {
        parPassedInModelRendererArray[partPartIndex].offsetX =
                offsetArray[partPartIndex][0] + 
                (targetPose[partPartIndex].offsetX - offsetArray[partPartIndex][0])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[partPartIndex].offsetY =
                offsetArray[partPartIndex][1] + 
                (targetPose[partPartIndex].offsetY - offsetArray[partPartIndex][1])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[partPartIndex].offsetZ =
                offsetArray[partPartIndex][2] + 
                (targetPose[partPartIndex].offsetZ - offsetArray[partPartIndex][2])
                / (stepsInTween - currentTweenStep);
    }
    /*
     * Inertial tween of the rotateAngles between current angles and target angles
     */
    protected void nextInertialTweenRotations(EntityDinosaur parEntity, MowzieModelRenderer[] parPassedInModelRendererArray, int parPartIndex)
    {
//        // DEBUG
//      if (parPartIndex == partIndexFromName("Neck 4"))
//      {
//          System.out.println("For entity ID = "+parEntity.getEntityId()+" current rotation = "+rotationArray[parPartIndex][0]);
//      }
        
        float inertialFactor = 1.0F;
        if ((currentTweenStep < (stepsInTween * 0.25F)) && (currentTweenStep >= (stepsInTween - stepsInTween * 0.25F)))
        {
            inertialFactor = 0.5F;
        }
        else
        {
            inertialFactor = 1.5F;
        }
    
        parPassedInModelRendererArray[parPartIndex].rotateAngleX =
                rotationArray[parPartIndex][0] + inertialFactor *
                (targetPose[parPartIndex].rotateAngleX - rotationArray[parPartIndex][0])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[parPartIndex].rotateAngleY =
                rotationArray[parPartIndex][1] + inertialFactor *
                (targetPose[parPartIndex].rotateAngleY - rotationArray[parPartIndex][1])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[parPartIndex].rotateAngleZ =
                rotationArray[parPartIndex][2] + inertialFactor *
                (targetPose[parPartIndex].rotateAngleZ - rotationArray[parPartIndex][2])
                / (stepsInTween - currentTweenStep);
    }

    /*
     * Inertial tween of the rotatePoints between current positions and target positions
     */
    protected void nextInertialTweenPositions(EntityDinosaur parEntity, MowzieModelRenderer[] parPassedInModelRendererArray, int parPartIndex)
    {
        
        float inertialFactor = 1.0F;
        if ((currentTweenStep < (stepsInTween * 0.25F)) && (currentTweenStep >= (stepsInTween - stepsInTween * 0.25F)))
        {
            inertialFactor = 0.5F;
        }
        else
        {
            inertialFactor = 1.5F;
        }
        
        parPassedInModelRendererArray[parPartIndex].rotationPointX =
                positionArray[parPartIndex][0] + inertialFactor *
                (targetPose[parPartIndex].rotationPointX - positionArray[parPartIndex][0])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[parPartIndex].rotationPointY =
                positionArray[parPartIndex][1] + inertialFactor *
                (targetPose[parPartIndex].rotationPointY - positionArray[parPartIndex][1])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[parPartIndex].rotationPointZ = 
                positionArray[parPartIndex][2] + inertialFactor *
                (targetPose[parPartIndex].rotationPointZ - positionArray[parPartIndex][2])
                / (stepsInTween - currentTweenStep);
    }

    /*
     * Inertial tween of the offsets between current offsets and target offsets
     */
    protected void nextInertialTweenOffsets(EntityDinosaur parEntity, MowzieModelRenderer[] parPassedInModelRendererArray, int partPartIndex)
    {
        
        float inertialFactor = 1.0F;
        if ((currentTweenStep < (stepsInTween * 0.25F)) && (currentTweenStep >= (stepsInTween - stepsInTween * 0.25F)))
        {
            inertialFactor = 0.5F;
        }
        else
        {
            inertialFactor = 1.5F;
        }

        parPassedInModelRendererArray[partPartIndex].offsetX =
                offsetArray[partPartIndex][0] + inertialFactor *
                (targetPose[partPartIndex].offsetX - offsetArray[partPartIndex][0])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[partPartIndex].offsetY =
                offsetArray[partPartIndex][1] + inertialFactor *
                (targetPose[partPartIndex].offsetY - offsetArray[partPartIndex][1])
                / (stepsInTween - currentTweenStep);
        parPassedInModelRendererArray[partPartIndex].offsetZ =
                offsetArray[partPartIndex][2] + inertialFactor *
                (targetPose[partPartIndex].offsetZ - offsetArray[partPartIndex][2])
                / (stepsInTween - currentTweenStep);
    }
    
    protected void handleFinishedTween(EntityDinosaur parEntity)
    {
        incrementSequenceStep();
        
        // DEBUG
        System.out.println("finished tween, next sequence step = "+currentSequenceStep);

        finishedTween = false;
        setNextTween(parEntity);
    }

    public static ModelDinosaur getTabulaModel(String tabulaModel, int geneticVariant) 
    {
        // catch the exception so you can call method with implicit superconstructor
        try
        {
            return new ModelDinosaur(TabulaModelHelper.parseModel(tabulaModel), null); // okay to use null for animator parameter as we get animator from passed-in model
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public ModelDinosaur getTabulaModel(String tabulaModel)
    {
        return getTabulaModel(tabulaModel, 0);
    }

    protected float degToRad(float degrees)
    {
        return (float) (Math.PI / 180.0F * degrees);
    }
    
    protected float radToDeg(float rads)
    {
        return (float) (180.0F / Math.PI * rads);
    }
    
    protected int partIndexFromName(String parName)
    {
        int theIndex = -1;
        for (int i = 0; i < partNameArray.length; i++)
        {
            if (partNameArray[i].toLowerCase().equals(parName.toLowerCase()))
            {
                theIndex = i;
            }
        }
        return theIndex;
    }

    /*
     * Chainable methods
     */
    public JabelarAnimationHelper setNumSequenceSteps(int parNumStepsInSequence)
    {
        // DEBUG
        System.out.println("Setting number of sequence steps to "+parNumStepsInSequence);
        numSequenceSteps = parNumStepsInSequence;
        return this;
    }
    
    public void setRandomSequenceStep()
    {
        currentSequenceStep = ((int)Math.floor(Math.random()*numSequenceSteps));
    }
    
    // boolean returned indicates if sequence was finished
    public boolean incrementSequenceStep()
    {       
        // increment current sequence step
        currentSequenceStep++;
        // check if finished sequence
        if (currentSequenceStep >= numSequenceSteps)
        {
            currentSequenceStep = 0;
            return true;
        }
//        // DEBUG
//        System.out.println("Next pose is sequence step = "+currentSequenceStep);
        return false;
    }
    
    // boolean returned indicates if tween was finished
    public boolean incrementTweenStep()
    {
        currentTweenStep++;
//      // DEBUG
//      System.out.println("current tween step = "+currentTweenStep);
        if (currentTweenStep >= stepsInTween)
        {
            finishedTween = true;
        }
        return finishedTween;
    }
    
    // this getter method includes any sequence step modifier
    public int getSequenceStep()
    {
        return currentSequenceStep;
//        return (currentSequenceStep + currentSequenceStepModifier)%numStepsInSequence;
    }
}