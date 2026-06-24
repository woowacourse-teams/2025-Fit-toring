package fittoring.application.chat.service;

import fittoring.application.chat.presentation.dto.response.ChatImagePresignedResponse;
import fittoring.application.chat.repository.ChatImageUploadTicketRepository;
import fittoring.application.image.presentation.dto.response.PresignedIssueResponse;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.image.service.dto.IssuedPresignedDto;
import fittoring.domain.model.ImageExtension;
import fittoring.domain.model.ImageType;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 채팅 이미지 업로드 확정 전 임시 권한(티켓)을 발급하는 thin 서비스.
 * 채팅방 참여 권한을 확인한 뒤 기존 presigned 발급 로직을 재사용하고,
 * {@code uploadId -> memberId/chatRoomId/s3Key} 티켓을 Redis에 TTL과 함께 저장한다.
 */
@Service
public class ChatImageUploadService {

    private final ChatRoomService chatRoomService;
    private final PresignedUrlService presignedUrlService;
    private final ChatImageUploadTicketRepository ticketRepository;
    private final Duration ticketTtl;

    public ChatImageUploadService(
            ChatRoomService chatRoomService,
            PresignedUrlService presignedUrlService,
            ChatImageUploadTicketRepository ticketRepository,
            @Value("${chat-image-upload.ticket.ttl-seconds:300}") long ticketTtlSeconds
    ) {
        this.chatRoomService = chatRoomService;
        this.presignedUrlService = presignedUrlService;
        this.ticketRepository = ticketRepository;
        this.ticketTtl = Duration.ofSeconds(ticketTtlSeconds);
    }

    public ChatImagePresignedResponse issue(Long chatRoomId, Long memberId, ImageExtension extension) {
        chatRoomService.getAccessibleChatRoom(chatRoomId, memberId);

        PresignedIssueResponse presigned = presignedUrlService.issuePresignedUrl(
                new IssuedPresignedDto(ImageType.CHAT, extension)
        );

        String uploadId = UUID.randomUUID().toString();
        ticketRepository.create(uploadId, memberId, chatRoomId, presigned.key(), ticketTtl);

        return new ChatImagePresignedResponse(uploadId, presigned.presignedUrl(), presigned.expiresAt());
    }
}
