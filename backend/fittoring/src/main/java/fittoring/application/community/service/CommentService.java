package fittoring.application.community.service;

import fittoring.application.community.presentation.dto.response.CommentResponse;
import fittoring.application.community.repository.CommentLikeRepository;
import fittoring.application.community.repository.CommentRepository;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.community.service.dto.CommentCreateDto;
import fittoring.application.community.service.dto.CommentDeleteDto;
import fittoring.application.community.service.dto.CommentUpdateDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.CommentNotFoundException;
import fittoring.application.exception.EmptyRequestException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.InvalidCommentReplyException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.PostNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Comment;
import fittoring.domain.model.LikeActorKeyHash;
import fittoring.domain.model.Member;
import fittoring.domain.model.Post;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public CommentResponse createComment(CommentCreateDto dto) {
        Post post = getPost(dto.postId());
        validateReplyTarget(dto, post.getId());
        Comment comment = dto.memberId() == null ? createGuestComment(dto, post) : createMemberComment(dto, post);
        return CommentResponse.from(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findComments(Long postId, LikeActorKeyHash actorKeyHash) {
        getPost(postId);
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        Set<Long> likedCommentIds = findLikedCommentIds(comments, actorKeyHash);
        return comments.stream()
                .map(comment -> CommentResponse.from(comment, likedCommentIds.contains(comment.getId())))
                .toList();
    }

    private Set<Long> findLikedCommentIds(List<Comment> comments, LikeActorKeyHash actorKeyHash) {
        if (actorKeyHash == null || comments.isEmpty()) {
            return Set.of();
        }
        List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .toList();
        return new HashSet<>(commentLikeRepository.findLikedCommentIds(commentIds, actorKeyHash.getValue()));
    }

    @Transactional
    public void modifyComment(CommentUpdateDto dto) {
        Comment comment = getComment(dto.commentId());
        validateCommentAccessByCaller(comment, dto.memberId(), dto.guestPassword());
        comment.modify(dto.content());
    }

    @Transactional
    public void deleteComment(CommentDeleteDto dto) {
        Comment comment = getComment(dto.commentId());
        validateCommentAccessByCaller(comment, dto.memberId(), dto.guestPassword());
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public void validateGuestPassword(Long commentId, String guestPassword) {
        Comment comment = getComment(commentId);
        comment.matchGuestPassword(guestPassword);
    }

    @Transactional(readOnly = true)
    public List<Long> findOwnedCommentIds(Long postId, Long memberId) {
        return commentRepository.findIdsByPostIdAndMemberId(postId, memberId);
    }

    private Comment createMemberComment(CommentCreateDto dto, Post post) {
        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        String nickname = dto.isAnonymous() ? resolveAnonymousNickname(dto.nickname()) : member.getName();
        return Comment.forMember(post, member, dto.content(), dto.isAnonymous(), nickname, dto.rootId(), dto.parentId());
    }

    private Comment createGuestComment(CommentCreateDto dto, Post post) {
        validateGuestCommentCreateFields(dto);
        return Comment.forGuest(post, dto.content(), dto.nickname(), dto.guestPassword(), dto.rootId(), dto.parentId());
    }

    private void validateReplyTarget(CommentCreateDto dto, Long postId) {
        boolean hasRootId = dto.rootId() != null;
        boolean hasParentId = dto.parentId() != null;

        if (!hasRootId && !hasParentId) {
            return;
        }

        if (hasRootId != hasParentId) {
            throw new InvalidCommentReplyException(BusinessErrorMessage.INVALID_COMMENT_REPLY.getMessage());
        }

        Comment rootComment = getComment(dto.rootId());
        Comment parentComment = getComment(dto.parentId());
        if (!rootComment.belongsTo(postId) || !parentComment.belongsTo(postId)) {
            throw new ForbiddenException(BusinessErrorMessage.COMMENT_NOT_BELONG_TO_POST.getMessage());
        }
        if (!rootComment.isRootComment() || !parentComment.isInRoot(rootComment.getId())) {
            throw new InvalidCommentReplyException(BusinessErrorMessage.INVALID_COMMENT_REPLY.getMessage());
        }
    }

    private void validateCommentAccessByCaller(Comment comment, Long memberId, String guestPassword) {
        if (memberId != null) {
            validateMemberCommentAccess(comment, memberId);
        } else {
            validateGuestCommentAccess(comment, guestPassword);
        }
    }

    private void validateMemberCommentAccess(Comment comment, Long memberId) {
        if (comment.isGuestComment()) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
        if (!comment.isOwnedBy(memberId)) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
    }

    private void validateGuestCommentAccess(Comment comment, String guestPassword) {
        if (!comment.isGuestComment()) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
        comment.matchGuestPassword(guestPassword);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(BusinessErrorMessage.POST_NOT_FOUND.getMessage()));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(BusinessErrorMessage.COMMENT_NOT_FOUND.getMessage()));
    }

    private String resolveAnonymousNickname(String requestedNickname) {
        if (requestedNickname == null || requestedNickname.isBlank()) {
            return "익명";
        }
        return requestedNickname;
    }

    private void validateGuestCommentCreateFields(CommentCreateDto dto) {
        if (dto.nickname() == null || dto.nickname().isBlank()) {
            throw new EmptyRequestException(BusinessErrorMessage.GUEST_NICKNAME_REQUIRED.getMessage());
        }
        if (dto.guestPassword() == null || dto.guestPassword().isBlank()) {
            throw new EmptyRequestException(BusinessErrorMessage.GUEST_PASSWORD_REQUIRED.getMessage());
        }
    }
}
