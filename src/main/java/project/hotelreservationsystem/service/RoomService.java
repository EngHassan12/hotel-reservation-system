package project.hotelreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.hotelreservationsystem.dto.RoomDto;
import project.hotelreservationsystem.entity.Room;
import project.hotelreservationsystem.exception.DuplicateResourceException;
import project.hotelreservationsystem.exception.ResourceNotFoundException;
import project.hotelreservationsystem.repository.RoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public Room createRoom(RoomDto dto) {
        if (roomRepository.existsByRoomNumber(dto.getRoomNumber()))
            throw new DuplicateResourceException("Room number already exists");

        Room room = Room.builder()
                .roomNumber(dto.getRoomNumber())
                .roomType(dto.getRoomType())
                .pricePerNight(dto.getPricePerNight())
                .capacity(dto.getCapacity())
                .status(dto.getStatus() != null ? dto.getStatus() : "AVAILABLE")
                .build();

        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus("AVAILABLE");
    }

    public Room getRoomById(Integer id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    public Room updateRoom(Integer id, RoomDto dto) {
        Room room = getRoomById(id);
        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomType(dto.getRoomType());
        room.setPricePerNight(dto.getPricePerNight());
        room.setCapacity(dto.getCapacity());
        room.setStatus(dto.getStatus());
        return roomRepository.save(room);
    }

    public void deleteRoom(Integer id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }
}