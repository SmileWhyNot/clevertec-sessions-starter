package vlad.kuchuk.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vlad.kuchuk.entity.User;

@Mapper
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequest request);

    UserResponse toDto(User user);
}