# 댓글 좋아요 API 동작 정리

## 핵심 요약

댓글 좋아요는 **회원/비회원 구분 없이 쿠키 기반 식별자**로 동작한다.

- 식별 쿠키 이름: `likeActorId`
- 같은 쿠키로 같은 댓글에 좋아요를 여러 번 요청해도 좋아요 수는 한 번만 증가한다.
- 좋아요한 상태에서 취소 요청을 보내면 좋아요 수가 감소한다.
- 쿠키가 없는 상태에서 좋아요 취소를 요청하면 새 쿠키를 발급하지 않고 좋아요 수를 변경하지 않는다.
- 루트 댓글과 대댓글 모두 같은 API로 좋아요를 누를 수 있다.

## API 목록

| 기능 | Method | Path |
| --- | --- | --- |
| 댓글 좋아요 추가 | `POST` | `/posts/{postId}/comments/{commentId}/like` |
| 댓글 좋아요 취소 | `DELETE` | `/posts/{postId}/comments/{commentId}/like` |
| 댓글 목록 조회 | `GET` | `/posts/{postId}/comments` |

## 좋아요 추가

```http
POST /posts/{postId}/comments/{commentId}/like
Cookie: likeActorId={actorId}
```

### 쿠키가 있는 경우

서버는 `likeActorId` 값을 HMAC-SHA256으로 해시해서 `comment_like.actor_key_hash`에 저장한다.

이미 같은 `commentId`, `actor_key_hash` 조합이 있으면 새 row를 추가하지 않는다. 이 중복 방지는 DB 유니크 키로 보장한다.

```sql
UNIQUE KEY uk_comment_like_comment_id_actor_key_hash (comment_id, actor_key_hash)
```

### 쿠키가 없는 경우

서버가 새 `likeActorId` 쿠키를 발급한다.

```http
Set-Cookie: likeActorId={uuid}; Max-Age=31536000; ...
```

그 후 새 식별자를 해시해서 좋아요를 저장한다.

### 응답

```json
{
  "commentId": 1,
  "liked": true,
  "likeCount": 3
}
```

응답 의미:

- `commentId`: 좋아요 대상 댓글 ID
- `liked`: 요청 처리 후 현재 사용자가 좋아요한 상태인지 여부
- `likeCount`: 요청 처리 후 댓글의 전체 좋아요 수

## 좋아요 취소

```http
DELETE /posts/{postId}/comments/{commentId}/like
Cookie: likeActorId={actorId}
```

### 쿠키가 있는 경우

서버는 쿠키 값을 해시한 뒤, 해당 댓글의 좋아요 row를 삭제한다.

삭제된 row가 있으면 `comment.like_count`를 1 감소시킨다.

### 쿠키가 없는 경우

좋아요 취소는 아무 것도 삭제하지 않는다.

이때 새 쿠키도 발급하지 않는다. 쿠키가 없는 사용자는 서버 입장에서 어떤 좋아요를 취소해야 하는지 식별할 수 없기 때문이다.

### 응답

```json
{
  "commentId": 1,
  "liked": false,
  "likeCount": 2
}
```

## 댓글 목록 조회

```http
GET /posts/{postId}/comments
Cookie: likeActorId={actorId}
```

댓글 목록 응답은 각 댓글마다 좋아요 정보를 포함한다.

```json
[
  {
    "id": 1,
    "content": "댓글 내용",
    "nickname": "비회원",
    "isAnonymous": false,
    "isGuestComment": true,
    "rootId": null,
    "parentId": null,
    "likeCount": 3,
    "liked": true,
    "isDeleted": false,
    "createdAt": "2026-05-05T12:00:00"
  }
]
```

### `liked` 계산 방식

쿠키가 있으면 서버는 현재 페이지의 댓글 ID 목록을 기준으로 좋아요한 댓글 ID를 한 번에 조회한다.

```text
댓글 목록 조회
 -> comment 목록 조회
 -> comment_like에서 현재 actor가 좋아요한 commentId 목록 조회
 -> 각 CommentResponse에 liked 반영
```

쿠키가 없거나 유효하지 않으면 모든 댓글의 `liked`는 `false`다.

## 예외 흐름

### 게시글이 없는 경우

```http
POST /posts/999/comments/1/like
```

`404 Not Found`를 반환한다.

```json
{
  "message": "존재하지 않는 게시글입니다."
}
```

### 댓글이 없는 경우

```http
POST /posts/1/comments/999/like
```

`404 Not Found`를 반환한다.

```json
{
  "message": "존재하지 않는 댓글입니다."
}
```

### 댓글이 해당 게시글에 속하지 않는 경우

```http
POST /posts/1/comments/2/like
```

댓글 `2`가 게시글 `1`에 속하지 않으면 `403 Forbidden`을 반환한다.

```json
{
  "message": "해당 게시글에 속하지 않는 댓글입니다."
}
```

## 구현 흐름

### 좋아요 추가

```text
CommentLikeController.like()
 -> LikeActorResolver.resolveOrCreate()
 -> CommentLikeService.like()
 -> 게시글 존재 검증
 -> 댓글 존재 및 게시글 소속 검증
 -> comment_like INSERT IGNORE
 -> insert 성공 시 comment.like_count 증가
 -> CommentLikeResponse 반환
```

### 좋아요 취소

```text
CommentLikeController.unlike()
 -> LikeActorResolver.resolve()
 -> CommentLikeService.unlike()
 -> 게시글 존재 검증
 -> 댓글 존재 및 게시글 소속 검증
 -> actorKeyHash가 없으면 no-op 응답
 -> comment_like row 삭제
 -> delete 성공 시 comment.like_count 감소
 -> CommentLikeResponse 반환
```

## 설계상 주의점

### `likeActorId`는 회원 ID가 아니다

현재 "한 사람당 한 번"은 회원 계정 기준이 아니라 **브라우저 쿠키 기준**이다.

따라서 아래 상황에서는 같은 실제 사용자라도 다른 사용자처럼 처리될 수 있다.

- 쿠키 삭제
- 다른 브라우저 사용
- 다른 기기 사용

이 설계는 회원/비회원을 동일하게 처리하기 위한 타협이다. 강한 중복 방지가 필요하면 로그인 회원은 `memberId`, 비회원은 쿠키 식별자를 사용하는 별도 정책이 필요하다.

### `POST` 하나로 토글하지 않는다

현재 API는 좋아요 추가와 취소를 분리한다.

- `POST`: 좋아요 상태로 만든다.
- `DELETE`: 좋아요하지 않은 상태로 만든다.

프론트엔드는 댓글의 `liked` 값에 따라 호출할 API를 선택해야 한다.

```text
liked == false -> POST /like
liked == true  -> DELETE /like
```

이 방식은 네트워크 재시도나 중복 클릭 상황에서 단일 토글 API보다 상태를 예측하기 쉽다.
