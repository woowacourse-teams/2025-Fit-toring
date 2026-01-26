import LoadingSpinner from '../../../../common/components/LoadingSpinner/LoadingSpinner';
import EmptyData from '../EmptyData/EmptyData';
import MentorCardItem from '../MentorCardItem/MentorCardItem';

import type { MentorInformation } from '../../types/MentorInformation';

interface MentorCardListContentProps {
  isLoading: boolean;
  mentorList: MentorInformation[];
  hasFilter: boolean;
}

function MentorCardListContent({
  isLoading,
  mentorList,
  hasFilter,
}: MentorCardListContentProps) {
  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (mentorList.length === 0) {
    return (
      <EmptyData
        title={hasFilter ? '검색 결과가 없습니다' : '등록된 멘토링이 없습니다'}
        description={
          hasFilter
            ? '다른 조건으로 검색해보세요'
            : '첫 번째 멘토링을 개설해보세요!'
        }
      />
    );
  }

  return (
    <>
      {mentorList.map((mentor) => (
        <MentorCardItem key={mentor.id} mentor={mentor} />
      ))}
    </>
  );
}

export default MentorCardListContent;
