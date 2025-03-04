package util.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger implements ILogger {
    private MyLogger() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(MyLoggerLevel.DEBUG.level); /// 안넣어도 됨
        logger = Logger.getLogger(MyLogger.class.getName());
        logger.addHandler(handler);
        logger.setLevel(MyLoggerLevel.DEBUG.level);
        logger.setUseParentHandlers(false);
    }
    private static Logger logger;
    private static MyLogger instance;

    public static MyLogger getLogger() {


        if (instance == null) {
            instance = new MyLogger();
        }
        return instance;
    }

    public void setLevel(MyLoggerLevel level){
        logger.setLevel(level.level);
    }

    @Override
    public void debug(String msg) {
        logger.log(Level.INFO, msg);
    }

    @Override
    public void info(String msg) {
        logger.log(Level.INFO, msg);
    }

    @Override
    public void warn(String msg) {
        logger.log(Level.WARNING, msg);
    }

    @Override
    public void error(String msg) {
        logger.log(Level.SEVERE, msg);
    }
}

interface ILogger {
    void debug(String msg);
    void info(String msg);
    void warn(String msg);
    void error(String msg);
}
