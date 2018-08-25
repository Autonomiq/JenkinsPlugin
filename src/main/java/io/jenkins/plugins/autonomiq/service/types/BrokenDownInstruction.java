package io.jenkins.plugins.autonomiq.service.types;

public class BrokenDownInstruction {
    private String instruction;
    private String data;
    private String instructionNumber;
    private Boolean sendToTestcaseParser;
    private BrokenDownInstruction subinstructions[];
    private String columnName;
    private String status;
    private String recorderData;
    private String message;

    public BrokenDownInstruction(String instruction, String data, String instructionNumber,
                                 Boolean sendToTestcaseParser, BrokenDownInstruction[] subinstructions,
                                 String columnName, String status, String recorderData, String message) {
        this.instruction = instruction;
        this.data = data;
        this.instructionNumber = instructionNumber;
        this.sendToTestcaseParser = sendToTestcaseParser;
        this.subinstructions = subinstructions;
        this.columnName = columnName;
        this.status = status;
        this.recorderData = recorderData;
        this.message = message;
    }
    @SuppressWarnings("unused")
    public String getInstruction() {
        return instruction;
    }
    @SuppressWarnings("unused")
    public String getData() {
        return data;
    }
    @SuppressWarnings("unused")
    public String getInstructionNumber() {
        return instructionNumber;
    }
    @SuppressWarnings("unused")
    public Boolean getSendToTestcaseParser() {
        return sendToTestcaseParser;
    }
    @SuppressWarnings("unused")
    public BrokenDownInstruction[] getSubinstructions() {
        return subinstructions;
    }
    @SuppressWarnings("unused")
    public String getColumnName() {
        return columnName;
    }
    @SuppressWarnings("unused")
    public String getStatus() {
        return status;
    }
    @SuppressWarnings("unused")
    public String getRecorderData() {
        return recorderData;
    }
    @SuppressWarnings("unused")
    public String getMessage() {
        return message;
    }
}

