export interface ChatRoomInfo {
  mentorName: string;
  price: number;
  profileImageUrl: string;
  mentoringId: number;
  opponentName: string;
  myRole: 'MENTEE' | 'MENTOR';
  status: 'ACTIVATE' | 'DEACTIVATE';
}
