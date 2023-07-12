//package com.at.t3;
//
//
//import javafx.event.Event;
//import javafx.event.EventDispatcher;
//
//import java.io.IOException;
//import java.nio.file.*;
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
//public class Hi {
//
//    private void callListeners(final WatchEvent<?> event, final Path path) {
//
//        //重复注册的listener删除掉
//
//        synchronized (EventDispatcher.class){
//
//            for (Path p:listeners.keySet ()){
//
//                if(listeners.get ( p )==null || listeners.get ( p ).size ()==0){
//
//                    listeners.remove ( p );
//
//                }
//
//            }
//
//        }
//
//        boolean matchedOne = false;
//
//        for (Map.Entry<Path, List<WatchEventListener>> list : listeners.entrySet()) {
//
//            if (path.startsWith(list.getKey())) {
//
//                matchedOne = true;
//
//                for (WatchEventListener listener : new ArrayList<>(list.getValue())) {
//
//                    WatchFileEvent agentEvent = new HotswapWatchFileEvent(event, path);
//
//                    try {
//
//                        //调用核心热加载逻辑
//
//                        listener.onEvent(agentEvent);
//
//                    } catch (Throwable e) {
//
//                        // LOGGER.error("Error in watch event '{}' listener
//
//                        // '{}'", e, agentEvent, listener);
//
//                    }
//
//                }
//
//            }
//
//        }
//
//        if (!matchedOne) {
//
//            LOGGER.error("无匹配 '{}',  path '{}'", event, path);
//
//        }
//
//    }
//
//    private final ArrayBlockingQueue<Event> eventQueue = new ArrayBlockingQueue<>(500);
//
////将监听到的文件传输给队列，异步消费。
//
//    public void add(WatchEvent<Path> event, Path path) {
//
//        eventQueue.offer(new Event(event, path));
//
//    }
//
//
//
//
//
//
//    private boolean processEvents() throws InterruptedException {
//
//        // wait for key to be signaled
//
//        WatchKey key = watcher.poll(10, TimeUnit.MILLISECONDS);
//
//        //没有文件变更时，直接返回
//
//        if (key == null) {
//
//            return true;
//
//        }
//
//        //防止文件没有写完、下面获取到正在写入的文件。停止一会，文件无论如何也写完了。
//
//        //因为我们文件变更采用的是Watch文件监听。这里涉及到一个问题
//
//        //当java文件过大，有可能文件还没有写完全，已经被监听到了，很容易引发EOF，这里这里适当休眠一小会
//
//        //服务端测试，修改1万5000行代码写入监听无压力。
//
//        Thread.sleep ( 200 );
//
//        Path dir = keys.get(key);
//
//        if (dir == null) {
//
//            return true;
//
//        }
//
//        for (WatchEvent<?> event : key.pollEvents()) {
//
//            WatchEvent.Kind<?> kind = event.kind();
//
//
//            if (kind == OVERFLOW) {
//
//                continue;
//
//            }
//
//            // Context for directory entry event is the file name of entry
//
//            WatchEvent<Path> ev = cast(event);
//
//            Path name = ev.context();
//
//            //获取到当前变更的文件。
//
//            Path child = dir.resolve(name);
//
//            //核心逻辑，交给时间监视器来处理
//
//            dispatcher.add(ev, child);
//
//            if (kind == ENTRY_CREATE) {
//
//                try {
//
//                    if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
//
//                        //当文件首次监控到，需要先初始化一下监控的目录。手动调用Listener
//
//                        recursiveFiles(child.toFile ().getAbsolutePath (),dispatcher,ev);
//
//                        registerAll(child);
//
//                    }
//
//                } catch (IOException x) {
//
//                }
//
//            }
//
//        }
//
//        boolean valid = key.reset();
//
//        if (!valid) {
//
//            keys.remove(key);
//
//            // all directories are inaccessible
//
//            if (keys.isEmpty()) {
//
//                return false;
//
//            }
//
//            if (classLoaderListeners.isEmpty()) {
//
//                for (WatchKey k : keys.keySet()) {
//
//                    k.cancel();
//
//                }
//
//                return false;
//
//            }
//
//        }
//
//        return true;
//
//    }
//
//}
