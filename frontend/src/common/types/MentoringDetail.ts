export interface MentoringDetail {
  id: number;
  mentorName: string;
  ratingAverage: string;
  ratingCount: number;
  categories: string[];
  price: number;
  career: number;
  profileImageUrl: string | null;
  introduction: string;
  content: string;
  certificates: Certificates[];
}

export interface Certificates {
  certificateId: string;
  title: string;
  type: string;
  imageUrl: string;
}
