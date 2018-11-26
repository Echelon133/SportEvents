package ml.echelon133.sportevents;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import ml.echelon133.sportevents.event.EventMixIn;
import ml.echelon133.sportevents.event.types.dto.MatchEventDto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SportEventsApp {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // We need a mix-in to enable deserialization to a specific type, that is decided based on the value of
        // 'type' field of deserialized object. Different 'type' fields make object mapper deserialize json
        // to different classes that extend MatchEventDto
        mapper.addMixIn(MatchEventDto.class, EventMixIn.class);
        return mapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(SportEventsApp.class, args);
    }
}
