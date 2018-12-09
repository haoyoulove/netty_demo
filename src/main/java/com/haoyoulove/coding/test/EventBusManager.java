//package com.haoyoulove.coding.test;
//
//import com.game.net.core.serializer.IEvent;
//import com.game.net.core.serializer.IRecevierInvoke;
//import com.game.net.core.serializer.ReceiverDefinition;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * demo
// *
// * @author bobi create by 2017-07-22 上午12:34
// **/
//@Component
//public class EventBusManager {
//    private static final int POOL_SIZE = 2;
//
//    private ExecutorService[] executors;
//
//    private Map<Class<? extends IEvent>, List<IRecevierInvoke>> recevierDefinitionMap = new HashMap<>();
//
//    public EventBusManager() {
//        this.executors = new ExecutorService[POOL_SIZE];
//        for (int i = 0; i < POOL_SIZE; i++) {
//            this.executors[i] = Executors.newSingleThreadExecutor();
//        }
//    }
//
//    /**
//     * 同步事件,触发事件后会在原线程执行 但是要注意事件循环抛出导致死循环
//     *
//     * @param event
//     */
//    public void syncSubmit(IEvent event) {
//        doSubmitEvent(event);
//    }
//
//    /**
//     * 异步事件,不在原线程池中执行的. 但是可以保证事件顺序处理
//     */
//    public void asynSubmit(final IEvent event) {
//        this.executors[Math.abs(event.getOwner() % POOL_SIZE)].submit(new Runnable() {
//            @Override
//            public void run() {
//                EventBusManager.this.doSubmitEvent(event);
//            }
//        });
//    }
//
//
//    private void doSubmitEvent(IEvent event) {
//        List<IRecevierInvoke> invokes = getReceiversByEvent(event);
//        for (IRecevierInvoke invoke : invokes) {
//            invoke.invoke(event);
//        }
//
//
//    }
//
//    private List<IRecevierInvoke> getReceiversByEvent(IEvent event) {
//        Class<? extends IEvent> clz = event.getClass();
//        List<IRecevierInvoke> recevierInvokes = this.recevierDefinitionMap.get(clz);
//        if (recevierInvokes == null || recevierInvokes.isEmpty()) {
////            LoggerUtils.warn("这个事件{}没有任何人接受", clz.getSimpleName());
//        }
//
//        return recevierInvokes;
//    }
//
//    @SuppressWarnings("unchecked")
//    public void registerReceiver(Class<? extends IEvent> clz, ReceiverDefinition definition) {
//        if (!recevierDefinitionMap.containsKey(clz)) {
//            this.recevierDefinitionMap.put(clz, new CopyOnWriteArrayList());
//        }
//        List list;
//        if (!(list = recevierDefinitionMap.get(clz)).contains(definition)) {
//            list.add(definition);
//        }
//
//    }
//}
