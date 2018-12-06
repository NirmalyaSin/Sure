
package com.surefiz.screens.dashboard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChartList {

    @SerializedName("weightProgress")
    @Expose
    private WeightProgress weightProgress;
    @SerializedName("weightLoss")
    @Expose
    private WeightLoss weightLoss;
    @SerializedName("composition")
    @Expose
    private Composition composition;
    @SerializedName("BMI")
    @Expose
    private BMI bMI;
    @SerializedName("nextSubGoal")
    @Expose
    private NextSubGoal nextSubGoal;
    @SerializedName("subGoalsProgress")
    @Expose
    private SubGoalsProgress subGoalsProgress;
    @SerializedName("goals")
    @Expose
    private Goals goals;
    @SerializedName("currentCompositions")
    @Expose
    private CurrentCompositions currentCompositions;

    public WeightProgress getWeightProgress() {
        return weightProgress;
    }

    public void setWeightProgress(WeightProgress weightProgress) {
        this.weightProgress = weightProgress;
    }

    public WeightLoss getWeightLoss() {
        return weightLoss;
    }

    public void setWeightLoss(WeightLoss weightLoss) {
        this.weightLoss = weightLoss;
    }

    public Composition getComposition() {
        return composition;
    }

    public void setComposition(Composition composition) {
        this.composition = composition;
    }

    public BMI getBMI() {
        return bMI;
    }

    public void setBMI(BMI bMI) {
        this.bMI = bMI;
    }

    public NextSubGoal getNextSubGoal() {
        return nextSubGoal;
    }

    public void setNextSubGoal(NextSubGoal nextSubGoal) {
        this.nextSubGoal = nextSubGoal;
    }

    public SubGoalsProgress getSubGoalsProgress() {
        return subGoalsProgress;
    }

    public void setSubGoalsProgress(SubGoalsProgress subGoalsProgress) {
        this.subGoalsProgress = subGoalsProgress;
    }

    public Goals getGoals() {
        return goals;
    }

    public void setGoals(Goals goals) {
        this.goals = goals;
    }

    public CurrentCompositions getCurrentCompositions() {
        return currentCompositions;
    }

    public void setCurrentCompositions(CurrentCompositions currentCompositions) {
        this.currentCompositions = currentCompositions;
    }

}
