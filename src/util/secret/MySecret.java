package util.secret;

import java.util.HashMap;
import java.util.Map;

public class MySecret implements ISecret {
    private MySecret() {}
    private static MySecret instance;

    public static MySecret getSecret() throws NoEnvExcedption {
        if (instance == null) {
            instance = new MySecret();
            for (SecretCategory category : SecretCategory.values()) {
                instance.setSecret(category.key);
            }
        }
        return instance;
    }

    final Map<String, String> map = new HashMap<>();
    @Override
    public void setSecret(String key) throws NoEnvExcedption {
        String env = System.getenv(key);
        if(env == null){
            throw new NoEnvExcedption(key);
        }
        map.put(key, System.getenv(key));
    }

    @Override
    public String getSecret(String key) throws NoEnvExcedption {
        if (!map.containsKey(key)) {
            throw new NoEnvExcedption(key);
        }
        return map.get(key);
    }
}

interface ISecret {
    void setSecret(String key) throws NoEnvExcedption;
    String getSecret(String key) throws NoEnvExcedption;
}

