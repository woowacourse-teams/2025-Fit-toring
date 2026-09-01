package fittoring.application.chat.repository;

import java.time.Duration;
import java.util.Optional;

/**
 * 채팅 이미지 업로드 임시 권한(티켓)을 관리하는 포트.
 * presigned 발급 시점에 {@code uploadId -> memberId/chatRoomId/s3Key} 티켓을 생성하고,
 * 메시지 전송(dispatch) 시점에 1회 검증 후 소비(삭제)한다.
 */
public interface ChatImageUploadTicketRepository {

    void create(String uploadId, Long memberId, Long chatRoomId, String s3Key, Duration ttl);

    /**
     * 티켓을 검증하고 소비(삭제)한다.
     * uploadId가 존재하고 memberId/chatRoomId가 일치하면 저장된 s3Key를 반환하며 티켓을 삭제한다.
     * 검증 실패(부재/만료/소유자 불일치/방 불일치) 시 빈 값을 반환한다.
     */
    Optional<String> consume(String uploadId, Long memberId, Long chatRoomId);
}
