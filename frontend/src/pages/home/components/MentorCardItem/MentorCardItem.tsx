import MentorDetailInfoButton from './MentorDetailInfoButton';

// 멘토 카드 아이템 컴포넌트
// - 멘토 정보를 보여줘야 한다.
//   - 멘토 정보 객체와 협력해야 한다.
// 상세 정보 보기를 클릭 시 다음 페이지로 넘어갈 수 있어야 한다.

function MentorCardItem() {
  return (
    <li>
      <MentorDetailInfoButton />
    </li>
  );
}

export default MentorCardItem;
