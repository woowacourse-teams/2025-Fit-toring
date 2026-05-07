package fittoring.application.community.service;

import fittoring.application.community.presentation.dto.response.PostDetailResponse;
import fittoring.application.community.presentation.dto.response.PostListResponse;
import fittoring.application.community.repository.CommentRepository;
import fittoring.application.community.repository.PostLikeRepository;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.community.service.dto.PostCreateDto;
import fittoring.application.community.service.dto.PostDeleteDto;
import fittoring.application.community.service.dto.PostPaginationResult;
import fittoring.application.community.service.dto.PostUpdateDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.EmptyRequestException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.PostNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.LikeActorKeyHash;
import fittoring.domain.model.Post;
import fittoring.util.CursorCodec;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public PostDetailResponse createPost(PostCreateDto dto) {
        Post post = createPostByAuthorType(dto);
        Post saved = postRepository.save(post);
        return PostDetailResponse.from(saved, 0, false);
    }

    @Transactional(readOnly = true)
    public PostListResponse findPosts(String cursorCode) {
        PostPaginationResult result = postRepository.findPostsWithPagination(CursorCodec.decode(cursorCode));
        Map<Long, Long> commentCountByPostId = countCommentsByPostId(result.posts());
        List<PostListResponse.PostSummary> summaries = createPostSummaries(result.posts(), commentCountByPostId);
        return new PostListResponse(summaries, result.nextCursorCode(), result.hasNext());
    }

    private Map<Long, Long> countCommentsByPostId(List<Post> posts) {
        List<Long> postIds = extractPostIds(posts);
        return commentRepository.findAllByPostIdIn(postIds).stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getPost().getId(),
                        Collectors.counting()));
    }

    private List<Long> extractPostIds(List<Post> posts) {
        return posts.stream()
                .map(Post::getId)
                .toList();
    }

    private List<PostListResponse.PostSummary> createPostSummaries(
            List<Post> posts,
            Map<Long, Long> commentCountByPostId
    ) {
        return posts.stream()
                .map(post -> createPostSummary(post, commentCountByPostId))
                .toList();
    }

    private PostListResponse.PostSummary createPostSummary(Post post, Map<Long, Long> commentCountByPostId) {
        int commentCount = commentCountByPostId.getOrDefault(post.getId(), 0L).intValue();
        return PostListResponse.PostSummary.from(post, commentCount);
    }

    @Transactional
    public PostDetailResponse findPost(Long postId) {
        return findPost(postId, null);
    }

    @Transactional
    public PostDetailResponse findPost(Long postId, LikeActorKeyHash actorKeyHash) {
        Post post = getPost(postId);
        post.increaseViewCount();
        int commentCount = (int) commentRepository.countByPostId(post.getId());
        boolean liked = isLiked(actorKeyHash, post);
        return PostDetailResponse.from(post, commentCount, liked);
    }

    private boolean isLiked(LikeActorKeyHash actorKeyHash, Post post) {
        if (actorKeyHash == null) {
            return false;
        }
        return postLikeRepository.existsByPostIdAndActorKeyHashValue(post.getId(), actorKeyHash.getValue());
    }

    @Transactional
    public void modifyPost(PostUpdateDto dto) {
        Post post = getPost(dto.postId());
        validatePostAccess(post, dto.memberId(), dto.guestPassword());
        post.modify(dto.title(), dto.content());
    }

    @Transactional
    public void deletePost(PostDeleteDto dto) {
        Post post = getPost(dto.postId());
        validatePostAccess(post, dto.memberId(), dto.guestPassword());
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public void validateGuestPassword(Long postId, String guestPassword) {
        Post post = getPost(postId);
        post.matchGuestPassword(guestPassword);
    }

    @Transactional(readOnly = true)
    public boolean checkOwnership(Long postId, Long memberId) {
        Post post = getPost(postId);
        return post.isOwnedBy(memberId);
    }

    private Post createMemberPost(PostCreateDto dto) {
        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        String nickname = dto.isAnonymous() ? resolveAnonymousNickname(dto.nickname()) : member.getName();
        return Post.forMember(member, dto.title(), dto.content(), dto.isAnonymous(), nickname);
    }

    private Post createPostByAuthorType(PostCreateDto dto) {
        if (dto.memberId() == null) {
            return createGuestPost(dto);
        }
        return createMemberPost(dto);
    }

    private Post createGuestPost(PostCreateDto dto) {
        validateGuestPostCreateFields(dto);
        return Post.forGuest(dto.title(), dto.content(), dto.nickname(), dto.guestPassword());
    }

    private void validatePostAccess(Post post, Long memberId, String guestPassword) {
        if (post.isGuestPost()) {
            post.matchGuestPassword(guestPassword);
            return;
        }
        if (memberId != null && post.isOwnedBy(memberId)) {
            return;
        }
        throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(BusinessErrorMessage.POST_NOT_FOUND.getMessage()));
    }

    private String resolveAnonymousNickname(String requestedNickname) {
        if (requestedNickname == null || requestedNickname.isBlank()) {
            return "익명";
        }
        return requestedNickname;
    }

    private void validateGuestPostCreateFields(PostCreateDto dto) {
        if (dto.nickname() == null || dto.nickname().isBlank()) {
            throw new EmptyRequestException(BusinessErrorMessage.GUEST_NICKNAME_REQUIRED.getMessage());
        }
        if (dto.guestPassword() == null || dto.guestPassword().isBlank()) {
            throw new EmptyRequestException(BusinessErrorMessage.GUEST_PASSWORD_REQUIRED.getMessage());
        }
    }
}
