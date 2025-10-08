ALTER TABLE chat_room
ADD CONSTRAINT uq_chat_room_reservation_id UNIQUE (reservation_id);
