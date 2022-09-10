package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookingDto {
    private Long id;
    @NotNull(groups = {Create.class})
    private LocalDateTime start;
    @NotNull(groups = {Create.class})
    private LocalDateTime end;
    @NotNull(groups = {Create.class})
    private Long itemId;
    private BookingStatus status;
    private Long bookerId;


    public BookingDto(Long id, LocalDateTime start, LocalDateTime end, Long itemId) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.itemId = itemId;
    }
}
