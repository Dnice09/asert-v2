package tz.go.mnrt.asert.modules.form.formsubmission.services;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;

// Helper class for score calculation
@Data
@AllArgsConstructor
public class SectionScoreResult {
    private final FormSection section;
    private final double score;
    private final double maxPossible;
    private final List<SectionScoreResult> subsectionScores;

    public double getPercentage() {
        return maxPossible > 0 ? (score / maxPossible) * 100 : 0;
    }
}
