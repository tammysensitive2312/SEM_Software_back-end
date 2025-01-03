package org.example.sem_backend.modules.notification_module.domain.mapper;

import org.example.sem_backend.modules.notification_module.domain.dto.NotificationRequest;
import org.example.sem_backend.modules.notification_module.domain.dto.NotificationResponse;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.mapstruct.*;
import org.ocpsoft.prettytime.PrettyTime;

import java.time.LocalDateTime;
import java.util.Date;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    Notification toEntity(NotificationRequest notificationRequest);

    NotificationRequest toDto(Notification notification);

    @Mapping(target = "time", source = "createAt", qualifiedByName = "formatLocalDateTime")
    NotificationResponse toDtoResponse(Notification notification);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Notification partialUpdate(NotificationRequest notificationRequest, @MappingTarget Notification notification);

    @Named("formatLocalDateTime")
    static String formatLocalDateTime(LocalDateTime createdAt) {
        PrettyTime prettyTime = new PrettyTime();
        return prettyTime.format(createdAt);
    }

}