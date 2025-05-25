package dev.genro.luan.packing_test.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PackedBoxResponse(@JsonProperty(value = "box_id") String boxId,
                                List<String> products,
                                String observation) {
}
