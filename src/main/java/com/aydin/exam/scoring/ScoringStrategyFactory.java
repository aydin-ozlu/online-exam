package com.aydin.exam.scoring;

public class ScoringStrategyFactory {
    public static ScoringStrategy getStrategy(String questionType) {
        return switch (questionType.toLowerCase()) {
            case "multiple_choice" -> new MultipleChoiceScoring();
            case "essay" -> new EssayScoring();
            default -> (q, a) -> 0; // bilinmeyen tip
        };
    }
}
