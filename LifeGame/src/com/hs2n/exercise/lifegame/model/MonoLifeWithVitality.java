package com.hs2n.exercise.lifegame.model;

import java.util.function.Predicate;

public class MonoLifeWithVitality implements Cloneable {

    public static final int VITALITY_MAX = 3;
    private static final Predicate<Integer> INITIAL_VITALITY_RANGE = (v -> v > 0 && v <= VITALITY_MAX);

    private int vitality;

    public MonoLifeWithVitality() {
        this(VITALITY_MAX);
    }

    public MonoLifeWithVitality(int vitality) {
        if (!INITIAL_VITALITY_RANGE.test(vitality)) {
            throw new IllegalArgumentException();
        }
        this.vitality = vitality;
    }

    public boolean weaken() {
        if (vitality > 0) {
            vitality -= 1;
        }
        return vitality > 0;
    }

    public int getVitality() {
        return vitality;
    }

    @Override
    public MonoLifeWithVitality clone() {
        try {
            return getClass().cast(super.clone());
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return Integer.toString(vitality);
    }
}
