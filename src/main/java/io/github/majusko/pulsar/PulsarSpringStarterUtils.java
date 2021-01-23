package io.github.majusko.pulsar;

import com.google.protobuf.GeneratedMessageV3;
import io.github.majusko.pulsar.constant.Serialization;
import io.github.majusko.pulsar.error.exception.ProducerInitException;
import org.apache.pulsar.client.api.Schema;

import java.lang.reflect.Method;

public class PulsarSpringStarterUtils {

    private static <T> Schema<?> getGenericSchema(Serialization serialization, Class<T> clazz) throws RuntimeException {
        switch (serialization) {
            case JSON: {
                return Schema.JSON(clazz);
            }
            case AVRO: {
                return Schema.AVRO(clazz);
            }
            case STRING: {
                return Schema.STRING;
            }
            case BYTE: {
                return Schema.BYTES;
            }
            default: {
                throw new ProducerInitException("Unknown producer schema.");
            }
        }
    }

    private static <T extends com.google.protobuf.GeneratedMessageV3> Schema<?> getProtoSchema(Serialization serialization, Class<T> clazz) throws RuntimeException {
        if (serialization == Serialization.PROTOBUF) {
            return Schema.PROTOBUF(clazz);
        }
        throw new ProducerInitException("Unknown producer schema.");
    }

    public static Schema<?> getSchema(Serialization serialisation, Class<?> clazz) {
        if (clazz == byte[].class) {
            return Schema.BYTES;
        }

        if (isProto(serialisation)) {
            return getProtoSchema(serialisation, (Class<? extends GeneratedMessageV3>) clazz);
        }

        return getGenericSchema(serialisation, clazz);
    }

    public static boolean isProto(Serialization serialization) {
        return serialization == Serialization.PROTOBUF;
    }

    public static Class<?> getParameterType(Method method) {
        return method.getParameterTypes()[0];
    }

}
