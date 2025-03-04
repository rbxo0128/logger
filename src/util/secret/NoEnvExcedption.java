package util.secret;

public class NoEnvExcedption extends Exception {
    public NoEnvExcedption(String key) {
        super("존재 하지 않는 환경 변수 %s".formatted(key));
    }
}