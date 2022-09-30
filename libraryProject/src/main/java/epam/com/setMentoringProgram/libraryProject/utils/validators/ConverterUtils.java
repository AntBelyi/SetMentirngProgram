package epam.com.setMentoringProgram.libraryProject.utils.validators;

import org.modelmapper.ModelMapper;

public class ConverterUtils {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static <T> T convertToEntity(Object entity, Class<T> clazz) {
        return modelMapper.map(entity, clazz);
    }
}
