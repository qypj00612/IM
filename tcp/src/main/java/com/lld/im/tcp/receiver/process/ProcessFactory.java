package com.lld.im.tcp.receiver.process;

public class ProcessFactory {

    private static BaseProcess process;

    static {
        process = new BaseProcess() {
            @Override
            public void processBefore() {

            }

            @Override
            public void processAfter() {

            }
        };
    }

    public static BaseProcess getProcess(Integer command) {
        return process;
    }

}
