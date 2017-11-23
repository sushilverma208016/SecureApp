package commonsdk.server.mapper;

import commonsdk.server.dto.MessageDTO;
import commonsdk.server.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.TargetType;

/**
 * Created by mouradzouabi on 04/12/15.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    public MessageDTO toDTO(Message message);

    public Message toEntity(MessageDTO message);

    public void mapToEntity(MessageDTO messageDTO, @MappingTarget Message message);

}
