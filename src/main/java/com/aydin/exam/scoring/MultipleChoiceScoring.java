package com.aydin.exam.scoring;

import com.aydin.exam.model.Choice;
import com.aydin.exam.model.MultipleChoiceQuestion;
import com.aydin.exam.model.Question;

public class MultipleChoiceScoring implements ScoringStrategy {

    @Override
    public double score(Question question, String answer) {
        if (!(question instanceof MultipleChoiceQuestion mcq)) {
            return 0;
        }

        try {
            int answerChoiceId = Integer.parseInt(answer);
            for (Choice choice : mcq.getChoices()) {
                if (choice.getId() == answerChoiceId) {
                	return choice.isCorrect() ? 1.0 : 0;
                }
            }
        } catch (NumberFormatException e) {
            return 0;
        }
        return 0;
    }
}
