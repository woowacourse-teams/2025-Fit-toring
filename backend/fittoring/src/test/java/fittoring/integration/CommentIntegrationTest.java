package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.community.presentation.dto.request.CommentCreateRequest;
import fittoring.application.community.presentation.dto.request.CommentUpdateRequest;
import fittoring.application.community.presentation.dto.request.GuestPasswordRequest;
import fittoring.application.community.presentation.dto.response.CommentResponse;
import fittoring.application.community.repository.CommentRepository;
import fittoring.application.community.repository.PostRepository;
import fittoring.domain.model.Comment;
import fittoring.domain.model.Post;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @DisplayName("비회원 댓글 작성은 201을 반환한다.")
    @Test
    void createComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        CommentCreateRequest request = new CommentCreateRequest("content", false, "guest", "1234", null, null);

        CommentResponse response = RestAssured.given(spec)
                .filter(documentWithTag("comment/post-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("comment")
                                .requestSchema(Schema.schema("CommentCreateRequest"))
                                .responseSchema(Schema.schema("CommentResponse"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/posts/{postId}/comments", post.getId())
                .then()
                .statusCode(201)
                .extract()
                .as(CommentResponse.class);

        assertThat(response.content()).isEqualTo("content");
    }

    @DisplayName("댓글 목록 조회는 200을 반환한다.")
    @Test
    void findComments() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        commentRepository.save(Comment.forGuest(post, "content", "guest", "1234", null, null));

        List<CommentResponse> response = RestAssured.given(spec)
                .filter(documentWithTag("comment/get-list",
                        resource(ResourceSnippetParameters.builder()
                                .tag("comment")
                                .responseSchema(Schema.schema("CommentListResponse"))
                                .build())))
                .when()
                .get("/posts/{postId}/comments", post.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {});

        assertThat(response).hasSize(1);
    }

    @DisplayName("댓글 수정은 200을 반환한다.")
    @Test
    void modifyComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(Comment.forGuest(post, "old", "guest", "1234", null, null));
        CommentUpdateRequest request = new CommentUpdateRequest("new", "1234");

        RestAssured.given(spec)
                .filter(documentWithTag("comment/patch-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("comment")
                                .requestSchema(Schema.schema("CommentUpdateRequest"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch("/comments/{commentId}", comment.getId())
                .then()
                .statusCode(200);
    }

    @DisplayName("댓글 삭제는 204를 반환한다.")
    @Test
    void deleteComment() {
        Post post = postRepository.save(FixtureUtil.testGuestPost());
        Comment comment = commentRepository.save(Comment.forGuest(post, "old", "guest", "1234", null, null));
        GuestPasswordRequest request = new GuestPasswordRequest("1234");

        RestAssured.given(spec)
                .filter(documentWithTag("comment/delete-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("comment")
                                .requestSchema(Schema.schema("GuestPasswordRequest"))
                                .build())))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .delete("/comments/{commentId}", comment.getId())
                .then()
                .statusCode(204);
    }
}
