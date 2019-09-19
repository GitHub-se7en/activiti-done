package com.java.activiti.model;

/**
 * @ClassName TaskRepresentation
 * @Description 每个任务所对应的实体类映射
 * @Author itw_zhaowg
 * @Date 2019/8/15 14:37
 **/
public class TaskRepresentation {
    private String id;
    private String name;
    private String processInstanceId;
    private String taskDescription;

    public TaskRepresentation() {
    }

    public TaskRepresentation(String id, String name, String processInstanceId, String taskDescription) {
        this.id = id;
        this.name = name;
        this.processInstanceId = processInstanceId;
        this.taskDescription = taskDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Override
    public String toString() {
        return "TaskRepresentation{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                '}';
    }
}
