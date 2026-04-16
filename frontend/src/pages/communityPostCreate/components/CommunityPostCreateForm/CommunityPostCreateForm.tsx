import { useState } from 'react';

import styled from '@emotion/styled';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import Checkbox from '../../../../common/components/Checkbox/Checkbox';
import CommunityPostForm from '../../../../common/components/CommunityPostForm/CommunityPostForm';
import LoadingSpinner from '../../../../common/components/LoadingSpinner/LoadingSpinner';
import { PAGE_URL } from '../../../../common/constants/url';
import { authCheckQueryOptions } from '../../../../common/queries/auth';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { postCommunityPostDetail } from '../../apis/postCommunityPostDetail';

function CommunityPostCreateForm() {
  const navigate = useNavigate();
  const [nickname, setNickname] = useState('');
  const [guestPassword, setGuestPassword] = useState('');
  const [isAnonymous, setIsAnonymous] = useState(false);

  const { isPending, isSuccess: isAuthenticated } = useQuery(
    authCheckQueryOptions,
  );

  const { mutate, isPending: isSubmitPending } = useMutation({
    mutationFn: postCommunityPostDetail,
    onSuccess: () => {
      alert('커뮤니티 글이 성공적으로 등록되었습니다.');
      navigate(PAGE_URL.COMMUNITY);
    },
    onError: (error) => {
      alert('커뮤니티 글 등록에 실패했습니다. 다시 시도해주세요.');
      captureSentryError({
        error,
        level: 'warning',
        feature: 'community-post-create',
        step: 'post-community-post-create',
      });
    },
  });

  if (isPending) {
    return (
      <S_LoadingContainer>
        <LoadingSpinner size="large" />
      </S_LoadingContainer>
    );
  }

  const shouldShowGuestFields = !isAuthenticated || isAnonymous;
  const isOptionValid = shouldShowGuestFields
    ? Boolean(nickname.trim() && guestPassword.trim())
    : true;

  const optionSection = (
    <S_Section>
      <S_Divider />
      {shouldShowGuestFields && (
        <>
          <S_Row>
            <S_RowLabel>닉네임</S_RowLabel>
            <S_Input
              value={nickname}
              placeholder="닉네임을 입력하세요."
              onChange={(e) => setNickname(e.target.value)}
            />
          </S_Row>
          <S_Row>
            <S_RowLabel>비밀번호</S_RowLabel>
            <S_Input
              type="password"
              value={guestPassword}
              placeholder="비밀번호를 입력하세요."
              onChange={(e) => setGuestPassword(e.target.value)}
            />
          </S_Row>
        </>
      )}

      <S_CheckboxRow>
        <Checkbox
          label="익명"
          checked={isAnonymous}
          onChange={(e) => setIsAnonymous(e.target.checked)}
        />
      </S_CheckboxRow>
    </S_Section>
  );

  return (
    <CommunityPostForm
      isSubmitPending={isSubmitPending}
      isOptionValid={isOptionValid}
      optionSection={optionSection}
      submitLabel="작성 완료"
      onSavePost={({ title, content }) =>
        mutate({
          title,
          content,
          isAnonymous,
          ...(shouldShowGuestFields
            ? {
                nickname: nickname.trim(),
                guestPassword: guestPassword.trim(),
              }
            : {}),
        })
      }
    />
  );
}

export default CommunityPostCreateForm;

const S_LoadingContainer = styled.main`
  display: flex;
  align-items: center;
  justify-content: center;

  min-height: calc(100dvh - 5.7rem);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Section = styled.section`
  display: flex;
  flex-direction: column;
  gap: 1.6rem;

  padding: 1.2rem 1.4rem 1.6rem;
`;

const S_Divider = styled.div`
  width: 100%;
  height: 1px;

  background-color: ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_Row = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const S_RowLabel = styled.span`
  flex: 0 0 6.4rem;

  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
`;

const S_CheckboxRow = styled.div`
  display: flex;
  justify-content: flex-end;
`;

const S_Input = styled.input`
  flex: 1;

  height: 4.4rem;
  min-width: 0;
  padding: 0 1.3rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 1.2rem;

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};

  &::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY300};
  }

  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }
`;
