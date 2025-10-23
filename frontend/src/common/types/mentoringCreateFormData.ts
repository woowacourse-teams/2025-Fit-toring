export interface mentoringCreateFormData {
  price: number;
  category: string[];
  introduction: string;
  profileImageUrl: string | null;
  career: number;
  content: string;
  certificateInfoRequests: {
    type: string | null;
    title: string | null;
    imageUrl?: string | null;
  }[];
}
