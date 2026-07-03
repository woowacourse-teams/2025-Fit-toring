import { useState } from 'react';
import type { ReactNode } from 'react';

import styled from '@emotion/styled';

import menuDotsIcon from '../../../../common/assets/images/menuDots.svg';
import LikeToggleButton from '../../../../common/components/LikeToggleButton/LikeToggleButton';
import useOutsideClickRef from '../../../../common/hooks/useOutsideClickRef';
import { formatTimeAgo } from '../../../../common/utils/formatTimeAgo';

import type { PostComment } from '../../types/postComment';

interface CommentItemProps {
  comment: PostComment;
  depth: number;
  onReplyClick: (comment: PostComment) => void;
  onEditClick: (comment: PostComment) => void;
  onDeleteClick: (comment: PostComment) => void;
  onLikeClick: (comment: PostComment) => void;
  isLikePending?: boolean;
  children?: ReactNode;
}

function CommentItem({
  comment,
  depth,
  onReplyClick,
  onEditClick,
  onDeleteClick,
  onLikeClick,
  isLikePending = false,
  children,
}: CommentItemProps) {
  const [menuOpened, setMenuOpened] = useState(false);
  const canManageComment = comment.isGuestComment || comment.isMine;
  const { ref: menuRef } = useOutsideClickRef<HTMLDivElement>(() =>
    setMenuOpened(false),
  );

  const handleMenuButtonClick = () => {
    setMenuOpened((prev) => !prev);
  };

  const handleEditClick = () => {
    setMenuOpened(false);
    onEditClick(comment);
  };

  const handleDeleteClick = () => {
    setMenuOpened(false);
    onDeleteClick(comment);
  };

  return (
    <S_Container depth={depth}>
      <S_Header>
        <S_Nickname>{comment.nickname}</S_Nickname>
        <S_HeaderRight>
          <S_CreatedAt>{formatTimeAgo(comment.createdAt)}</S_CreatedAt>
          {canManageComment && !comment.isDeleted ? (
            <S_ActionContainer ref={menuRef}>
              <S_MenuButton
                type="button"
                aria-haspopup="menu"
                aria-expanded={menuOpened}
                aria-label="댓글 관리 메뉴 열기"
                onClick={handleMenuButtonClick}
              >
                <S_MenuIcon src={menuDotsIcon} alt="" />
              </S_MenuButton>
              <S_MenuList opened={menuOpened} role="menu">
                <S_MenuItem role="none">
                  <S_MenuActionButton
                    type="button"
                    role="menuitem"
                    onClick={handleEditClick}
                  >
                    수정
                  </S_MenuActionButton>
                </S_MenuItem>
                <S_MenuItem role="none">
                  <S_MenuActionButton
                    type="button"
                    role="menuitem"
                    onClick={handleDeleteClick}
                  >
                    삭제
                  </S_MenuActionButton>
                </S_MenuItem>
              </S_MenuList>
            </S_ActionContainer>
          ) : null}
        </S_HeaderRight>
      </S_Header>
      <S_Content>
        {comment.isDeleted ? '삭제된 댓글입니다.' : comment.content}
      </S_Content>
      {!comment.isDeleted ? (
        <S_Actions>
          <S_ReplyButton type="button" onClick={() => onReplyClick(comment)}>
            답글쓰기
          </S_ReplyButton>
          <LikeToggleButton
            count={comment.likeCount}
            pressed={comment.liked}
            size="small"
            ariaLabel={`좋아요 ${comment.likeCount}개`}
            disabled={isLikePending}
            onClick={() => onLikeClick(comment)}
          />
        </S_Actions>
      ) : null}
      {children}
    </S_Container>
  );
}

export default CommentItem;

const S_Container = styled.li<{ depth: number }>`
  display: flex;
  flex-direction: column;
  gap: 0.6rem;

  padding: 1.2rem 0 1.2rem ${({ depth }) => `${depth * 1.6}rem`};
`;

const S_Header = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.2rem;
`;

const S_HeaderRight = styled.div`
  display: flex;
  align-items: center;
  gap: 0.8rem;
`;

const S_Nickname = styled.strong`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_SB}
`;

const S_CreatedAt = styled.span`
  flex-shrink: 0;

  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;

const S_ActionContainer = styled.div`
  position: relative;
`;

const S_MenuButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;

  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const S_MenuIcon = styled.img`
  width: 2rem;
  height: 2rem;
`;

const S_MenuList = styled.ul<{ opened: boolean }>`
  visibility: ${({ opened }) => (opened ? 'visible' : 'hidden')};
  position: absolute;
  top: calc(100% + 0.8rem);
  right: 0;
  z-index: 10;

  width: 12rem;
  padding: 0.6rem 0;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 1.2rem;
  box-shadow: 0 4px 16px rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};
  opacity: ${({ opened }) => (opened ? 1 : 0)};
  transform: ${({ opened }) =>
    opened ? 'translateY(0)' : 'translateY(-0.8rem)'};
  transition: opacity 0.2s ease, transform 0.2s ease, visibility 0.2s ease;
`;

const S_MenuItem = styled.li`
  list-style: none;
`;

const S_MenuActionButton = styled.button`
  width: 100%;
  padding: 1rem 1.4rem;
  border: none;

  background-color: transparent;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: left;
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
  cursor: pointer;

  &:hover {
    background-color: ${({ theme }) => theme.SYSTEM.GRAY50};
  }
`;

const S_Content = styled.p`
  color: ${({ theme }) => theme.FONT.B02};
  white-space: pre-wrap;
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_Actions = styled.div`
  display: flex;
  align-items: center;
  gap: 1.2rem;
`;

const S_ReplyButton = styled.button`
  padding: 0;
  border: none;

  background: transparent;

  color: ${({ theme }) => theme.FONT.B04};
  cursor: pointer;
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;
