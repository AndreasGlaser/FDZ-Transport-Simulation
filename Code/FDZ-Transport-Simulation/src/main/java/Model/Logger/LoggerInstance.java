package Model.Logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerInstance {

    public static final Logger log = LoggerFactory.getLogger(LoggerInstance.class);
    public static LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
    public static Logger rootLogger = loggerContext.getLogger(LoggerInstance.class);

    public static void infoLevel (){
        ((ch.qos.logback.classic.Logger) rootLogger).setLevel(Level.INFO);
    }

    public void debugLevel (){
        ((ch.qos.logback.classic.Logger) rootLogger).setLevel(Level.DEBUG);
    }

    public void warnLevel (){
        ((ch.qos.logback.classic.Logger) rootLogger).setLevel(Level.WARN);
    }

    public void errorLevel (){
        ((ch.qos.logback.classic.Logger) rootLogger).setLevel(Level.ERROR);
    }

}
