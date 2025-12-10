package kzone.cache.common.serde;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class DataSerializerTest {
    @Test
    void serde() {
        MyData myData = new MyData("id", "data");
        String serialized = DataSerializer.serializeOrException(myData);

        MyData deserialized = DataSerializer.deserializeOrNull(serialized, MyData.class);
        assertThat(deserialized).isEqualTo(myData);
    }

    record MyData(
            String id, String data
    ) {

    }
}