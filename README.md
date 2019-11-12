# activiti-done
activiti请假，天数判断，审批人请假，查询历史流程，下一节点审批人，每个节点的意见（同意还是不同意），查询经过我的流程和由我发起的流程

#### 难点一：流程中的变量取不出来
发起流程的时候，会传入相关的流程变量，这些流程变量在以后的节点可以使用${}这个表达式获取到变量值，而且经过我的测试即便是使用双引号括起来也是可以取到值的，
![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/start-event.png)


![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/user-task.png)


但是就是因为这一点，导致后面查询历史变量的时候出现问题，先看下面这个图

![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/taskid-null.png)

这个是activiti的历史变量表，由于上面的操作，导致历史变量表里面taskid为null，这也导致了，查询某一个流程里面的task的变量是查询不到的，经过查询activiti的资料，activiti中的变量有三种作用域，一种是instance范围的，一种是task范围的，后来我把这个变量限制到task的时候，确实是有taskid了，但是下一个task根本取不到上一个task的值，而这是不能允许的，因为我需要上一个task的变量作为网关的判断条件

#### 解决方式：新建一张表，存储每个task的变量


![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/activiti-user.png)     

本来一开始我没有想建立中间表，但是权衡在activiti的表里面插入数据和在自己的表里面插入数据，果断选择了新建一张表，这张表里面只存储了同意与否，因为前段需要显示每个节点的审批人的意见，所以只存了这一个变量

#### 难点二：处理数据
下面的整套代码都是清洗数据的代码，大体过程是这样的，首先利用createHistoricActivityInstanceQuery()这个方法查询出这个流程的所有活动，记住活动和任务是不一样的，活动是所有的，任务很少，只有关键的点

由于节点中只有taskid，并没有任务详情，所以我使用这个方法createHistoricTaskInstanceQuery()查询出这个任务的详情是什么，为以后做准备，可能会用到这个    


-----------------华丽的分割线-----------------------
191112更新，删除了多余的代码，完整的代码在类里面可以找到，这里只是提一下思路

    @GetMapping("/user-get-one-process")
    public List<HistoricActivityInstance> userGetOneInstance(String instanceId) {
        List<HistoricActivityInstance> historicActivityInstance = processEngine.getHistoryService() // 历史任务Service
                .createHistoricActivityInstanceQuery() // 创建历史活动实例查询
                .processInstanceId(instanceId) // 指定流程实例id
                .finished() // 查询已经完成的任务
                .list();
        List listInstance = new ArrayList();
        //查询出来的所有的节点没法直接返给前端，所以使用map进行处理一遍
        historicActivityInstance.forEach(l -> {
            if ("start event".equals(l.getActivityName())) {
                //这两步是筛选出需要显示到前端的活动，由于任务没有开始节点，所有的活动又不能都显示在前端，只能是这样处理
            }
            if (!"Request approved?".equals(l.getActivityName()) && !"start event".equals(l.getActivityName())) {
                
                }
                map.put("startTime", l.getStartTime());
                map.put("endTime", l.getEndTime());
                //由于节点中只有任务id，所以采用了利用任务id查询出任务详情以及任务变量
                map.put("taskId", l.getTaskId());
                //查询任务详情
                if (l.getTaskId() != null) {
                    HistoricTaskInstance historicTaskInstance = processEngine.getHistoryService() // 历史相关Service
                            .createHistoricTaskInstanceQuery()
                            .processInstanceId(instanceId)
                            .finished()
                            .taskId(l.getTaskId())
                            .singleResult();
                    map.put("taskIdDescription", historicTaskInstance.getDescription());
                    //根据流程id和任务id锁定这个任务是同意了还是拒绝了
                    ActivitiUser ifApproved = activitiUserService.checkIfApproved();
                    map.put("taskIdIfApproved", ifApproved.getVacationApproved());
                }
            }
        });
        //1.创建运行服务对象//2.创建查询流程实例//3.获取流程实例ID//4.查询是否存在进行中的流程
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();
        //5-1.没有说明已经结束
        Map instanceFinishedFlag = new HashMap();
        if (processInstance == null) {
            instanceFinishedFlag.put("instanceId," + instanceId, "done");
            log.info("instanceId为==" + instanceId + "==的流程已结束");
        }
        //5-2.有说明正在进行
        else {
            instanceFinishedFlag.put("instanceId," + instanceId, "doing");
            log.info("instanceId为==" + instanceId + "==的流程正在运行");
        }
        listInstance.add(instanceFinishedFlag);
        return listInstance;

    }


#### 查询所有发起的流程与查询单个发起的流程

按照正常的逻辑的话，如果是查询流程的话，请假的相关信息应该在第一个start event里面，但是很可惜，取不出来，这些变量只能是在下一个user task里面可以拿到，这样想下面推导的时候就出现了这样一个场景，某人发起了一个请假申请，然后查看所有由我发起的申请，这个时候由于流程还没有走到下一步，所以获取不了传入的变量，所以显示不了数据


#### 19111更新-成型之后梳理难点

-------------这是一条华丽的分割线--------

#### 流程状态的判断以及显示    
![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/%E6%88%90%E5%9E%8B%E5%9B%BE.png)
整个请假流程分为已经结束和正在进行，其中已经结束里面又区分了是请假通过还是请假拒绝，其中请假拒绝需要显示哪个节点请假拒绝的，    
这个在技术上是这么实现的，ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(instanceId).singleResult();       
根据这个方法可以查询出来相关的流程实例，判断这个流程实例是否存在，如果存在的话，那就意味着流程正在执行，如果不存在的话，那就意味着流程已经结束，应该是流程表和历史表之间的区别。    
如果流程已经结束，我还需要判断出来究竟是同意了还是拒绝了，这个根据activiti的方法没有查询出来，    
我想过在整个流程图bpmn文件中动手脚，但是太乱了，    
所以想到了一个折中的办法，    
那就是在新增加的表里面通过查询每个节点同意与否的状态来实现判断整个的流程状态的判断，如果某一个节点的意见是拒绝的话，那就意味着整个的流程是拒绝状态，    
所以最终的结果实现就是拒绝的整个的流程状态是某个人拒绝了，但是同意的话，是没有显示某个人同意的

#### 下一个节点审批人

![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/%E4%B8%8B%E4%B8%80%E8%8A%82%E7%82%B9%E5%AE%A1%E6%89%B9%E4%BA%BA.png) 

下一节点的审批人，看起来是很简单的，但是实际操作起来是很复杂，因为查询的都是历史表里面的数据，这就意味着下一节点的数据是没有办法在历史表里面获取的，下一节点的审批人是在运行表里面，这就需要拼接数据才行。    
技术采用：    
List<Task> active = processEngine.getTaskService().createTaskQuery().processInstanceId(instanceId).active().list();    
active的方法或获取目前正在运行的节点的数据


#### 查询经过我的流程和由我发起的流程

从请假人的角度来看，我想看到由我发起的请假，，，从审批人的角度来看，我想看到我审批过的请假，这就意味着我需要记录流程的发起人以及经过这个节点的所有人，这个是利用第三张表实现的，    
所有由我发起的请假：    
![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/%E7%94%B1%E6%88%91%E5%8F%91%E8%B5%B7%E7%9A%84%E8%AF%B7%E5%81%87%E6%B5%81%E7%A8%8B.png)     
所有经过我的请假：    
![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/%E7%BB%8F%E8%BF%87%E6%88%91%E7%9A%84%E8%AF%B7%E5%81%87%E6%B5%81%E7%A8%8B.png)









