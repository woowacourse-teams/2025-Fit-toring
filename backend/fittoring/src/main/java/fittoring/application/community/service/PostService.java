package fittoring.application.community.service;

import fittoring.application.community.presentation.dto.response.PostDetailResponse;
import fittoring.application.community.presentation.dto.response.PostListResponse;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.community.service.dto.PostCreateDto;
import fittoring.application.community.service.dto.PostDeleteDto;
import fittoring.application.community.service.dto.PostPaginationResult;
import fittoring.application.community.service.dto.PostUpdateDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.exception.PostNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.Post;
import fittoring.util.CursorCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PostDetailResponse createPost(PostCreateDto dto) {
        Post post = dto.memberId() == null ? createGuestPost(dto) : createMemberPost(dto);
        return PostDetailResponse.from(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public PostListResponse findPosts(String cursorCode) {
        PostPaginationResult result = postRepository.findPostsWithPagination(CursorCodec.decode(cursorCode));
        return PostListResponse.from(result);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse findPost(Long postId) {
        return PostDetailResponse.from(getPost(postId));
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

    private Post createMemberPost(PostCreateDto dto) {
        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        String nickname = dto.isAnonymous() ? defaultIfBlank(dto.nickname(), "익명") : member.getName();
        return Post.forMember(member, dto.title(), dto.content(), dto.isAnonymous(), nickname);
    }

    private Post createGuestPost(PostCreateDto dto) {
        String nickname = defaultIfBlank(dto.nickname(), "비회원");
        return Post.forGuest(dto.title(), dto.content(), nickname, dto.guestPassword());
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

    private String defaultIfBlank(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}
