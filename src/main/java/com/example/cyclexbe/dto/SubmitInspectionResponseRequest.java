package com.example.cyclexbe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class SubmitInspectionResponseRequest {

    @Valid
    @NotEmpty(message = "Answers cannot be empty")
    private List<AnswerItem> answers = new ArrayList<>();

    public SubmitInspectionResponseRequest() {}

    public SubmitInspectionResponseRequest(List<AnswerItem> answers) {
        this.answers = answers;
    }

    public List<AnswerItem> getAnswers() { return answers; }
    public void setAnswers(List<AnswerItem> answers) { this.answers = answers; }

    public static class AnswerItem {
        private Integer requirementId;
        private String text;

        public AnswerItem() {}

        public AnswerItem(Integer requirementId, String text) {
            this.requirementId = requirementId;
            this.text = text;
        }

        public Integer getRequirementId() { return requirementId; }
        public void setRequirementId(Integer requirementId) { this.requirementId = requirementId; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}