package com.aydin.exam.scoring;

import com.aydin.exam.model.Question;

public interface ScoringStrategy {
    double score(Question question, String answer);
}
