package epam.com.setMentoringProgram.libraryProject.utils.validators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;

public class ConverterUtils {

    private static final ModelMapper modelMapper = new ModelMapper();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T convertToEntity(Object entity, Class<T> clazz) {
        return modelMapper.map(entity, clazz);
    }

    public static <T> String getJsonFromObject(T serviceObject) {
        String jsonFromObject = null;
        try {
            jsonFromObject = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(serviceObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonFromObject;
    }
}
