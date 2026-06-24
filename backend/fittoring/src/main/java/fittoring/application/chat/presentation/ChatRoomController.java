package fittoring.application.chat.presentation;

import fittoring.application.image.presentation.dto.response.ImageUrlResponse;
import fittoring.application.chat.presentation.dto.request.ChatImagePresignedRequest;
import fittoring.application.chat.presentation.dto.response.ChatImagePresignedResponse;
import fittoring.application.chat.presentation.dto.response.ChatMessagePaginationResponse;
import fittoring.application.chat.presentation.dto.response.ChatRoomPreviewResponse;
import fittoring.application.chat.service.ChatImageUploadService;
import fittoring.application.chat.service.ChatMessageService;
import fittoring.application.chat.service.ChatRoomFacadeService;
import fittoring.application.chat.presentation.dto.response.ChatRoomInfoResponse;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/chatrooms")
@RestController
public class ChatRoomController {

    private final ChatRoomFacadeService chatRoomFacadeService;
    private final ChatMessageService chatMessageService;
    private final ChatImageUploadService chatImageUploadService;

    @AuthRequired
    @GetMapping
    public ResponseEntity<List<ChatRoomPreviewResponse>> getChatRoomPreviews(
            @Login LoginInfo loginInfo
    ) {
        List<ChatRoomPreviewResponse> response = chatRoomFacadeService.getChatRoomPreviews(loginInfo.memberId());
        return ResponseEntity.ok(response);
    }

    @AuthRequired
    @GetMapping("/{chatroomId}")
    public ResponseEntity<ChatRoomInfoResponse> getChatRoom(
            @Login LoginInfo loginInfo,
            @PathVariable("chatroomId") Long chatroomId
    ) {
        ChatRoomInfoResponse response = chatRoomFacadeService.getChatRoom(chatroomId, loginInfo.memberId());
        return ResponseEntity.ok(response);
    }

    @AuthRequired
    @PostMapping("/{chatroomId}/images/presigned")
    public ResponseEntity<ChatImagePresignedResponse> issueChatImagePresignedUrl(
            @Login LoginInfo loginInfo,
            @PathVariable("chatroomId") Long chatRoomId,
            @Valid @RequestBody ChatImagePresignedRequest request
    ) {
        ChatImagePresignedResponse response = chatImageUploadService.issue(
                chatRoomId,
                loginInfo.memberId(),
                request.extension()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AuthRequired
    @GetMapping("/{chatroomId}/messages")
    public ResponseEntity<ChatMessagePaginationResponse> getChatMessages(
            @Login LoginInfo loginInfo,
            @PathVariable("chatroomId") Long chatRoomId,
            @RequestParam(required = false) String cursorCode
    ) {
        ChatMessagePaginationResponse response = chatMessageService.findChatMessages(
                chatRoomId,
                loginInfo.memberId(),
                cursorCode
        );
        return ResponseEntity.ok(response);
    }

    @AuthRequired
    @GetMapping("/{chatroomId}/messages/{messageId}/image-url")
    public ResponseEntity<ImageUrlResponse> getImageUrl(
            @Login LoginInfo loginInfo,
            @PathVariable("chatroomId") Long chatRoomId,
            @PathVariable("messageId") Long messageId
    ) {
        ImageUrlResponse response = chatMessageService.reissueImageUrl(
                chatRoomId, messageId, loginInfo.memberId()
        );
        return ResponseEntity.ok(response);
    }
}
