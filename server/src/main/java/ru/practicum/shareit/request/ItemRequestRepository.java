package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Page<ItemRequest> findItemRequestsByRequestorNot(long requestorId, Pageable pageable);

    List<ItemRequest> findItemRequestsByRequestor(long userId);

    ItemRequest findItemRequestByRequestor(long requestorId);

    ItemRequest findItemRequestById(long requestId);

    ItemRequest findItemRequestByIdAndRequestor(long id, long requestorId);

}
