package io.jenkins.plugins.autonomiq.service.types;

public class BrokenDownInstruction {
    private String instr;
    private String data;
    private String instrNum;
    private Boolean sendToTestCaseParser;
    private BrokenDownInstruction subInstructions[];
    private String columnName;
    private String status;
    private String recorderData;
    private String message;
    private String screenshotPaths[];
    private String screenshotNo;
    private String stepTime;
    private String actualResult;
    private String md5sum;

    public BrokenDownInstruction(String instr, String data, String instrNum, Boolean sendToTestCaseParser,
                                 BrokenDownInstruction[] subInstructions, String columnName, String status,
                                 String recorderData, String message, String screenshotPaths[],
                                 String screenshotNo, String stepTime, String actualResult, String md5sum) {
        this.instr = instr;
        this.data = data;
        this.instrNum = instrNum;
        this.sendToTestCaseParser = sendToTestCaseParser;
        this.subInstructions = subInstructions;
        this.columnName = columnName;
        this.status = status;
        this.recorderData = recorderData;
        this.message = message;
        this.screenshotPaths = screenshotPaths;
        this.screenshotNo = screenshotNo;
        this.stepTime = stepTime;
        this.actualResult = actualResult;
        this.md5sum = md5sum;
    }

    public String getInstr() {
        return instr;
    }

    public void setInstr(String instr) {
        this.instr = instr;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getInstrNum() {
        return instrNum;
    }

    public void setInstrNum(String instrNum) {
        this.instrNum = instrNum;
    }

    public Boolean getSendToTestCaseParser() {
        return sendToTestCaseParser;
    }

    public void setSendToTestCaseParser(Boolean sendToTestCaseParser) {
        this.sendToTestCaseParser = sendToTestCaseParser;
    }

    public BrokenDownInstruction[] getSubInstructions() {
        return subInstructions;
    }

    public void setSubInstructions(BrokenDownInstruction[] subInstructions) {
        this.subInstructions = subInstructions;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecorderData() {
        return recorderData;
    }

    public void setRecorderData(String recorderData) {
        this.recorderData = recorderData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getScreenshotPaths() {
        return screenshotPaths;
    }

    public void setScreenshotPaths(String screenshotPaths[]) {
        this.screenshotPaths = screenshotPaths;
    }

    public String getScreenshotNo() {
        return screenshotNo;
    }

    public void setScreenshotNo(String screenshotNo) {
        this.screenshotNo = screenshotNo;
    }

    public String getStepTime() {
        return stepTime;
    }

    public void setStepTime(String stepTime) {
        this.stepTime = stepTime;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }
}

