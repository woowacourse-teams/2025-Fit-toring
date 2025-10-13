export interface mentoringCreateFormData {
  price: number;
  category: string[];
  introduction: string;
  profileImageUrl: string | null;
  career: number;
  content: string;
  chatUrl: string;
  certificateInfos: {
    type: string | null;
    title: string | null;
    imageUrl?: string | null;
  }[];
}
