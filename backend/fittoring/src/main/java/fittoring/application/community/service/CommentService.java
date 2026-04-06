package fittoring.application.community.service;

import fittoring.application.community.presentation.dto.response.CommentResponse;
import fittoring.application.community.repository.CommentRepository;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.community.service.dto.CommentCreateDto;
import fittoring.application.community.service.dto.CommentDeleteDto;
import fittoring.application.community.service.dto.CommentUpdateDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.CommentNotFoundException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.PostNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Comment;
import fittoring.domain.model.Member;
import fittoring.domain.model.Post;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentResponse createComment(CommentCreateDto dto) {
        Post post = getPost(dto.postId());
        validateReplyTarget(dto, post.getId());
        Comment comment = dto.memberId() == null ? createGuestComment(dto, post) : createMemberComment(dto, post);
        return CommentResponse.from(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findComments(Long postId) {
        getPost(postId);
        return commentRepository.findAllByPostId(postId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public void modifyComment(CommentUpdateDto dto) {
        Comment comment = getComment(dto.commentId());
        validateCommentAccess(comment, dto.memberId(), dto.guestPassword());
        comment.modify(dto.content());
    }

    @Transactional
    public void deleteComment(CommentDeleteDto dto) {
        Comment comment = getComment(dto.commentId());
        validateCommentAccess(comment, dto.memberId(), dto.guestPassword());
        commentRepository.delete(comment);
    }

    private Comment createMemberComment(CommentCreateDto dto, Post post) {
        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        String nickname = dto.isAnonymous() ? defaultIfBlank(dto.nickname(), "익명") : member.getName();
        return Comment.forMember(post, member, dto.content(), dto.isAnonymous(), nickname, dto.rootId(), dto.parentId());
    }

    private Comment createGuestComment(CommentCreateDto dto, Post post) {
        String nickname = defaultIfBlank(dto.nickname(), "비회원");
        return Comment.forGuest(post, dto.content(), nickname, dto.guestPassword(), dto.rootId(), dto.parentId());
    }

    private void validateReplyTarget(CommentCreateDto dto, Long postId) {
        if (dto.rootId() == null && dto.parentId() == null) {
            return;
        }
        Comment rootComment = getComment(dto.rootId());
        Comment parentComment = getComment(dto.parentId());
        if (!rootComment.belongsTo(postId) || !parentComment.belongsTo(postId)) {
            throw new ForbiddenException(BusinessErrorMessage.COMMENT_NOT_BELONG_TO_POST.getMessage());
        }
    }

    private void validateCommentAccess(Comment comment, Long memberId, String guestPassword) {
        if (comment.isGuestComment()) {
            comment.matchGuestPassword(guestPassword);
            return;
        }
        if (memberId != null && comment.isOwnedBy(memberId)) {
            return;
        }
        throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(BusinessErrorMessage.POST_NOT_FOUND.getMessage()));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(BusinessErrorMessage.COMMENT_NOT_FOUND.getMessage()));
    }

    private String defaultIfBlank(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}
