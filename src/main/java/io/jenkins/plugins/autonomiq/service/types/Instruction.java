package io.jenkins.plugins.autonomiq.service.types;

public class Instruction {
    private String instr;
    private String data;
    private String recorderData;
    private String columnName;

    public Instruction(String instr, String data, String recorderData, String columnName) {
        this.instr = instr;
        this.data = data;
        this.recorderData = recorderData;
        this.columnName = columnName;
    }
    @SuppressWarnings("unused")
    public String getInstr() {
        return instr;
    }
    @SuppressWarnings("unused")
    public String getData() {
        return data;
    }
    @SuppressWarnings("unused")
    public String getRecorderData() {
        return recorderData;
    }
    @SuppressWarnings("unused")
    public String getColumnName() {
        return columnName;
    }
}

