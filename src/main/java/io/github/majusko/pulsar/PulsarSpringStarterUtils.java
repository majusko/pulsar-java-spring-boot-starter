package io.github.majusko.pulsar;

import io.github.majusko.pulsar.constant.Serialization;
import io.github.majusko.pulsar.error.exception.ProducerInitException;
import org.apache.pulsar.client.api.Schema;

import java.lang.reflect.Method;

public class PulsarSpringStarterUtils {

    public static <T> Schema<?> getSchema(Serialization serialization, Class<T> clazz) throws RuntimeException {
        switch (serialization) {
            case JSON: {
                return Schema.JSON(clazz);
            }
            case AVRO: {
                return Schema.AVRO(clazz);
            }
            default: {
                throw new ProducerInitException("Unknown producer schema.");
            }
        }
    }

    public static Class<?> getParameterType(Method method) {
        return method.getParameterTypes()[0];
    }

}
