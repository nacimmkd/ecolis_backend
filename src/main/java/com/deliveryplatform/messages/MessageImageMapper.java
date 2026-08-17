package com.deliveryplatform.messages;

import com.deliveryplatform.messages.dto.MessageImageDto;
import com.deliveryplatform.storage.MediaUrlResolver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {MediaUrlResolver.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MessageImageMapper {

    @Mapping(target = "url", source = "key", qualifiedByName = "resolveUrl")
    @Mapping(target = "content", source = "mediaType.content")
    @Mapping(target = "uploadedAt", source = "createdAt")
    MessageImageDto toDto(MessageImage image);

    List<MessageImageDto> toDto(List<MessageImage> images);
}