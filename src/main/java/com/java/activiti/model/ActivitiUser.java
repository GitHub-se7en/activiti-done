package com.java.activiti.model;

public class ActivitiUser {
    /**
     * 主键，采用之前的格式，英文数字加时间戳保证唯一性
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    private String id;

    /**
     *发起人/申请人/启动人/提交人，按照咖啡兔的博客的demo，重新建一张表而不是在activiti中的表里面存数据
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    private String userId;

    /**
     *这个人发起的流程的id，作为唯一标识
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    private String instanceId;

    /**
     *这个人的任务id，这个是为了以后查询所有和这个人相关的流程而保留的
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    private String taskId;

    /**
     *创建时间
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    private String createTime;

    /**
     *是否同意请假
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    private String vacationApproved;

    /**
     **冗余字段
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    private String spare3;

    public ActivitiUser() {
    }

    public ActivitiUser(String id, String userId, String instanceId, String createTime) {
        this.id = id;
        this.userId = userId;
        this.instanceId = instanceId;
        this.createTime = createTime;
    }

    public ActivitiUser(String id, String userId, String instanceId, String taskId, String createTime) {
        this.id = id;
        this.userId = userId;
        this.instanceId = instanceId;
        this.taskId = taskId;
        this.createTime = createTime;
    }

    public ActivitiUser(String id, String userId, String instanceId, String taskId, String createTime, String vacationApproved) {
        this.id = id;
        this.userId = userId;
        this.instanceId = instanceId;
        this.taskId = taskId;
        this.createTime = createTime;
        this.vacationApproved = vacationApproved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getVacationApproved() {
        return vacationApproved;
    }

    public void setVacationApproved(String vacationApproved) {
        this.vacationApproved = vacationApproved;
    }

    public String getSpare3() {
        return spare3;
    }

    public void setSpare3(String spare3) {
        this.spare3 = spare3;
    }

}