export interface ChatRoomInfo {
  mentorName: string;
  price: number;
  profileImageUrl: string | null;
  mentoringId: number;
  opponentName: string;
  myRole: 'MENTEE' | 'MENTOR';
  status: 'ACTIVATE' | 'DEACTIVATE';
}
