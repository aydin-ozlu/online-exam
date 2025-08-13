package com.aydin.exam.scoring;

import com.aydin.exam.model.EssayQuestion;
import com.aydin.exam.model.Question;

public class EssayScoring implements ScoringStrategy {
	@Override
    public double score(Question question, String answer) {
        if (!(question instanceof EssayQuestion essayQuestion)) {
            return 0;
        }

        String correctAnswer = essayQuestion.getTextAnswer();
        if (correctAnswer == null || answer == null) {
            return 0;
        }

        if (correctAnswer.trim().equalsIgnoreCase(answer.trim())) {
            return 1.0;
        } else {
            return 0;
        }
    }
}
